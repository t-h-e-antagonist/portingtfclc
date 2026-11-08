package com.vomiter.tfclc.mixin.worldgen.water;

import mcjty.lostcities.worldgen.ChunkDriver;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LostCityTerrainFeature.class, remap = false)
public abstract class LostCityTerrainFeature_NoFluidSmoothingMixin {

    @Shadow @Final
    public IDimensionInfo provider;

    @Shadow
    public abstract ChunkDriver getDriver();

    @Unique
    private boolean tfclc$hasFluidInRange(int x, int z, int y1, int y2) {
        int min = Math.max(provider.getWorld().getMinBuildHeight(), Math.min(y1, y2));
        int max = Math.min(provider.getWorld().getMaxBuildHeight() - 1, Math.max(y1, y2));
        for (int y = min; y <= max; y++) {
            BlockState state = getDriver().getBlock(x, y, z);
            FluidState fluid = state.getFluidState();
            if (!fluid.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Inject(
        method = "moveUp",
        at = @At("HEAD"),
        cancellable = true
    )
    private void tfclc$skipMoveUpIfFluid(int x, int z, int height, boolean dowater, CallbackInfoReturnable<Integer> cir) {
        int minBuild = provider.getWorld().getMinBuildHeight();

        // 只要從地表目標高度往下的這段柱體中有 fluid，就整柱不做 upward smoothing
        if (tfclc$hasFluidInRange(x, z, minBuild, height)) {
            cir.setReturnValue((int) Short.MIN_VALUE);
        }
    }

    @Inject(
        method = "moveDown",
        at = @At("HEAD"),
        cancellable = true
    )
    private void tfclc$skipMoveDownIfFluid(int x, int z, int height, int maxBuildLimit, CallbackInfoReturnable<Integer> cir) {
        int maxY = Math.min(provider.getWorld().getMaxBuildHeight() - 1, maxBuildLimit - 1);

        // 只要此次 downward smoothing 會處理的區間內有 fluid，就整柱跳過
        if (tfclc$hasFluidInRange(x, z, height, maxY)) {
            cir.setReturnValue((int) Short.MIN_VALUE);
        }
    }
}