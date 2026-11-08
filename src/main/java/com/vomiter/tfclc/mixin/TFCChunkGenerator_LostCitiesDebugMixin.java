package com.vomiter.tfclc.mixin;

import mcjty.lostcities.LostCities;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.setup.Registration;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TFCChunkGenerator.class)
public abstract class TFCChunkGenerator_LostCitiesDebugMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("HEAD"), remap = false)
    private void tfclc$onDecorate(WorldGenLevel level, ChunkAccess chunk, StructureManager sm, CallbackInfo ci) {
        LostCities.getLogger().warn("[TFCLC-TEST] applyBiomeDecoration called!");
    }

    @Inject(
            method = "applyBiomeDecoration",
            at = @At("TAIL"),
            remap = false
    )
    private void tfclc$generateLostCities(
            WorldGenLevel level,
            ChunkAccess chunk,
            StructureManager structureManager,
            CallbackInfo ci
    ) {
        if (!(level instanceof WorldGenRegion region)) {
            return;
        }

        final ChunkPos center = region.getCenter();
        final ChunkPos current = chunk.getPos();
        if (current.x != center.x || current.z != center.z) {
            return;
        }

        // 1. 获取 Lost Cities 维度信息
        final IDimensionInfo dimInfo = Registration.LOSTCITY_FEATURE.get().getDimensionInfo(level);
        if (dimInfo == null) {
            LostCities.getLogger().warn("[TFCLostCities] dimInfo is NULL for chunk {}, {}", current.x, current.z);
            return;
        }

        // 2. 获取该维度的 Profile（配置）
        final LostCityProfile profile = dimInfo.getProfile();
        if (profile == null) {
            return;
        }

        try {
            // 3. 构造 LostCityTerrainFeature 并执行生成
            RandomSource random = region.getRandom();
            LostCityTerrainFeature feature = new LostCityTerrainFeature(dimInfo, profile, random);
            
            // generate 方法签名为：generate(WorldGenRegion, ChunkAccess)
            feature.generate(region, chunk);
            
            LostCities.getLogger().warn("[TFCLostCities] Generated Lost City at chunk {}, {}", current.x, current.z);
        } catch (Exception e) {
            LostCities.getLogger().error("[TFCLostCities] Failed to generate Lost City at chunk {}, {}", current.x, current.z, e);
        }
    }
}