package com.vomiter.tfclc.util;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;

public record GenerationMarker(int regionIdentity, int chunkX, int chunkZ) {
        public GenerationMarker(WorldGenRegion region, ChunkPos chunkPos) {
            this(System.identityHashCode(region), chunkPos.x, chunkPos.z);
        }

        public boolean matches(WorldGenRegion region, ChunkPos chunkPos) {
            return this.regionIdentity == System.identityHashCode(region)
                && this.chunkX == chunkPos.x
                && this.chunkZ == chunkPos.z;
        }
    }
