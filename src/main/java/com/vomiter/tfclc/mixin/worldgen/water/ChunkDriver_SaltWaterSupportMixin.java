package com.vomiter.tfclc.mixin.worldgen.water;

import mcjty.lostcities.worldgen.ChunkDriver;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkDriver.class, remap = false)
public abstract class ChunkDriver_SaltWaterSupportMixin {
    @Inject(
        method = "getBlock(Lnet/minecraft/core/BlockPos;)" +
                 "Lnet/minecraft/world/level/block/state/BlockState;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void tfclc$normalizeSaltWater(
        BlockPos pos,
        CallbackInfoReturnable<BlockState> cir
    ) {
        BlockState state = cir.getReturnValue();

        if (state.getFluidState().is(TFCTags.Fluids.WATER_LIKE)) {
            cir.setReturnValue(Blocks.WATER.defaultBlockState());
        }
    }
}