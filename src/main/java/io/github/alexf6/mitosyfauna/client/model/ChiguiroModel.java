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
 * Modelo 3D del Chigüiro (capibara) con pose sentada cuando está domesticado.
 */
public class ChiguiroModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("chiguiro"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart frontRightLeg;
    private final ModelPart frontLeftLeg;
    private final ModelPart backRightLeg;
    private final ModelPart backLeftLeg;

    public ChiguiroModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
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
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-5.0F, -4.5F, -7.0F, 10.0F, 9.0F, 14.0F),
                PartPose.offset(0.0F, 14.5F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 23).addBox(-3.5F, -4.0F, -7.0F, 7.0F, 6.0F, 8.0F),
                PartPose.offset(0.0F, 12.0F, -6.5F));
        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(34, 0).addBox(2.5F, -5.0F, -2.0F, 1.5F, 2.0F, 2.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(34, 0).addBox(-4.0F, -5.0F, -2.0F, 1.5F, 2.0F, 2.0F),
                PartPose.ZERO);

        CubeListBuilder leg = CubeListBuilder.create().texOffs(40, 20).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F);
        root.addOrReplaceChild("front_right_leg", leg, PartPose.offset(-3.0F, 19.0F, -4.5F));
        root.addOrReplaceChild("front_left_leg", leg, PartPose.offset(3.0F, 19.0F, -4.5F));
        root.addOrReplaceChild("back_right_leg", leg, PartPose.offset(-3.0F, 19.0F, 4.5F));
        root.addOrReplaceChild("back_left_leg", leg, PartPose.offset(3.0F, 19.0F, 4.5F));

        return LayerDefinition.create(mesh, 64, 40);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

        if (state.sitting) {
            this.body.y = 17.5F;
            this.head.y = 15.0F;
            this.frontRightLeg.xRot = -1.2F;
            this.frontLeftLeg.xRot = -1.2F;
            this.backRightLeg.xRot = -1.2F;
            this.backLeftLeg.xRot = -1.2F;
        } else {
            this.body.y = 14.5F;
            this.head.y = 12.0F;
            float swing = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.1F * state.walkAnimationSpeed;
            this.frontRightLeg.xRot = swing;
            this.backLeftLeg.xRot = swing;
            this.frontLeftLeg.xRot = -swing;
            this.backRightLeg.xRot = -swing;
        }
    }
}
