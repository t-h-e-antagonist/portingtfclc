package com.vomiter.tfclc.mixin.worldgen.remap;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.vomiter.tfclc.worldgen.remapper.wood.LostCitiesWoodRemapper;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BuildingInfo.class, remap = false)
public abstract class BuildingInfo_TFCDoorMixin {

    @ModifyExpressionValue(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lmcjty/lostcities/worldgen/lost/BuildingInfo;getRandomDoor(Ljava/util/Random;)Lnet/minecraft/world/level/block/Block;"
        )
    )
    private Block tfclc$replaceRandomDoor(Block original) {
        final Block remapped = LostCitiesWoodRemapper.mapVanillaWood(original);
        return remapped != null ? remapped : original;
    }
}