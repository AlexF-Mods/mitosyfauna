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
 * Modelo 3D de La Patasola: figura espectral selvática con brazos con garras y una sola pierna central.
 */
public class PatasolaModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("patasola"), "main");

    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart singleLeg;

    public PatasolaModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.singleLeg = root.getChild("single_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 16).addBox(-3.5F, 0.0F, -2.0F, 7.0F, 12.0F, 4.0F)
                        .texOffs(0, 32).addBox(-4.0F, 8.0F, -2.5F, 8.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        CubeListBuilder arm = CubeListBuilder.create().texOffs(40, 16).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 13.0F, 3.0F);
        root.addOrReplaceChild("right_arm", arm, PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", arm, PartPose.offset(5.0F, 2.0F, 0.0F));

        // Rasgo distintivo: una única pierna centrada para desplazarse a saltos
        root.addOrReplaceChild("single_leg",
                CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 48);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

        float hop = Mth.sin(state.walkAnimationPos * 0.9F) * 0.8F * state.walkAnimationSpeed;
        this.singleLeg.xRot = hop;
        this.rightArm.xRot = -1.1F + hop * 0.4F;
        this.leftArm.xRot = -1.1F - hop * 0.4F;
    }
}
