package com.vomiter.tfclc.util;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class LostCitiesTFCLocalFloraHelper {

    private LostCitiesTFCLocalFloraHelper() {
    }

    public static void tryPlaceLocalFlora(LevelAccessor level, BlockPos pos) {
        if (!level.getBlockState(pos).isAir()) {
            return;
        }

        if (!level.getBlockState(pos.above()).isAir()) {
            return;
        }

        final ChunkData data = ChunkData.get(level, pos);
        final float rainfall = data.getAverageRainfall(pos);
        final float avgTemp = data.getAverageSeaLevelTemp(pos);
        final ForestType forestType = data.getForestType();
        final int forestDensity = forestType.getDensity();

        final RandomSource random = level.getRandom();

        // 植被密度放高，越常嘗試（用 getDensity() 的整數值近似原本的 float 門檻）
        int tries = 1;
        if (forestDensity >= 1) tries++;
        if (forestDensity >= 2) tries++;
        if (forestDensity >= 3) tries++;
        if (forestType.isPrimary() || forestType.isSecondary()) tries++;
        if (rainfall > 220f) tries++;

        for (int i = 0; i < tries; i++) {
            Plant candidate = pickPlant(random, rainfall, avgTemp, forestType, forestDensity);
            if (candidate == null) {
                continue;
            }

            if (tryPlacePlant(level, pos, candidate)) {
                return;
            }
        }

        // 真的都放不上去時，再試幾個很泛用的草
        Plant[] fallback = new Plant[] {
                Plant.BLUEGRASS,
                Plant.BROMEGRASS,
                Plant.ORCHARD_GRASS,
                Plant.RYEGRASS,
                Plant.TIMOTHY_GRASS,
                Plant.FOUNTAIN_GRASS
        };

        for (Plant plant : fallback) {
            if (tryPlacePlant(level, pos, plant)) {
                return;
            }
        }
    }

    private static Plant pickPlant(RandomSource random, float rainfall, float avgTemp, ForestType forestType, int forestDensity) {
        // 冷涼潮濕森林
        if (avgTemp < 8f) {
            if (rainfall > 250f) {
                return pick(random,
                        Plant.LADY_FERN,
                        Plant.ATHYRIUM_FERN,
                        Plant.HOUSTONIA,
                        Plant.TRILLIUM,
                        Plant.LILY_OF_THE_VALLEY,
                        Plant.BLUEGRASS,
                        Plant.ORCHARD_GRASS
                );
            }
            return pick(random,
                    Plant.HOUSTONIA,
                    Plant.BLUEGRASS,
                    Plant.BROMEGRASS,
                    Plant.ORCHARD_GRASS,
                    Plant.TIMOTHY_GRASS,
                    Plant.OXEYE_DAISY
            );
        }

        // 非常乾燥 / 熱乾
        if (rainfall < 110f) {
            if (avgTemp > 20f) {
                return pick(random,
                        Plant.DEAD_BUSH,
                        Plant.SAGEBRUSH,
                        Plant.YUCCA,
                        Plant.FOUNTAIN_GRASS,
                        Plant.SILVER_SPURFLOWER,
                        Plant.BARREL_CACTUS
                );
            }
            return pick(random,
                    Plant.DEAD_BUSH,
                    Plant.SAGEBRUSH,
                    Plant.FOUNTAIN_GRASS,
                    Plant.BROMEGRASS,
                    Plant.RYEGRASS
            );
        }

        // 濕熱
        if (avgTemp > 22f && rainfall > 260f) {
            if (forestType.isPrimary() || forestType.isSecondary() || forestDensity >= 3) {
                return pick(random,
                        Plant.CANNA,
                        Plant.BLUE_GINGER,
                        Plant.HELICONIA,
                        Plant.STRELITZIA,
                        Plant.TROPICAL_MILKWEED,
                        Plant.PHILODENDRON,
                        Plant.ANTHURIUM,
                        Plant.FOUNTAIN_GRASS
                );
            }
            return pick(random,
                    Plant.CANNA,
                    Plant.BLUE_GINGER,
                    Plant.TROPICAL_MILKWEED,
                    Plant.FOUNTAIN_GRASS,
                    Plant.RADDIA_GRASS,
                    Plant.ORCHARD_GRASS
            );
        }

        // 濕地／高雨
        if (rainfall > 320f) {
            if (random.nextInt(100) < 35) {
                return pick(random,
                        Plant.CATTAIL,
                        Plant.PHRAGMITE,
                        Plant.PICKERELWEED,
                        Plant.BUR_REED,
                        Plant.WATER_TARO
                );
            }
            return pick(random,
                    Plant.FIELD_HORSETAIL,
                    Plant.LADY_FERN,
                    Plant.BLUEGRASS,
                    Plant.BROMEGRASS,
                    Plant.ORCHARD_GRASS,
                    Plant.TIMOTHY_GRASS,
                    Plant.GOLDENROD
            );
        }

        // 林地
        if (forestType.isPrimary() || forestType.isSecondary() || forestDensity >= 3) {
            if (avgTemp > 18f) {
                return pick(random,
                        Plant.GOLDENROD,
                        Plant.NASTURTIUM,
                        Plant.GRAPE_HYACINTH,
                        Plant.ORCHARD_GRASS,
                        Plant.BLUEGRASS,
                        Plant.BROMEGRASS,
                        Plant.LADY_FERN
                );
            }
            return pick(random,
                    Plant.HOUSTONIA,
                    Plant.TRILLIUM,
                    Plant.OXEYE_DAISY,
                    Plant.BLUEGRASS,
                    Plant.ORCHARD_GRASS,
                    Plant.TIMOTHY_GRASS,
                    Plant.ATHYRIUM_FERN
            );
        }

        // 一般溫帶開闊地
        if (avgTemp >= 8f && avgTemp <= 22f) {
            return pick(random,
                    Plant.BLUEGRASS,
                    Plant.BROMEGRASS,
                    Plant.ORCHARD_GRASS,
                    Plant.RYEGRASS,
                    Plant.TIMOTHY_GRASS,
                    Plant.GOLDENROD,
                    Plant.DANDELION,
                    Plant.OXEYE_DAISY,
                    Plant.POPPY
            );
        }

        // 保底
        return pick(random,
                Plant.BLUEGRASS,
                Plant.BROMEGRASS,
                Plant.ORCHARD_GRASS,
                Plant.RYEGRASS,
                Plant.TIMOTHY_GRASS,
                Plant.FOUNTAIN_GRASS
        );
    }

    private static boolean tryPlacePlant(LevelAccessor level, BlockPos pos, Plant plant) {
        final Block block = TFCBlocks.PLANTS.get(plant).get();
        final BlockState state = block.defaultBlockState();

        if (!state.canSurvive(level, pos)) {
            return false;
        }

        if (block instanceof DoublePlantBlock) {
            if (!level.getBlockState(pos.above()).isAir()) {
                return false;
            }
            DoublePlantBlock.placeAt(level, state, pos, 2);
            return true;
        }

        level.setBlock(pos, state, 2);
        return true;
    }

    @SafeVarargs
    private static <T> T pick(RandomSource random, T... values) {
        return values[random.nextInt(values.length)];
    }
}