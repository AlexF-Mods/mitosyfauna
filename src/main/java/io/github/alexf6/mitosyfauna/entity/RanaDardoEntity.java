package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Rana dardo venenosa selvática en tres variantes de color (dorada, azul y roja).
 * Al tocarla sin sigilo envenena al jugador, pero permite extraer toxina en frascos
 * o impregnar flechas con clic derecho sin necesidad de matarla.
 */
public class RanaDardoEntity extends Animal {

    public static final int VARIANT_GOLD = 0;
    public static final int VARIANT_BLUE = 1;
    public static final int VARIANT_RED = 2;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(RanaDardoEntity.class, EntityDataSerializers.INT);

    private int toxinCooldownTicks = 0;
    private int touchPoisonCooldown = 0;

    public RanaDardoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.TEMPT_RANGE, 8.0);
    }

    public int getVariant() {
        return Math.floorMod(this.entityData.get(DATA_VARIANT), 3);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, Math.floorMod(variant, 3));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, VARIANT_GOLD);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.35));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.05, this::isFood, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.95));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            SpawnGroupData spawnData
    ) {
        setVariant(level.getRandom().nextInt(3));
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnData);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (toxinCooldownTicks > 0) {
            toxinCooldownTicks--;
        }
        if (touchPoisonCooldown > 0) {
            touchPoisonCooldown--;
        }
    }

    @Override
    public void playerTouch(Player player) {
        super.playerTouch(player);
        if (!this.level().isClientSide() && touchPoisonCooldown <= 0 && !player.isShiftKeyDown() && !player.getAbilities().instabuild) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
            touchPoisonCooldown = 50;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.GLASS_BOTTLE) || stack.is(Items.ARROW)) {
            if (this.level().isClientSide()) {
                return InteractionResult.CONSUME;
            }
            if (toxinCooldownTicks > 0) {
                return InteractionResult.FAIL;
            }
            ItemStack output = stack.is(Items.GLASS_BOTTLE)
                    ? new ItemStack(ModItems.DART_TOXIN)
                    : new ItemStack(ModItems.POISON_DART);
            stack.consume(1, player);
            if (!player.getInventory().add(output)) {
                player.drop(output, false);
            }
            toxinCooldownTicks = 300;
            this.playSound(SoundEvents.BOTTLE_FILL, 0.9F, 1.3F);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.SLIME_BALL) || stack.is(Items.SPIDER_EYE);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        RanaDardoEntity baby = ModEntities.RANA_DARDO.create(level, EntitySpawnReason.BREEDING);
        if (baby != null) {
            baby.setVariant(this.random.nextBoolean() ? this.getVariant() : ((RanaDardoEntity) partner).getVariant());
        }
        return baby;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", getVariant());
        output.putInt("ToxinCooldown", toxinCooldownTicks);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setVariant(input.getIntOr("Variant", VARIANT_GOLD));
        toxinCooldownTicks = input.getIntOr("ToxinCooldown", 0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FROG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }
}
