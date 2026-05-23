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

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("primegodling", "textures/entity/halo_ring.png");

    private static final int SEGMENTS = 24;
    private static final float RADIUS = 11.0F;
    private static final float SEG_W = 2.0F;
    private static final float SEG_H = 3.0F;
    private static final float SEG_D = 3.0F;

    private final ModelPart ring;

    public PrimordialHaloModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.ring = root.getChild("ring");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition ring = root.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        for (int i = 0; i < SEGMENTS; i++) {
            double angle = i * Math.PI * 2 / SEGMENTS;
            float x = (float) (Math.sin(angle) * RADIUS);
            float z = (float) (Math.cos(angle) * RADIUS);
            ring.addOrReplaceChild("seg" + i,
                    CubeListBuilder.create().texOffs(0, 0)
                            .addBox(-SEG_W / 2, -SEG_H / 2, -SEG_D / 2, SEG_W, SEG_H, SEG_D),
                    PartPose.offsetAndRotation(x, 0.0F, z, 0.0F, (float) angle, 0.0F));
        }

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
