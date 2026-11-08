package com.vomiter.tfclc.common.registry;

import com.vomiter.tfclc.TFCLostCities;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ModRegistries {
    static List<DeferredRegister<?>> REGISTRIES = new ArrayList<>();

    // 新签名：接收 vanilla Registry 实例（如 BuiltInRegistries.ITEM）
    public static <B> DeferredRegister<B> createRegistry(Registry<B> registry){
        return DeferredRegister.create(registry, TFCLostCities.MOD_ID);
    }

    // 新签名：接收 ResourceKey<Registry<B>>
    public static <B> DeferredRegister<B> createRegistry(ResourceKey<Registry<B>> key){
        return DeferredRegister.create(key, TFCLostCities.MOD_ID);
    }

    static void add(DeferredRegister<?> r){
        REGISTRIES.add(r);
    }

    public static void register(IEventBus modBus){
        add(ModItems.ITEMS);
        add(TFCLCBlocks.BLOCKS);
        for (DeferredRegister<?> registry : REGISTRIES) {
            registry.register(modBus);
        }
    }
}