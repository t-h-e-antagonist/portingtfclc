package com.vomiter.tfclc.mixin.worldgen.remap;

import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import mcjty.lostcities.worldgen.lost.cityassets.CompiledPalette;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.TFCLeavesBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LostCityTerrainFeature.class, remap = false)
public abstract class LostCityTerrainFeature_TFCRandomVegetationMixin {

    @Inject(
        method = "getRandomLeaf",
        at = @At("RETURN"),
        cancellable = true
    )
    private void tfclc$replaceRandomLeafClutter(
            BuildingInfo info,
            CompiledPalette compiledPalette,
            CallbackInfoReturnable<BlockState> cir
    ) {
        BlockState original = cir.getReturnValue();
        if (original == null) {
            return;
        }

        Block block = original.getBlock();
        BlockState replacement = tfclc$mapLeaf(block);
        if (replacement != null) {
            cir.setReturnValue(replacement);
        }
    }

    @Unique
    private static BlockState tfclc$mapLeaf(Block vanillaLeaf) {
        if (vanillaLeaf == Blocks.OAK_LEAVES) {
            return tfclc$persistentLeaves(Wood.OAK);
        }
        if (vanillaLeaf == Blocks.JUNGLE_LEAVES) {
            return tfclc$persistentLeaves(Wood.KAPOK);
        }
        if (vanillaLeaf == Blocks.SPRUCE_LEAVES) {
            return tfclc$persistentLeaves(Wood.SPRUCE);
        }

        return null;
    }

    @Unique
    private static BlockState tfclc$persistentLeaves(Wood wood) {
        return TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LEAVES).get()
                .defaultBlockState()
                .setValue(TFCLeavesBlock.PERSISTENT, true);
    }
}