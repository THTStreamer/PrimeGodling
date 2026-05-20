package com.primegodling.primegodling.common.data.race;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class ScaledEPRequirement extends EvolutionRequirement {

    private static final String ENTRY_EP_KEY = "primegodling:entry_ep";
    private final double multiplier;

    public ScaledEPRequirement(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
        double currentEP = EnergyHelper.getBaseMaxEP(entity);
        double entryEP = readEntryEP(instance);
        if (entryEP <= 0) {
            storeEntryEP(instance, entity);
            entryEP = currentEP;
            if (entryEP <= 0) return 0;
        }
        double required = entryEP * multiplier;
        if (required <= 0) return 1.0f;
        return (float) Math.min(1.0, currentEP / required);
    }

    @Override
    public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
        double currentEP = EnergyHelper.getBaseMaxEP(entity);
        double entryEP = readEntryEP(instance);
        if (entryEP <= 0) {
            return Component.translatable("primegodling.evolution.scaled_ep", (long) currentEP, (long) (currentEP * multiplier));
        }
        double required = entryEP * multiplier;
        return Component.translatable("primegodling.evolution.scaled_ep", (long) currentEP, (long) required);
    }

    public static void storeEntryEP(ManasRaceInstance instance, LivingEntity entity) {
        double ep = EnergyHelper.getBaseMaxEP(entity);
        instance.getOrCreateTag().putDouble(ENTRY_EP_KEY, ep);
    }

    private static double readEntryEP(ManasRaceInstance instance) {
        var tag = instance.getTag();
        return tag != null ? tag.getDouble(ENTRY_EP_KEY) : 0;
    }
}
