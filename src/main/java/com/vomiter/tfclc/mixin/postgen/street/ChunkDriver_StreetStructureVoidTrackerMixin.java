package com.vomiter.tfclc.mixin.postgen.street;

import com.vomiter.tfclc.worldgen.postprocess.LostCitiesPostProcessTracker;
import com.vomiter.tfclc.worldgen.postprocess.street.PendingPreservedSurfaceKind;
import com.vomiter.tfclc.worldgen.postprocess.street.TFCLCStreetVoidContext;
import mcjty.lostcities.worldgen.ChunkDriver;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkDriver.class, remap = false)
public abstract class ChunkDriver_StreetStructureVoidTrackerMixin {

    @Shadow @Final
    private BlockPos.MutableBlockPos current;

    @Inject(
            method = "add(Lnet/minecraft/world/level/block/state/BlockState;)Lmcjty/lostcities/worldgen/ChunkDriver;",
            at = @At("HEAD")
    )
    private void tfclc$trackStreetStructureVoid(BlockState state, CallbackInfoReturnable<ChunkDriver> cir) {
        if (!TFCLCStreetVoidContext.isActive()) {
            return;
        }

        if (!state.is(Blocks.STRUCTURE_VOID)) {
            return;
        }

        LostCitiesPostProcessTracker.addPreservedSurface(
                current.immutable(),
                PendingPreservedSurfaceKind.STREET_PRESERVE
        );
    }
}