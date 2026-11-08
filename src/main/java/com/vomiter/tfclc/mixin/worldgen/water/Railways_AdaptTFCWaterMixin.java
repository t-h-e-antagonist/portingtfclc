package com.vomiter.tfclc.mixin.worldgen.water;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mcjty.lostcities.worldgen.ChunkDriver;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.gen.Railways;
import net.dries007.tfc.common.TFCTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Railways.class, remap = false)
public abstract class Railways_AdaptTFCWaterMixin {
    @WrapOperation(method = "generateRailways", at = @At(value = "INVOKE", target = "Lmcjty/lostcities/worldgen/ChunkDriver;getBlock(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState tfclc$tfcwaterToWater(ChunkDriver instance, int x, int y, int z, Operation<BlockState> original){
        var originalState = original.call(instance, x, y, z);
        if(originalState.getFluidState().is(TFCTags.Fluids.WATER_LIKE)) return Blocks.WATER.defaultBlockState();
        return originalState;
    }

    @WrapOperation(method = "generateRailways", at = @At(value = "INVOKE", target = "Lmcjty/lostcities/worldgen/ChunkDriver;getBlock()Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState tfclc$tfcwaterToWater2(ChunkDriver instance, Operation<BlockState> original){
        var originalState = original.call(instance);
        if(originalState.getFluidState().is(TFCTags.Fluids.WATER_LIKE)) return Blocks.WATER.defaultBlockState();
        return originalState;
    }

}