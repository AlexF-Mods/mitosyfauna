package io.github.alexf6.mitosyfauna.client.renderer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Renderizador genérico reutilizable para los mobs de Mitos y Fauna.
 * Evita duplicar clases boilerplate manteniendo la extracción segura hacia {@link FolkRenderState}.
 */
public class FolkMobRenderer<T extends Mob, M extends EntityModel<FolkRenderState>>
        extends MobRenderer<T, FolkRenderState, M> {

    private final Function<FolkRenderState, Identifier> textureResolver;
    private final BiConsumer<T, FolkRenderState> extraExtractor;

    public FolkMobRenderer(
            EntityRendererProvider.Context context,
            M model,
            float shadowRadius,
            Identifier singleTexture
    ) {
        this(context, model, shadowRadius, state -> singleTexture, null);
    }

    public FolkMobRenderer(
            EntityRendererProvider.Context context,
            M model,
            float shadowRadius,
            Function<FolkRenderState, Identifier> textureResolver,
            BiConsumer<T, FolkRenderState> extraExtractor
    ) {
        super(context, model, shadowRadius);
        this.textureResolver = textureResolver;
        this.extraExtractor = extraExtractor;
    }

    @Override
    public FolkRenderState createRenderState() {
        return new FolkRenderState();
    }

    @Override
    public void extractRenderState(T entity, FolkRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.variant = 0;
        state.flag = false;
        if (extraExtractor != null) {
            extraExtractor.accept(entity, state);
        }
    }

    @Override
    public Identifier getTextureLocation(FolkRenderState state) {
        return textureResolver.apply(state);
    }
}
