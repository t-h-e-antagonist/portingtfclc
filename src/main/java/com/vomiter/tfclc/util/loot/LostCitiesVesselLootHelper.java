package com.vomiter.tfclc.util.loot;

import net.dries007.tfc.common.blockentities.LargeVesselBlockEntity;
import net.dries007.tfc.common.blocks.LargeVesselBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public final class LostCitiesVesselLootHelper {

    private LostCitiesVesselLootHelper() {
    }

    public static void initializePendingLoot(
            ServerLevel level,
            LargeVesselBlockEntity vessel,
            ResourceLocation lootId,
            boolean sealAfterFill
    ) {
        BlockPos pos = vessel.getBlockPos();

        // 1.21.1 战利品表获取方式已变
        LootTable table = level.getServer().reloadableRegistries()
    .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootId));

        LootParams params = new LootParams.Builder(level)
                .withOptionalParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .create(LootContextParamSets.EMPTY);

        List<ItemStack> items = table.getRandomItems(params);

        // 1.21.1 从 Level 查询方块能力，而非 BlockEntity
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (handler != null) {
            clear(handler);

            for (ItemStack stack : items) {
                if (stack.isEmpty()) {
                    continue;
                }
                ItemStack remain = ItemHandlerHelper.insertItemStacked(handler, stack.copy(), false);

                if (!remain.isEmpty()) {
                    // no-op
                }
            }
        }

        vessel.setChanged();

        if (sealAfterFill) {
            sealLargeVessel(level, pos);
        }
    }

    public static void sealLargeVessel(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);

        if (!state.hasProperty(LargeVesselBlock.SEALED)) {
            return;
        }

        if (!state.getValue(LargeVesselBlock.SEALED)) {
            level.setBlock(pos, state.setValue(LargeVesselBlock.SEALED, true), 3);

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LargeVesselBlockEntity sealedVessel) {
                sealedVessel.onSeal();
                sealedVessel.setChanged();
            }
        }
    }

    private static void clear(IItemHandler handler) {
        if (handler instanceof IItemHandlerModifiable modifiable) {
            for (int i = 0; i < modifiable.getSlots(); i++) {
                modifiable.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }
}