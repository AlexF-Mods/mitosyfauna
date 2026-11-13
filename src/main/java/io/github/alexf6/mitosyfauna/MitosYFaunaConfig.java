package io.github.alexf6.mitosyfauna;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuración persistente de {@code mitosyfauna} guardada en {@code config/mitosyfauna.json}.
 * Valida y acota los valores directamente en el constructor canónico del registro.
 */
public record MitosYFaunaConfig(
        boolean enableMythBossBars,
        boolean enableHorrorDirectionalSounds,
        boolean enableMohanItemStealing,
        boolean enableSombreronMountSpooking,
        boolean enableGastronomiaIntegration,
        double mohanMaxHp,
        double patasolaMaxHp,
        double lloronaMaxHp,
        double sombreronMaxHp,
        double chiguiroAuraRadius,
        double directionalSoundRadius,
        int pesosPerEmerald
) {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = MitosYFaunaMod.MOD_ID + ".json";

    public MitosYFaunaConfig {
        mohanMaxHp = clamp(mohanMaxHp, 20.0, 500.0);
        patasolaMaxHp = clamp(patasolaMaxHp, 20.0, 500.0);
        lloronaMaxHp = clamp(lloronaMaxHp, 20.0, 500.0);
        sombreronMaxHp = clamp(sombreronMaxHp, 20.0, 500.0);
        chiguiroAuraRadius = clamp(chiguiroAuraRadius, 2.0, 24.0);
        directionalSoundRadius = clamp(directionalSoundRadius, 4.0, 32.0);
        pesosPerEmerald = Math.max(1, Math.min(64, pesosPerEmerald));
    }

    public static MitosYFaunaConfig defaults() {
        return new MitosYFaunaConfig(
                true,
                true,
                true,
                true,
                true,
                70.0,
                60.0,
                65.0,
                80.0,
                6.0,
                14.0,
                16
        );
    }

    private static double clamp(double value, double min, double max) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    public static MitosYFaunaConfig get() {
        return Holder.INSTANCE;
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        Holder.INSTANCE = loadOrCreate(configPath);
    }

    static MitosYFaunaConfig loadOrCreate(Path path) {
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                Raw raw = GSON.fromJson(reader, Raw.class);
                if (raw != null) {
                    MitosYFaunaConfig merged = raw.toConfig(defaults());
                    write(path, merged);
                    return merged;
                }
                MitosYFaunaMod.LOGGER.warn("Archivo de configuración vacío en {}; usando valores por defecto.", path);
                return defaults();
            } catch (Exception e) {
                MitosYFaunaMod.LOGGER.warn(
                        "No se pudo leer {}; usando valores por defecto sin sobrescribir el archivo.", path, e
                );
                return defaults();
            }
        }
        MitosYFaunaConfig defaults = defaults();
        write(path, defaults);
        return defaults;
    }

    private static void write(Path path, MitosYFaunaConfig config) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            MitosYFaunaMod.LOGGER.warn("No se pudo guardar {}", path, e);
        }
    }

    private static final class Holder {
        private static volatile MitosYFaunaConfig INSTANCE = defaults();
    }

    private static final class Raw {
        Boolean enableMythBossBars;
        Boolean enableHorrorDirectionalSounds;
        Boolean enableMohanItemStealing;
        Boolean enableSombreronMountSpooking;
        Boolean enableGastronomiaIntegration;
        Double mohanMaxHp;
        Double patasolaMaxHp;
        Double lloronaMaxHp;
        Double sombreronMaxHp;
        Double chiguiroAuraRadius;
        Double directionalSoundRadius;
        Integer pesosPerEmerald;

        MitosYFaunaConfig toConfig(MitosYFaunaConfig d) {
            return new MitosYFaunaConfig(
                    enableMythBossBars != null ? enableMythBossBars : d.enableMythBossBars,
                    enableHorrorDirectionalSounds != null ? enableHorrorDirectionalSounds : d.enableHorrorDirectionalSounds,
                    enableMohanItemStealing != null ? enableMohanItemStealing : d.enableMohanItemStealing,
                    enableSombreronMountSpooking != null ? enableSombreronMountSpooking : d.enableSombreronMountSpooking,
                    enableGastronomiaIntegration != null ? enableGastronomiaIntegration : d.enableGastronomiaIntegration,
                    mohanMaxHp != null ? mohanMaxHp : d.mohanMaxHp,
                    patasolaMaxHp != null ? patasolaMaxHp : d.patasolaMaxHp,
                    lloronaMaxHp != null ? lloronaMaxHp : d.lloronaMaxHp,
                    sombreronMaxHp != null ? sombreronMaxHp : d.sombreronMaxHp,
                    chiguiroAuraRadius != null ? chiguiroAuraRadius : d.chiguiroAuraRadius,
                    directionalSoundRadius != null ? directionalSoundRadius : d.directionalSoundRadius,
                    pesosPerEmerald != null ? pesosPerEmerald : d.pesosPerEmerald
            );
        }
    }
}
