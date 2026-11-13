package io.github.alexf6.mitosyfauna.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.alexf6.mitosyfauna.MitosYFaunaMod;
import io.github.alexf6.mitosyfauna.entity.FolkProjectile;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Renderizador orientado en vuelo para {@link FolkProjectile} (chorro de agua del Mohán y dardo tóxico).
 */
public class WaterJetRenderer extends EntityRenderer<FolkProjectile, WaterJetRenderer.ProjectileState> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(MitosYFaunaMod.id("folk_projectile"), "main");

    private static final Identifier WATER_TEX = MitosYFaunaMod.id("textures/entity/water_jet.png");
    private static final Identifier DART_TEX = MitosYFaunaMod.id("textures/entity/poison_dart_entity.png");

    private final ProjectileModel model;

    public WaterJetRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.15F;
        this.model = new ProjectileModel(context.bakeLayer(LAYER));
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "core",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -3.5F, 3.0F, 3.0F, 7.0F),
                PartPose.ZERO
        );
        return LayerDefinition.create(mesh, 32, 16);
    }

    @Override
    public ProjectileState createRenderState() {
        return new ProjectileState();
    }

    @Override
    public void extractRenderState(FolkProjectile entity, ProjectileState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.kind = entity.getKind();
        state.yRot = entity.getYRot(partialTick);
        state.xRot = entity.getXRot(partialTick);
    }

    @Override
    public void submit(
            ProjectileState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera
    ) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.15F, 0.0F);
        poseStack.rotateDegrees(Axis.YP, 180.0F - state.yRot);
        poseStack.rotateDegrees(Axis.XP, state.xRot);
        Identifier tex = state.kind == FolkProjectile.Kind.WATER_JET ? WATER_TEX : DART_TEX;
        collector.submitModel(
                model,
                state,
                poseStack,
                model.renderType(tex),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                state.outlineColor
        );
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    public static class ProjectileState extends EntityRenderState {
        public FolkProjectile.Kind kind = FolkProjectile.Kind.WATER_JET;
        public float yRot;
        public float xRot;
    }

    private static final class ProjectileModel extends EntityModel<ProjectileState> {
        private ProjectileModel(ModelPart root) {
            super(root);
        }
    }
}
