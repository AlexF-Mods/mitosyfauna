package io.github.alexf6.mitosyfauna.item;

import io.github.alexf6.mitosyfauna.entity.FolkProjectile;
import io.github.alexf6.mitosyfauna.entity.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Habilidades activas de los ítems de fauna y reliquias de mitos, separadas del registro de ítems.
 */
public final class ItemAbilities {

    private ItemAbilities() {
    }

    public static InteractionResult useCondorFeather(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack elytra = findRepairableElytra(player);
        if (elytra.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            elytra.setDamageValue(Math.max(0, elytra.getDamageValue() - 65));
            stack.consume(1, player);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.9F, 1.2F);
        }
        return InteractionResult.SUCCESS;
    }

    private static ItemStack findRepairableElytra(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.is(Items.ELYTRA) && chest.isDamaged()) {
            return chest;
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(Items.ELYTRA) && offhand.isDamaged()) {
            return offhand;
        }
        return ItemStack.EMPTY;
    }

    public static InteractionResult usePoisonDart(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            FolkProjectile dart = new FolkProjectile(ModEntities.FOLK_PROJECTILE, player, level, FolkProjectile.Kind.POISON_DART);
            dart.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            dart.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.1F, 1.0F);
            level.addFreshEntity(dart);
            stack.consume(1, player);
            player.getCooldowns().addCooldown(stack, 12);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.8F, 1.45F);
        }
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult useMohanAmulet(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel serverLevel) {
            player.extinguishFire();
            repelNearbyMonsters(serverLevel, player, 5.0, 0.7, 0.25, new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
            serverLevel.sendParticles(ParticleTypes.SPLASH, player.getX(), player.getY() + 0.9, player.getZ(),
                    28, 1.5, 0.5, 1.5, 0.1);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_SPLASH_HIGH_SPEED, SoundSource.PLAYERS, 0.9F, 1.1F);
            player.getCooldowns().addCooldown(stack, 240);
        }
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult usePatasolaClaw(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            Vec3 look = player.getLookAngle();
            player.push(look.x * 1.15, 0.32, look.z * 1.15);
            player.hurtMarked = true;
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 120, 1));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0));
            player.getCooldowns().addCooldown(stack, 160);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FOX_AGGRO, SoundSource.PLAYERS, 0.8F, 0.9F);
        }
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult useLloronaTear(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel serverLevel) {
            player.removeEffect(MobEffects.BLINDNESS);
            player.removeEffect(MobEffects.DARKNESS);
            player.removeEffect(MobEffects.SLOWNESS);
            player.removeEffect(MobEffects.WEAKNESS);
            player.removeEffect(MobEffects.POISON);
            player.removeEffect(MobEffects.WITHER);
            player.removeEffect(MobEffects.NAUSEA);
            repelNearbyMonsters(serverLevel, player, 5.5, 0.9, 0.3, null);
            serverLevel.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1.0, player.getZ(),
                    20, 1.4, 0.5, 1.4, 0.05);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.PLAYERS, 1.0F, 0.85F);
            player.getCooldowns().addCooldown(stack, 300);
        }
        return InteractionResult.SUCCESS;
    }

    private static void repelNearbyMonsters(
            ServerLevel level,
            Player player,
            double radius,
            double horizontalForce,
            double verticalForce,
            MobEffectInstance optionalEffect
    ) {
        for (Monster mob : level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(radius))) {
            if (optionalEffect != null) {
                mob.addEffect(new MobEffectInstance(optionalEffect));
            }
            Vec3 away = mob.position().subtract(player.position());
            double horiz = Math.max(0.01, Math.hypot(away.x, away.z));
            mob.push((away.x / horiz) * horizontalForce, verticalForce, (away.z / horiz) * horizontalForce);
            mob.hurtMarked = true;
        }
    }
}
