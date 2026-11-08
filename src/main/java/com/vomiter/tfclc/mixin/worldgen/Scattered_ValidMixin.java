package com.vomiter.tfclc.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.gen.Scattered;
import mcjty.lostcities.worldgen.lost.regassets.data.BiomeMatcher;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Scattered.class, remap = false)
public class Scattered_ValidMixin {

    @WrapOperation(
            method = "isValidScatterBiome",
            at = @At(
                    value = "INVOKE",
                    target = "Lmcjty/lostcities/worldgen/lost/regassets/data/BiomeMatcher;test(Lnet/minecraft/core/Holder;)Z"
            )
    )
    private static boolean tfclc$testTFCBiome(
            BiomeMatcher instance,
            Holder<Biome> biome,
            Operation<Boolean> original,
            @Local(argsOnly = true) LostCityTerrainFeature feature
    ) {
        final RegistryAccess access = feature.provider.getWorld().registryAccess();
        final Registry<Biome> biomeRegistry = access.registryOrThrow(Registries.BIOME);

        final Holder<Biome> remapped = tfclc$remapBiomeHolder(biome, biomeRegistry);
        return original.call(instance, remapped);
    }

    @Unique
    private static Holder<Biome> tfclc$remapBiomeHolder(Holder<Biome> biome, Registry<Biome> biomeRegistry) {
        if (
                biome.is(TFCBiomes.DEEP_OCEAN.key()) || biome.is(TFCBiomes.DEEP_OCEAN_TRENCH.key())) {
            return biomeRegistry.getHolderOrThrow(Biomes.DEEP_OCEAN);
        }
        if (
                biome.is(TFCBiomes.OCEAN.key())
                || biome.is(TFCBiomes.OCEAN_REEF.key())
                || biome.is(TFCBiomes.SHORE.key())
                || biome.is(TFCBiomes.SALT_MARSH.key())
        ) {
            return biomeRegistry.getHolderOrThrow(Biomes.OCEAN);
        }
        if (biome.is(TFCBiomes.RIVER.key()) || biome.is(TFCBiomes.LAKE.key())) {
            return biomeRegistry.getHolderOrThrow(Biomes.RIVER);
        }
        return biome;
    }
}