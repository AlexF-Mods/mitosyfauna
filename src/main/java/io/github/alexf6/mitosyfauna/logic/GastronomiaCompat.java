package io.github.alexf6.mitosyfauna.logic;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import io.github.alexf6.mitosyfauna.entity.FolkMerchantEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

/**
 * Integración blanda y opcional con el mod hermano {@code gastronomia}.
 * Nunca introduce una dependencia obligatoria: si {@code gastronomia} no está instalado,
 * los comerciantes usan exclusivamente sus productos locales y Vanilla.
 */
public final class GastronomiaCompat {

    public static final String GASTRONOMIA_MOD_ID = "gastronomia";

    private GastronomiaCompat() {
    }

    public static boolean isAvailable() {
        return MitosYFaunaConfig.get().enableGastronomiaIntegration()
                && FabricLoader.getInstance().isModLoaded(GASTRONOMIA_MOD_ID);
    }

    public static Optional<Item> findItem(String path) {
        if (!isAvailable()) {
            return Optional.empty();
        }
        return BuiltInRegistries.ITEM.getOptional(Identifier.fromNamespaceAndPath(GASTRONOMIA_MOD_ID, path))
                .filter(item -> item != Items.AIR);
    }

    /**
     * Agrega una especificación comercial solo si el ítem de {@code gastronomia} existe en el registro activo.
     */
    public static void addOptionalOfferSpec(
            List<FolkMerchantEntity.OfferSpec> specs,
            Item currency,
            int baseCost,
            boolean applyDiscount,
            String gastronomiaItemId,
            int resultCount,
            int maxUses,
            int xp
    ) {
        findItem(gastronomiaItemId).ifPresent(item ->
                specs.add(new FolkMerchantEntity.OfferSpec(
                        currency, baseCost, applyDiscount, item, resultCount, maxUses, xp
                ))
        );
    }
}
