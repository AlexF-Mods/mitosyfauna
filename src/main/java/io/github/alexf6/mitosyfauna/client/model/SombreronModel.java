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
 * Modelo 3D de El Sombrerón: silueta alta estilo Enderman con sombrero de ala muy ancha.
 */
public class SombreronModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("sombreron"), "main");

    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public SombreronModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, -10.0F, 0.0F));
        head.addOrReplaceChild("hat_brim",
                CubeListBuilder.create().texOffs(0, 32).addBox(-8.5F, -8.5F, -8.5F, 17.0F, 1.5F, 17.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("hat_crown",
                CubeListBuilder.create().texOffs(0, 50).addBox(-4.5F, -13.0F, -4.5F, 9.0F, 4.5F, 9.0F),
                PartPose.ZERO);

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 15.0F, 4.0F),
                PartPose.offset(0.0F, -10.0F, 0.0F));

        CubeListBuilder arm = CubeListBuilder.create().texOffs(56, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 22.0F, 2.0F);
        root.addOrReplaceChild("right_arm", arm, PartPose.offset(-5.0F, -8.0F, 0.0F));
        root.addOrReplaceChild("left_arm", arm, PartPose.offset(5.0F, -8.0F, 0.0F));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(40, 20).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 19.0F, 2.0F);
        root.addOrReplaceChild("right_leg", leg, PartPose.offset(-2.0F, 5.0F, 0.0F));
        root.addOrReplaceChild("left_leg", leg, PartPose.offset(2.0F, 5.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

        float swing = Mth.cos(state.walkAnimationPos * 0.6662F) * 0.9F * state.walkAnimationSpeed;
        this.rightLeg.xRot = swing;
        this.leftLeg.xRot = -swing;
        float reach = state.angry ? -0.75F : 0.0F;
        this.rightArm.xRot = -swing * 0.7F + reach;
        this.leftArm.xRot = swing * 0.7F + reach;
    }
}
