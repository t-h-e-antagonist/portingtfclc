package com.vomiter.tfclc.worldgen.postprocess;

import com.vomiter.tfclc.worldgen.postprocess.street.PendingPreservedSurfaceKind;
import com.vomiter.tfclc.worldgen.postprocess.street.TFCLCStreetVoidContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LostCitiesPostProcessTracker {

    private static final ThreadLocal<Map<ChunkPos, Map<BlockPos, PendingBlockEntityKind>>> PENDING =
            ThreadLocal.withInitial(LinkedHashMap::new);

    private static final ThreadLocal<Map<ChunkPos, Map<BlockPos, PendingPreservedSurfaceKind>>> PENDING_PRESERVED_SURFACES =
            ThreadLocal.withInitial(LinkedHashMap::new);

    private LostCitiesPostProcessTracker() {
    }

    public static void add(BlockPos pos, PendingBlockEntityKind kind) {
        if (pos == null || kind == null) {
            return;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Map<BlockPos, PendingBlockEntityKind>> root = PENDING.get();
        Map<BlockPos, PendingBlockEntityKind> byChunk =
                root.computeIfAbsent(chunkPos, cp -> new LinkedHashMap<>());

        byChunk.put(pos.immutable(), kind);
    }

    public static void addPreservedSurface(BlockPos pos, PendingPreservedSurfaceKind kind) {
        if (pos == null || kind == null) {
            return;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Map<BlockPos, PendingPreservedSurfaceKind>> root = PENDING_PRESERVED_SURFACES.get();
        Map<BlockPos, PendingPreservedSurfaceKind> byChunk =
                root.computeIfAbsent(chunkPos, cp -> new LinkedHashMap<>());

        byChunk.put(pos.immutable(), kind);
    }

    public static Map<BlockPos, PendingBlockEntityKind> consumeChunk(ChunkPos chunkPos) {
        Map<ChunkPos, Map<BlockPos, PendingBlockEntityKind>> root = PENDING.get();
        Map<BlockPos, PendingBlockEntityKind> removed = root.remove(chunkPos);
        return removed == null ? Map.of() : removed;
    }

    public static Map<BlockPos, PendingPreservedSurfaceKind> consumePreservedSurfaces(ChunkPos chunkPos) {
        Map<ChunkPos, Map<BlockPos, PendingPreservedSurfaceKind>> root = PENDING_PRESERVED_SURFACES.get();
        Map<BlockPos, PendingPreservedSurfaceKind> removed = root.remove(chunkPos);
        return removed == null ? Map.of() : removed;
    }

    public static void clear() {
        PENDING.get().clear();
        PENDING_PRESERVED_SURFACES.get().clear();
        TFCLCStreetVoidContext.clear();
    }
}