package com.vomiter.tfclc.mixin.worldgen.water;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mcjty.lostcities.worldgen.ChunkDriver;
import mcjty.lostcities.worldgen.gen.Scattered;
import net.dries007.tfc.common.TFCTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Scattered.class, remap = false)
public class Scattered_RepeatSliceMixin {
    @WrapOperation(method = "generateScatteredBuilding", at = @At(value = "INVOKE", target = "Lmcjty/lostcities/worldgen/ChunkDriver;getBlock()Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState tfclc$delegateState(ChunkDriver instance, Operation<BlockState> original){
        if(original.call(instance).getFluidState().is(TFCTags.Fluids.WATER_LIKE)){
            return Blocks.WATER.defaultBlockState();
        }
        return original.call(instance);
    }
}
