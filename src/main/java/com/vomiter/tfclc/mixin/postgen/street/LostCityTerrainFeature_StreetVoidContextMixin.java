package com.vomiter.tfclc.mixin.postgen.street;

import com.vomiter.tfclc.worldgen.postprocess.street.TFCLCStreetVoidContext;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.Transform;
import mcjty.lostcities.worldgen.lost.cityassets.IBuildingPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LostCityTerrainFeature.class, remap = false)
public abstract class LostCityTerrainFeature_StreetVoidContextMixin {

    @Inject(
            method = "generatePart",
            at = @At("HEAD")
    )
    private void tfclc$enterStreetVoidContext(
            BuildingInfo info, IBuildingPart part, Transform transform, int ox, int oy, int oz, LostCityTerrainFeature.HardAirSetting airWaterLevel, CallbackInfoReturnable<Integer> cir
    ) {
        if (airWaterLevel == LostCityTerrainFeature.HardAirSetting.VOID) {
            TFCLCStreetVoidContext.push();
        }
    }

    @Inject(
            method = "generatePart",
            at = @At("RETURN")
    )
    private void tfclc$exitStreetVoidContext(
            BuildingInfo info, IBuildingPart part, Transform transform, int ox, int oy, int oz, LostCityTerrainFeature.HardAirSetting airWaterLevel, CallbackInfoReturnable<Integer> cir
    ) {
        if (airWaterLevel == LostCityTerrainFeature.HardAirSetting.VOID) {
            TFCLCStreetVoidContext.pop();
        }
    }
}