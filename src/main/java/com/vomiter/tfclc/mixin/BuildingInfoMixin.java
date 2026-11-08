package com.vomiter.tfclc.mixin;

import com.vomiter.tfclc.TFCLCConfig;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(value = BuildingInfo.class, remap = false)
public abstract class BuildingInfoMixin {

    /*
     * The spawn position can change while the initial spawn chunks are being
     * generated. Freeze the first observed position for each ServerLevel so
     * city generation does not depend on chunk generation order.
     *
     * WeakHashMap allows unloaded ServerLevel instances to be garbage-collected.
     */
    @Unique
    private static final Map<ServerLevel, BlockPos> tfclostcities$spawnAnchors =
            Collections.synchronizedMap(new WeakHashMap<>());

    @Inject(
            method = "isCityRaw",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void tfclostcities$excludeCitiesNearSpawn(
            ChunkCoord coord,
            IDimensionInfo provider,
            LostCityProfile profile,
            CallbackInfoReturnable<Boolean> cir
    ) {
        int minimumDistance =
                TFCLCConfig.MIN_CITY_DISTANCE_FROM_SPAWN.get();

        if (minimumDistance <= 0) {
            return;
        }

        WorldGenLevel world = provider.getWorld();

        if (world == null) {
            return;
        }

        ServerLevel level = world.getLevel();
        BlockPos spawnAnchor =
                tfclostcities$getOrCreateSpawnAnchor(level);

        if (tfclostcities$isChunkInsideExclusionRadius(
                coord,
                spawnAnchor,
                minimumDistance
        )) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Returns an immutable horizontal spawn anchor for this ServerLevel.
     *
     * TFC sets a preliminary spawn position before generating chunks around it.
     * this intentionally retain the first observed position instead of following
     * later changes to the exact shared spawn position.
     */
    @Unique
    private static BlockPos tfclostcities$getOrCreateSpawnAnchor(
            ServerLevel level
    ) {
        synchronized (tfclostcities$spawnAnchors) {
            return tfclostcities$spawnAnchors.computeIfAbsent(
                    level,
                    key -> {
                        BlockPos spawn = key.getSharedSpawnPos();

                        return new BlockPos(
                                spawn.getX(),
                                0,
                                spawn.getZ()
                        );
                    }
            );
        }
    }

    /**
     * Checks the shortest horizontal distance between the spawn point and the
     * complete 16x16 block area occupied by the chunk.
     *
     * This differs from checking the chunk center: a chunk is excluded whenever
     * any part of it enters the configured radius.
     */
    @Unique
    private static boolean tfclostcities$isChunkInsideExclusionRadius(
            ChunkCoord coord,
            BlockPos spawn,
            int minimumDistance
    ) {
        long chunkMinX = (long) coord.chunkX() << 4;
        long chunkMinZ = (long) coord.chunkZ() << 4;
        long chunkMaxX = chunkMinX + 15L;
        long chunkMaxZ = chunkMinZ + 15L;

        long dx = tfclostcities$distanceToRange(
                spawn.getX(),
                chunkMinX,
                chunkMaxX
        );

        long dz = tfclostcities$distanceToRange(
                spawn.getZ(),
                chunkMinZ,
                chunkMaxZ
        );

        long distanceSquared = dx * dx + dz * dz;
        long minimumDistanceSquared =
                (long) minimumDistance * minimumDistance;

        return distanceSquared < minimumDistanceSquared;
    }

    /**
     * Returns the distance between one coordinate and a closed interval.
     * Returns zero when the coordinate is inside the interval.
     */
    @Unique
    private static long tfclostcities$distanceToRange(
            long position,
            long minimum,
            long maximum
    ) {
        if (position < minimum) {
            return minimum - position;
        }

        if (position > maximum) {
            return position - maximum;
        }

        return 0L;
    }
}