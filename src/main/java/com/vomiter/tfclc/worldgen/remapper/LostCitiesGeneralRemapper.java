package com.vomiter.tfclc.worldgen.remapper;

import com.vomiter.tfclc.Helpers;
import com.vomiter.tfclc.worldgen.remapper.wood.LostCitiesWoodRemapper;
import com.vomiter.tfclc.worldgen.postprocess.LostCitiesPostProcessTracker;
import com.vomiter.tfclc.worldgen.postprocess.PendingBlockEntityKind;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.Registries;

public class LostCitiesGeneralRemapper {
    public static BlockState remap(BlockState state, BlockPos pos, LevelAccessor region) {
        if (state == null) {
            return null;
        }

        if(state.is(Blocks.BOOKSHELF)){
            LostCitiesPostProcessTracker.add(pos, PendingBlockEntityKind.FILLED_BOOKSHELF);
            return TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.BOOKSHELF).get().defaultBlockState();
        }

        if(state.is(Blocks.GLOWSTONE)){
            LostCitiesPostProcessTracker.add(pos, PendingBlockEntityKind.LAVA_LAMP);
            return TFCBlocks.METALS.get(Metal.BLUE_STEEL).get(Metal.BlockType.LAMP).get().defaultBlockState();
        }

        if(state.is(Blocks.ENCHANTING_TABLE)){
            return TFCBlocks.METALS
                    .get(Metal.BISMUTH_BRONZE)
                    .get(Metal.BlockType.ANVIL)
                    .get()
                    .defaultBlockState();
        }

        if(state.is(BlockTags.ANVIL)){
            return TFCBlocks.METALS
                    .get(Metal.BLACK_BRONZE)
                    .get(Metal.BlockType.ANVIL)
                    .get()
                    .defaultBlockState();
        }

        if(state.is(Blocks.CHEST) || state.is(Blocks.BARREL)){
            return TFCBlocks.LARGE_VESSEL.get().defaultBlockState();
        }

        if(state.is(BlockTags.FLOWER_POTS)){
            if(state.is(Blocks.POTTED_CACTUS)) return TFCBlocks.POTTED_PLANTS.get(Plant.BARREL_CACTUS).get().defaultBlockState();
            else return TFCBlocks.POTTED_PLANTS.get(Plant.DEAD_BUSH).get().defaultBlockState();
        }

        if(state.is(Blocks.FURNACE)){
            return TFCBlocks.CRUCIBLE.get().defaultBlockState();
        }

        if(state.is(Blocks.WALL_TORCH)){
            return TFCBlocks.DEAD_WALL_TORCH.get().defaultBlockState().setValue(WallTorchBlock.FACING ,state.getValue(WallTorchBlock.FACING));
        }

        if(state.is(Blocks.TORCH)){
            return TFCBlocks.DEAD_TORCH.get().defaultBlockState();
        }

        if(state.is(Blocks.IRON_BARS)){
            return TFCBlocks.METALS
                    .get(Metal.WROUGHT_IRON)
                    .get(Metal.BlockType.BARS).get()
                    .defaultBlockState()
                    .setValue(IronBarsBlock.EAST, state.getValue(IronBarsBlock.EAST))
                    .setValue(IronBarsBlock.WEST, state.getValue(IronBarsBlock.WEST))
                    .setValue(IronBarsBlock.NORTH, state.getValue(IronBarsBlock.NORTH))
                    .setValue(IronBarsBlock.SOUTH, state.getValue(IronBarsBlock.SOUTH));
        }

        if(state.is(Blocks.CHAIN)){
            return TFCBlocks.METALS
                    .get(Metal.WROUGHT_IRON)
                    .get(Metal.BlockType.CHAIN).get()
                    .defaultBlockState()
                    .setValue(ChainBlock.AXIS, state.getValue(ChainBlock.AXIS));
        }

        if(state.is(Blocks.OAK_PLANKS)){
            return TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.PLANKS).get().defaultBlockState();
        }

        if(state.is(Blocks.DEEPSLATE_TILES) || state.is(Blocks.CRACKED_DEEPSLATE_TILES)){
            return TFCBlocks.ROCK_BLOCKS.get(Rock.SHALE).get(Rock.BlockType.CHISELED).get().defaultBlockState();
        }

        if(state.is(Blocks.IRON_BLOCK)){
            return TFCBlocks.METALS.get(Metal.WROUGHT_IRON).get(Metal.BlockType.BLOCK).get().defaultBlockState();
        }


        var woodRemap = LostCitiesWoodRemapper.remapVanillaWood(state);
        if(woodRemap != null && woodRemap != state) return woodRemap;
        var TFCIsOcean = TagKey.create(
        Registries.BIOME,
        Helpers.id("tfc", "is_ocean")
        );

        if (region.getBiome(pos).is(TFCIsOcean) && state.is(Blocks.WATER)){
            return TFCBlocks.SALT_WATER.get().defaultBlockState();
        }

        return state;
    }
}
