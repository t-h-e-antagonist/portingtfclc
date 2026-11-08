package com.vomiter.tfclc.worldgen.postprocess;

import net.minecraft.core.BlockPos;

public record PendingBlockEntityPlacement(BlockPos pos, PendingBlockEntityKind kind) {
}