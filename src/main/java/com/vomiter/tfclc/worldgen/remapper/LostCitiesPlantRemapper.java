package com.vomiter.tfclc.worldgen.remapper;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class LostCitiesPlantRemapper {

    private LostCitiesPlantRemapper() {
    }

    /**
     * 只處理 vanilla 植物（非 wood 類）。
     * 若有對應替換，回傳 replacement default state；若無需替換，回傳 null。
     */
    @Nullable
    public static BlockState remapVanillaPlant(BlockState original) {
        final Block block = original.getBlock();
        final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) {
            return null;
        }

        if (!"minecraft".equals(id.getNamespace())) {
            return null;
        }

        final Block replacement = mapVanillaPlantToTFC(block);
        return replacement != null ? replacement.defaultBlockState() : null;
    }

    @Nullable
    public static Block mapVanillaPlantToTFC(Block vanilla) {
        // ---------- flowers / grass-like ----------
        if (vanilla == Blocks.DANDELION) {
            return TFCBlocks.PLANTS.get(Plant.DANDELION).get();
        }
        if (vanilla == Blocks.POPPY) {
            return TFCBlocks.PLANTS.get(Plant.POPPY).get();
        }
        if (vanilla == Blocks.BLUE_ORCHID) {
            return TFCBlocks.PLANTS.get(Plant.BLUE_ORCHID).get();
        }
        if (vanilla == Blocks.ALLIUM) {
            return TFCBlocks.PLANTS.get(Plant.ALLIUM).get();
        }
        if (vanilla == Blocks.AZURE_BLUET) {
            return TFCBlocks.PLANTS.get(Plant.HOUSTONIA).get();
        }
        if (vanilla == Blocks.RED_TULIP) {
            return TFCBlocks.PLANTS.get(Plant.POPPY).get();
        }
        if (vanilla == Blocks.ORANGE_TULIP) {
            return TFCBlocks.PLANTS.get(Plant.NASTURTIUM).get();
        }
        if (vanilla == Blocks.WHITE_TULIP) {
            return TFCBlocks.PLANTS.get(Plant.OXEYE_DAISY).get();
        }
        if (vanilla == Blocks.PINK_TULIP) {
            return TFCBlocks.PLANTS.get(Plant.MAIDEN_PINK).get();
        }
        if (vanilla == Blocks.OXEYE_DAISY) {
            return TFCBlocks.PLANTS.get(Plant.OXEYE_DAISY).get();
        }
        if (vanilla == Blocks.CORNFLOWER) {
            return TFCBlocks.PLANTS.get(Plant.BLUE_GINGER).get();
        }
        if (vanilla == Blocks.LILY_OF_THE_VALLEY) {
            return TFCBlocks.PLANTS.get(Plant.LILY_OF_THE_VALLEY).get();
        }
        if (vanilla == Blocks.TORCHFLOWER) {
            return TFCBlocks.PLANTS.get(Plant.DESERT_FLAME).get();
        }

        // ---------- tall flowers ----------
        if (vanilla == Blocks.LILAC) {
            return TFCBlocks.PLANTS.get(Plant.LILAC).get();
        }
        if (vanilla == Blocks.ROSE_BUSH) {
            return TFCBlocks.PLANTS.get(Plant.HIBISCUS).get();
        }
        if (vanilla == Blocks.PEONY) {
            return TFCBlocks.PLANTS.get(Plant.HEATHER).get();
        }
        if (vanilla == Blocks.SUNFLOWER) {
            return TFCBlocks.PLANTS.get(Plant.GOLDENROD).get();
        }

        // ---------- short grass / fern / dead bush ----------
        if (vanilla == Blocks.SHORT_GRASS) {
            return TFCBlocks.PLANTS.get(Plant.BLUEGRASS).get();
        }
        if (vanilla == Blocks.FERN) {
            return TFCBlocks.PLANTS.get(Plant.ATHYRIUM_FERN).get();
        }
        if (vanilla == Blocks.DEAD_BUSH) {
            return TFCBlocks.PLANTS.get(Plant.DEAD_BUSH).get();
        }

        return null;
    }
}