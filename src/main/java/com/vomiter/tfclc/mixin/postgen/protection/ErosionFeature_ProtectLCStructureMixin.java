package com.vomiter.tfclc.mixin.postgen.protection;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.tfclc.worldgen.CityChunkData;
import net.dries007.tfc.world.feature.ErosionFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ErosionFeature.class)
public abstract class ErosionFeature_ProtectLCStructureMixin {

    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/dries007/tfc/world/feature/ErosionFeature;setBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void tfclc$avoidCity(
            ErosionFeature instance,
            WorldGenLevel level,
            ChunkAccess chunk,
            BlockPos pos,
            BlockState state,
            Operation<Void> original
    ) {
        CityChunkData cityData = (CityChunkData) chunk;
        if (cityData.tfclc$isCity()) {
            return;
        }
        original.call(instance, level, chunk, pos, state);
    }

}