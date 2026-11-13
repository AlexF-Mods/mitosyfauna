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
 * Modelo 3D del Oso de anteojos andino.
 */
public class OsoAnteojosModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("oso_anteojos"), "main");

    private final ModelPart head;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;

    public OsoAnteojosModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.frontRightLeg = root.getChild("front_right_leg");
        this.frontLeftLeg = root.getChild("front_left_leg");
        this.backRightLeg = root.getChild("back_right_leg");
        this.backLeftLeg = root.getChild("back_left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -6.0F, -10.0F, 14.0F, 12.0F, 20.0F),
                PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.5F, -4.5F, -7.0F, 9.0F, 8.0F, 8.0F)
                        .texOffs(34, 32).addBox(-2.5F, 0.0F, -10.0F, 5.0F, 3.5F, 3.0F),
                PartPose.offset(0.0F, 10.0F, -10.0F));
        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(48, 0).addBox(3.0F, -6.0F, -3.0F, 2.5F, 2.5F, 2.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(48, 0).addBox(-5.5F, -6.0F, -3.0F, 2.5F, 2.5F, 2.0F),
                PartPose.ZERO);

        CubeListBuilder leg = CubeListBuilder.create().texOffs(48, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F);
        root.addOrReplaceChild("front_right_leg", leg, PartPose.offset(-4.0F, 16.0F, -6.5F));
        root.addOrReplaceChild("front_left_leg", leg, PartPose.offset(4.0F, 16.0F, -6.5F));
        root.addOrReplaceChild("back_right_leg", leg, PartPose.offset(-4.0F, 16.0F, 6.5F));
        root.addOrReplaceChild("back_left_leg", leg, PartPose.offset(4.0F, 16.0F, 6.5F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

        float swing = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.1F * state.walkAnimationSpeed;
        this.frontRightLeg.xRot = swing;
        this.backLeftLeg.xRot = swing;
        this.frontLeftLeg.xRot = -swing;
        this.backRightLeg.xRot = -swing;
    }
}
