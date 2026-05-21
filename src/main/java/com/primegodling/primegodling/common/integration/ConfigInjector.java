package com.primegodling.primegodling.common.integration;

import com.primegodling.primegodling.common.data.RaceRegistry;
import com.primegodling.primegodling.common.data.SkillRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigInjector {

    public static final List<ResourceLocation> ALL_RACES = RaceRegistry.EVOLUTION_CHAIN_RL;

    public static final List<ResourceLocation> ALL_SKILLS = Arrays.asList(
            SkillRegistry.PRIMORDIAL_BLOOM,
            SkillRegistry.COSMIC_AWARENESS,
            SkillRegistry.STELLAR_ASCENSION,
            SkillRegistry.ECLIPTIC_MASTERY,
            SkillRegistry.LUMINARCH_BLESSING,
            SkillRegistry.PRIMORDIAL_FORTITUDE,
            SkillRegistry.CREATION_AUTHORITY,
            SkillRegistry.DIVINE_DEVOUR
    );

    private ConfigInjector() {}

    public static List<ResourceLocation> getRaceIds() {
        return Collections.unmodifiableList(ALL_RACES);
    }

    public static List<ResourceLocation> getSkillIds() {
        return Collections.unmodifiableList(ALL_SKILLS);
    }
}
