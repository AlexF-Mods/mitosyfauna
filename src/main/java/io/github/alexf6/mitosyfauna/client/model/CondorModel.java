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
 * Modelo 3D del Cóndor de los Andes con alas amplias articuladas y collarín blanco.
 */
public class CondorModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("condor"), "main");

    private final ModelPart head;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public CondorModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.5F, -3.0F, -6.0F, 7.0F, 6.0F, 12.0F)
                        .texOffs(0, 18).addBox(-3.0F, -1.0F, 6.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F));

        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(38, 0).addBox(-2.5F, -3.0F, -4.0F, 5.0F, 4.0F, 5.0F)
                        .texOffs(24, 18).addBox(-1.0F, -1.0F, -7.0F, 2.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, 14.0F, -6.5F));

        root.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(26, 20).addBox(0.0F, -0.5F, -4.0F, 13.0F, 1.0F, 8.0F),
                PartPose.offset(3.5F, 14.0F, -1.0F));

        root.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(26, 20).addBox(-13.0F, -0.5F, -4.0F, 13.0F, 1.0F, 8.0F),
                PartPose.offset(-3.5F, 14.0F, -1.0F));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F);
        root.addOrReplaceChild("left_leg", leg, PartPose.offset(2.0F, 19.0F, 0.5F));
        root.addOrReplaceChild("right_leg", leg, PartPose.offset(-2.0F, 19.0F, 0.5F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

        float flap = Mth.sin(state.ageInTicks * 0.35F) * 0.45F;
        this.leftWing.zRot = -flap;
        this.rightWing.zRot = flap;
    }
}
