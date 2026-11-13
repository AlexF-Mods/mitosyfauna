package io.github.alexf6.mitosyfauna;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MitosYFaunaConfigTest {

    @Test
    void defaultsHaveReasonableValues() {
        MitosYFaunaConfig cfg = MitosYFaunaConfig.defaults();
        assertTrue(cfg.enableMythBossBars());
        assertTrue(cfg.enableHorrorDirectionalSounds());
        assertTrue(cfg.enableMohanItemStealing());
        assertTrue(cfg.enableSombreronMountSpooking());
        assertTrue(cfg.enableGastronomiaIntegration());
        assertEquals(70.0, cfg.mohanMaxHp());
        assertEquals(60.0, cfg.patasolaMaxHp());
        assertEquals(65.0, cfg.lloronaMaxHp());
        assertEquals(80.0, cfg.sombreronMaxHp());
        assertEquals(16, cfg.pesosPerEmerald());
    }

    @Test
    void constructorClampsOutOfRangeAndInvalidNumbers() {
        MitosYFaunaConfig clean = new MitosYFaunaConfig(
                false, true, false, true, false,
                Double.NaN, 1000.0, -5.0, Double.POSITIVE_INFINITY,
                0.5, 99.0, 200
        );
        assertFalse(clean.enableMythBossBars());
        assertEquals(20.0, clean.mohanMaxHp());
        assertEquals(500.0, clean.patasolaMaxHp());
        assertEquals(20.0, clean.lloronaMaxHp());
        assertEquals(20.0, clean.sombreronMaxHp());
        assertEquals(2.0, clean.chiguiroAuraRadius());
        assertEquals(32.0, clean.directionalSoundRadius());
        assertEquals(64, clean.pesosPerEmerald());
    }

    @Test
    void loadOrCreateWritesDefaultsAndMergesPartialJson(@TempDir Path tempDir) throws Exception {
        Path configPath = tempDir.resolve("mitosyfauna.json");
        MitosYFaunaConfig created = MitosYFaunaConfig.loadOrCreate(configPath);
        assertTrue(Files.exists(configPath));
        assertEquals(MitosYFaunaConfig.defaults(), created);

        Files.writeString(configPath, "{\"enableMythBossBars\": false, \"pesosPerEmerald\": 20}");
        MitosYFaunaConfig merged = MitosYFaunaConfig.loadOrCreate(configPath);
        assertFalse(merged.enableMythBossBars());
        assertEquals(20, merged.pesosPerEmerald());
        assertEquals(70.0, merged.mohanMaxHp());

        Files.writeString(configPath, "{not valid json");
        MitosYFaunaConfig recovered = MitosYFaunaConfig.loadOrCreate(configPath);
        assertEquals(MitosYFaunaConfig.defaults(), recovered);
        assertEquals("{not valid json", Files.readString(configPath));
    }
}
