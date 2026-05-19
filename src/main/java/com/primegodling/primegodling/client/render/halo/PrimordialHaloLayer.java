package com.primegodling.primegodling.client.render.halo;

import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import com.mojang.blaze3d.vertex.PoseStack;
import com.primegodling.primegodling.common.data.RaceRegistry;

public class PrimordialHaloLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PrimordialHaloLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        if (!hasHalo(player)) return;

        if ((int) ageInTicks % 4 == 0) {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                double radius = 0.5;
                double y = player.getY() + 2.5;
                for (int i = 0; i < 6; i++) {
                    double angle = 2 * Math.PI * i / 6 + ageInTicks * 0.04;
                    double x = player.getX() + radius * Math.cos(angle);
                    double z = player.getZ() + radius * Math.sin(angle);
                    level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
                }
            }
        }
    }

    private static boolean hasHalo(AbstractClientPlayer player) {
        if (player.getPersistentData().getBoolean("primegodling:awakened_nexus")) {
            return true;
        }
        return RaceAPI.getRaceFrom(player)
                .getRace()
                .map(ManasRaceInstance::getRace)
                .map(ManasRace::getRegistryName)
                .filter(id -> id.equals(RaceRegistry.ID_PRIMORDIAL_SUPREME_GOD))
                .isPresent();
    }
}
