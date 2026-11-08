package com.vomiter.tfclc.mixin.worldgen.terrain;

import com.vomiter.tfclc.worldgen.CityChunkData;
import com.vomiter.tfclc.worldgen.CityTerrainSmoothingHelper;
import mcjty.lostcities.setup.Registration;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.LostCityFeature;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Mixin(TFCChunkGenerator.class)
public abstract class TFCChunkGenerator_CitySmoothingMixin {

    @Inject(
            method = "applyCarvers",
            at = @At("RETURN"),
            remap = false
    )
    private void tfclc$smoothAfterCarvers(
            WorldGenRegion level,
            long seed,
            RandomState randomState,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunk,
            GenerationStep.Carving step,
            CallbackInfo ci
    ) {
        if (step != GenerationStep.Carving.AIR) {
            return;
        }

        // 安全地检查 chunk 是否为 CityChunkData 的实例
        if (!(chunk instanceof CityChunkData chunkData)) {
            return; 
        }

        LostCityFeature feature = Registration.LOSTCITY_FEATURE.get();
        IDimensionInfo provider = feature.getDimensionInfo(level);

        if (provider == null) { 
            return; 
        }

        // 获取当前区块坐标
        ChunkPos chunkPos = chunk.getPos();
        
        // ⚠️ 警告：不要在这里调用 BuildingInfo.getBuildingInfo()！
        // 在 applyCarvers 阶段，相邻区块还未生成，调用它会导致游戏崩溃。
        
        // 我们只根据当前区块是否已经被标记为城市来进行简单的平滑处理。
        // 如果 CityChunkData 中保存了城市地面的高度，我们可以直接使用它。
        // 假设你的 CityChunkData 有一个方法可以获取城市高度（例如 tfclc$getCityFloor()）
        int cityFloor = chunkData.tfclc$getCityFloor();
        
        if (cityFloor > 0) { 
            // 如果当前区块有城市地面高度，则进行平滑
            CityTerrainSmoothingHelper.lowerCurrentChunk(chunk, new CityTerrainSmoothingHelper.TargetHeightHolder(OptionalInt.of(cityFloor), 0, 0));
            CityTerrainSmoothingHelper.primeAllHeightmaps(chunk);
        }
    }
}