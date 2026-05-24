package com.primegodling.primegodling.common.data.race;

import com.primegodling.primegodling.network.ClientNexusCoresCache;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class NexusCoreRequirement extends EvolutionRequirement {

    private final int requiredCores;

    public NexusCoreRequirement(int requiredCores) {
        this.requiredCores = requiredCores;
    }

    private static int getAvailable(ManasRaceInstance instance, LivingEntity entity) {
        if (entity != null && entity.level().isClientSide() && entity instanceof Player player) {
            int eaten = ClientNexusCoresCache.getEaten(player.getUUID());
            int spent = ClientNexusCoresCache.getSpent(player.getUUID());
            return eaten - spent;
        }
        CompoundTag tag = instance.getTag();
        if (tag == null) return 0;
        int eaten = tag.getInt("nexus_cores_eaten");
        int spent = tag.getInt("nexus_cores_spent");
        return eaten - spent;
    }

    @Override
    public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
        int available = getAvailable(instance, entity);
        return requiredCores > 0 ? Math.min(1.0f, (float) available / requiredCores) : 1.0f;
    }

    @Override
    public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
        return Component.translatable("primegodling.evolution.nexus_cores", getAvailable(instance, entity), requiredCores);
    }
}
