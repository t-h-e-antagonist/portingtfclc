package com.vomiter.tfclc.common.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TFCLCBlocks {
    public static final DeferredRegister<Block> BLOCKS
            = ModRegistries.createRegistry(BuiltInRegistries.BLOCK);

    public static final DeferredHolder<Block, Block> CITY_PADDING = BLOCKS.register(
            "city_padding",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS))
    );
}