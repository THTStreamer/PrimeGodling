package com.primegodling.primegodling.common.data.race;

import com.primegodling.primegodling.common.config.ServerConfig;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class NexusCoreRequirement extends EvolutionRequirement {

    private static int required() {
        return ServerConfig.COMMON.nexusCoresRequired.get();
    }

    public NexusCoreRequirement() {}

    @Override
    public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            int eaten = player.getPersistentData().getInt("primegodling:nexus_cores_eaten");
            return Math.min(1.0f, (float) eaten / required());
        }
        return 0;
    }

    @Override
    public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
        int eaten = entity instanceof ServerPlayer player
                ? player.getPersistentData().getInt("primegodling:nexus_cores_eaten")
                : 0;
        return Component.translatable("primegodling.evolution.nexus_cores", eaten, required());
    }
}
