package com.primegodling.primegodling.common.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RaceRegistry {

    private RaceRegistry() {}

    public static final String NAMESPACE = "primegodling";

    public static final String PRIME_GODLING          = "prime_godling";
    public static final String CELESTIAL_ESSENCE       = "celestial_essence";
    public static final String ECLIPTIC_WARDEN         = "ecliptic_warden";
    public static final String LUMINARCH_GOD           = "luminarch_god";
    public static final String PRIMORDIAL_SUPREME_GOD  = "primordial_supreme_god";

    public static final List<String> ALL_STAGES_KEY = List.of(
            PRIME_GODLING, CELESTIAL_ESSENCE, ECLIPTIC_WARDEN, LUMINARCH_GOD, PRIMORDIAL_SUPREME_GOD
    );

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }

    public static final ResourceLocation ID_PRIME_GODLING          = rl(PRIME_GODLING);
    public static final ResourceLocation ID_CELESTIAL_ESSENCE       = rl(CELESTIAL_ESSENCE);
    public static final ResourceLocation ID_ECLIPTIC_WARDEN         = rl(ECLIPTIC_WARDEN);
    public static final ResourceLocation ID_LUMINARCH_GOD           = rl(LUMINARCH_GOD);
    public static final ResourceLocation ID_PRIMORDIAL_SUPREME_GOD  = rl(PRIMORDIAL_SUPREME_GOD);

    public static final List<String> ALL_SKILL_IDS = List.of(
            "primordial_bloom", "cosmic_awareness", "stellar_ascension",
            "ecliptic_mastery", "luminarch_blessing", "primordial_omnipotence",
            "creation_authority"
    );

    public static final List<String> EVOLUTION_CHAIN = List.of(
            PRIME_GODLING, CELESTIAL_ESSENCE, ECLIPTIC_WARDEN, LUMINARCH_GOD, PRIMORDIAL_SUPREME_GOD
    );

    public static final List<ResourceLocation> EVOLUTION_CHAIN_RL = List.of(
            ID_PRIME_GODLING, ID_CELESTIAL_ESSENCE, ID_ECLIPTIC_WARDEN, ID_LUMINARCH_GOD, ID_PRIMORDIAL_SUPREME_GOD
    );

    public static final long EP_STAGE_0   = 0;
    public static final long EP_STAGE_1   = 250_000;
    public static final long EP_STAGE_2   = 1_000_000;
    public static final long EP_STAGE_3   = 5_000_000;
    public static final long EP_STAGE_4   = 30_000_000;
    public static final long[] EP_THRESHOLDS = { EP_STAGE_0, EP_STAGE_1, EP_STAGE_2, EP_STAGE_3, EP_STAGE_4 };

    public static final long EP_PRIME_GODLING_MIN          = 5_000;
    public static final long EP_PRIME_GODLING_MAX          = 25_000;

    public static final long MP_PRIME_GODLING           = 5_000;
    public static final long MP_CELESTIAL_ESSENCE        = 3_000_000;
    public static final long MP_ECLIPTIC_WARDEN          = 7_000_000;
    public static final long MP_LUMINARCH_GOD            = 10_000_000;
    public static final long MP_PRIMORDIAL_SUPREMEGOD    = 10_000_000;

    public static final int  NEXUS_UNIQUE_SKILL_COUNT = 5;
    public static final long NEXUS_EP_REQUIRED         = 150_000;
    public static final long NEXUS_EP_MULTIPLIER       = 5L;
}
