package com.primegodling.primegodling.client.render.halo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.primegodling.primegodling.PrimeGodling;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class HaloMeshRenderer {

    private static boolean registered = false;
    private static MeshFileData meshData;

    public static void register() {
        if (registered) return;
        registered = true;
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(HaloMeshRenderer::onRenderLevelStage);
    }

    private static MeshFileData loadMesh() {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("primegodling", "models/halo_mesh.bin");
        try (InputStream is = Minecraft.getInstance().getResourceManager().open(loc)) {
            byte[] bytes = is.readAllBytes();
            ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

            byte[] magic = new byte[4];
            buf.get(magic);
            int version = buf.getInt();
            int vertCount = buf.getInt();
            int idxCount = buf.getInt();
            if (magic[0] != 'H' || magic[1] != 'A' || magic[2] != 'L' || magic[3] != 'O') {
                throw new IOException("Bad magic");
            }

            float[] positions = new float[vertCount * 3];
            float[] normals = new float[vertCount * 3];
            int[] indices = new int[idxCount];

            FloatBuffer fb = buf.asFloatBuffer();
            fb.get(positions);
            buf.position(buf.position() + positions.length * 4);
            fb = buf.asFloatBuffer();
            fb.get(normals);
            buf.position(buf.position() + normals.length * 4);
            // skip UVs
            int uvFloats = vertCount * 2;
            buf.position(buf.position() + uvFloats * 4);
            IntBuffer ib = buf.asIntBuffer();
            ib.get(indices);

            PrimeGodling.LOGGER.info("Loaded halo mesh: {} verts, {} tris", vertCount, idxCount / 3);
            return new MeshFileData(positions, normals, indices);
        } catch (IOException e) {
            PrimeGodling.LOGGER.error("Failed to load halo mesh", e);
            return null;
        }
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (meshData == null) {
            meshData = loadMesh();
            if (meshData == null) return;
        }

        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();

        for (Entity entity : mc.level.players()) {
            if (!(entity instanceof AbstractClientPlayer player)) continue;
            if (player.isSpectator()) continue;
            if (player.isInvisible()) continue;

            Vec3 playerPos = player.position();

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate(playerPos.x - camPos.x, playerPos.y - camPos.y + 2.3, playerPos.z - camPos.z);
            poseStack.scale(0.6F, 0.6F, 0.6F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-player.tickCount * 2.0F));

            var pose = poseStack.last();

            float[] pos = meshData.positions;
            float[] norm = meshData.normals;
            int[] idx = meshData.indices;

            RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            var mesh = Tesselator.getInstance().begin(
                    VertexFormat.Mode.TRIANGLES,
                    DefaultVertexFormat.POSITION_COLOR
            );

            for (int i = 0; i < idx.length; i += 3) {
                addPosColor(mesh, pose, pos, norm, idx[i]);
                addPosColor(mesh, pose, pos, norm, idx[i + 1]);
                addPosColor(mesh, pose, pos, norm, idx[i + 2]);
            }

            BufferUploader.drawWithShader(mesh.buildOrThrow());

            poseStack.popPose();
        }
    }

    private static void addPosColor(BufferBuilder buffer, PoseStack.Pose pose,
                                    float[] pos, float[] norm, int vi) {
        float x = pos[vi * 3];
        float y = pos[vi * 3 + 1];
        float z = pos[vi * 3 + 2];
        float ny = norm[vi * 3 + 1];
        float brightness = 0.6f + 0.4f * Math.abs(ny);
        int r2 = Math.min(255, (int)(255 * brightness));
        int g2 = Math.min(255, (int)(200 * brightness));
        int b2 = Math.min(255, (int)(80 * brightness));
        buffer.addVertex(pose, x, y, z).setColor(r2, g2, b2, 255);
    }

    private record MeshFileData(float[] positions, float[] normals, int[] indices) {}
}
