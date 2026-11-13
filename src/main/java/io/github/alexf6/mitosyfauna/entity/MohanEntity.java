package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import io.github.alexf6.mitosyfauna.item.ModItems;
import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * El Mohán: mito nocturno de los ríos y riberas. Ataca cuerpo a cuerpo y con chorros de agua,
 * y puede arrebatar temporalmente un ítem al jugador (garantizando su devolución al caer).
 */
public class MohanEntity extends MythBossEntity {

    private static final EntityDataAccessor<Boolean> DATA_HOLDING_STOLEN =
            SynchedEntityData.defineId(MohanEntity.class, EntityDataSerializers.BOOLEAN);

    private ItemStack stolenStack = ItemStack.EMPTY;
    private int stealCooldownTicks = 0;
    private int waterJetCooldownTicks = 40;

    public MohanEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, BossEvent.BossBarColor.BLUE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 70.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.FOLLOW_RANGE, 26.0);
    }

    @Override
    protected double configuredMaxHealth() {
        return MitosYFaunaConfig.get().mohanMaxHp();
    }

    public boolean isHoldingStolenItem() {
        return this.entityData.get(DATA_HOLDING_STOLEN);
    }

    private void syncStolenState() {
        this.entityData.set(DATA_HOLDING_STOLEN, !stolenStack.isEmpty());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HOLDING_STOLEN, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (stealCooldownTicks > 0) {
            stealCooldownTicks--;
        }
        if (waterJetCooldownTicks > 0) {
            waterJetCooldownTicks--;
        }

        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            double distSq = this.distanceToSqr(target);
            if (distSq > 12.0 && distSq < 324.0 && waterJetCooldownTicks <= 0 && this.hasLineOfSight(target)) {
                fireWaterJet(level, target);
                waterJetCooldownTicks = 55;
            }
        }
    }

    private void fireWaterJet(ServerLevel level, LivingEntity target) {
        FolkProjectile jet = new FolkProjectile(ModEntities.FOLK_PROJECTILE, this, level, FolkProjectile.Kind.WATER_JET);
        jet.setPos(getX(), getEyeY() - 0.1, getZ());
        double dx = target.getX() - getX();
        double dy = target.getY(0.5) - jet.getY();
        double dz = target.getZ() - getZ();
        double horiz = Math.hypot(dx, dz);
        jet.shoot(dx, dy + horiz * 0.12, dz, 1.35F, 2.5F);
        level.addFreshEntity(jet);
        this.playSound(SoundEvents.PLAYER_SPLASH, 1.0F, 0.9F);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof Player player && !player.getAbilities().instabuild) {
            tryStealPlayerItem(level, player);
        }
        return hit;
    }

    private void tryStealPlayerItem(ServerLevel level, Player player) {
        InteractionHand targetHand = InteractionHand.OFF_HAND;
        ItemStack candidate = player.getItemInHand(InteractionHand.OFF_HAND);
        if (candidate.isEmpty() || isProtectedItem(candidate)) {
            targetHand = InteractionHand.MAIN_HAND;
            candidate = player.getItemInHand(InteractionHand.MAIN_HAND);
        }

        boolean allowed = HorrorAiLogic.canMohanSteal(
                MitosYFaunaConfig.get().enableMohanItemStealing(),
                candidate.isEmpty(),
                !stolenStack.isEmpty(),
                isProtectedItem(candidate),
                stealCooldownTicks
        );
        if (!allowed) {
            return;
        }

        this.stolenStack = candidate.copy();
        player.setItemInHand(targetHand, ItemStack.EMPTY);
        this.setItemInHand(InteractionHand.OFF_HAND, this.stolenStack.copy());
        this.setPersistenceRequired();
        this.stealCooldownTicks = 240;
        syncStolenState();

        this.playSound(SoundEvents.ITEM_PICKUP, 1.1F, 0.7F);
        level.sendParticles(ParticleTypes.SPLASH, getX(), getEyeY(), getZ(), 14, 0.35, 0.35, 0.35, 0.12);
    }

    private static boolean isProtectedItem(ItemStack stack) {
        return stack.is(ModItems.MOHAN_AMULET)
                || stack.is(ModItems.PATASOLA_CLAW)
                || stack.is(ModItems.LLORONA_TEAR)
                || stack.is(ModItems.SOMBRERON_HAT);
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        if (!stolenStack.isEmpty()) {
            this.spawnAtLocation(level, stolenStack);
            stolenStack = ItemStack.EMPTY;
            this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            syncStolenState();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (!stolenStack.isEmpty()) {
            output.store("StolenItem", ItemStack.CODEC, stolenStack);
        }
        output.putInt("StealCooldown", stealCooldownTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.stolenStack = input.read("StolenItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.stealCooldownTicks = input.getIntOr("StealCooldown", 0);
        if (!this.stolenStack.isEmpty()) {
            this.setItemInHand(InteractionHand.OFF_HAND, this.stolenStack.copy());
            this.setPersistenceRequired();
        }
        syncStolenState();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DROWNED_DEATH;
    }
}
