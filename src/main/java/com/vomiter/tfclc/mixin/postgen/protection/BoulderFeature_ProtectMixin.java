package com.vomiter.tfclc.mixin.postgen.protection;

import com.vomiter.tfclc.worldgen.CityChunkData;
import net.dries007.tfc.world.feature.BouldersFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BouldersFeature.class)
public class BoulderFeature_ProtectMixin {
    @Inject(
            method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false // 添加这一行
    )
    private void tfclc$avoidCity(
            FeaturePlaceContext<?> context,
            CallbackInfoReturnable<Boolean> cir
    ) {
        BlockPos origin = context.origin();
        ChunkPos chunkPos = new ChunkPos(origin);

        ChunkAccess chunk = context.level().getChunk(
                chunkPos.x,
                chunkPos.z
        );

        CityChunkData cityData = (CityChunkData) chunk;

        if (cityData.tfclc$isCity()
                && origin.getY() >= cityData.tfclc$getCityFloor()) {
            cir.setReturnValue(false);
        }
    }
}