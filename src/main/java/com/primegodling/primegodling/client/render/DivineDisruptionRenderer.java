package com.primegodling.primegodling.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.SkillRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;
import java.util.Optional;

public class DivineDisruptionRenderer {

    private static final float CASTER_HEIGHT_OFFSET = 1.5f;
    private static final int MAX_ORBS_PER_TENDRIL = 12;
    private static final int GROUND_RING_SEGMENTS = 48;
    private static final int TENDRIL_SEGMENTS = 16;

    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        registered = true;
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(DivineDisruptionRenderer::onRenderLevelStage);
    }

    private static boolean isDivineDisruptionActive(LocalPlayer player) {
        Skills skills = SkillAPI.getSkillsFrom(player);
        Optional<ManasSkillInstance> opt = skills.getSkill(SkillRegistry.DIVINE_DISRUPTION);
        if (opt.isEmpty()) return false;
        ManasSkillInstance instance = opt.get();
        return instance.getOrCreateTag().getBoolean("divine_disruption_active");
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;

        if (!isDivineDisruptionActive(player)) return;

        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        DeltaTracker delta = event.getPartialTick();
        float partialTick = delta.getGameTimeDeltaPartialTick(true);
        long gameTime = level.getGameTime();
        float time = (gameTime + partialTick) * 0.05f;

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(SkillConfig.COMMON.divineDisruptionAoeRadius.get()),
                e -> e != player && e.isAlive());

        Vec3 casterPos = player.getPosition(partialTick).add(0, CASTER_HEIGHT_OFFSET, 0);

        // === PASS 1: Ground indicator ring (depth-tested, subtle) ===
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(() -> GameRenderer.getPositionColorShader());

        renderGroundRing(casterPos, camPos, time);

        // === PASS 2: Tendrils and orbs (additive glow, no depth) ===
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE);
        RenderSystem.disableDepthTest();

        for (LivingEntity target : targets) {
            Vec3 targetPos = target.getPosition(partialTick).add(0, target.getBbHeight() * 0.5f, 0);
            renderTendril(casterPos, targetPos, camPos, time, partialTick);
        }

        // Pulsing energy sphere around caster
        renderCasterAura(casterPos, camPos, time);

        // === PASS 3: Shockwave ring (additive, expanding) ===
        renderShockwaveRing(casterPos, camPos, time);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void renderGroundRing(Vec3 center, Vec3 camPos, float time) {
        double y = center.y - CASTER_HEIGHT_OFFSET + 0.05;
        float pulse = 0.4f + 0.2f * (float) Math.sin(time * 3.0);
        int r = 60, g = 100, b = 180;
        int a = (int) (pulse * 80);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINE_STRIP,
                DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i <= GROUND_RING_SEGMENTS; i++) {
            double angle = (i / (double) GROUND_RING_SEGMENTS) * Math.PI * 2;
            double px = center.x + Math.cos(angle) * SkillConfig.COMMON.divineDisruptionAoeRadius.get() - camPos.x;
            double py = y - camPos.y;
            double pz = center.z + Math.sin(angle) * SkillConfig.COMMON.divineDisruptionAoeRadius.get() - camPos.z;
            buffer.addVertex(new PoseStack().last(), (float) px, (float) py, (float) pz)
                    .setColor(r, g, b, a);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // Inner pulsing ring
        double innerRadius = SkillConfig.COMMON.divineDisruptionAoeRadius.get() * (0.3 + 0.1 * Math.sin(time * 5.0));
        BufferBuilder innerBuffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINE_STRIP,
                DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i <= GROUND_RING_SEGMENTS; i++) {
            double angle = (i / (double) GROUND_RING_SEGMENTS) * Math.PI * 2;
            double px = center.x + Math.cos(angle) * innerRadius - camPos.x;
            double py = y - camPos.y;
            double pz = center.z + Math.sin(angle) * innerRadius - camPos.z;
            innerBuffer.addVertex(new PoseStack().last(), (float) px, (float) py, (float) pz)
                    .setColor(80, 140, 255, (int) (pulse * 120));
        }

        BufferUploader.drawWithShader(innerBuffer.buildOrThrow());
    }

    private static void renderTendril(Vec3 from, Vec3 to, Vec3 camPos, float time, float partialTick) {
        // Render a smooth curved tendril from caster to target
        for (int seg = 0; seg < TENDRIL_SEGMENTS; seg++) {
            float t0 = (float) seg / TENDRIL_SEGMENTS;
            float t1 = (float) (seg + 1) / TENDRIL_SEGMENTS;

            Vec3 p0 = getTendrilPoint(from, to, t0, time);
            Vec3 p1 = getTendrilPoint(from, to, t1, time);

            Vec3 rp0 = p0.subtract(camPos);
            Vec3 rp1 = p1.subtract(camPos);

            float alpha0 = getTendrilAlpha(t0, time);
            float alpha1 = getTendrilAlpha(t1, time);
            float width0 = getTendrilWidth(t0, time);
            float width1 = getTendrilWidth(t1, time);

            renderTendrilSegment(rp0, rp1, width0, width1, alpha0, alpha1, time);
        }

        // Floating orbs along the tendril
        renderTendrilOrbs(from, to, camPos, time);
    }

    private static Vec3 getTendrilPoint(Vec3 from, Vec3 to, float t, float time) {
        Vec3 base = from.lerp(to, t);

        // Spiral displacement
        double spiralAngle = t * Math.PI * 4 + time * 2.0;
        double spiralRadius = Math.sin(t * Math.PI) * 0.4;
        double waveX = Math.cos(spiralAngle) * spiralRadius;
        double waveZ = Math.sin(spiralAngle) * spiralRadius;

        // Vertical wave
        double waveY = Math.sin(t * Math.PI * 2 + time * 1.5) * 0.15;

        return base.add(waveX, waveY, waveZ);
    }

    private static float getTendrilAlpha(float t, float time) {
        float fadeIn = Math.min(1.0f, t * 5.0f);
        float fadeOut = Math.min(1.0f, (1.0f - t) * 5.0f);
        float pulse = 0.7f + 0.3f * (float) Math.sin(time * 4.0 + t * 6.0);
        return fadeIn * fadeOut * pulse * 0.85f;
    }

    private static float getTendrilWidth(float t, float time) {
        float baseWidth = (float) Math.sin(t * Math.PI) * 0.12f;
        float pulse = 1.0f + 0.3f * (float) Math.sin(time * 3.0 + t * 4.0);
        return baseWidth * pulse;
    }

    private static void renderTendrilSegment(Vec3 p0, Vec3 p1, float w0, float w1,
            float a0, float a1, float time) {
        int r = 80, g = 140, b = 255;

        // Calculate perpendicular direction for width
        Vec3 dir = p1.subtract(p0);
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = dir.cross(up).normalize();
        if (right.lengthSqr() < 0.001) {
            right = new Vec3(1, 0, 0);
        }

        PoseStack pose = new PoseStack();
        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR);

        // Quad for this segment
        Vec3 lp0 = p0.subtract(right.scale(w0));
        Vec3 rp0 = p0.add(right.scale(w0));
        Vec3 lp1 = p1.subtract(right.scale(w1));
        Vec3 rp1 = p1.add(right.scale(w1));

        int a0i = (int) (a0 * 255);
        int a1i = (int) (a1 * 255);

        buffer.addVertex(pose.last(), (float) lp0.x, (float) lp0.y, (float) lp0.z)
                .setColor(r, g, b, a0i);
        buffer.addVertex(pose.last(), (float) lp1.x, (float) lp1.y, (float) lp1.z)
                .setColor(r, g, b, a1i);
        buffer.addVertex(pose.last(), (float) rp1.x, (float) rp1.y, (float) rp1.z)
                .setColor(r, g, b, a1i);
        buffer.addVertex(pose.last(), (float) rp0.x, (float) rp0.y, (float) rp0.z)
                .setColor(r, g, b, a0i);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void renderTendrilOrbs(Vec3 from, Vec3 to, Vec3 camPos, float time) {
        int orbCount = MAX_ORBS_PER_TENDRIL;

        for (int i = 0; i < orbCount; i++) {
            float t = ((float) i / orbCount + time * 0.3f) % 1.0f;
            Vec3 orbBase = getTendrilPoint(from, to, t, time);
            Vec3 orbPos = orbBase.subtract(camPos);

            float fadeIn = Math.min(1.0f, t * 4.0f);
            float fadeOut = Math.min(1.0f, (1.0f - t) * 4.0f);
            float alpha = fadeIn * fadeOut * (0.6f + 0.4f * (float) Math.sin(time * 3.0 + i * 1.2));

            // Outer glow
            renderGlowOrb(orbPos, 0.15f, alpha * 0.4f, 40, 80, 180);
            // Core
            renderGlowOrb(orbPos, 0.06f, alpha * 0.9f, 100, 170, 255);
        }
    }

    private static void renderGlowOrb(Vec3 pos, float size, float alpha, int r, int g, int b) {
        if (alpha <= 0.01f) return;
        int a = (int) (alpha * 255);

        PoseStack poseStack = new PoseStack();
        poseStack.translate(pos.x, pos.y, pos.z);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR);

        float s = size;
        buffer.addVertex(poseStack.last(), -s, -s, 0).setColor(r, g, b, a);
        buffer.addVertex(poseStack.last(), -s, s, 0).setColor(r, g, b, a);
        buffer.addVertex(poseStack.last(), s, s, 0).setColor(r, g, b, a);
        buffer.addVertex(poseStack.last(), s, -s, 0).setColor(r, g, b, a);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void renderCasterAura(Vec3 center, Vec3 camPos, float time) {
        Vec3 rel = center.subtract(camPos);
        float pulse = 0.5f + 0.3f * (float) Math.sin(time * 4.0);
        float radius = 0.8f + 0.2f * (float) Math.sin(time * 2.5);

        // Inner glow sphere
        renderGlowOrb(rel, radius * 0.3f, pulse * 0.5f, 60, 120, 220);
        renderGlowOrb(rel, radius * 0.6f, pulse * 0.25f, 40, 80, 180);

        // Orbiting particles
        for (int i = 0; i < 6; i++) {
            double angle = time * 2.0 + (i / 6.0) * Math.PI * 2;
            double orbitRadius = 0.6 + 0.1 * Math.sin(time + i);
            Vec3 orbitPos = rel.add(
                    Math.cos(angle) * orbitRadius,
                    Math.sin(time * 1.5 + i * 0.8) * 0.3,
                    Math.sin(angle) * orbitRadius);
            renderGlowOrb(orbitPos, 0.04f, pulse * 0.7f, 90, 160, 255);
        }
    }

    private static void renderShockwaveRing(Vec3 center, Vec3 camPos, float time) {
        // Expanding shockwave that resets periodically
        float cycle = (time * 1.5f) % 1.0f;
        double expandRadius = SkillConfig.COMMON.divineDisruptionAoeRadius.get() * cycle;
        float alpha = (1.0f - cycle) * 0.35f;

        if (alpha <= 0.01f) return;

        double y = center.y - CASTER_HEIGHT_OFFSET + 0.1;
        int r = 70, g = 130, b = 240;
        int a = (int) (alpha * 255);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.DEBUG_LINE_STRIP,
                DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i <= GROUND_RING_SEGMENTS; i++) {
            double angle = (i / (double) GROUND_RING_SEGMENTS) * Math.PI * 2;
            double px = center.x + Math.cos(angle) * expandRadius - camPos.x;
            double py = y - camPos.y;
            double pz = center.z + Math.sin(angle) * expandRadius - camPos.z;
            buffer.addVertex(new PoseStack().last(), (float) px, (float) py, (float) pz)
                    .setColor(r, g, b, a);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // Second shockwave offset by half
        float cycle2 = ((time * 1.5f + 0.5f) % 1.0f);
        double expandRadius2 = SkillConfig.COMMON.divineDisruptionAoeRadius.get() * cycle2;
        float alpha2 = (1.0f - cycle2) * 0.25f;
        int a2 = (int) (alpha2 * 255);

        if (a2 > 0) {
            BufferBuilder buffer2 = Tesselator.getInstance().begin(
                    VertexFormat.Mode.DEBUG_LINE_STRIP,
                    DefaultVertexFormat.POSITION_COLOR);

            for (int i = 0; i <= GROUND_RING_SEGMENTS; i++) {
                double angle = (i / (double) GROUND_RING_SEGMENTS) * Math.PI * 2;
                double px = center.x + Math.cos(angle) * expandRadius2 - camPos.x;
                double py = y - camPos.y;
                double pz = center.z + Math.sin(angle) * expandRadius2 - camPos.z;
                buffer2.addVertex(new PoseStack().last(), (float) px, (float) py, (float) pz)
                        .setColor(50, 100, 200, a2);
            }

            BufferUploader.drawWithShader(buffer2.buildOrThrow());
        }
    }
}
