package io.github.alexf6.mitosyfauna.entity;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Registra las reglas de colocación ({@link SpawnPlacements}) y distribución por biomas
 * para toda la fauna nativa, mitos nocturnos y comerciantes locales.
 */
public final class ModSpawns {

    private ModSpawns() {
    }

    public static void init() {
        registerPlacements();
        registerBiomes();
    }

    private static void registerPlacements() {
        SpawnPlacements.register(ModEntities.CHIGUIRO, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModSpawns::checkRiverFaunaSpawnRules);
        SpawnPlacements.register(ModEntities.OSO_ANTEOJOS, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.CONDOR, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModSpawns::checkHighlandBirdSpawnRules);
        SpawnPlacements.register(ModEntities.RANA_DARDO, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);

        SpawnPlacements.register(ModEntities.MOHAN, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythBossEntity::checkNocturnalMythSpawnRules);
        SpawnPlacements.register(ModEntities.PATASOLA, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythBossEntity::checkNocturnalMythSpawnRules);
        SpawnPlacements.register(ModEntities.LLORONA, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythBossEntity::checkNocturnalMythSpawnRules);
        SpawnPlacements.register(ModEntities.SOMBRERON, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythBossEntity::checkNocturnalMythSpawnRules);

        SpawnPlacements.register(ModEntities.TENDERO, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModSpawns::checkMerchantSpawnRules);
        SpawnPlacements.register(ModEntities.VENDEDOR_AMBULANTE, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModSpawns::checkMerchantSpawnRules);
    }

    private static void registerBiomes() {
        // Fauna nativa
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_RIVER)
                        .or(BiomeSelectors.tag(BiomeTags.IS_BEACH))
                        .or(BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
                MobCategory.CREATURE, ModEntities.CHIGUIRO, 10, 2, 4
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN)
                        .or(BiomeSelectors.tag(BiomeTags.IS_HILL))
                        .or(BiomeSelectors.includeByKey(Biomes.MEADOW, Biomes.GROVE)),
                MobCategory.CREATURE, ModEntities.OSO_ANTEOJOS, 8, 1, 2
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN),
                MobCategory.CREATURE, ModEntities.CONDOR, 6, 1, 2
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                MobCategory.CREATURE, ModEntities.RANA_DARDO, 12, 2, 4
        );

        // Mitos nocturnos
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_RIVER)
                        .or(BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
                MobCategory.MONSTER, ModEntities.MOHAN, 5, 1, 1
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_FOREST)
                        .or(BiomeSelectors.includeByKey(Biomes.DARK_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.OLD_GROWTH_PINE_TAIGA)),
                MobCategory.MONSTER, ModEntities.PATASOLA, 5, 1, 1
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.tag(BiomeTags.IS_RIVER)
                        .or(BiomeSelectors.includeByKey(Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.DARK_FOREST)),
                MobCategory.MONSTER, ModEntities.LLORONA, 5, 1, 1
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.SAVANNA)
                        .or(BiomeSelectors.tag(BiomeTags.IS_FOREST)),
                MobCategory.MONSTER, ModEntities.SOMBRERON, 5, 1, 1
        );

        // Comerciantes locales
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.MEADOW, Biomes.SAVANNA, Biomes.TAIGA),
                MobCategory.CREATURE, ModEntities.TENDERO, 3, 1, 1
        );
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.MEADOW, Biomes.SAVANNA)
                        .or(BiomeSelectors.tag(BiomeTags.IS_JUNGLE)),
                MobCategory.CREATURE, ModEntities.VENDEDOR_AMBULANTE, 3, 1, 1
        );
    }

    private static boolean checkRiverFaunaSpawnRules(
            EntityType<? extends Mob> type,
            ServerLevelAccessor level,
            EntitySpawnReason reason,
            BlockPos pos,
            RandomSource random
    ) {
        BlockState below = level.getBlockState(pos.below());
        boolean validGround = below.is(Blocks.GRASS_BLOCK) || below.is(Blocks.SAND)
                || below.is(Blocks.MUD) || below.is(Blocks.CLAY) || below.is(Blocks.DIRT);
        return validGround && level.getRawBrightness(pos, 0) > 8 && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }

    private static boolean checkHighlandBirdSpawnRules(
            EntityType<? extends Mob> type,
            ServerLevelAccessor level,
            EntitySpawnReason reason,
            BlockPos pos,
            RandomSource random
    ) {
        return pos.getY() >= 86 && level.canSeeSky(pos) && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }

    private static boolean checkMerchantSpawnRules(
            EntityType<? extends Mob> type,
            ServerLevelAccessor level,
            EntitySpawnReason reason,
            BlockPos pos,
            RandomSource random
    ) {
        BlockState below = level.getBlockState(pos.below());
        boolean validGround = below.is(Blocks.GRASS_BLOCK) || below.is(Blocks.DIRT_PATH)
                || below.is(Blocks.COARSE_DIRT) || below.is(Blocks.SAND);
        return validGround && level.getRawBrightness(pos, 0) > 8 && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }
}
