package com.vomiter.tfclc.mixin.worldgen.water;

import com.llamalad7.mixinextras.sugar.Local;
import com.vomiter.tfclc.TFCLostCities;
import mcjty.lostcities.worldgen.ChunkDriver;
import mcjty.lostcities.worldgen.ChunkHeightmap;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LostCityTerrainFeature.class, remap = false)
public abstract class LostCityTerrainFeature_SaltWaterSupportMixin {
    @Shadow private BlockState base;

    @Shadow
    public abstract ChunkDriver getDriver();

    @Inject(
        method = "generateBorderSupport",
        at = @At("TAIL"),
        cancellable = true
    )
    private void tfclc$generateBorderSupportTreatSaltWaterAsLiquid(
        BuildingInfo info,
        BlockState wall,
        int x,
        int z,
        int offset,
        ChunkHeightmap heightmap,
        CallbackInfo ci
    ) {
        var driver = getDriver();
        int y = driver.getY();
        TFCLostCities.LOGGER.info("[TFCLC][Support] {}", driver.getBlock());
        int height = heightmap.getHeight();
        if (height > 1) {
            while (y > 1 && driver.getBlock().canBeReplaced()) {
                driver.block(base).decY();
                y--;
            }
        }
        ci.cancel();
    }
}