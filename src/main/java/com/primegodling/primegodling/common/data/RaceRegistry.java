package com.primegodling.primegodling.common.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RaceRegistry {

    private RaceRegistry() {}

    public static final String NAMESPACE = "primegodling";

    // 7-stage evolution chain
    public static final String HALF_GODLING            = "half_godling";
    public static final String DEMI_GODLING             = "demi_godling";
    public static final String PRIME_GODLING            = "prime_godling";
    public static final String CELESTIAL_GODLING         = "celestial_godling";
    public static final String ECLIPTIC_GODLING          = "ecliptic_godling";
    public static final String NEW_GOD                   = "new_god";
    public static final String PRIMORDIAL_SUPREME_GOD    = "primordial_supreme_god";

    public static final List<String> ALL_STAGES_KEY = List.of(
            HALF_GODLING, DEMI_GODLING, PRIME_GODLING, CELESTIAL_GODLING,
            ECLIPTIC_GODLING, NEW_GOD, PRIMORDIAL_SUPREME_GOD
    );

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    public static final ResourceLocation ID_HALF_GODLING            = rl(HALF_GODLING);
    public static final ResourceLocation ID_DEMI_GODLING             = rl(DEMI_GODLING);
    public static final ResourceLocation ID_PRIME_GODLING            = rl(PRIME_GODLING);
    public static final ResourceLocation ID_CELESTIAL_GODLING         = rl(CELESTIAL_GODLING);
    public static final ResourceLocation ID_ECLIPTIC_GODLING          = rl(ECLIPTIC_GODLING);
    public static final ResourceLocation ID_NEW_GOD                   = rl(NEW_GOD);
    public static final ResourceLocation ID_PRIMORDIAL_SUPREME_GOD    = rl(PRIMORDIAL_SUPREME_GOD);

    public static final List<String> ALL_SKILL_IDS = List.of(
            "primordial_bloom", "cosmic_awareness", "stellar_ascension",
            "ecliptic_mastery", "luminarch_blessing", "primordial_fortitude",
            "creation_authority", "divine_devour"
    );

    public static final List<String> EVOLUTION_CHAIN = List.of(
            HALF_GODLING, DEMI_GODLING, PRIME_GODLING, CELESTIAL_GODLING,
            ECLIPTIC_GODLING, NEW_GOD, PRIMORDIAL_SUPREME_GOD
    );

    public static final List<ResourceLocation> EVOLUTION_CHAIN_RL = List.of(
            ID_HALF_GODLING, ID_DEMI_GODLING, ID_PRIME_GODLING, ID_CELESTIAL_GODLING,
            ID_ECLIPTIC_GODLING, ID_NEW_GOD, ID_PRIMORDIAL_SUPREME_GOD
    );

    // Fixed EP thresholds for evolution (50k base, x2 each)
    public static final long EP_STAGE_0   = 0;
    public static final long EP_STAGE_1   = 50_000;
    public static final long EP_STAGE_2   = 100_000;
    public static final long EP_STAGE_3   = 200_000;
    public static final long EP_STAGE_4   = 400_000;
    public static final long EP_STAGE_5   = 800_000;
    public static final long EP_STAGE_6   = 1_600_000;
    public static final long[] EP_THRESHOLDS = {
            EP_STAGE_0, EP_STAGE_1, EP_STAGE_2, EP_STAGE_3,
            EP_STAGE_4, EP_STAGE_5, EP_STAGE_6
    };

    // Nexus Cores required per evolution (10 base, x4 each)
    public static final int CORES_STAGE_0 = 0;
    public static final int CORES_STAGE_1 = 10;
    public static final int CORES_STAGE_2 = 40;
    public static final int CORES_STAGE_3 = 160;
    public static final int CORES_STAGE_4 = 640;
    public static final int CORES_STAGE_5 = 2_560;
    public static final int CORES_STAGE_6 = 10_240;
    public static final int[] CORES_REQUIRED = {
            CORES_STAGE_0, CORES_STAGE_1, CORES_STAGE_2, CORES_STAGE_3,
            CORES_STAGE_4, CORES_STAGE_5, CORES_STAGE_6
    };
}
