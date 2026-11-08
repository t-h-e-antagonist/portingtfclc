package com.vomiter.tfclc.util.loot;

import net.minecraft.resources.ResourceLocation;

public interface IPendingLootVessel {
    void tfclc$setPendingLoot(ResourceLocation lootId, boolean sealAfterFill);
    boolean tfclc$hasPendingLoot();
    ResourceLocation tfclc$getPendingLootId();
    boolean tfclc$shouldSealAfterFill();
    boolean tfclc$isLootInitialized();
    void tfclc$setLootInitialized(boolean initialized);
    void tfclc$clearPendingLoot();
    void tfclc$tryInitializePendingLoot();
}