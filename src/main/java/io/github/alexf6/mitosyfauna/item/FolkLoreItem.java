package io.github.alexf6.mitosyfauna.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * Ítem reutilizable con descripción traducible automática y acción opcional al usar con clic derecho.
 */
public class FolkLoreItem extends Item {

    @FunctionalInterface
    public interface UseAction {
        InteractionResult onUse(Level level, Player player, InteractionHand hand);
    }

    private final String tooltipKey;
    private final UseAction useAction;

    public FolkLoreItem(Properties properties, String tooltipKey) {
        this(properties, tooltipKey, null);
    }

    public FolkLoreItem(Properties properties, String tooltipKey, UseAction useAction) {
        super(properties);
        this.tooltipKey = tooltipKey;
        this.useAction = useAction;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (useAction != null) {
            return useAction.onUse(level, player, hand);
        }
        return super.use(level, player, hand);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> consumer,
            TooltipFlag flag
    ) {
        super.appendHoverText(stack, context, display, consumer, flag);
        if (tooltipKey != null && !tooltipKey.isEmpty()) {
            consumer.accept(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
