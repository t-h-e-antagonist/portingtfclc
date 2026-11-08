package com.vomiter.tfclc.util.stone;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Map;

public final class LostCitiesProtectionTracker {

    private static final Map<Long, LongSet> PROTECTED = new Long2ObjectOpenHashMap<>();
    public static final String CITY_FLOOR_KEY = "tfclc:city_floor";


    private LostCitiesProtectionTracker() {
    }

    public static void mark(BlockPos pos) {
        final long chunkKey = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        PROTECTED.computeIfAbsent(chunkKey, k -> new LongOpenHashSet()).add(pos.asLong());
    }

    public static void mark(LevelAccessor level, BlockPos pos) {
        mark(pos);
    }

    public static void mark(ChunkAccess chunk, BlockPos pos) {
        mark(pos);
    }

    public static void markRange(LevelAccessor level, int x, int y1, int z, int y2) {
        if (y2 < y1) {
            return;
        }
        for (int y = y1; y <= y2; y++) {
            mark(BlockPos.containing(x, y, z));
        }
    }

    public static void markRange(ChunkAccess chunk, int x, int y1, int z, int y2) {
        if (y2 < y1) {
            return;
        }
        for (int y = y1; y <= y2; y++) {
            mark(BlockPos.containing(x, y, z));
        }
    }

    public static boolean isProtected(BlockPos pos) {
        final long chunkKey = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        final LongSet set = PROTECTED.get(chunkKey);
        return set != null && set.contains(pos.asLong());
    }

    public static boolean isProtected(ChunkAccess chunk, BlockPos pos) {
        return isProtected(pos);
    }

    public static void clearChunk(ChunkPos chunkPos) {
        PROTECTED.remove(chunkPos.toLong());
    }

    public static void clearAll() {
        PROTECTED.clear();
    }
}