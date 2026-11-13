package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Cóndor de los Andes, ave planeadora de las cumbres rocosas y picos helados.
 * Planea en círculos elevados sobre las montañas, desciende suavemente y muda plumas de cóndor.
 */
public class CondorEntity extends Animal {

    private int featherShedTimer = 4800;

    public CondorEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 16, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.FLYING_SPEED, 0.62)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.TEMPT_RANGE, 10.0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.15, this::isFood, false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new SoarMountainPeaksGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // Simula planeo sustentado reduciendo la tasa de caída cuando vuela sobre cumbres
        if (!this.onGround()) {
            Vec3 vel = this.getDeltaMovement();
            if (vel.y < -0.07) {
                this.setDeltaMovement(vel.x, vel.y * 0.62, vel.z);
            }
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (!this.isBaby() && this.isAlive()) {
            featherShedTimer--;
            if (featherShedTimer <= 0) {
                featherShedTimer = 4800 + this.random.nextInt(2400);
                this.spawnAtLocation(level, ModItems.CONDOR_FEATHER);
                this.playSound(SoundEvents.CHICKEN_EGG, 0.7F, 0.9F);
            }
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // Las aves planeadoras no reciben daño por caída.
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH) || stack.is(Items.BEEF) || stack.is(Items.MUTTON);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.CONDOR.create(level, EntitySpawnReason.BREEDING);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    /**
     * Objetivo de planeo circular en altura sobre las crestas montañosas.
     */
    private static final class SoarMountainPeaksGoal extends Goal {
        private final CondorEntity condor;
        private double angleRad;
        private BlockPos anchor;
        private int retargetTicks;

        SoarMountainPeaksGoal(CondorEntity condor) {
            this.condor = condor;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !condor.isBaby() && condor.getRandom().nextInt(30) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return retargetTicks > 0 && condor.getTarget() == null;
        }

        @Override
        public void start() {
            this.anchor = condor.blockPosition().above(10 + condor.getRandom().nextInt(8));
            this.angleRad = condor.getRandom().nextDouble() * Math.PI * 2.0;
            this.retargetTicks = 140 + condor.getRandom().nextInt(100);
        }

        @Override
        public void tick() {
            retargetTicks--;
            angleRad += 0.14;
            if (condor.getNavigation().isDone() || retargetTicks % 20 == 0) {
                double radius = 14.0;
                double tx = anchor.getX() + Math.cos(angleRad) * radius;
                double tz = anchor.getZ() + Math.sin(angleRad) * radius;
                double ty = Math.min(condor.level().getMaxY() - 6, anchor.getY());
                condor.getNavigation().moveTo(tx, ty, tz, 1.05);
            }
        }
    }
}
