package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * La Llorona: espectro errante nocturno de riberas y bosques sombríos.
 * Desorienta con llantos direccionales lejanos o tras la espalda antes de liberar
 * una onda de lamento que nubla la visión del jugador.
 */
public class LloronaEntity extends MythBossEntity {

    private int wailBurstCooldown = 80;

    public LloronaEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.PINK);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 65.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected double configuredMaxHealth() {
        return MitosYFaunaConfig.get().lloronaMaxHp();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.95));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (wailBurstCooldown > 0) {
            wailBurstCooldown--;
        }

        LivingEntity target = this.getTarget();
        if (target instanceof Player player && target.isAlive()) {
            double distSq = this.distanceToSqr(player);
            if (distSq > 20.0 && distSq < 625.0) {
                SoundEvent crySound = this.random.nextBoolean() ? SoundEvents.GHAST_WARN : SoundEvents.ALLAY_HURT;
                tryPlayDirectionalLure(level, player, crySound, 1.0F, 0.55F + this.random.nextFloat() * 0.15F, 70);
            }

            if (distSq <= 64.0 && wailBurstCooldown <= 0) {
                unleashWailWave(level);
                wailBurstCooldown = 110;
            }
        }
    }

    private void unleashWailWave(ServerLevel level) {
        this.playSound(SoundEvents.GHAST_SCREAM, 1.1F, 0.65F);
        level.sendParticles(ParticleTypes.SOUL, getX(), getEyeY(), getZ(), 22, 1.8, 0.8, 1.8, 0.04);
        for (Player nearby : level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(7.5), Player::isAlive)) {
            if (!nearby.getAbilities().instabuild) {
                nearby.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
                nearby.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 0));
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GHAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }
}
