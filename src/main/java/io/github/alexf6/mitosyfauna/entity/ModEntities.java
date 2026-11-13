package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/**
 * Registro centralizado de todas las criaturas, mitos, comerciantes y proyectiles del mod.
 */
public final class ModEntities {

    // Fauna nativa
    public static final EntityType<ChiguiroEntity> CHIGUIRO = register(
            "chiguiro",
            EntityType.Builder.of(ChiguiroEntity::new, MobCategory.CREATURE)
                    .sized(0.85F, 0.75F)
                    .clientTrackingRange(8)
    );

    public static final EntityType<OsoAnteojosEntity> OSO_ANTEOJOS = register(
            "oso_anteojos",
            EntityType.Builder.of(OsoAnteojosEntity::new, MobCategory.CREATURE)
                    .sized(1.25F, 1.35F)
                    .clientTrackingRange(8)
    );

    public static final EntityType<CondorEntity> CONDOR = register(
            "condor",
            EntityType.Builder.of(CondorEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.85F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<RanaDardoEntity> RANA_DARDO = register(
            "rana_dardo",
            EntityType.Builder.of(RanaDardoEntity::new, MobCategory.CREATURE)
                    .sized(0.45F, 0.4F)
                    .clientTrackingRange(8)
    );

    // Mitos y leyendas nocturnos
    public static final EntityType<MohanEntity> MOHAN = register(
            "mohan",
            EntityType.Builder.of(MohanEntity::new, MobCategory.MONSTER)
                    .sized(0.85F, 1.95F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<PatasolaEntity> PATASOLA = register(
            "patasola",
            EntityType.Builder.of(PatasolaEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 1.9F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<LloronaEntity> LLORONA = register(
            "llorona",
            EntityType.Builder.of(LloronaEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 1.95F)
                    .clientTrackingRange(10)
    );

    public static final EntityType<SombreronEntity> SOMBRERON = register(
            "sombreron",
            EntityType.Builder.of(SombreronEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.75F)
                    .clientTrackingRange(10)
    );

    // Comerciantes locales
    public static final EntityType<TenderoEntity> TENDERO = register(
            "tendero",
            EntityType.Builder.of(TenderoEntity::new, MobCategory.CREATURE)
                    .sized(0.65F, 1.95F)
                    .clientTrackingRange(8)
    );

    public static final EntityType<VendedorAmbulanteEntity> VENDEDOR_AMBULANTE = register(
            "vendedor_ambulante",
            EntityType.Builder.of(VendedorAmbulanteEntity::new, MobCategory.CREATURE)
                    .sized(0.65F, 1.95F)
                    .clientTrackingRange(8)
    );

    // Proyectil compartido (chorro de agua y dardo tóxico)
    public static final EntityType<FolkProjectile> FOLK_PROJECTILE = register(
            "folk_projectile",
            EntityType.Builder.<FolkProjectile>of(FolkProjectile::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F)
                    .clientTrackingRange(8)
                    .updateInterval(10)
    );

    private ModEntities() {
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, MitosYFaunaMod.id(name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void init() {
        FabricDefaultAttributeRegistry.register(CHIGUIRO, ChiguiroEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(OSO_ANTEOJOS, OsoAnteojosEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CONDOR, CondorEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RANA_DARDO, RanaDardoEntity.createAttributes());

        FabricDefaultAttributeRegistry.register(MOHAN, MohanEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(PATASOLA, PatasolaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(LLORONA, LloronaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SOMBRERON, SombreronEntity.createAttributes());

        FabricDefaultAttributeRegistry.register(TENDERO, FolkMerchantEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(VENDEDOR_AMBULANTE, FolkMerchantEntity.createAttributes());
    }
}
