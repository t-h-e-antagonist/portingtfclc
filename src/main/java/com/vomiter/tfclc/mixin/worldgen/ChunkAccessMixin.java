package com.vomiter.tfclc.mixin.worldgen;

import com.vomiter.tfclc.worldgen.CityChunkData;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkAccess.class)
public abstract class ChunkAccessMixin implements CityChunkData {
    @Unique
    private boolean tfclc$isCity;

    @Unique
    private boolean tfclc$terrainSmoothed;

    @Unique
    private int tfclc$cityFloor;

    @Unique
    private volatile int tfclc$distanceToNearestCities = -1;

    @Override
    public void tfclc$setCityFloor(int height) {
        tfclc$isCity = true;
        tfclc$cityFloor = height;
    }

    @Override
    public boolean tfclc$isCity() {
        return tfclc$isCity;
    }

    @Override
    public boolean tfclc$terrainSmoothed(){
        return tfclc$terrainSmoothed;
    }

    @Override
    public void tfclc$setTerrainSmoothed(boolean b) {
        tfclc$terrainSmoothed = b;
    }

    @Override
    public int tfclc$getCityFloor() {
        return tfclc$cityFloor;
    }

    public int tfclc$getDistanceToNearestCities() {
        return tfclc$distanceToNearestCities;
    }

    public void tfclc$setDistanceToNearestCities(int tfclc$distanceToNearestCity) {
        this.tfclc$distanceToNearestCities = tfclc$distanceToNearestCity;
    }
}