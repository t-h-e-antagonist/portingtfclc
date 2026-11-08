package com.vomiter.tfclc.util.bookshelf;

import net.dries007.tfc.common.blockentities.BookshelfBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class LostCitiesBookshelfFiller {

    private LostCitiesBookshelfFiller() {
    }

    public static BlockState fillRandom(BookshelfBlockEntity bookshelf, BlockState state, RandomSource random, HolderLookup.Provider registries) {
        NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);

        for (int i = 0; i < 6; i++) {
            ItemStack stack = ItemStack.EMPTY;
            if (random.nextFloat() < 0.65f) {
                stack = randomBook(random);
            }
            items.set(i, stack);

            state = state.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i), !stack.isEmpty());
        }

        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, items, registries);
        // 1.21.1 中 BlockEntity 的加载方法是 loadAdditional
        bookshelf.loadAdditional(tag, registries);
        bookshelf.setChanged();

        return state;
    }

    private static ItemStack randomBook(RandomSource random) {
        float roll = random.nextFloat();

        if (roll < 0.75f) {
            return new ItemStack(Items.BOOK);
        }
        return new ItemStack(Items.WRITABLE_BOOK);
    }
}