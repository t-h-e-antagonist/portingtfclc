package com.vomiter.tfclc.mixin.postgen.decoration;

import com.vomiter.tfclc.worldgen.postprocess.decorator.LostCitiesTFCToolRackDecorator;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.worldgen.ChunkFixer;
import mcjty.lostcities.worldgen.IDimensionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkFixer.class, remap = false)
public abstract class ChunkFixer_TFCToolRackDecoratorMixin {

    @Inject(
        method = "fix",
        at = @At("TAIL")
    )
    private static void tfclostcities$decorateToolRacks(IDimensionInfo info, ChunkCoord coord, CallbackInfo ci) {
        LostCitiesTFCToolRackDecorator.decorateChunk(info, coord);
    }
}