package com.vomiter.tfclc.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class LostCitiesSpawnHelper {

    public static boolean shouldDenyNaturalSpawn(ServerLevel level, BlockPos pos, EntityType<?> entityType) {
        if (!isLikelyLostCitiesRoof(level, pos)) {
            return false;
        }

        return true;
    }

    public static boolean isLikelyLostCitiesRoof(ServerLevel level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState below = level.getBlockState(belowPos);

        if (below.isAir() || !below.isSolidRender(level, belowPos)) {
            return false;
        }

        // 上方必須夠空，避免把室內當屋頂
        int openAbove = 0;
        for (int i = 1; i <= 5; i++) {
            if (level.isEmptyBlock(pos.above(i))) {
                openAbove++;
            }
        }
        if (openAbove < 4) {
            return false;
        }

        // 周圍下方採樣，看看比較像人工建材還是自然地表
        int artificial = 0;
        int natural = 0;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos samplePos = pos.offset(dx, -1, dz);
                BlockState sample = level.getBlockState(samplePos);

                if (sample.isAir()) {
                    continue;
                }

                if (isNaturalGround(sample)) {
                    natural++;
                } else if (isLikelyArtificialBlock(sample)) {
                    artificial++;
                }
            }
        }

        if (artificial < 6) {
            return false;
        }

        if (natural >= 5) {
            return false;
        }

        return true;
    }

    public static boolean isNaturalGround(BlockState state) {
        ResourceLocation id = getBlockId(state);
        if (id == null) {
            return false;
        }

        String s = id.toString();
        return s.contains("dirt")
            || s.contains("grass")
            || s.contains("soil")
            || s.contains("mud")
            || s.contains("sand")
            || s.contains("gravel")
            || s.contains("clay");
    }

    public static boolean isLikelyArtificialBlock(BlockState state) {
        ResourceLocation id = getBlockId(state);
        if (id == null) {
            return false;
        }

        String s = id.toString();
        return s.contains("brick")
            || s.contains("bricks")
            || s.contains("glass")
            || s.contains("concrete")
            || s.contains("terracotta")
            || s.contains("plaster")
            || s.contains("tile")
            || s.contains("slab")
            || s.contains("stairs")
            || s.contains("roof")
            || s.contains("metal");
    }

    private static ResourceLocation getBlockId(BlockState state) {
        Block block = state.getBlock();
        return block.builtInRegistryHolder().key().location();
    }
}