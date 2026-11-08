package com.vomiter.tfclc.worldgen;

public interface CityChunkData {
    void tfclc$setCityFloor(int height);
    boolean tfclc$isCity();
    void tfclc$setTerrainSmoothed(boolean b);
    boolean tfclc$terrainSmoothed();
    int tfclc$getCityFloor();
    int tfclc$getDistanceToNearestCities();
    void tfclc$setDistanceToNearestCities(int i);
}