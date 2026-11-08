package com.vomiter.tfclc.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class SubLootTable implements LootTableSubProvider {
    public SubLootTable(HolderLookup.Provider lookupProvider) { }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
        // 在这里通过 writer.accept(...) 生成战利品表
    }
}