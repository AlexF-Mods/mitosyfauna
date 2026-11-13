package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Chigüiro (capibara) nativo de los ríos y humedales. Es pacífico, se domestica con caña de azúcar,
 * sandía o manzana, puede sentarse o seguir a su dueño e irradia un aura calmante de regeneración leve.
 */
public class ChiguiroEntity extends TamableAnimal {

    private int auraTickCounter = 0;

    public ChiguiroEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.TEMPT_RANGE, 10.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.35));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.15, this::isFood, false));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.1, 6.0F, 2.0F));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.05));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 7.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SUGAR_CANE) || stack.is(Items.MELON_SLICE) || stack.is(Items.APPLE);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (!this.isTame() || !this.isAlive()) {
            return;
        }
        auraTickCounter++;
        if (auraTickCounter < 80) {
            return;
        }
        auraTickCounter = 0;
        double radius = MitosYFaunaConfig.get().chiguiroAuraRadius();
        AABB box = this.getBoundingBox().inflate(radius);
        List<Player> players = level.getEntitiesOfClass(Player.class, box, Player::isAlive);
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, false, true));
        }
        if (!players.isEmpty()) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 0.7, getZ(), 2, 0.3, 0.2, 0.3, 0.01);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.level().isClientSide()) {
            boolean accepts = this.isOwnedBy(player) || this.isTame() || (isFood(stack) && !this.isTame());
            return accepts ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        if (this.isTame()) {
            if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
                stack.consume(1, player);
                this.heal(4.0F);
                this.level().broadcastEntityEvent(this, (byte) 7);
                return InteractionResult.SUCCESS;
            }
            InteractionResult parentResult = super.mobInteract(player, hand);
            if (!parentResult.consumesAction() && this.isOwnedBy(player)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget((LivingEntity) null);
                return InteractionResult.SUCCESS;
            }
            return parentResult;
        }

        if (this.isFood(stack)) {
            stack.consume(1, player);
            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.navigation.stop();
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        ChiguiroEntity baby = ModEntities.CHIGUIRO.create(level, EntitySpawnReason.BREEDING);
        if (baby != null && this.getOwner() instanceof Player ownerPlayer) {
            baby.tame(ownerPlayer);
        }
        return baby;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PIG_DEATH;
    }
}
