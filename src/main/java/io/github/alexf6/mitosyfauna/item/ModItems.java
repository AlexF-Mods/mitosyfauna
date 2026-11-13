package io.github.alexf6.mitosyfauna.item;

import io.github.alexf6.mitosyfauna.MitosYFaunaMod;
import io.github.alexf6.mitosyfauna.entity.ModEntities;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Registro de todos los ítems, frutas locales, bebida de café campesino, prendas culturales,
 * reliquias de mitos y huevos generadores de {@code mitosyfauna}.
 */
public final class ModItems {

    private static final ResourceKey<EquipmentAsset> SOMBRERON_HAT_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, MitosYFaunaMod.id("sombreron_hat"));
    private static final ResourceKey<EquipmentAsset> SOMBRERO_VUELTIAO_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, MitosYFaunaMod.id("sombrero_vueltiao"));
    private static final ResourceKey<EquipmentAsset> RUANA_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, MitosYFaunaMod.id("ruana"));

    // Economía y materiales de fauna
    public static final Item PESO_COLOMBIANO = registerSimple("peso_colombiano");
    public static final Item CONDOR_FEATHER = register("condor_feather",
            p -> new FolkLoreItem(p, "item.mitosyfauna.condor_feather.desc", ItemAbilities::useCondorFeather));
    public static final Item DART_TOXIN = registerSimple("dart_toxin");
    public static final Item POISON_DART = register("poison_dart",
            p -> new FolkLoreItem(p.stacksTo(16), "item.mitosyfauna.poison_dart.desc", ItemAbilities::usePoisonDart));

    // Frutas tropicales locales y bebida de café propio
    public static final Item MANGO_BICHE = registerConsumable("mango_biche", 4, 0.5F, false, null,
            entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 160, 0));
                entity.removeEffect(MobEffects.POISON);
            });
    public static final Item LULO = registerConsumable("lulo", 4, 0.5F, false, null,
            entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.HASTE, 240, 0));
                entity.setAirSupply(Math.min(entity.getMaxAirSupply(), entity.getAirSupply() + 90));
            });
    public static final Item MARACUYA = registerConsumable("maracuya", 5, 0.6F, false, null,
            entity -> entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0)));
    public static final Item CAFE_CAMPESINO = registerConsumable("cafe_campesino", 2, 0.3F, true, Items.GLASS_BOTTLE,
            entity -> entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 900, 0)));

    // Reliquias de los mitos nocturnos
    public static final Item MOHAN_AMULET = register("mohan_amulet",
            p -> new FolkLoreItem(p.stacksTo(1), "item.mitosyfauna.mohan_amulet.desc", ItemAbilities::useMohanAmulet));
    public static final Item PATASOLA_CLAW = register("patasola_claw",
            p -> new FolkLoreItem(p.stacksTo(1), "item.mitosyfauna.patasola_claw.desc", ItemAbilities::usePatasolaClaw));
    public static final Item LLORONA_TEAR = register("llorona_tear",
            p -> new FolkLoreItem(p.stacksTo(1), "item.mitosyfauna.llorona_tear.desc", ItemAbilities::useLloronaTear));

    // Prendas culturales equipables
    public static final Item SOMBRERON_HAT = registerEquippable(
            "sombreron_hat", EquipmentSlot.HEAD, SOMBRERON_HAT_ASSET, 280);
    public static final Item SOMBRERO_VUELTIAO = registerEquippable(
            "sombrero_vueltiao", EquipmentSlot.HEAD, SOMBRERO_VUELTIAO_ASSET, 220);
    public static final Item RUANA = registerEquippable(
            "ruana", EquipmentSlot.CHEST, RUANA_ASSET, 320);

    // Huevos generadores
    public static final Item CHIGUIRO_SPAWN_EGG = registerSpawnEgg("chiguiro_spawn_egg", ModEntities.CHIGUIRO);
    public static final Item OSO_ANTEOJOS_SPAWN_EGG = registerSpawnEgg("oso_anteojos_spawn_egg", ModEntities.OSO_ANTEOJOS);
    public static final Item CONDOR_SPAWN_EGG = registerSpawnEgg("condor_spawn_egg", ModEntities.CONDOR);
    public static final Item RANA_DARDO_SPAWN_EGG = registerSpawnEgg("rana_dardo_spawn_egg", ModEntities.RANA_DARDO);
    public static final Item MOHAN_SPAWN_EGG = registerSpawnEgg("mohan_spawn_egg", ModEntities.MOHAN);
    public static final Item PATASOLA_SPAWN_EGG = registerSpawnEgg("patasola_spawn_egg", ModEntities.PATASOLA);
    public static final Item LLORONA_SPAWN_EGG = registerSpawnEgg("llorona_spawn_egg", ModEntities.LLORONA);
    public static final Item SOMBRERON_SPAWN_EGG = registerSpawnEgg("sombreron_spawn_egg", ModEntities.SOMBRERON);
    public static final Item TENDERO_SPAWN_EGG = registerSpawnEgg("tendero_spawn_egg", ModEntities.TENDERO);
    public static final Item VENDEDOR_AMBULANTE_SPAWN_EGG =
            registerSpawnEgg("vendedor_ambulante_spawn_egg", ModEntities.VENDEDOR_AMBULANTE);

    public static final ResourceKey<CreativeModeTab> MITOSYFAUNA_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            MitosYFaunaMod.id("mitosyfauna")
    );

    private static final List<Item> FOOD_ITEMS = List.of(MANGO_BICHE, LULO, MARACUYA, CAFE_CAMPESINO);
    private static final List<Item> GEAR_ITEMS = List.of(SOMBRERO_VUELTIAO, RUANA, SOMBRERON_HAT);
    private static final List<Item> MATERIAL_AND_RELIC_ITEMS = List.of(
            PESO_COLOMBIANO, CONDOR_FEATHER, DART_TOXIN, POISON_DART,
            MOHAN_AMULET, PATASOLA_CLAW, LLORONA_TEAR
    );
    private static final List<Item> SPAWN_EGG_ITEMS = List.of(
            CHIGUIRO_SPAWN_EGG, OSO_ANTEOJOS_SPAWN_EGG, CONDOR_SPAWN_EGG, RANA_DARDO_SPAWN_EGG,
            MOHAN_SPAWN_EGG, PATASOLA_SPAWN_EGG, LLORONA_SPAWN_EGG, SOMBRERON_SPAWN_EGG,
            TENDERO_SPAWN_EGG, VENDEDOR_AMBULANTE_SPAWN_EGG
    );

    private static final List<Item> ALL_ITEMS = Stream.of(
            MATERIAL_AND_RELIC_ITEMS.stream(),
            FOOD_ITEMS.stream(),
            GEAR_ITEMS.stream(),
            SPAWN_EGG_ITEMS.stream()
    ).flatMap(s -> s).toList();

    private ModItems() {
    }

    private static Item registerSimple(String name) {
        return register(name, p -> new FolkLoreItem(p, "item.mitosyfauna." + name + ".desc"));
    }

    private static Item registerConsumable(
            String name,
            int nutrition,
            float saturation,
            boolean drink,
            Item remainder,
            Consumer<LivingEntity> effect
    ) {
        return register(name, props -> {
            FoodProperties food = new FoodProperties.Builder()
                    .nutrition(nutrition)
                    .saturationModifier(saturation)
                    .alwaysEdible()
                    .build();
            Consumable consumable = Consumable.builder()
                    .consumeSeconds(1.2F)
                    .animation(drink ? ItemUseAnimation.DRINK : ItemUseAnimation.EAT)
                    .sound(drink ? SoundEvents.GENERIC_DRINK : SoundEvents.GENERIC_EAT)
                    .hasConsumeParticles(!drink)
                    .build();
            Item.Properties configured = props.food(food, consumable);
            if (remainder != null) {
                configured = configured.stacksTo(16).usingConvertsTo(remainder);
            }
            return new TropicalFruitItem(configured, "item.mitosyfauna." + name + ".desc", effect);
        });
    }

    private static Item registerEquippable(
            String name,
            EquipmentSlot slot,
            ResourceKey<EquipmentAsset> assetKey,
            int durability
    ) {
        return register(name, props -> new FolkLoreItem(
                props.stacksTo(1)
                        .durability(durability)
                        .component(
                                DataComponents.EQUIPPABLE,
                                Equippable.builder(slot)
                                        .setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
                                        .setAsset(assetKey)
                                        .build()
                        ),
                "item.mitosyfauna." + name + ".desc"
        ));
    }

    private static Item registerSpawnEgg(String name, EntityType<? extends Mob> entityType) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, MitosYFaunaMod.id(name));
        return Registry.register(
                BuiltInRegistries.ITEM,
                key,
                new SpawnEggItem(new Item.Properties().spawnEgg(entityType).setId(key))
        );
    }

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, MitosYFaunaMod.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                MITOSYFAUNA_TAB_KEY,
                FabricCreativeModeTab.builder()
                        .icon(() -> new ItemStack(SOMBRERO_VUELTIAO))
                        .title(Component.translatable("itemGroup.mitosyfauna.mitosyfauna"))
                        .displayItems((params, output) -> ALL_ITEMS.forEach(output::accept))
                        .build()
        );

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS)
                .register(tab -> FOOD_ITEMS.forEach(tab::accept));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register(tab -> {
                    GEAR_ITEMS.forEach(tab::accept);
                    tab.accept(POISON_DART);
                });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(tab -> MATERIAL_AND_RELIC_ITEMS.forEach(tab::accept));
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS)
                .register(tab -> SPAWN_EGG_ITEMS.forEach(tab::accept));
    }
}
