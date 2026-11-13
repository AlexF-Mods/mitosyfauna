package io.github.alexf6.mitosyfauna.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Proyectil compartido cuyo comportamiento se define mediante {@link Kind}.
 */
public class FolkProjectile extends ThrowableProjectile {

    /**
     * Tipo de proyectil con parámetros físicos, de impacto y de partículas encapsulados.
     */
    public enum Kind {
        WATER_JET(0, 0.035, 5.0F, ParticleTypes.SPLASH, SoundEvents.GENERIC_SPLASH, 1.1F) {
            @Override
            void applyHitEffects(FolkProjectile proj, Entity target) {
                target.extinguishFire();
                if (target instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 0));
                    double dx = target.getX() - proj.getX();
                    double dz = target.getZ() - proj.getZ();
                    double len = Math.max(0.01, Math.hypot(dx, dz));
                    living.push((dx / len) * 0.55, 0.25, (dz / len) * 0.55);
                    living.hurtMarked = true;
                }
            }
        },
        POISON_DART(1, 0.02, 4.0F, ParticleTypes.ITEM_SLIME, SoundEvents.ARROW_HIT, 1.35F) {
            @Override
            void applyHitEffects(FolkProjectile proj, Entity target) {
                if (target instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                }
            }
        };

        final int id;
        final double gravity;
        final float damage;
        final ParticleOptions particle;
        final SoundEvent hitSound;
        final float hitPitch;

        Kind(int id, double gravity, float damage, ParticleOptions particle, SoundEvent hitSound, float hitPitch) {
            this.id = id;
            this.gravity = gravity;
            this.damage = damage;
            this.particle = particle;
            this.hitSound = hitSound;
            this.hitPitch = hitPitch;
        }

        abstract void applyHitEffects(FolkProjectile proj, Entity target);

        public static Kind byId(int id) {
            for (Kind kind : values()) {
                if (kind.id == id) {
                    return kind;
                }
            }
            return WATER_JET;
        }
    }

    private static final EntityDataAccessor<Integer> DATA_KIND =
            SynchedEntityData.defineId(FolkProjectile.class, EntityDataSerializers.INT);

    public FolkProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public FolkProjectile(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level, Kind kind) {
        super(entityType, shooter, level);
        setKind(kind);
    }

    public Kind getKind() {
        return Kind.byId(this.entityData.get(DATA_KIND));
    }

    public void setKind(Kind kind) {
        this.entityData.set(DATA_KIND, kind.id);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_KIND, Kind.WATER_JET.id);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            Kind kind = getKind();
            this.level().addParticle(kind.particle, getX(), getY(), getZ(), 0.0, 0.01, 0.0);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return getKind().gravity;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Kind kind = getKind();
        DamageSource source = this.damageSources().thrown(this, this.getOwner());
        target.hurtServer(serverLevel, source, kind.damage);
        kind.applyHitEffects(this, target);
        serverLevel.playSound(null, getX(), getY(), getZ(), kind.hitSound, getSoundSource(), 0.9F, kind.hitPitch);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(getKind().particle, getX(), getY(), getZ(), 10, 0.15, 0.15, 0.15, 0.08);
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ProjectileKind", getKind().id);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setKind(Kind.byId(input.getIntOr("ProjectileKind", Kind.WATER_JET.id)));
    }
}
