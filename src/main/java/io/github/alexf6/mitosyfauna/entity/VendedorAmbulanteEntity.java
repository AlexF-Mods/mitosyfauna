package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.item.ModItems;
import io.github.alexf6.mitosyfauna.logic.GastronomiaCompat;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * Vendedor ambulante con dos variantes: frutas tropicales ({@link #VARIANT_FRUTAS})
 * y café/artesanías ({@link #VARIANT_CAFE}). Acepta tanto pesos colombianos como esmeraldas
 * y funciona completamente de forma independiente sin requerir {@code gastronomia}.
 */
public class VendedorAmbulanteEntity extends FolkMerchantEntity {

    public static final int VARIANT_FRUTAS = 0;
    public static final int VARIANT_CAFE = 1;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(VendedorAmbulanteEntity.class, EntityDataSerializers.INT);

    public VendedorAmbulanteEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    public int getVariant() {
        return Math.floorMod(this.entityData.get(DATA_VARIANT), 2);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, Math.floorMod(variant, 2));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, VARIANT_FRUTAS);
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            SpawnGroupData spawnData
    ) {
        setVariant(level.getRandom().nextInt(2));
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, spawnData);
        if (level instanceof ServerLevel serverLevel) {
            this.updateTrades(serverLevel);
        }
        return data;
    }

    @Override
    protected List<OfferSpec> buildOfferSpecs() {
        List<OfferSpec> list = new ArrayList<>();

        if (getVariant() == VARIANT_FRUTAS) {
            list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 5, true, ModItems.MANGO_BICHE, 3, 16, 3));
            list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 6, true, ModItems.LULO, 3, 16, 3));
            list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 7, true, ModItems.MARACUYA, 3, 16, 4));
            list.add(new OfferSpec(Items.EMERALD, 1, true, ModItems.MANGO_BICHE, 6, 12, 3));
            list.add(new OfferSpec(Items.EMERALD, 2, true, ModItems.MARACUYA, 8, 12, 4));
        } else {
            // Café campesino siempre disponible en modo independiente
            list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 6, true, ModItems.CAFE_CAMPESINO, 2, 16, 4));
            list.add(new OfferSpec(Items.EMERALD, 1, true, ModItems.CAFE_CAMPESINO, 4, 12, 4));
            list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 6, true, Items.COCOA_BEANS, 8, 16, 3));
            list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 8, true, Items.SUGAR, 8, 16, 2));
            list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 10, true, ModItems.LULO, 4, 12, 3));

            GastronomiaCompat.addOptionalOfferSpec(list, ModItems.PESO_COLOMBIANO, 7, true, "tinto", 2, 16, 4);
            GastronomiaCompat.addOptionalOfferSpec(list, ModItems.PESO_COLOMBIANO, 12, true, "empanada_aji", 2, 12, 5);
            GastronomiaCompat.addOptionalOfferSpec(list, Items.EMERALD, 2, true, "roasted_coffee_beans", 8, 12, 4);
        }

        // Artesanía exclusiva del vendedor ambulante
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 24, true, ModItems.SOMBRERO_VUELTIAO, 1, 4, 8));
        list.add(new OfferSpec(Items.EMERALD, 4, true, ModItems.SOMBRERO_VUELTIAO, 1, 4, 8));
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 14, true, ModItems.CONDOR_FEATHER, 2, 8, 4));

        return list;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", getVariant());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setVariant(input.getIntOr("Variant", VARIANT_FRUTAS));
    }
}
