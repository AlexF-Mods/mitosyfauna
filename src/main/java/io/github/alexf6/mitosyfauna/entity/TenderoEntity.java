package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import io.github.alexf6.mitosyfauna.item.ModItems;
import io.github.alexf6.mitosyfauna.logic.EconomyLogic;
import io.github.alexf6.mitosyfauna.logic.GastronomiaCompat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Tendero de barrio con delantal: cambia esmeraldas por pesos colombianos (y viceversa),
 * vende víveres cotidianos, la Ruana tradicional e ítems opcionales de {@code gastronomia}.
 */
public class TenderoEntity extends FolkMerchantEntity {

    public TenderoEntity(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected List<OfferSpec> buildOfferSpecs() {
        List<OfferSpec> list = new ArrayList<>();
        int pesosRate = MitosYFaunaConfig.get().pesosPerEmerald();
        int pesosGain = EconomyLogic.emeraldsToPesos(1, pesosRate);

        // Cambio de divisas local (sin descuento para evitar arbitraje)
        list.add(new OfferSpec(Items.EMERALD, 1, false, ModItems.PESO_COLOMBIANO, pesosGain, 24, 2));
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, pesosRate, false, Items.EMERALD, 1, 24, 2));

        // Compra de cosechas locales con pesos colombianos
        list.add(new OfferSpec(Items.WHEAT, 16, false, ModItems.PESO_COLOMBIANO, 8, 16, 2));
        list.add(new OfferSpec(Items.SUGAR_CANE, 16, false, ModItems.PESO_COLOMBIANO, 8, 16, 2));

        // Víveres de la tienda de barrio
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 4, true, Items.BREAD, 4, 16, 2));
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 6, true, Items.COOKED_BEEF, 3, 12, 3));
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 8, true, Items.HONEY_BOTTLE, 2, 12, 3));
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 10, true, Items.COCOA_BEANS, 6, 12, 3));

        // Prenda exclusiva del tendero
        list.add(new OfferSpec(ModItems.PESO_COLOMBIANO, 28, true, ModItems.RUANA, 1, 4, 8));
        list.add(new OfferSpec(Items.EMERALD, 5, true, ModItems.RUANA, 1, 4, 8));

        // Integración opcional con el mod gastronomia
        GastronomiaCompat.addOptionalOfferSpec(list, ModItems.PESO_COLOMBIANO, 8, true, "tinto", 2, 12, 3);
        GastronomiaCompat.addOptionalOfferSpec(list, ModItems.PESO_COLOMBIANO, 10, true, "empanada", 2, 12, 4);
        GastronomiaCompat.addOptionalOfferSpec(list, ModItems.PESO_COLOMBIANO, 6, true, "chocoramo", 2, 12, 3);
        GastronomiaCompat.addOptionalOfferSpec(list, ModItems.PESO_COLOMBIANO, 5, true, "bon_bon_bum", 3, 16, 2);

        return list;
    }
}
