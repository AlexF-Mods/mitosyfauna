package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.item.ModItems;
import io.github.alexf6.mitosyfauna.logic.EconomyLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * Clase base compartida para los comerciantes locales del mod ({@link TenderoEntity} y
 * {@link VendedorAmbulanteEntity}). Maneja la interfaz vanilla de comercio, la recompensa de XP
 * y el descuento dinámico por cliente al portar el Sombrero Vueltiao preservando los usos previos.
 */
public abstract class FolkMerchantEntity extends AbstractVillager {

    /**
     * Definición base de una oferta comercial para recalcular descuentos por cliente sin perder usos.
     */
    public record OfferSpec(
            Item currency,
            int baseCost,
            boolean applyDiscount,
            Item result,
            int count,
            int maxUses,
            int xp
    ) {
        public MerchantOffer toOffer(boolean wearingVueltiao, int preservedUses) {
            int finalCost = applyDiscount
                    ? EconomyLogic.computeDiscountedTradeCost(baseCost, wearingVueltiao)
                    : baseCost;
            MerchantOffer offer = new MerchantOffer(
                    new ItemCost(currency, finalCost),
                    Optional.empty(),
                    new ItemStack(result, count),
                    maxUses,
                    xp,
                    0.05F
            );
            int cappedUses = Math.min(preservedUses, maxUses);
            for (int i = 0; i < cappedUses; i++) {
                offer.increaseUses();
            }
            return offer;
        }
    }

    private Boolean lastCustomerHadVueltiao = null;

    protected FolkMerchantEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.45);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 0.75));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    protected abstract List<OfferSpec> buildOfferSpecs();

    @Override
    protected void updateTrades(ServerLevel level) {
        rebuildOffersForCustomer(false);
    }

    private void rebuildOffersForCustomer(boolean wearingVueltiao) {
        List<OfferSpec> specs = buildOfferSpecs();
        MerchantOffers offers = this.getOffers();
        int[] previousUses = new int[specs.size()];
        for (int i = 0; i < Math.min(offers.size(), specs.size()); i++) {
            previousUses[i] = offers.get(i).getUses();
        }
        offers.clear();
        for (int i = 0; i < specs.size(); i++) {
            offers.add(specs.get(i).toOffer(wearingVueltiao, previousUses[i]));
        }
        lastCustomerHadVueltiao = wearingVueltiao;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isAlive() || this.isTrading() || this.isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (!(this.level() instanceof ServerLevel)) {
            return InteractionResult.CONSUME;
        }

        boolean wearingVueltiao = player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SOMBRERO_VUELTIAO);
        if (this.getOffers().isEmpty() || lastCustomerHadVueltiao == null || lastCustomerHadVueltiao != wearingVueltiao) {
            rebuildOffersForCustomer(wearingVueltiao);
        }

        this.setTradingPlayer(player);
        this.openTradingScreen(player, this.getDisplayName(), 1);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int xp = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), getX(), getY() + 0.5, getZ(), xp));
        }
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean isYesSound) {
        return isYesSound ? SoundEvents.VILLAGER_YES : SoundEvents.VILLAGER_NO;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }
}
