package com.primegodling.primegodling.common.data.race;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class FixedEPRequirement extends EvolutionRequirement {

    private final long requiredEP;

    public FixedEPRequirement(long requiredEP) {
        this.requiredEP = requiredEP;
    }

    @Override
    public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
        double currentEP = EnergyHelper.getBaseMaxEP(entity);
        if (requiredEP <= 0) return 1.0f;
        return (float) Math.min(1.0, currentEP / requiredEP);
    }

    @Override
    public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
        double currentEP = EnergyHelper.getBaseMaxEP(entity);
        return Component.translatable("primegodling.evolution.fixed_ep", (long) currentEP, requiredEP);
    }
}
