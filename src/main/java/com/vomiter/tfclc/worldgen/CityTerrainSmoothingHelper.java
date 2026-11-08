package com.vomiter.tfclc.worldgen;

import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.ChunkHeightFiller;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public final class CityTerrainSmoothingHelper {

    public static final int HEIGHT_PER_CHUNK = 3;

    public static final int MAX_SEARCH_RADIUS = 24;

    private static final int SURFACE_DEPTH = 6;

    // TFCBlocks.SOIL.get(...).get(...) 返回 TFCBlocks.Id<Block>，需要再 .get() 取得 Block
    private static final BlockState GRASS = TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(SoilBlockType.Variant.ENTISOL).holder().get().defaultBlockState();

    private static final EnumSet<Heightmap.Types> ALL_HEIGHTMAPS =
            EnumSet.of(
                    Heightmap.Types.WORLD_SURFACE_WG,
                    Heightmap.Types.OCEAN_FLOOR_WG,
                    Heightmap.Types.WORLD_SURFACE,
                    Heightmap.Types.OCEAN_FLOOR,
                    Heightmap.Types.MOTION_BLOCKING,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
            );

    public record TargetHeightHolder(OptionalInt targetHeight, int xSign, int zSign){
        public boolean isEmpty(){
            return targetHeight().isEmpty();
        }

        public static TargetHeightHolder empty(){
            return new TargetHeightHolder(OptionalInt.empty(), 0, 0);
        }
    }

    /**
     * 計算目前 normal chunk 的地形高度上限。
     * 如果目前是城市、附近沒有城市、自然地形已經能正常銜接，
     * 或中途已經接回自然地形，便回傳 empty。
     */

    public static TargetHeightHolder findTargetHeight(
            WorldGenRegion level, IDimensionInfo provider,
            TFCChunkGenerator generator,
            ChunkPos currentPos
    ) {
        BuildingInfo currentInfo = getBuildingInfo(provider, currentPos);

        if (isGeneratedAsCity(currentInfo)) {
            return TargetHeightHolder.empty();
        }

        Map<Long, Integer> naturalHeightCache = new HashMap<>();

        int currentNaturalHeight = getNaturalHeight(
                generator,
                currentPos,
                naturalHeightCache
        );

        List<CitySeed> nearestCities = findNearestCities(level, provider, currentPos, MAX_SEARCH_RADIUS);

        if (nearestCities.isEmpty()) { //無城市
            return TargetHeightHolder.empty();
        }

        int bestTarget = Integer.MAX_VALUE; //從最高往下追

        int xSign = 0;
        int zSign = 0;

        for (CitySeed city : nearestCities) {
            //每個city進行一次計算
            int distance = chebyshevDistance(
                    city.pos(),
                    currentPos
            );

            int allowedHeight = city.cityGround() + HEIGHT_PER_CHUNK * (distance - 1);

             //目前自然高度已經低於坡度上限 = 目前 chunk 本身就是自然銜接點。
            if (currentNaturalHeight <= allowedHeight) {
                continue;
            }

            /*
             * 如果從城市到目前 chunk 的途中已經接回自然
             * 就不應再次讓同一座城市影響目前 chunk。
             */
            if (hasNaturalJoinBefore(
                    generator,
                    city,
                    currentPos,
                    naturalHeightCache
            )) {
                continue;
            }

            if(allowedHeight < bestTarget){
                xSign = Integer.compare(city.pos().x - currentPos.x, 0);
                zSign = Integer.compare(city.pos().z - currentPos.z, 0);
                bestTarget = Math.min(bestTarget, allowedHeight);
            }
        }

        if (bestTarget == Integer.MAX_VALUE) {
            return TargetHeightHolder.empty();
        }


        return new TargetHeightHolder(OptionalInt.of(bestTarget), xSign, zSign);
    }

    /**
     * 找出距離目前 chunk 最近的 city chunk
     * 會把有相同距離的chunk都包在list內回傳
     */
    private static List<CitySeed> findNearestCities(
            WorldGenRegion level,
            IDimensionInfo provider,
            ChunkPos origin,
            int maxRadius
    ) {
        CitySearchCache cache = getNearestCitySearchCache(level, origin, maxRadius);
        int minSearchRadius;
        int maxSearchRadius;
        if (cache != null){
            minSearchRadius = Math.max(1, cache.distanceToNearestCities - cache.distanceBetweenTwoChunks);
            maxSearchRadius = Math.min(maxRadius, cache.distanceToNearestCities + cache.distanceBetweenTwoChunks);
        } else {
            minSearchRadius = 1;
            maxSearchRadius = maxRadius;
        }

        for (int radius = minSearchRadius; radius <= maxSearchRadius; radius++) {
            List<CitySeed> found = new ArrayList<>();
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }

                    ChunkPos candidatePos = new ChunkPos(origin.x + dx, origin.z + dz);
                    BuildingInfo info = getBuildingInfo(provider, candidatePos);
                    if (isGeneratedAsCity(info)) {
                        found.add(new CitySeed(candidatePos, info.getCityGroundLevel()));
                    }
                }
            }

            if (!found.isEmpty()) {
                ChunkAccess originChunk = level.getChunk(origin.x, origin.z);
                if(originChunk instanceof CityChunkData chunkData){
                    chunkData.tfclc$setDistanceToNearestCities(radius);
                }

                return found;
            }
        }
        ChunkAccess originChunk = level.getChunk(origin.x, origin.z);
        if(originChunk instanceof CityChunkData chunkData){
            chunkData.tfclc$setDistanceToNearestCities(maxRadius + 1);
        }
        return List.of();
    }


    /**
     * 根據附近能取得的 Chunk Access 決定之後搜尋的 radius 上下界
     */
    @Nullable
    private static CitySearchCache getNearestCitySearchCache(WorldGenRegion level, ChunkPos origin, int maxRadius){
        int minCacheDistance = Integer.MAX_VALUE;
        boolean found = false;
        for (int radius = 1; radius <= maxRadius / 2; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {

                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) {
                        continue;
                    }

                    ChunkPos candidatePos = new ChunkPos(origin.x + dx, origin.z + dz);
                    if(level.hasChunk(candidatePos.x, candidatePos.z)){
                        if (level.getChunk(candidatePos.x, candidatePos.z) instanceof CityChunkData chunkData){
                            if (chunkData.tfclc$getDistanceToNearestCities() == -1){
                                continue;
                            }
                            minCacheDistance = Math.min(minCacheDistance, chunkData.tfclc$getDistanceToNearestCities());
                            found = true;
                        }
                    }
                }
            }

            if (found) {
                return new CitySearchCache(radius, minCacheDistance);
            }
        }
        return null;
    }



    record CitySearchCache(int distanceBetweenTwoChunks, int distanceToNearestCities){}

    private static BuildingInfo getBuildingInfo(
            IDimensionInfo provider,
            ChunkPos pos
    ) {
        ChunkCoord coord = new ChunkCoord(provider.getType(), pos.x, pos.z);
        return BuildingInfo.getBuildingInfo(coord, provider);
    }

    /**
     * same as the condition in LC generate()
     */
    public static boolean isGeneratedAsCity(
            BuildingInfo info
    ) {
        return info.isCity || (info.outsideChunk && info.hasBuilding);
    }

    /**
     * 檢查城市與目前 chunk 之間，是否已經提前接回自然地形。
     * Chebyshev 距離的離散直線
     */
    private static boolean hasNaturalJoinBefore(
            TFCChunkGenerator generator,
            CitySeed city,
            ChunkPos destination,
            Map<Long, Integer> naturalHeightCache
    ) {
        int deltaX = destination.x - city.pos().x;
        int deltaZ = destination.z - city.pos().z;
        int steps = Math.max(Math.abs(deltaX), Math.abs(deltaZ));

        //相鄰不會有中間點
        if (steps <= 1) {
            return false;
        }

        ChunkPos previous = city.pos();

        for (int step = 1; step < steps; step++) {

            double progress = step / (double) steps;
            int chunkX = city.pos().x + (int) Math.round(deltaX * progress);
            int chunkZ = city.pos().z + (int) Math.round(deltaZ * progress);
            ChunkPos samplePos = new ChunkPos(chunkX, chunkZ);
            /*
            * 例子    :
            * 城市 chunk = (10, 20)
            * 目標 chunk = (14, 22)
            * 如果允許斜向移動，從 (10,20) 到 (14,22) 需要四步
            * => (10, 20), (11, 21), (12, 21), (13, 21), (14, 22)
            * https://en.wikipedia.org/wiki/Chebyshev_distance
             */

            // 避免離散化時重複取到同一個 chunk
            if (samplePos.equals(previous)) {
                continue;
            }

            previous = samplePos;

            int distance = chebyshevDistance(city.pos(), samplePos);

            int allowedHeight = city.cityGround() + HEIGHT_PER_CHUNK * (distance - 1);

            int naturalHeight = getNaturalHeight(generator, samplePos, naturalHeightCache);

            if (naturalHeight <= allowedHeight) {
                return true;
            }
        }

        return false;
    }

    /**
     * 取得 TFC 自然地形代表高度。使用中心和四角共五個取樣點，取最大值
     */
    private static int getNaturalHeight(TFCChunkGenerator generator, ChunkPos chunkPos, Map<Long, Integer> cache) {
        long key = chunkPos.toLong();

        Integer cached = cache.get(key);
        if (cached != null) { return cached; }

        ChunkHeightFiller filler = generator.createHeightFillerForChunk(chunkPos);

        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        int center = sample(filler, minX + 8, minZ + 8);
        int northWest = sample(filler, minX + 2, minZ + 2);
        int northEast = sample(filler, minX + 14, minZ + 2);
        int southWest = sample(filler, minX + 2, minZ + 14);
        int southEast = sample(filler, minX + 14, minZ + 14);
        int height = Math.max(
                center,
                Math.max(
                        Math.max(northWest, northEast),
                        Math.max(southWest, southEast)
                )
        );

        cache.put(key, height);
        return height;
    }

    private static int sample(ChunkHeightFiller filler, int blockX, int blockZ) {
        return (int) Math.floor(filler.sampleHeight(blockX, blockZ));
    }

    /**
     * 只削低目前 chunk 中高於 targetHeight 的地表。
     * 每欄保留最上方六層，搬到新的高度，避免把所有地表都直接
     * 變成裸岩。
     */
    public static boolean lowerCurrentChunk(ChunkAccess chunk, TargetHeightHolder targetHeightHolder) {
        int minBuildHeight = chunk.getMinBuildHeight();
        int maxBuildHeight = chunk.getMaxBuildHeight();
        if(targetHeightHolder.targetHeight.isEmpty()) return false;

        int targetHeight = Math.max(
                minBuildHeight + SURFACE_DEPTH,
                Math.min(targetHeightHolder.targetHeight.getAsInt(), maxBuildHeight - 1)
        );

        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean changed = false;

        for (int localX = 0; localX < 16; localX++) {
            for (int localZ = 0; localZ < 16; localZ++) {

                int surfaceY = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, localX, localZ);

                int x = minX + localX;
                int z = minZ + localZ;
                int localXsign = Integer.compare(localX, 8);
                int localZsign = Integer.compare(localZ, 8);
                int localTargetHeight = targetHeight - (localXsign * targetHeightHolder.xSign * localX + localZsign * targetHeightHolder.zSign * localZsign) * HEIGHT_PER_CHUNK / 16;

                if (surfaceY <= localTargetHeight) {
                    continue;
                }

                int availableDepth = Math.min(SURFACE_DEPTH, surfaceY - minBuildHeight + 1);

                BlockState[] surface = new BlockState[availableDepth];

                for (int depth = 0; depth < availableDepth; depth++) {
                    cursor.set(x, surfaceY - depth, z);
                    surface[depth] = chunk.getBlockState(cursor);
                }

                for (int y = localTargetHeight; y <= surfaceY; y++) {
                    cursor.set(x, y, z);
                    chunk.setBlockState(cursor, Blocks.AIR.defaultBlockState(), false);
                }

                for (int depth = 0; depth < availableDepth; depth++) {

                    int y = localTargetHeight - depth;
                    if (y < minBuildHeight) {
                        break;
                    }

                    cursor.set(x, y, z);
                    if (depth == 0 && !surface[depth].is(BlockTags.DIRT)){
                        chunk.setBlockState(cursor, GRASS, false);
                    } else {
                        chunk.setBlockState(cursor, surface[depth], false);
                    }

                }

                changed = true;
            }
        }

        return changed;
    }

    public static int chebyshevDistance(ChunkPos first, ChunkPos second) {
        return Math.max(
                Math.abs(first.x - second.x),
                Math.abs(first.z - second.z)
        );
    }

    private record CitySeed(ChunkPos pos, int cityGround) {
    }

    public static void primeAllHeightmaps(
            ChunkAccess chunk
    ) {
        Heightmap.primeHeightmaps(
                chunk,
                ALL_HEIGHTMAPS
        );
    }
}