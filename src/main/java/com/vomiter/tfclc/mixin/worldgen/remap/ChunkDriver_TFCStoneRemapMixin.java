package com.vomiter.tfclc.mixin.worldgen.remap;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.tfclc.worldgen.remapper.LostCitiesStoneRemapper;
import mcjty.lostcities.worldgen.ChunkDriver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ChunkDriver.class, remap = false)
public abstract class ChunkDriver_TFCStoneRemapMixin {

    @Shadow private LevelAccessor region;
    @Shadow private ChunkAccess primer;
    @Shadow @Final private BlockPos.MutableBlockPos current;

    @ModifyVariable(
            method = "block(Lnet/minecraft/world/level/block/state/BlockState;)Lmcjty/lostcities/worldgen/ChunkDriver;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapBlock(BlockState state) {
        return LostCitiesStoneRemapper.remapAndMark(region, current.immutable(), state);
    }

    @ModifyVariable(
            method = "add(Lnet/minecraft/world/level/block/state/BlockState;)Lmcjty/lostcities/worldgen/ChunkDriver;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapAdd(BlockState state) {
        return LostCitiesStoneRemapper.remapAndMark(region, current.immutable(), state);
    }

    @ModifyVariable(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapSetBlock(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        return LostCitiesStoneRemapper.remapAndMark(region, pos, state);
    }

    @ModifyVariable(
            method = "setBlockRange(IIIILnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapSetBlockRange(
            BlockState state,
            @Local(argsOnly = true, ordinal = 0) int x,
            @Local(argsOnly = true, ordinal = 1) int y,
            @Local(argsOnly = true, ordinal = 2) int z,
            @Local(argsOnly = true, ordinal = 3) int y2) {
        final int worldX = x + (primer.getPos().x << 4);
        final int worldZ = z + (primer.getPos().z << 4);
        return LostCitiesStoneRemapper.remapAndMarkRange(region, worldX, y, worldZ, y2 - 1, state);
    }

    @ModifyVariable(
            method = "setBlockRange(IIIILnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Predicate;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapSetBlockRangeWithPredicate(
            BlockState state,
            @Local(argsOnly = true, ordinal = 0) int x,
            @Local(argsOnly = true, ordinal = 1) int y,
            @Local(argsOnly = true, ordinal = 2) int z,
            @Local(argsOnly = true, ordinal = 3) int y2) {
        final int worldX = x + (primer.getPos().x << 4);
        final int worldZ = z + (primer.getPos().z << 4);
        return LostCitiesStoneRemapper.remapAndMarkRange(region, worldX, y, worldZ, y2 - 1, state);
    }
}