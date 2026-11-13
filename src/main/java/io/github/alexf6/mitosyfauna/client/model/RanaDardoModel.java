package io.github.alexf6.mitosyfauna.client.model;

import io.github.alexf6.mitosyfauna.MitosYFaunaMod;
import io.github.alexf6.mitosyfauna.client.renderer.FolkRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Modelo 3D compacto de la Rana dardo venenosa.
 */
public class RanaDardoModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("rana_dardo"), "main");

    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;

    public RanaDardoModel(ModelPart root) {
        super(root);
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.5F, 6.0F, 3.5F, 7.0F),
                PartPose.offset(0.0F, 22.0F, 0.0F));
        body.addOrReplaceChild("left_eye",
                CubeListBuilder.create().texOffs(0, 11).addBox(1.5F, -4.5F, -3.0F, 1.5F, 1.5F, 1.5F),
                PartPose.ZERO);
        body.addOrReplaceChild("right_eye",
                CubeListBuilder.create().texOffs(0, 11).addBox(-3.0F, -4.5F, -3.0F, 1.5F, 1.5F, 1.5F),
                PartPose.ZERO);

        CubeListBuilder front = CubeListBuilder.create().texOffs(18, 11).addBox(-1.0F, 0.0F, -1.0F, 1.5F, 2.5F, 1.5F);
        root.addOrReplaceChild("front_left_leg", front, PartPose.offset(2.2F, 21.5F, -2.5F));
        root.addOrReplaceChild("front_right_leg", front, PartPose.offset(-2.0F, 21.5F, -2.5F));

        CubeListBuilder hind = CubeListBuilder.create().texOffs(8, 11).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 2.5F, 3.0F);
        root.addOrReplaceChild("left_hind_leg", hind, PartPose.offset(2.5F, 21.5F, 2.0F));
        root.addOrReplaceChild("right_hind_leg", hind, PartPose.offset(-2.5F, 21.5F, 2.0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        float hop = Mth.sin(state.walkAnimationPos * 1.2F) * 0.7F * state.walkAnimationSpeed;
        this.rightHindLeg.xRot = hop;
        this.leftHindLeg.xRot = hop;
    }
}
