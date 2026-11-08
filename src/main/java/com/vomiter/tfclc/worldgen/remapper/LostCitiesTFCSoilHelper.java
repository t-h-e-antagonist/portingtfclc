package com.vomiter.tfclc.worldgen.remapper;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.IGrassBlock;
import net.dries007.tfc.common.blocks.soil.ISoilBlock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.IdentityHashMap;
import java.util.Map;

public final class LostCitiesTFCSoilHelper {
    private static final Map<Block, SoilBlockType.Variant> BLOCK_TO_VARIANT = new IdentityHashMap<>();
    private static final Map<Block, SoilBlockType> BLOCK_TO_TYPE = new IdentityHashMap<>();
    private static RandomSource randomSource;

    // 找不到鄰近 TFC soil 時的保底值
    private static final SoilBlockType.Variant FALLBACK_VARIANT = SoilBlockType.Variant.ENTISOL;

    static {
        for (SoilBlockType type : SoilBlockType.values()) {
            for (Map.Entry<SoilBlockType.Variant, ? extends TFCBlocks.Id<Block>> entry : TFCBlocks.SOIL.get(type).entrySet()) {
                // TFCBlocks.Id 是 record，透過 holder() 取得 DeferredHolder 再 .get() 拿到 Block
                Block block = entry.getValue().holder().get();
                BLOCK_TO_VARIANT.put(block, entry.getKey());
                BLOCK_TO_TYPE.put(block, type);
            }
        }
    }

    public static BlockState remapVanillaSurface(BlockState original, LevelAccessor level, BlockPos pos) {
        if (original == null) {
            return null;
        }

        final Block block = original.getBlock();
        if(randomSource == null){
            randomSource = level.getRandom().fork();
        }

        if (block == Blocks.GRASS_BLOCK) {
            if(randomSource.nextInt(10) < 3){
                return getSoilState(level, pos, SoilBlockType.CLAY_GRASS);
            }
            return getSoilState(level, pos, SoilBlockType.GRASS);
        }
        if (block == Blocks.DIRT) {
            if(randomSource.nextInt(10) < 3){
                return getSoilState(level, pos, SoilBlockType.CLAY);
            }

            return getSoilState(level, pos, SoilBlockType.DIRT);
        }
        if (block == Blocks.DIRT_PATH) {
            return getSoilState(level, pos, SoilBlockType.GRASS_PATH);
        }
        if (block == Blocks.FARMLAND) {
            return getSoilState(level, pos, SoilBlockType.FARMLAND);
        }
        if (block == Blocks.ROOTED_DIRT) {
            return getSoilState(level, pos, SoilBlockType.ROOTED_DIRT);
        }

        return original;
    }

    public static BlockState getSoilState(LevelAccessor level, BlockPos pos, SoilBlockType targetType) {
        final SoilBlockType.Variant variant = inferVariant(level, pos);
        // 同樣需要 .holder().get()
        BlockState state = TFCBlocks.SOIL.get(targetType).get(variant).holder().get().defaultBlockState();

        // TFC grass 預設 state 不一定立刻帶好連接資訊；這裡先手動補一次簡單的 neighbor shape
        if (state.getBlock() instanceof ConnectedGrassBlock grass) {
            state = updateConnectedGrassState(level, pos, state, grass);
        }

        return state;
    }

    private static SoilBlockType.Variant inferVariant(LevelAccessor level, BlockPos pos) {
        // 優先順序：
        // 1. 正下方
        // 2. 四周
        // 3. 上下
        // 4. fallback

        SoilBlockType.Variant variant;

        variant = getVariantAt(level, pos.below());
        if (variant != null) return variant;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            variant = getVariantAt(level, pos.relative(direction));
            if (variant != null) return variant;
        }

        variant = getVariantAt(level, pos.above());
        if (variant != null) return variant;

        return FALLBACK_VARIANT;
    }

    private static SoilBlockType.Variant getVariantAt(LevelAccessor level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return BLOCK_TO_VARIANT.get(block);
    }

    private static BlockState updateConnectedGrassState(LevelAccessor level, BlockPos pos, BlockState state, ConnectedGrassBlock grass) {
        // 模擬一個最小版的 neighbor 檢查，避免剛放下去時四側都 false
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            boolean connected = isSoilLike(neighbor);
            switch (direction) {
                case NORTH -> state = state.setValue(ConnectedGrassBlock.NORTH, connected);
                case SOUTH -> state = state.setValue(ConnectedGrassBlock.SOUTH, connected);
                case EAST  -> state = state.setValue(ConnectedGrassBlock.EAST, connected);
                case WEST  -> state = state.setValue(ConnectedGrassBlock.WEST, connected);
            }
        }

        BlockState up = level.getBlockState(pos.above());
        boolean snowy = up.is(Blocks.SNOW) || up.is(Blocks.SNOW_BLOCK);
        state = state.setValue(ConnectedGrassBlock.SNOWY, snowy);

        return state;
    }

    private static boolean isSoilLike(BlockState state) {
        return state.getBlock() instanceof ISoilBlock
            || state.getBlock() instanceof IGrassBlock
            || BLOCK_TO_VARIANT.containsKey(state.getBlock());
    }
}