package com.vomiter.tfclc.worldgen.remapper;

import com.vomiter.tfclc.util.stone.LostCitiesProtectionTracker;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class LostCitiesStoneRemapper {

    private LostCitiesStoneRemapper() {
    }

    public static BlockState remap(BlockState state) {
        if (state == null) {
            return null;
        }

        if (state.is(Blocks.QUARTZ_BLOCK)) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.SMOOTH).get().defaultBlockState();
        }

        if (state.is(Blocks.STONE)) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.HARDENED).get().defaultBlockState();
        }

        if (state.is(Blocks.QUARTZ_BRICKS)) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.MARBLE).get(Rock.BlockType.BRICKS).get().defaultBlockState();
        }

        if (state.is(Blocks.STONE_BRICKS)) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.BRICKS).get().defaultBlockState();
        }

        if (state.is(Blocks.CRACKED_STONE_BRICKS)) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.CRACKED_BRICKS).get().defaultBlockState();
        }

        if (state.is(Blocks.MOSSY_STONE_BRICKS)) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.ANDESITE).get(Rock.BlockType.MOSSY_BRICKS).get().defaultBlockState();
        }

        if (state.is(Blocks.QUARTZ_STAIRS)) {
            BlockState remapped = TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.MARBLE)
                    .get(Rock.BlockType.BRICKS).stair().get()
                    .defaultBlockState();
            return copyStairProperties(state, remapped);
        }

        if (state.is(Blocks.STONE_BRICK_STAIRS)) {
            BlockState remapped = TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.ANDESITE)
                    .get(Rock.BlockType.BRICKS).stair().get()
                    .defaultBlockState();
            return copyStairProperties(state, remapped);
        }

        if (state.is(Blocks.MOSSY_STONE_BRICK_STAIRS)) {
            BlockState remapped = TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.ANDESITE)
                    .get(Rock.BlockType.MOSSY_BRICKS).stair().get()
                    .defaultBlockState();
            return copyStairProperties(state, remapped);
        }

        if (state.is(Blocks.SMOOTH_STONE_SLAB) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.DACITE).get(Rock.BlockType.SMOOTH).get().defaultBlockState();
        }

        if (state.is(Blocks.SMOOTH_STONE)) {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.DACITE).get(Rock.BlockType.SMOOTH).get().defaultBlockState();
        }

        if (state.is(Blocks.SMOOTH_STONE_SLAB)) {
            return TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.DACITE)
                    .get(Rock.BlockType.SMOOTH).slab().get()
                    .defaultBlockState()
                    .setValue(SlabBlock.TYPE, state.getValue(SlabBlock.TYPE))
                    .setValue(SlabBlock.WATERLOGGED, state.getValue(SlabBlock.WATERLOGGED));
        }

        if (state.is(Blocks.QUARTZ_SLAB)) {
            return TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.MARBLE)
                    .get(Rock.BlockType.SMOOTH).slab().get()
                    .defaultBlockState()
                    .setValue(SlabBlock.TYPE, state.getValue(SlabBlock.TYPE))
                    .setValue(SlabBlock.WATERLOGGED, state.getValue(SlabBlock.WATERLOGGED));
        }

        if (state.is(Blocks.COBBLESTONE)) {
            return TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.DIORITE)
                    .get(Rock.BlockType.COBBLE).slab().get()
                    .defaultBlockState()
                    .setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        }

        if (state.is(Blocks.MOSSY_COBBLESTONE)) {
            return TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.DIORITE)
                    .get(Rock.BlockType.MOSSY_COBBLE).slab().get()
                    .defaultBlockState()
                    .setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        }

        if (state.is(Blocks.COBBLESTONE_WALL)) {
            BlockState remapped = TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.DIORITE)
                    .get(Rock.BlockType.COBBLE).wall().get()
                    .defaultBlockState();

            remapped = remapped.setValue(WallBlock.NORTH_WALL, state.getValue(WallBlock.NORTH_WALL));
            remapped = remapped.setValue(WallBlock.EAST_WALL, state.getValue(WallBlock.EAST_WALL));
            remapped = remapped.setValue(WallBlock.SOUTH_WALL, state.getValue(WallBlock.SOUTH_WALL));
            remapped = remapped.setValue(WallBlock.WEST_WALL, state.getValue(WallBlock.WEST_WALL));
            remapped = remapped.setValue(WallBlock.UP, state.getValue(WallBlock.UP));
            remapped = remapped.setValue(WallBlock.WATERLOGGED, state.getValue(WallBlock.WATERLOGGED));
            return remapped;
        }

        if (state.is(Blocks.MOSSY_COBBLESTONE_WALL)) {
            BlockState remapped = TFCBlocks.ROCK_DECORATIONS
                    .get(Rock.DIORITE)
                    .get(Rock.BlockType.MOSSY_COBBLE).wall().get()
                    .defaultBlockState();

            remapped = remapped.setValue(WallBlock.NORTH_WALL, state.getValue(WallBlock.NORTH_WALL));
            remapped = remapped.setValue(WallBlock.EAST_WALL, state.getValue(WallBlock.EAST_WALL));
            remapped = remapped.setValue(WallBlock.SOUTH_WALL, state.getValue(WallBlock.SOUTH_WALL));
            remapped = remapped.setValue(WallBlock.WEST_WALL, state.getValue(WallBlock.WEST_WALL));
            remapped = remapped.setValue(WallBlock.UP, state.getValue(WallBlock.UP));
            remapped = remapped.setValue(WallBlock.WATERLOGGED, state.getValue(WallBlock.WATERLOGGED));
            return remapped;
        }

        return state;
    }

    public static BlockState remapAndMark(LevelAccessor level, BlockPos pos, BlockState state) {
        final BlockState remapped = remap(state);
        if (shouldProtect(remapped)) {
            LostCitiesProtectionTracker.mark(pos.immutable());
        }
        return remapped;
    }

    public static BlockState remapAndMarkRange(LevelAccessor level, int x, int y1, int z, int y2, BlockState state) {
        final BlockState remapped = remap(state);
        if (shouldProtect(remapped)) {
            LostCitiesProtectionTracker.markRange(level, x, y1, z, y2);
        }
        return remapped;
    }

    public static boolean shouldProtect(BlockState state) {
        if (state == null) {
            return false;
        }
        final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id != null
                && "tfc".equals(id.getNamespace())
                && "rock/hardened/andesite".equals(id.getPath());
    }

    private static BlockState copyStairProperties(BlockState from, BlockState to) {
        to = to.setValue(StairBlock.FACING, from.getValue(StairBlock.FACING));
        to = to.setValue(StairBlock.HALF, from.getValue(StairBlock.HALF));
        to = to.setValue(StairBlock.SHAPE, from.getValue(StairBlock.SHAPE));
        to = to.setValue(StairBlock.WATERLOGGED, from.getValue(StairBlock.WATERLOGGED));
        return to;
    }
}