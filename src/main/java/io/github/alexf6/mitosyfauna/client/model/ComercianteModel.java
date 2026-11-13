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
 * Modelo 3D compartido para comerciantes locales: muestra delantal frontal en el Tendero
 * o sombrero vueltiao y canasto trasero en el Vendedor ambulante ({@code state.flag}).
 */
public class ComercianteModel extends EntityModel<FolkRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(MitosYFaunaMod.id("comerciante"), "main");

    private final ModelPart head;
    private final ModelPart apron;
    private final ModelPart vendorHat;
    private final ModelPart vendorBasket;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ComercianteModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.vendorHat = this.head.getChild("vendor_hat");
        this.apron = root.getChild("apron");
        this.vendorBasket = root.getChild("vendor_basket");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F)
                        .texOffs(24, 0).addBox(-1.0F, -3.0F, -6.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("vendor_hat",
                CubeListBuilder.create()
                        .texOffs(0, 38).addBox(-7.0F, -9.5F, -7.0F, 14.0F, 1.5F, 14.0F)
                        .texOffs(32, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 2.5F, 8.0F),
                PartPose.ZERO);

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 17).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("apron",
                CubeListBuilder.create().texOffs(0, 54).addBox(-3.5F, 1.0F, -3.6F, 7.0F, 10.0F, 0.8F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        root.addOrReplaceChild("vendor_basket",
                CubeListBuilder.create().texOffs(44, 17).addBox(-3.5F, 1.0F, 3.0F, 7.0F, 8.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        CubeListBuilder arm = CubeListBuilder.create().texOffs(44, 29).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F);
        root.addOrReplaceChild("right_arm", arm, PartPose.offset(-6.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_arm", arm, PartPose.offset(6.0F, 2.0F, 0.0F));

        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 17).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F);
        root.addOrReplaceChild("right_leg", leg, PartPose.offset(-2.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg", leg, PartPose.offset(2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;

        boolean isVendor = state.isVendor;
        this.apron.visible = !isVendor;
        this.vendorHat.visible = isVendor;
        this.vendorBasket.visible = isVendor;

        float swing = Mth.cos(state.walkAnimationPos * 0.6662F) * 1.1F * state.walkAnimationSpeed;
        this.rightLeg.xRot = swing;
        this.leftLeg.xRot = -swing;
        this.rightArm.xRot = -swing * 0.7F;
        this.leftArm.xRot = swing * 0.7F;
    }
}
