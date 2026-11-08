package com.vomiter.tfclc.spawn;

import com.vomiter.tfclc.TFCLostCities;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.api.ILostChunkInfo;
import mcjty.lostcities.api.ILostCityInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

import java.util.ArrayList;
import java.util.List;

//@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = "tfclc")
public final class LostCitiesSpawnEvents {
    static List<Mob> toCheckOnRoof = new ArrayList<>();

    private LostCitiesSpawnEvents() {
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        var level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        final Mob mob = event.getEntity();
        final EntityType<?> type = mob.getType();
        final MobSpawnType spawnType = event.getSpawnType();
        // 只攔自然生成 / chunk generation 的被動動物
        if (spawnType != MobSpawnType.NATURAL && spawnType != MobSpawnType.CHUNK_GENERATION) {
            TFCLostCities.LOGGER.info("[TFCLC] {} is not natural", mob);
            return;
        }
        if (!(mob instanceof Animal)) {
            return;
        }
        toCheckOnRoof.add(mob);
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Mob mob){
            if(mob.level().isClientSide()) return;
            final BlockPos pos = mob.blockPosition();
            TFCLostCities.LOGGER.info("[TFCLC] {} is spawning at {}", mob, pos);
            if(toCheckOnRoof.contains(mob) && shouldCancelNonGroundFloorPassiveSpawn(mob.level(), pos)){
                event.setCanceled(true);
            }
        }
    }

    /**
     * 只依 LC API 判定：
     * - 該 chunk 是否為 Lost City chunk
     * - 該位置是否高於該 chunk 的 city ground level（街道 / 地面樓層）
     */
    private static boolean shouldCancelNonGroundFloorPassiveSpawn(Level level, BlockPos pos) {
        final ILostCityInformation lostInfo;

        try {
            lostInfo = LostCities.lostCitiesImp.getLostInfo(level);
        } catch (ClassCastException e) {
            TFCLostCities.LOGGER.warn("[TFCLC] Unable to get city info from level");
            return false;
        }

        if (lostInfo == null) {
            TFCLostCities.LOGGER.info("[TFCLC] city info is null");
            return false;
        }

        final ILostChunkInfo chunkInfo = lostInfo.getChunkInfo(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunkInfo == null) {
            TFCLostCities.LOGGER.info("[TFCLC] chunk info is null");
            return false;
        }

        // 不在城市 chunk 就不管
        if (!chunkInfo.isCity()) {
            TFCLostCities.LOGGER.info("[TFCLC] not in city");
            return false;
        }

        // LC 的 city level 轉實際高度：
        // getRealHeight(level) = groundLevel + 6 * level
        // 這個值是樓板高度，實際站立空間通常是 +1
        final int cityGroundY = lostInfo.getRealHeight(chunkInfo.getCityLevel());
        final int allowedFeetY = cityGroundY + 1;
        TFCLostCities.LOGGER.info("[TFCLC] city ground = {}", cityGroundY);

        return pos.getY() > allowedFeetY;
    }
    //-867.00, y=89.00, z=1140.00
}