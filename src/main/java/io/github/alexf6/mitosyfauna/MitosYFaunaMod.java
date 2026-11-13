package io.github.alexf6.mitosyfauna;

import io.github.alexf6.mitosyfauna.entity.ModEntities;
import io.github.alexf6.mitosyfauna.entity.ModSpawns;
import io.github.alexf6.mitosyfauna.item.ModItems;
import io.github.alexf6.mitosyfauna.item.ModPlayerEffects;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Punto de entrada principal de Mitos y Fauna.
 * Registra configuración, criaturas, mitos nocturnos, comerciantes, ítems y spawns por bioma.
 */
public class MitosYFaunaMod implements ModInitializer {

    public static final String MOD_ID = "mitosyfauna";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MitosYFaunaConfig.load();
        ModEntities.init();
        ModItems.init();
        ModSpawns.init();
        ModPlayerEffects.init();
        LOGGER.info("Mitos y Fauna inicializado correctamente.");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
