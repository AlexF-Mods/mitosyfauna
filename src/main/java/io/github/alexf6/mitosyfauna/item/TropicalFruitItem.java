package io.github.alexf6.mitosyfauna.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * Fruta tropical consumible vendida por comerciantes locales, con efecto adicional al terminar de comerla.
 */
public class TropicalFruitItem extends FolkLoreItem {

    private final Consumer<LivingEntity> bonusEffect;

    public TropicalFruitItem(Properties properties, String tooltipKey, Consumer<LivingEntity> bonusEffect) {
        super(properties, tooltipKey, null);
        this.bonusEffect = bonusEffect;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide() && bonusEffect != null) {
            bonusEffect.accept(entity);
        }
        return result;
    }
}
