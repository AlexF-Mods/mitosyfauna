package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * La Patasola: acechadora nocturna de los bosques densos. Avanza dando saltos veloces
 * sobre una sola pierna y emite sonidos direccionales desplazados para desorientar antes de emboscar.
 */
public class PatasolaEntity extends MythBossEntity {

    private int hopCooldownTicks = 0;

    public PatasolaEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.RED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.31)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected double configuredMaxHealth() {
        return MitosYFaunaConfig.get().patasolaMaxHp();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.25, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (hopCooldownTicks > 0) {
            hopCooldownTicks--;
        }

        LivingEntity target = this.getTarget();
        if (target instanceof Player player && target.isAlive()) {
            double distSq = this.distanceToSqr(player);
            if (distSq > 25.0 && distSq < 576.0) {
                SoundEvent lureSound = this.random.nextBoolean() ? SoundEvents.FOX_SCREECH : SoundEvents.BAMBOO_BREAK;
                tryPlayDirectionalLure(level, player, lureSound, 0.95F, 0.72F + this.random.nextFloat() * 0.2F, 65);
            }

            if (this.onGround() && hopCooldownTicks <= 0 && distSq > 4.0 && distSq < 256.0) {
                double dx = player.getX() - getX();
                double dz = player.getZ() - getZ();
                double len = Math.max(0.01, Math.hypot(dx, dz));
                this.setDeltaMovement((dx / len) * 0.42, 0.36, (dz / len) * 0.42);
                this.needsSync = true;
                hopCooldownTicks = 22;
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FOX_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }
}
