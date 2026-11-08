package com.vomiter.tfclc.worldgen.postprocess.decorator;

import com.vomiter.tfclc.TFCLostCities;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.dries007.tfc.common.blockentities.IngotPileBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayDeque;
import java.util.Queue;

public final class LostCitiesTFCIngotPileDecorator {

    private static final Metal[] BASEMENT_METALS = new Metal[] {
            Metal.COPPER, Metal.TIN, Metal.ZINC, Metal.WROUGHT_IRON
    };
    private static final Metal[] RAIL_METALS = new Metal[] {
            Metal.NICKEL, Metal.BISMUTH, Metal.GOLD, Metal.SILVER
    };

    /** Positions whose block was placed during worldgen but whose BE still needs to be populated. */
    private static final Queue<PendingPile> PENDING = new ArrayDeque<>();

    private record PendingPile(ResourceLocation dimension, BlockPos pos, Metal metal, int count) {}

    private LostCitiesTFCIngotPileDecorator() {}

    public static void decorateChunk(IDimensionInfo provider, ChunkCoord coord) {
        final LevelAccessor level = provider.getWorld();
        final BuildingInfo info = BuildingInfo.getBuildingInfo(coord, provider);

        final long seed = Mth.getSeed(coord.chunkX(), info.getCityGroundLevel(), coord.chunkZ())
                ^ 0x31D8F4B2A4C91E77L;
        final RandomSource random = RandomSource.create(seed);

        if (info.hasBuilding) {
            decorateBasement(level, info, random);
        }
        decorateRailSide(level, info, random);
    }

    private static void decorateBasement(LevelAccessor level, BuildingInfo info, RandomSource random) {
        final int cityGround = info.getCityGroundLevel();
        final int minY = Math.max(1, cityGround - 12);
        final int maxY = cityGround - 2;
        if (maxY < minY) return;

        final int targetPiles = 1 + random.nextInt(2);
        int placed = 0;

        for (int i = 0; i < targetPiles * 16 && placed < targetPiles; i++) {
            final int rx = 2 + random.nextInt(12);
            final int rz = 2 + random.nextInt(12);
            final int y  = minY + random.nextInt(maxY - minY + 1);
            final BlockPos pos = info.getRelativePos(rx, y, rz);

            if (!isGoodPileSpot(level, pos)) continue;
            if (!isBasementLike(level, pos)) continue;
            if (!isNearWall(level, pos, 1)) continue;
            if (isNearRail(level, pos, 2)) continue;

            final Metal metal = randomElement(BASEMENT_METALS, random);
            final int count = 5 + random.nextInt(20);

            if (tryPlaceIngotPile(level, pos, metal, count)) {
                TFCLostCities.LOGGER.info("[TFCLC] BASEMENT INGOT PILE QUEUED AT {} ({}, {} ingots)", pos, metal.name(), count);
                placed++;
            }
        }
    }

    private static void decorateRailSide(LevelAccessor level, BuildingInfo info, RandomSource random) {
        final int cityGround = info.getCityGroundLevel();
        final int minY = Math.max(1, cityGround - 20);
        final int maxY = cityGround + 4;
        if (maxY < minY) return;

        final int targetPiles = 1 + random.nextInt(2);
        int placed = 0;

        for (int i = 0; i < targetPiles * 20 && placed < targetPiles; i++) {
            final int rx = 1 + random.nextInt(14);
            final int rz = 1 + random.nextInt(14);
            final int y  = minY + random.nextInt(maxY - minY + 1);
            final BlockPos pos = info.getRelativePos(rx, y, rz);

            if (!isGoodPileSpot(level, pos)) continue;
            if (!isNearRail(level, pos, 3)) continue;
            if (level.getBlockState(pos.below()).getBlock() instanceof BaseRailBlock) continue;

            final Metal metal = randomElement(RAIL_METALS, random);
            final int count = 6 + random.nextInt(19);

            if (tryPlaceIngotPile(level, pos, metal, count)) {
                TFCLostCities.LOGGER.info("[TFCLC] RAIL INGOT PILE QUEUED AT {} ({}, {} ingots)", pos, metal.name(), count);
                placed++;
            }
        }
    }

    /**
     * During worldgen we only place the block and remember the intended contents.
     * We must NOT call addIngot()/setChanged() here, because the BlockEntity
     * has no Level yet -> markForSync -> getLevel() == null -> NPE -> chunk aborted.
     */
    private static boolean tryPlaceIngotPile(LevelAccessor level, BlockPos pos, Metal metal, int count) {
        if (count < 1 || count > 64) return false;

        final BlockState pileState = TFCBlocks.INGOT_PILE.get()
                .defaultBlockState()
                .setValue(IngotPileBlock.COUNT, count);

        if (!pileState.canSurvive(level, pos)) return false;

        // Flag 2 = UPDATE_CLIENTS; do NOT use flag 3 (UPDATE_NEIGHBORS) — it can
        // trigger neighbour updates that try to fetch the chunk/level during worldgen.
        if (!level.setBlock(pos, pileState, 2)) return false;

        // Do not touch the block entity here — it isn't bound to a Level yet.
        // Record the pending contents; they'll be applied after the chunk loads.
        if (level instanceof Level realLevel) {
            PENDING.add(new PendingPile(
                    realLevel.dimension().location(),
                    pos.immutable(),
                    metal,
                    count
            ));
        } else {
            // If LevelAccessor is not a real Level (shouldn't happen for Lost Cities
            // postprocess, but guard anyway), skip — nothing we can safely queue.
            TFCLostCities.LOGGER.warn("[TFCLC] Ingot pile at {} queued without a real Level; skipping population", pos);
        }
        return true;
    }

    /**
     * Called every server tick (or on ChunkEvent.Load). Drains any pending piles whose
     * chunk is now fully loaded and populates the block entity safely.
     */
    public static void tick(Level level) {
        if (PENDING.isEmpty()) return;
        final ResourceLocation dim = level.dimension().location();

        // Iterate without iterator-removal issues; re-queue entries whose chunk isn't loaded yet.
        final int toProcess = PENDING.size();
        for (int i = 0; i < toProcess; i++) {
            final PendingPile p = PENDING.poll();
            if (p == null) break;

            if (!p.dimension().equals(dim)) {
                PENDING.add(p);
                continue;
            }
            if (!level.isLoaded(p.pos())) {
                PENDING.add(p);
                continue;
            }

            final BlockEntity be = level.getBlockEntity(p.pos());
            if (!(be instanceof IngotPileBlockEntity pile)) {
                TFCLostCities.LOGGER.warn("[TFCLC] No IngotPileBlockEntity at {} after load; dropping pending pile", p.pos());
                continue;
            }

            final ItemStack ingot = new ItemStack(
                    TFCItems.METAL_ITEMS.get(p.metal()).get(Metal.ItemType.INGOT).get()
            );
            for (int n = 0; n < p.count(); n++) {
                pile.addIngot(ingot.copy());
            }
            // addIngot internally calls markForSync; now safe because getLevel() != null.
            pile.setChanged();
            TFCLostCities.LOGGER.info("[TFCLC] BASEMENT/RAIL INGOT PILE POPULATED AT {} ({}, {} ingots)",
                    p.pos(), p.metal().name(), p.count());
        }
    }

    // ---------- unchanged scanning helpers ----------

    private static boolean isGoodPileSpot(LevelAccessor level, BlockPos pos) {
        final BlockState state = level.getBlockState(pos);
        final BlockState above = level.getBlockState(pos.above());
        final BlockState below = level.getBlockState(pos.below());

        if (!state.canBeReplaced()) return false;
        if (!state.getFluidState().isEmpty()) return false;
        if (!above.isAir()) return false;
        if (below.isAir()) return false;
        return below.isFaceSturdy(level, pos.below(), Direction.UP);
    }

    private static boolean isBasementLike(LevelAccessor level, BlockPos pos) {
        int solidCount = 0, checked = 0;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            cursor.set(pos.getX() + dir.getStepX(), pos.getY(), pos.getZ() + dir.getStepZ());
            checked++;
            if (!level.getBlockState(cursor).isAir()) solidCount++;

            cursor.set(pos.getX() + dir.getStepX(), pos.getY() + 1, pos.getZ() + dir.getStepZ());
            checked++;
            if (!level.getBlockState(cursor).isAir()) solidCount++;
        }
        return solidCount >= Math.max(3, checked / 2);
    }

    private static boolean isNearWall(LevelAccessor level, BlockPos pos, int radius) {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            for (int i = 1; i <= radius; i++) {
                cursor.set(pos.getX() + dir.getStepX() * i, pos.getY(), pos.getZ() + dir.getStepZ() * i);
                final BlockState state = level.getBlockState(cursor);
                if (!state.isAir() && state.isFaceSturdy(level, cursor, dir.getOpposite())) return true;
            }
        }
        return false;
    }

    private static boolean isNearRail(LevelAccessor level, BlockPos center, int radius) {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++)
            for (int dz = -radius; dz <= radius; dz++)
                for (int dy = -1; dy <= 1; dy++) {
                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    final BlockState state = level.getBlockState(cursor);
                    if (state.getBlock() instanceof BaseRailBlock) return true;
                    if (state.is(Blocks.RAIL) || state.is(Blocks.POWERED_RAIL)
                            || state.is(Blocks.DETECTOR_RAIL) || state.is(Blocks.ACTIVATOR_RAIL)) return true;
                }
        return false;
    }

    private static <T> T randomElement(T[] array, RandomSource random) {
        return array[random.nextInt(array.length)];
    }
}