package com.vomiter.tfclc.mixin.worldgen.remap;

import com.vomiter.tfclc.worldgen.remapper.LostCitiesPlantRemapper;
import com.vomiter.tfclc.worldgen.remapper.wood.LostCitiesWoodRemapper;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LostCityTerrainFeature.class, remap = false)
public abstract class LostCityTerrainFeature_TFCParkFoliageMixin {

    @ModifyVariable(
            method = "handleTodo",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private BlockState tfclc$replaceParkSaplingsAndFlowers(BlockState original) {
        if (original == null) {
            return null;
        }

        final BlockState remapped = tfclostcities$remapBlock(original);
        if (remapped == null) {
            return original;
        }

        BlockState replaced = remapped;

        for (Property<?> property : original.getProperties()) {
            if (replaced.hasProperty(property)) {
                replaced = tfclc$copyProperty(original, replaced, property);
            }
        }

        return replaced;
    }

    @Unique
    private static <T extends Comparable<T>> BlockState tfclc$copyProperty(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }

    @Unique
    private BlockState tfclostcities$remapBlock(BlockState original) {
        final BlockState wood = LostCitiesWoodRemapper.remapVanillaWood(original);
        if (wood != null) {
            return wood;
        }

        final BlockState plant = LostCitiesPlantRemapper.remapVanillaPlant(original);
        if (plant != null) {
            return plant;
        }

        return original;
    }

}