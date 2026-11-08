package com.vomiter.tfclc.mixin.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunk_HardenedRockWriteTraceMixin {

    @Unique
    private static final Logger TFCLC$LOGGER = LoggerFactory.getLogger("TFCLostCities/HardenedRockTrace");

    @Inject(
            method = "setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("HEAD"),
            remap = false // Add this
    )
    private void tfclc$traceHardenedRockOverwrite(
            BlockPos pos,
            BlockState newState,
            boolean moved,
            CallbackInfoReturnable<BlockState> cir
    ) {
        final ProtoChunk self = (ProtoChunk) (Object) this;
        final BlockState oldState = self.getBlockState(pos);

        if (!tfclc$isInterestingTransition(oldState, newState)) {
            return;
        }

        TFCLC$LOGGER.warn(
                "[TFCLC TRACE] ProtoChunk overwrite at pos={} chunk={} old={} -> new={}",
                pos,
                self.getPos(),
                tfclc$id(oldState),
                tfclc$id(newState)
        );

        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        final int start = 2;
        final int end = Math.min(stack.length, 18);

        for (int i = start; i < end; i++) {
            TFCLC$LOGGER.warn("[TFCLC TRACE]   at {}", stack[i]);
        }
    }

    @Unique
    private static boolean tfclc$isInterestingTransition(BlockState oldState, BlockState newState) {
        if (oldState == null || newState == null) {
            return false;
        }
        if (oldState.getBlock() == newState.getBlock()) {
            return false;
        }

        final ResourceLocation oldId = BuiltInRegistries.BLOCK.getKey(oldState.getBlock());
        final ResourceLocation newId = BuiltInRegistries.BLOCK.getKey(newState.getBlock());

        if (oldId == null || newId == null) {
            return false;
        }
        if (!"tfc".equals(oldId.getNamespace()) || !"tfc".equals(newId.getNamespace())) {
            return false;
        }

        final String oldPath = oldId.getPath();
        final String newPath = newId.getPath();

        if (!oldPath.contains("hardened") || !newPath.contains("hardened")) {
            return false;
        }
        if (!oldPath.contains("andesite")) {
            return false;
        }

        return !oldPath.equals(newPath);
    }

    @Unique
    private static String tfclc$id(BlockState state) {
        final Block block = state.getBlock();
        final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return id == null ? String.valueOf(block) : id.toString();
    }
}