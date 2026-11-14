package io.github.alexf6.mitosyfauna.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Aplica los efectos pasivos de reliquias y prendas culturales equipadas en jugadores del servidor.
 */
public final class ModPlayerEffects {

    private ModPlayerEffects() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 10 != 0) {
                return;
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                applyGearAndRelicEffects(player);
            }
        });
    }

    private static void applyGearAndRelicEffects(ServerPlayer player) {
        if (!player.isAlive()) {
            return;
        }
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        long dayTime = Math.floorMod(level.getOverworldClockTime(), 24000L);
        boolean isNight = dayTime >= 13000L && dayTime < 23500L;

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (head.is(ModItems.SOMBRERO_VUELTIAO)) {
            player.addEffect(new MobEffectInstance(MobEffects.LUCK, 100, 0, true, false, true));
            if (!isNight && level.canSeeSky(pos)) {
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0, true, false, true));
            }
        } else if (head.is(ModItems.SOMBRERON_HAT) && isNight) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 0, true, false, true));
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(ModItems.RUANA)) {
            player.setTicksFrozen(0);
            float temp = level.getBiome(pos).value().getBaseTemperature();
            if (temp < 0.35F || isNight) {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 0, true, false, true));
            }
        }

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (main.is(ModItems.MOHAN_AMULET) || off.is(ModItems.MOHAN_AMULET)) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 100, 0, true, false, true));
            if (player.isInWaterOrRain()) {
                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100, 0, true, false, true));
            }
        }

        if (off.is(ModItems.LLORONA_TEAR)) {
            // 280 ticks evita el parpadeo visual de Visión Nocturna por debajo de 200 ticks.
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 280, 0, true, false, true));
        }
    }
}
