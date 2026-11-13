package io.github.alexf6.mitosyfauna.client;

import io.github.alexf6.mitosyfauna.MitosYFaunaMod;
import io.github.alexf6.mitosyfauna.client.model.ChiguiroModel;
import io.github.alexf6.mitosyfauna.client.model.ComercianteModel;
import io.github.alexf6.mitosyfauna.client.model.CondorModel;
import io.github.alexf6.mitosyfauna.client.model.LloronaModel;
import io.github.alexf6.mitosyfauna.client.model.MohanModel;
import io.github.alexf6.mitosyfauna.client.model.OsoAnteojosModel;
import io.github.alexf6.mitosyfauna.client.model.PatasolaModel;
import io.github.alexf6.mitosyfauna.client.model.RanaDardoModel;
import io.github.alexf6.mitosyfauna.client.model.SombreronModel;
import io.github.alexf6.mitosyfauna.client.renderer.FolkMobRenderer;
import io.github.alexf6.mitosyfauna.client.renderer.WaterJetRenderer;
import io.github.alexf6.mitosyfauna.entity.ModEntities;
import io.github.alexf6.mitosyfauna.entity.RanaDardoEntity;
import io.github.alexf6.mitosyfauna.entity.VendedorAmbulanteEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.resources.Identifier;

/**
 * Inicializador cliente de Mitos y Fauna: registra capas geométricas 3D y renderizadores de entidades.
 */
public class MitosYFaunaClient implements ClientModInitializer {

    private static final Identifier CHIGUIRO_TEX = MitosYFaunaMod.id("textures/entity/chiguiro.png");
    private static final Identifier OSO_TEX = MitosYFaunaMod.id("textures/entity/oso_anteojos.png");
    private static final Identifier CONDOR_TEX = MitosYFaunaMod.id("textures/entity/condor.png");
    private static final Identifier RANA_GOLD_TEX = MitosYFaunaMod.id("textures/entity/rana_dardo_gold.png");
    private static final Identifier RANA_BLUE_TEX = MitosYFaunaMod.id("textures/entity/rana_dardo_blue.png");
    private static final Identifier RANA_RED_TEX = MitosYFaunaMod.id("textures/entity/rana_dardo_red.png");

    private static final Identifier MOHAN_TEX = MitosYFaunaMod.id("textures/entity/mohan.png");
    private static final Identifier PATASOLA_TEX = MitosYFaunaMod.id("textures/entity/patasola.png");
    private static final Identifier LLORONA_TEX = MitosYFaunaMod.id("textures/entity/llorona.png");
    private static final Identifier SOMBRERON_TEX = MitosYFaunaMod.id("textures/entity/sombreron.png");

    private static final Identifier TENDERO_TEX = MitosYFaunaMod.id("textures/entity/tendero.png");
    private static final Identifier VENDEDOR_FRUTAS_TEX = MitosYFaunaMod.id("textures/entity/vendedor_frutas.png");
    private static final Identifier VENDEDOR_CAFE_TEX = MitosYFaunaMod.id("textures/entity/vendedor_cafe.png");

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(ChiguiroModel.LAYER, ChiguiroModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(OsoAnteojosModel.LAYER, OsoAnteojosModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(CondorModel.LAYER, CondorModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(RanaDardoModel.LAYER, RanaDardoModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(MohanModel.LAYER, MohanModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(PatasolaModel.LAYER, PatasolaModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(LloronaModel.LAYER, LloronaModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(SombreronModel.LAYER, SombreronModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ComercianteModel.LAYER, ComercianteModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(WaterJetRenderer.LAYER, WaterJetRenderer::createLayer);

        EntityRendererRegistry.register(ModEntities.CHIGUIRO, ctx -> new FolkMobRenderer<>(
                ctx, new ChiguiroModel(ctx.bakeLayer(ChiguiroModel.LAYER)), 0.55F,
                state -> CHIGUIRO_TEX,
                (entity, state) -> state.sitting = entity.isInSittingPose()
        ));
        EntityRendererRegistry.register(ModEntities.OSO_ANTEOJOS, ctx -> new FolkMobRenderer<>(
                ctx, new OsoAnteojosModel(ctx.bakeLayer(OsoAnteojosModel.LAYER)), 0.8F, OSO_TEX
        ));
        EntityRendererRegistry.register(ModEntities.CONDOR, ctx -> new FolkMobRenderer<>(
                ctx, new CondorModel(ctx.bakeLayer(CondorModel.LAYER)), 0.55F, CONDOR_TEX
        ));
        EntityRendererRegistry.register(ModEntities.RANA_DARDO, ctx -> new FolkMobRenderer<>(
                ctx, new RanaDardoModel(ctx.bakeLayer(RanaDardoModel.LAYER)), 0.25F,
                state -> switch (state.variant) {
                    case RanaDardoEntity.VARIANT_BLUE -> RANA_BLUE_TEX;
                    case RanaDardoEntity.VARIANT_RED -> RANA_RED_TEX;
                    default -> RANA_GOLD_TEX;
                },
                (entity, state) -> state.variant = entity.getVariant()
        ));

        EntityRendererRegistry.register(ModEntities.MOHAN, ctx -> new FolkMobRenderer<>(
                ctx, new MohanModel(ctx.bakeLayer(MohanModel.LAYER)), 0.55F,
                state -> MOHAN_TEX,
                (entity, state) -> state.holdingStolen = entity.isHoldingStolenItem()
        ));
        EntityRendererRegistry.register(ModEntities.PATASOLA, ctx -> new FolkMobRenderer<>(
                ctx, new PatasolaModel(ctx.bakeLayer(PatasolaModel.LAYER)), 0.45F, PATASOLA_TEX
        ));
        EntityRendererRegistry.register(ModEntities.LLORONA, ctx -> new FolkMobRenderer<>(
                ctx, new LloronaModel(ctx.bakeLayer(LloronaModel.LAYER)), 0.5F, LLORONA_TEX
        ));
        EntityRendererRegistry.register(ModEntities.SOMBRERON, ctx -> new FolkMobRenderer<>(
                ctx, new SombreronModel(ctx.bakeLayer(SombreronModel.LAYER)), 0.55F,
                state -> SOMBRERON_TEX,
                (entity, state) -> state.angry = entity.isAngry()
        ));

        EntityRendererRegistry.register(ModEntities.TENDERO, ctx -> new FolkMobRenderer<>(
                ctx, new ComercianteModel(ctx.bakeLayer(ComercianteModel.LAYER)), 0.5F,
                state -> TENDERO_TEX,
                (entity, state) -> state.isVendor = false
        ));
        EntityRendererRegistry.register(ModEntities.VENDEDOR_AMBULANTE, ctx -> new FolkMobRenderer<>(
                ctx, new ComercianteModel(ctx.bakeLayer(ComercianteModel.LAYER)), 0.5F,
                state -> state.variant == VendedorAmbulanteEntity.VARIANT_CAFE ? VENDEDOR_CAFE_TEX : VENDEDOR_FRUTAS_TEX,
                (entity, state) -> {
                    state.variant = entity.getVariant();
                    state.isVendor = true;
                }
        ));

        EntityRendererRegistry.register(ModEntities.FOLK_PROJECTILE, WaterJetRenderer::new);
    }
}
