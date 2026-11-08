package com.vomiter.tfclc.worldgen.remapper.wood;

import com.vomiter.tfclc.Helpers;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class LostCitiesWoodRemapper {

    private static final String AFC_MOD_ID = "afc";

    private static final Map<Block, VanillaWoodMapping> VANILLA_WOOD_MAP =
            Util.make(new IdentityHashMap<>(), map -> {
                // ---------- saplings ----------
                map.put(Blocks.OAK_SAPLING, new VanillaWoodMapping(VanillaWoodSpecies.OAK, WoodProductType.SAPLING));
                map.put(Blocks.SPRUCE_SAPLING, new VanillaWoodMapping(VanillaWoodSpecies.SPRUCE, WoodProductType.SAPLING));
                map.put(Blocks.BIRCH_SAPLING, new VanillaWoodMapping(VanillaWoodSpecies.BIRCH, WoodProductType.SAPLING));
                map.put(Blocks.JUNGLE_SAPLING, new VanillaWoodMapping(VanillaWoodSpecies.JUNGLE, WoodProductType.SAPLING));
                map.put(Blocks.ACACIA_SAPLING, new VanillaWoodMapping(VanillaWoodSpecies.ACACIA, WoodProductType.SAPLING));
                map.put(Blocks.DARK_OAK_SAPLING, new VanillaWoodMapping(VanillaWoodSpecies.DARK_OAK, WoodProductType.SAPLING));
                map.put(Blocks.CHERRY_SAPLING, new VanillaWoodMapping(VanillaWoodSpecies.CHERRY, WoodProductType.SAPLING));
                map.put(Blocks.MANGROVE_PROPAGULE, new VanillaWoodMapping(VanillaWoodSpecies.MANGROVE, WoodProductType.SAPLING));

                // ---------- doors ----------
                map.put(Blocks.OAK_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.OAK, WoodProductType.DOOR));
                map.put(Blocks.SPRUCE_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.SPRUCE, WoodProductType.DOOR));
                map.put(Blocks.BIRCH_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.BIRCH, WoodProductType.DOOR));
                map.put(Blocks.JUNGLE_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.JUNGLE, WoodProductType.DOOR));
                map.put(Blocks.ACACIA_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.ACACIA, WoodProductType.DOOR));
                map.put(Blocks.DARK_OAK_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.DARK_OAK, WoodProductType.DOOR));
                map.put(Blocks.CHERRY_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.CHERRY, WoodProductType.DOOR));
                map.put(Blocks.MANGROVE_DOOR, new VanillaWoodMapping(VanillaWoodSpecies.MANGROVE, WoodProductType.DOOR));

                // ---------- fences ----------
                map.put(Blocks.OAK_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.OAK, WoodProductType.FENCE));
                map.put(Blocks.SPRUCE_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.SPRUCE, WoodProductType.FENCE));
                map.put(Blocks.BIRCH_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.BIRCH, WoodProductType.FENCE));
                map.put(Blocks.JUNGLE_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.JUNGLE, WoodProductType.FENCE));
                map.put(Blocks.ACACIA_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.ACACIA, WoodProductType.FENCE));
                map.put(Blocks.DARK_OAK_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.DARK_OAK, WoodProductType.FENCE));
                map.put(Blocks.CHERRY_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.CHERRY, WoodProductType.FENCE));
                map.put(Blocks.MANGROVE_FENCE, new VanillaWoodMapping(VanillaWoodSpecies.MANGROVE, WoodProductType.FENCE));

                // ---------- pressure plates ----------
                map.put(Blocks.OAK_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.OAK, WoodProductType.PRESSURE_PLATE));
                map.put(Blocks.SPRUCE_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.SPRUCE, WoodProductType.PRESSURE_PLATE));
                map.put(Blocks.BIRCH_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.BIRCH, WoodProductType.PRESSURE_PLATE));
                map.put(Blocks.JUNGLE_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.JUNGLE, WoodProductType.PRESSURE_PLATE));
                map.put(Blocks.ACACIA_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.ACACIA, WoodProductType.PRESSURE_PLATE));
                map.put(Blocks.DARK_OAK_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.DARK_OAK, WoodProductType.PRESSURE_PLATE));
                map.put(Blocks.CHERRY_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.CHERRY, WoodProductType.PRESSURE_PLATE));
                map.put(Blocks.MANGROVE_PRESSURE_PLATE, new VanillaWoodMapping(VanillaWoodSpecies.MANGROVE, WoodProductType.PRESSURE_PLATE));

            });

    private static final Map<VanillaWoodSpecies, Wood> TFC_WOOD_MAP =
            Util.make(new EnumMap<>(VanillaWoodSpecies.class), map -> {
                map.put(VanillaWoodSpecies.OAK, Wood.OAK);
                map.put(VanillaWoodSpecies.SPRUCE, Wood.HICKORY);
                map.put(VanillaWoodSpecies.BIRCH, Wood.DOUGLAS_FIR);
                map.put(VanillaWoodSpecies.JUNGLE, Wood.MAPLE);
                map.put(VanillaWoodSpecies.ACACIA, Wood.ACACIA);
                map.put(VanillaWoodSpecies.DARK_OAK, Wood.BLACKWOOD);
                map.put(VanillaWoodSpecies.CHERRY, Wood.KAPOK);
                map.put(VanillaWoodSpecies.MANGROVE, Wood.MANGROVE);
            });

    private LostCitiesWoodRemapper() {
    }

    /**
     * 只處理 vanilla 木系方塊（目前：sapling / door）。
     * 若有對應替換，回傳 replacement default state；若無需替換，回傳 null。
     */
    @Nullable
    public static BlockState remapVanillaWood(BlockState original) {
        final Block block = original.getBlock();
        final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return null;
        }

        final Block replacement = mapVanillaWood(block);
        return replacement != null ? replacement.defaultBlockState() : null;
    }

    /**
     * 統一入口：先嘗試 AFC，再 fallback 到 TFC。
     */
    @Nullable
    public static Block mapVanillaWood(Block vanilla) {
        final VanillaWoodMapping mapping = describeVanillaWood(vanilla);
        if (mapping == null) {
            return null;
        }

        /*
        if (ModList.get().isLoaded(AFC_MOD_ID)) {
            final Block afc = mapToAFC(mapping);
            if (afc != null) {
                return afc;
            }
        }

         */

        return mapToTFC(mapping);
    }

    @Nullable
    public static VanillaWoodMapping describeVanillaWood(Block vanilla) {
        return VANILLA_WOOD_MAP.get(vanilla);
    }

    @Nullable
    public static Block mapToTFC(VanillaWoodMapping mapping) {
        final Wood wood = TFC_WOOD_MAP.get(mapping.species());
        if (wood == null) {
            return null;
        }

        return switch (mapping.productType()) {
            case SAPLING -> TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SAPLING).get();
            case DOOR -> TFCBlocks.WOODS.get(wood).get(Wood.BlockType.DOOR).get();
            case FENCE -> TFCBlocks.WOODS.get(wood).get(Wood.BlockType.FENCE).get();
            case PRESSURE_PLATE -> TFCBlocks.WOODS.get(wood).get(Wood.BlockType.PRESSURE_PLATE).get();
            default -> TFCBlocks.WOODS.get(wood).get(Wood.BlockType.valueOf(mapping.productType().name())).get();
        };
    }
}