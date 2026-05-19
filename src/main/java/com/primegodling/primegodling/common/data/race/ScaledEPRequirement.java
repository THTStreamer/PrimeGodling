package com.primegodling.primegodling.common.data.race;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ScaledEPRequirement extends EvolutionRequirement {

    private static final String ENTRY_EP_KEY = "primegodling:entry_ep";
    private final double multiplier;

    public ScaledEPRequirement(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) return 0;
        double currentEP = EnergyHelper.getBaseMaxEP(entity);
        double entryEP = player.getPersistentData().getDouble(ENTRY_EP_KEY);
        if (entryEP <= 0) return currentEP > 0 ? 1.0f : 0;
        double required = entryEP * multiplier;
        // Progress = how far we've come from entry toward required
        double delta = currentEP - entryEP;
        double needed = required - entryEP;
        if (needed <= 0) return 1.0f;
        return (float) Math.min(1.0, delta / needed);
    }

    @Override
    public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
        double currentEP = 0;
        double entryEP = 0;
        if (entity instanceof ServerPlayer player) {
            currentEP = EnergyHelper.getBaseMaxEP(entity);
            entryEP = player.getPersistentData().getDouble(ENTRY_EP_KEY);
        }
        if (entryEP <= 0) {
            return Component.translatable("primegodling.evolution.scaled_ep", (long) currentEP, 0);
        }
        double required = entryEP * multiplier;
        return Component.translatable("primegodling.evolution.scaled_ep", (long) currentEP, (long) required);
    }

    public static void storeEntryEP(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            double ep = EnergyHelper.getBaseMaxEP(entity);
            player.getPersistentData().putDouble(ENTRY_EP_KEY, ep);
        }
    }
}
