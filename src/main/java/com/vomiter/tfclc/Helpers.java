package com.vomiter.tfclc;

import net.minecraft.resources.ResourceLocation;

public class Helpers {
    public static ResourceLocation id(String namespace, String path){
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation id(String path){
        return id(TFCLostCities.MOD_ID, path);
    }
}
