package com.vomiter.tfclc.mixin.postgen.loot;

import com.vomiter.tfclc.util.loot.IPendingLootVessel;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TFCBlockEntity.class, remap = false)
public class TFCBlockEntity_PendingLootMixin {
    @Inject(method = "onLoad", at = @At("TAIL"))
    private void tfclc$onLoadTryInit(CallbackInfo ci) {
        if(this instanceof IPendingLootVessel vessel) vessel.tfclc$tryInitializePendingLoot();
    }
}
