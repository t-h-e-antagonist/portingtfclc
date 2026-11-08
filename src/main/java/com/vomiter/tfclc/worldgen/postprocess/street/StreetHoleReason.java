package com.vomiter.tfclc.worldgen.postprocess.street;

public enum StreetHoleReason {
    AIR_WITH_STREET_NEIGHBORS,
    AIR_ABOVE_TFC_SURFACEABLE_BLOCK,
    AIR_ABOVE_NON_SURFACEABLE_BLOCK,
    AIR_NEAR_FLUID_OR_SLOPE,
    AIR_IN_INTERIOR_LIKE_SPACE
}