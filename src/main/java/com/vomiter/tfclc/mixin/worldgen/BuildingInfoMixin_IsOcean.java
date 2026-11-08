package com.vomiter.tfclc.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.tfclc.Helpers;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BuildingInfo.class)
public class BuildingInfoMixin_IsOcean {
    @WrapOperation(method = "isOcean", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean tfclc$isTFCOcean(Holder<Biome> instance, TagKey<Biome> tTagKey, Operation<Boolean> original){
        var TFCIsOcean = TagKey.create(
                Registries.BIOME,
                Helpers.id("tfc", "is_ocean")
        );
        return original.call(instance, TFCIsOcean);
    }
}