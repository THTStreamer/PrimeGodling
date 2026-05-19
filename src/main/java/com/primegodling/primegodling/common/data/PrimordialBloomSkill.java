package com.primegodling.primegodling.common.data;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * Primordial Bloom — Unique Skill granted to all Prime Godling stages.
 * Behaviour: passively regenerates magicules at configurable %/second
 * and converts a configurable fraction of all elemental damage taken
 * into magicule gain.  The governing tunables live in
 * {@link com.primegodling.primegodling.common.config.SkillConfig SkillConfig}.
 */
public class PrimordialBloomSkill {
    private static final ResourceLocation REGISTRY_KEY =
            ResourceLocation.fromNamespaceAndPath("primegodling", "primordial_bloom");

    public PrimordialBloomSkill() {
    }

    public static void initialize() {
    }

    public static ResourceLocation getId() {
        return REGISTRY_KEY;
    }
}