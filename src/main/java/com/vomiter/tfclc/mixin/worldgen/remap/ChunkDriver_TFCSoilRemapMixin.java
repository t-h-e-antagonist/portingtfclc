package com.vomiter.tfclc.mixin.worldgen.remap;

import com.vomiter.tfclc.worldgen.remapper.LostCitiesTFCSoilHelper;
import mcjty.lostcities.worldgen.ChunkDriver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ChunkDriver.class, remap = false)
public abstract class ChunkDriver_TFCSoilRemapMixin {

    @Shadow private LevelAccessor region;
    @Shadow @Final private BlockPos.MutableBlockPos current;

    @ModifyVariable(
        method = "block(Lnet/minecraft/world/level/block/state/BlockState;)Lmcjty/lostcities/worldgen/ChunkDriver;",
        at = @At("HEAD"),
        argsOnly = true
    )
    private BlockState tfclc$remapBlock(BlockState state) {
        if (state == null || region == null) {
            return state;
        }
        return LostCitiesTFCSoilHelper.remapVanillaSurface(state, region, current);
    }

    @ModifyVariable(
        method = "add(Lnet/minecraft/world/level/block/state/BlockState;)Lmcjty/lostcities/worldgen/ChunkDriver;",
        at = @At("HEAD"),
        argsOnly = true
    )
    private BlockState tfclc$remapAdd(BlockState state) {
        if (state == null || region == null) {
            return state;
        }
        return LostCitiesTFCSoilHelper.remapVanillaSurface(state, region, current);
    }
}