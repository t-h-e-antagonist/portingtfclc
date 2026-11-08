package com.vomiter.tfclc.mixin.postgen;

import com.vomiter.tfclc.TFCLostCities;
import com.vomiter.tfclc.util.bookshelf.LostCitiesBookshelfFacingHelper;
import com.vomiter.tfclc.util.bookshelf.LostCitiesBookshelfFiller;
import com.vomiter.tfclc.worldgen.postprocess.LostCitiesPostProcessTracker;
import com.vomiter.tfclc.worldgen.postprocess.PendingBlockEntityKind;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.worldgen.ChunkFixer;
import mcjty.lostcities.worldgen.IDimensionInfo;
import net.dries007.tfc.common.blockentities.BookshelfBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.LampBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ChunkFixer.class, remap = false)
public abstract class ChunkFixer_PostProcessSpecialBEMixin {

    @Inject(
            method = "fix",
            at = @At("TAIL")
    )
    private static void tfclc$postProcessSpecialBlockEntities(IDimensionInfo info, ChunkCoord coord, CallbackInfo ci) {
        LevelAccessor level = info.getWorld();
        ChunkPos chunkPos = new ChunkPos(coord.chunkX(), coord.chunkZ());
        Map<BlockPos, PendingBlockEntityKind> pending = LostCitiesPostProcessTracker.consumeChunk(chunkPos);
        if (pending.isEmpty()) {
            return;
        }

        long seed = Mth.getSeed(coord.chunkX(), 0, coord.chunkZ()) ^ 0x6E624EB7D4C15A3BL;
        RandomSource random = RandomSource.create(seed);

        for (Map.Entry<BlockPos, PendingBlockEntityKind> entry : pending.entrySet()) {
            BlockPos pos = entry.getKey();
            PendingBlockEntityKind kind = entry.getValue();

            switch (kind) {
                case FILLED_BOOKSHELF -> tfclc$processBookshelf(level, pos, random);
                case LAVA_LAMP -> tfclc$processLamp(level, pos);
            }
        }
    }

    @Unique
    private static void tfclc$processBookshelf(LevelAccessor level, BlockPos pos, RandomSource random) {
        BlockState state = level.getBlockState(pos);

        if (!state.is(TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.BOOKSHELF).get())) {
            return;
        }

        BlockState fixedState = LostCitiesBookshelfFacingHelper.fixFacing(level, pos, state);
        if (fixedState != state && fixedState.canSurvive(level, pos)) {
            if (!level.setBlock(pos, fixedState, 2)) {
                return;
            }
            state = level.getBlockState(pos);
        }

        if (!(level.getBlockEntity(pos) instanceof BookshelfBlockEntity bookshelf)) {
            return;
        }

        // RegistryAccess 在 1.21.1 中实现了 HolderLookup.Provider，可直接传入
        HolderLookup.Provider registries = level.registryAccess();
        BlockState filledState = LostCitiesBookshelfFiller.fillRandom(bookshelf, state, random, registries);

        if (filledState != state) {
            level.setBlock(pos, filledState, 2);
        }
    }

    @Unique
    private static void tfclc$processLamp(LevelAccessor level, BlockPos pos) {
        if (!(level instanceof Level realLevel)) return;

        IFluidHandler fluidHandler = realLevel.getCapability(
                Capabilities.FluidHandler.BLOCK, pos, null
        );
        if (fluidHandler == null) return;

        fluidHandler.fill(new FluidStack(Fluids.LAVA, 250), IFluidHandler.FluidAction.EXECUTE);
        realLevel.setBlock(pos, realLevel.getBlockState(pos).setValue(LampBlock.LIT, true), 2);
        TFCLostCities.LOGGER.info("LIT LAVA LAMP AT {}", pos);
    }
}