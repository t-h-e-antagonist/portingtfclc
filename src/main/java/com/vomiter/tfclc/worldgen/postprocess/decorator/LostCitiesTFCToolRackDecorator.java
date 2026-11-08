package com.vomiter.tfclc.worldgen.postprocess.decorator;

import com.vomiter.tfclc.Helpers;
import com.vomiter.tfclc.TFCLostCities;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.dries007.tfc.common.blockentities.ToolRackBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.ToolRackBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LostCitiesTFCToolRackDecorator {

    private static final TagKey<Item> TOOL_RACK_ITEMS = TagKey.create(
            Registries.ITEM,
            Helpers.id("tfc", "usable_on_tool_rack")
    );

    private static final TagKey<Item> NOT_TOOL_RACK_ITEMS = TagKey.create(
            Registries.ITEM,
            Helpers.id("tfclc", "old_tool_rack_items_blacklist")
    );

    private static final Wood[] RACK_WOODS = new Wood[] {
            Wood.OAK,
            Wood.SPRUCE,
            Wood.BIRCH,
            Wood.ASH,
            Wood.MAPLE,
            Wood.HICKORY
    };

    private LostCitiesTFCToolRackDecorator() {
    }

    public static void decorateChunk(IDimensionInfo provider, ChunkCoord coord) {
        final LevelAccessor level = provider.getWorld();
        final BuildingInfo info = BuildingInfo.getBuildingInfo(coord, provider);

        if (!info.hasBuilding) {
            return;
        }

        final long seed = Mth.getSeed(coord.chunkX(), info.getCityGroundLevel(), coord.chunkZ()) ^ 0x54CFC7A1D93E4B17L;
        final RandomSource random = RandomSource.create(seed);

        final List<ItemStack> itemPool = getRackableItemPool(level);
        if (itemPool.isEmpty()) {
            return;
        }

        final int minY = info.getCityGroundLevel() + 1;
        final int maxY = Math.max(minY, info.getMaxHeight() - 2);

        final int attempts = 3 + random.nextInt(3);
        int placed = 0;

        for (int i = 0; i < attempts * 12 && placed < attempts; i++) {
            final int rx = 2 + random.nextInt(12);
            final int rz = 2 + random.nextInt(12);
            final int y = random.nextInt(maxY - minY + 1) + minY;

            final BlockPos pos = info.getRelativePos(rx, y, rz);

            if (!isGoodCandidateAir(level, pos)) {
                continue;
            }
            if (!hasWalkableFloor(level, pos)) {
                continue;
            }

            final Direction facing = findWallFacing(level, pos, random);
            if (facing == null) {
                continue;
            }

            if (tryPlaceToolRack(level, pos, facing, random, itemPool)) {
                TFCLostCities.LOGGER.debug("[TFCLC] RACK PLACED AT　{}", pos);
                placed++;
            }
        }
    }

    private static boolean tryPlaceToolRack(LevelAccessor level, BlockPos pos, Direction facing, RandomSource random, List<ItemStack> pool) {
        final BlockState rackState = TFCBlocks.WOODS.get(randomWood(random))
                .get(Wood.BlockType.TOOL_RACK)
                .get()
                .defaultBlockState()
                .setValue(ToolRackBlock.FACING, facing)
                .setValue(ToolRackBlock.FLUID, ToolRackBlock.FLUID.keyFor(Fluids.EMPTY));

        if (!rackState.canSurvive(level, pos)) {
            return false;
        }
        if (!level.setBlock(pos, rackState, 2)) {
            return false;
        }
        if (!(level.getBlockEntity(pos) instanceof ToolRackBlockEntity rack)) {
            return false;
        }

        if (!(level instanceof Level realLevel)) return false;
        final IItemHandler handler = realLevel.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (handler == null) {
            return false;
        }

        final List<ItemStack> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, new java.util.Random(random.nextLong()));

        final int count = 1 + random.nextInt(3);
        int inserted = 0;
        int slot = 0;

        for (ItemStack original : shuffled) {
            if (slot >= handler.getSlots() || inserted >= count) {
                break;
            }

            ItemStack stack = original.copy();
            stack.setCount(1);

            if (stack.isDamageableItem()) {
                int maxDamage = stack.getMaxDamage();
                if (maxDamage > 8) {
                    int damage = random.nextInt(Math.min(Math.max(1, (int) (maxDamage * 0.95f)), maxDamage - 1));
                    stack.setDamageValue(damage);
                }
            }

            ItemStack remainder = handler.insertItem(slot, stack, false);
            if (remainder.isEmpty()) {
                inserted++;
                slot++;
            }
        }

        rack.setChanged();
        return true;
    }

    @Nullable
    private static Direction findWallFacing(LevelAccessor level, BlockPos pos, RandomSource random) {
        List<Direction> dirs = new ArrayList<>(4);
        dirs.add(Direction.NORTH);
        dirs.add(Direction.SOUTH);
        dirs.add(Direction.WEST);
        dirs.add(Direction.EAST);
        Collections.shuffle(dirs, new java.util.Random(random.nextLong()));

        for (Direction facing : dirs) {
            final BlockPos supportPos = pos.relative(facing.getOpposite());
            final BlockPos frontPos = pos.relative(facing);

            final BlockState supportState = level.getBlockState(supportPos);
            final BlockState frontState = level.getBlockState(frontPos);

            if (supportState.isAir()) {
                continue;
            }
            if (supportState.getBlock() instanceof DoorBlock) {
                continue;
            }
            if (!supportState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                continue;
            }
            if (!supportState.isFaceSturdy(level, supportPos, facing)) {
                continue;
            }
            // 替换不存在的 Tags.Blocks.GLASS
            if (supportState.is(net.minecraft.world.level.block.Blocks.GLASS) 
                || supportState.is(net.minecraft.world.level.block.Blocks.TINTED_GLASS)) {
                continue;
            }

            if (!frontState.isAir()) {
                continue;
            }

            return facing;
        }

        return null;
    }

    private static boolean isGoodCandidateAir(LevelAccessor level, BlockPos pos) {
        final BlockState state = level.getBlockState(pos);
        if (!state.canBeReplaced()) {
            return false;
        }
        if (!state.getFluidState().isEmpty()) {
            return false;
        }

        return level.getBlockState(pos.above()).isAir();
    }

    private static boolean hasWalkableFloor(LevelAccessor level, BlockPos pos) {
        final BlockPos below2 = pos.below().below();
        final BlockState floor = level.getBlockState(below2);
        final BlockState space = level.getBlockState(pos.below());
        return space.isAir() && !floor.isAir() && floor.isFaceSturdy(level, below2, Direction.UP);
    }

    private static Wood randomWood(RandomSource random) {
        return RACK_WOODS[random.nextInt(RACK_WOODS.length)];
    }

    private static List<ItemStack> getRackableItemPool(LevelAccessor level) {
        final Registry<Item> registry = level.registryAccess().registryOrThrow(Registries.ITEM);
        final List<ItemStack> result = new ArrayList<>();

        registry.getTag(TOOL_RACK_ITEMS).ifPresent(named -> {
            for (Holder<Item> holder : named) {
                final Item item = holder.value();
                if (item == null || item == Items.AIR) {
                    continue;
                }
                var stack = new ItemStack(item);
                if (stack.is(NOT_TOOL_RACK_ITEMS)) continue;
                result.add(stack);
            }
        });

        return result;
    }
}