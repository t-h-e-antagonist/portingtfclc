package com.vomiter.tfclc.mixin.worldgen.remap;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.tfclc.worldgen.remapper.LostCitiesGeneralRemapper;
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
public abstract class ChunkDriver_TFCGeneralRemapMixin {
    @Shadow
    @Final
    private BlockPos.MutableBlockPos current;

    @Shadow
    private LevelAccessor region;

    @Shadow
    private ChunkAccess primer;

    @ModifyVariable(
            method = "block(Lnet/minecraft/world/level/block/state/BlockState;)Lmcjty/lostcities/worldgen/ChunkDriver;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapBlock(BlockState state) {

        return LostCitiesGeneralRemapper.remap(state, current, region);
    }

    @ModifyVariable(
            method = "add(Lnet/minecraft/world/level/block/state/BlockState;)Lmcjty/lostcities/worldgen/ChunkDriver;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapAdd(BlockState state) {
        return LostCitiesGeneralRemapper.remap(state, current, region);
    }

    @ModifyVariable(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapSetBlock(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        return LostCitiesGeneralRemapper.remap(state, pos, region);
    }

    @ModifyVariable(
            method = "setBlockRange(IIIILnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapSetBlockRange(
            BlockState state,
            int x,
            int y,
            int z,
            int y2
    ) {
        BlockPos pos = new BlockPos(
                primer.getPos().getMinBlockX() + x,
                y,
                primer.getPos().getMinBlockZ() + z
        );
        return LostCitiesGeneralRemapper.remap(state, pos, region);
    }

    @ModifyVariable(
            method = "setBlockRange(IIIILnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Predicate;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private BlockState tfclc$remapSetBlockRangeWithPredicate(BlockState state) {
        return LostCitiesGeneralRemapper.remap(state, null, region);
    }
}