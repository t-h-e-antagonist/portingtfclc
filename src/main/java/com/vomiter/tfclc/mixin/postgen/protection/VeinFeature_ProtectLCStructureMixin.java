package com.vomiter.tfclc.mixin.postgen.protection;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.vomiter.tfclc.worldgen.CityChunkData;
import net.dries007.tfc.world.feature.vein.VeinFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = VeinFeature.class)
public abstract class VeinFeature_ProtectLCStructureMixin {

    @Unique
    private static final ThreadLocal<ChunkAccess> tfclc$chunkAcess = ThreadLocal.withInitial(() -> null);

    @WrapMethod(method = "place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z")
    private boolean tfclc$place(FeaturePlaceContext<?> context, Operation<Boolean> original){
        try{
            BlockPos origin = context.origin();
            ChunkPos chunkPos = new ChunkPos(origin);

            ChunkAccess chunk = context.level().getChunk(
                    chunkPos.x,
                    chunkPos.z
            );
            tfclc$chunkAcess.set(chunk);

            return original.call(context);
        } finally {
            tfclc$chunkAcess.remove();
        }
    }

    @WrapOperation(method = "place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;IILnet/dries007/tfc/world/feature/vein/IVein;Lnet/dries007/tfc/world/feature/vein/IVeinConfig;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean tfclc$avoidCity(
            WorldGenLevel instance,
            BlockPos pos,
            BlockState state,
            int i,
            Operation<Boolean> original
    ) {

        CityChunkData cityData = (CityChunkData) tfclc$chunkAcess.get();

        if (cityData.tfclc$isCity()
                && pos.getY() >= cityData.tfclc$getCityFloor()) {
            return false;
        }
        return original.call(instance, pos, state, i);
    }

}