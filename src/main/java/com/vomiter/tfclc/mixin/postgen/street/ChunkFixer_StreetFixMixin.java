package com.vomiter.tfclc.mixin.postgen.street;

import com.vomiter.tfclc.util.LostCitiesTFCLocalFloraHelper;
import com.vomiter.tfclc.worldgen.remapper.LostCitiesTFCSoilHelper;
import com.vomiter.tfclc.worldgen.postprocess.LostCitiesPostProcessTracker;
import com.vomiter.tfclc.worldgen.postprocess.street.PendingPreservedSurfaceKind;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.worldgen.ChunkFixer;
import mcjty.lostcities.worldgen.IDimensionInfo;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ChunkFixer.class, remap = false)
public abstract class ChunkFixer_StreetFixMixin {

    @Inject(
            method = "fix",
            at = @At("TAIL")
    )
    private static void tfclc$postProcessSpecialBlockEntities(IDimensionInfo info, ChunkCoord coord, CallbackInfo ci) {
        LevelAccessor level = info.getWorld();
        ChunkPos chunkPos = new ChunkPos(coord.chunkX(), coord.chunkZ());
        Map<BlockPos, PendingPreservedSurfaceKind> preserved =
                LostCitiesPostProcessTracker.consumePreservedSurfaces(chunkPos);

        for (Map.Entry<BlockPos, PendingPreservedSurfaceKind> entry : preserved.entrySet()) {
            BlockPos pos = entry.getKey();

            if (!level.getBlockState(pos).isAir()) {
                continue;
            }

            if (level.getBlockState(pos.below()).isAir()) {
                continue;
            }

            level.setBlock(pos, LostCitiesTFCSoilHelper.getSoilState(level, pos, SoilBlockType.GRASS), 2);
            level.setBlock(pos, LostCitiesTFCSoilHelper.getSoilState(level, pos, SoilBlockType.GRASS), 2);
            LostCitiesTFCLocalFloraHelper.tryPlaceLocalFlora(level, pos.above());
        }
    }

}