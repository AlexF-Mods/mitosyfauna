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
 * Modelo 3D de La Llorona: aparición espectral con túnica flotante, velo y brazos extendidos.
 */
public class LloronaModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("llorona"), "main");

    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public LloronaModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("veil",
                CubeListBuilder.create().texOffs(32, 0).addBox(-4.5F, -8.2F, -4.2F, 9.0F, 12.0F, 6.0F),
                PartPose.ZERO);

        root.addOrReplaceChild("gown",
                CubeListBuilder.create().texOffs(0, 18).addBox(-4.5F, 0.0F, -3.0F, 9.0F, 22.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        CubeListBuilder arm = CubeListBuilder.create().texOffs(32, 18).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 12.0F, 3.0F);
        root.addOrReplaceChild("right_arm", arm, PartPose.offset(-5.5F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", arm, PartPose.offset(5.5F, 2.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 48);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

        float sway = Mth.sin(state.ageInTicks * 0.12F) * 0.15F;
        this.rightArm.xRot = -1.35F + sway;
        this.leftArm.xRot = -1.35F - sway;
    }
}
