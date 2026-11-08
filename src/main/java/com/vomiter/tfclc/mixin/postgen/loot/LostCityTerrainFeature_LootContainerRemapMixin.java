package com.vomiter.tfclc.mixin.postgen.loot;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.tfclc.worldgen.remapper.LostCitiesGeneralRemapper;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LostCityTerrainFeature.class, remap = false)
public abstract class LostCityTerrainFeature_LootContainerRemapMixin {

    @WrapOperation(
            method = "lambda$handleLoot$9",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    remap = true
            )
    )
    private boolean tfclc$remapLootContainerBlock(WorldGenLevel world, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
        return original.call(world, pos, LostCitiesGeneralRemapper.remap(state, pos, world), flags);
    }
}