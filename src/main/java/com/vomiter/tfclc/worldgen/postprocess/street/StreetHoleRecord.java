package com.vomiter.tfclc.worldgen.postprocess.street;

import net.minecraft.core.BlockPos;

public record StreetHoleRecord(
        BlockPos pos,
        StreetHoleReason reason,
        Object belowState,
        Object placedState
) {}

