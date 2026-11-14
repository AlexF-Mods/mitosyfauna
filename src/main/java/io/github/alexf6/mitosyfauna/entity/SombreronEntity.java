package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import io.github.alexf6.mitosyfauna.item.ModItems;
import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic;
import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * El Sombrerón: figura oscura estilo Enderman que merodea por caminos solitarios de noche.
 * Persigue ferozmente a jugadores que viajan a caballo o en otra montura, y a quienes lo miran fijamente.
 */
public class SombreronEntity extends MythBossEntity {

    private static final EntityDataAccessor<Boolean> DATA_ANGRY =
            SynchedEntityData.defineId(SombreronEntity.class, EntityDataSerializers.BOOLEAN);

    private int teleportCooldownTicks = 60;
    private int spookMountCooldownTicks = 40;

    public SombreronEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.PURPLE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.33)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected double configuredMaxHealth() {
        return MitosYFaunaConfig.get().sombreronMaxHp();
    }

    public boolean isAngry() {
        return this.entityData.get(DATA_ANGRY);
    }

    private void setAngry(boolean angry) {
        this.entityData.set(DATA_ANGRY, angry);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ANGRY, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.35, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.95));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                true,
                false,
                this::shouldAggroOnPlayer
        ));
    }

    private boolean shouldAggroOnPlayer(LivingEntity candidate, ServerLevel level) {
        if (!(candidate instanceof Player player) || player.getAbilities().instabuild) {
            return false;
        }
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        boolean looking = HorrorAiLogic.isLookingAtEntity(
                new Vec3d(eyePos.x, eyePos.y, eyePos.z),
                new Vec3d(look.x, look.y, look.z),
                new Vec3d(getX(), getEyeY(), getZ()),
                0.96
        );
        boolean wearingHat = player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SOMBRERON_HAT);
        return HorrorAiLogic.shouldSombreronAggro(
                player.isPassenger(),
                looking,
                this.distanceToSqr(player),
                576.0,
                wearingHat
        );
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (teleportCooldownTicks > 0) {
            teleportCooldownTicks--;
        }
        if (spookMountCooldownTicks > 0) {
            spookMountCooldownTicks--;
        }

        LivingEntity target = this.getTarget();
        boolean hasTarget = target != null && target.isAlive();
        setAngry(hasTarget);
        if (!hasTarget) {
            return;
        }

        double distSq = this.distanceToSqr(target);
        if (distSq > 49.0 && distSq < 900.0 && teleportCooldownTicks <= 0) {
            tryTeleportBehind(level, target);
            teleportCooldownTicks = 90;
        }

        if (target.isPassenger() && distSq <= 36.0 && spookMountCooldownTicks <= 0
                && MitosYFaunaConfig.get().enableSombreronMountSpooking()) {
            spookVictimMount(target, distSq);
            spookMountCooldownTicks = 65;
        }
    }

    private void spookVictimMount(LivingEntity rider, double distSq) {
        Entity mount = rider.getVehicle();
        if (mount == null) {
            return;
        }
        if (mount instanceof AbstractHorse horse) {
            horse.makeMad();
        } else {
            double joltX = (this.random.nextDouble() - 0.5) * 0.7;
            double joltZ = (this.random.nextDouble() - 0.5) * 0.7;
            mount.push(joltX, 0.25, joltZ);
            mount.needsSync = true;
            this.playSound(SoundEvents.HORSE_ANGRY, 1.0F, 0.85F);
        }
        if (distSq <= 16.0 && rider instanceof Player player
                && !player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SOMBRERON_HAT)
                && this.random.nextFloat() < 0.45F) {
            player.stopRiding();
        }
    }

    private void tryTeleportBehind(ServerLevel level, LivingEntity target) {
        Vec3 look = target.getViewVector(1.0F);
        Vec3d desired = HorrorAiLogic.computeTeleportBehindPosition(
                new Vec3d(target.getX(), target.getY(), target.getZ()),
                new Vec3d(look.x, look.y, look.z),
                3.0,
                (this.random.nextDouble() - 0.5) * 2.0
        );
        BlockPos basePos = BlockPos.containing(desired.x(), desired.y(), desired.z());
        for (int dy = 3; dy >= -3; dy--) {
            BlockPos candidate = basePos.offset(0, dy, 0);
            if (isPassableSpace(level, candidate)) {
                level.sendParticles(ParticleTypes.SMOKE, getX(), getY() + 1.2, getZ(), 18, 0.4, 0.9, 0.4, 0.04);
                this.teleportTo(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.8F);
                level.sendParticles(ParticleTypes.PORTAL, getX(), getY() + 1.2, getZ(), 24, 0.4, 0.9, 0.4, 0.08);
                return;
            }
        }
    }

    private static boolean isPassableSpace(ServerLevel level, BlockPos pos) {
        boolean solidFloor = !level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty();
        return solidFloor
                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && level.getBlockState(pos.above(2)).getCollisionShape(level, pos.above(2)).isEmpty();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isAngry() ? SoundEvents.ENDERMAN_SCREAM : SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }
}
