package com.vomiter.tfclc.common.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    static DeferredRegister<Item> ITEMS
            = ModRegistries.createRegistry(BuiltInRegistries.ITEM);

    public static final DeferredHolder<Item, Item> TEST_ITEM
            = ITEMS.register("test", () -> new Item(new Item.Properties()));
}