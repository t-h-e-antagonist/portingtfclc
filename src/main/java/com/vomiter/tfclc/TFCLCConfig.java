package com.vomiter.tfclc;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.common.EventBusSubscriber;

// NeoForge 1.21.1 使用 ModConfigSpec 替代 ForgeConfigSpec
@EventBusSubscriber(modid = TFCLostCities.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class TFCLCConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    public static ModConfigSpec.IntValue MIN_CITY_DISTANCE_FROM_SPAWN = BUILDER
            .comment("This value determines the minimum distance between world spawn and any city chunks.")
            .defineInRange("minCityDistanceFromSpawn", 0, 0, Integer.MAX_VALUE);
    
    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        // 如果后续需要读取配置值，可以在这里调用 MIN_CITY_DISTANCE_FROM_SPAWN.get()
    }
}