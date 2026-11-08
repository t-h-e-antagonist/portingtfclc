package com.vomiter.tfclc.mixin.postgen.loot;

import com.vomiter.tfclc.Helpers;
import com.vomiter.tfclc.util.loot.IPendingLootVessel;
import mcjty.lostcities.worldgen.IDimensionInfo;
import mcjty.lostcities.worldgen.LostCityTerrainFeature;
import mcjty.lostcities.worldgen.lost.BuildingInfo;
import net.dries007.tfc.common.blockentities.LargeVesselBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LostCityTerrainFeature.class, remap = false)
public abstract class LostCityTerrainFeature_TFCVesselLootMixin {

    @Shadow @Final public IDimensionInfo provider;

    @Inject(
            method = "generateLoot",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tfclc$handleLargeVesselLoot(
            BuildingInfo info,
            LevelAccessor world,
            BlockPos pos,
            BuildingInfo.ConditionTodo condition,
            CallbackInfo ci
    ) {
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof LargeVesselBlockEntity vessel && be instanceof IPendingLootVessel pending) {
            if (provider.getProfile().GENERATE_LOOT) {
                pending.tfclc$setPendingLoot(Helpers.id("large_vessel/default"), true);
                vessel.setChanged();
            }
            ci.cancel();
        }
    }
}