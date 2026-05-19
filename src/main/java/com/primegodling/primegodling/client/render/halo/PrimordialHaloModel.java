package com.primegodling.primegodling.client.render.halo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class PrimordialHaloModel extends EntityModel<Entity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("primegodling", "primordial_halo"), "main");

    private final ModelPart ring;

    public PrimordialHaloModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.ring = root.getChild("ring");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition ring = root.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        ring.addOrReplaceChild("front",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-5.0F, 0.0F, -1.5F, 10.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, 0.0F, -6.5F));
        ring.addOrReplaceChild("back",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-5.0F, 0.0F, -1.5F, 10.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, 0.0F, 6.5F));
        ring.addOrReplaceChild("left",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 10.0F),
                PartPose.offset(-6.5F, 0.0F, 0.0F));
        ring.addOrReplaceChild("right",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 10.0F),
                PartPose.offset(6.5F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        ring.yRot = ageInTicks * 0.03F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                                int packedLight, int packedOverlay, int color) {
        ring.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
