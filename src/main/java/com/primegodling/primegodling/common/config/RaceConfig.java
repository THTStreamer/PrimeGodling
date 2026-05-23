package com.primegodling.primegodling.common.config;

import com.primegodling.primegodling.PrimeGodling;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class RaceConfig {
    public static final int CONFIG_VERSION = 3;

    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    public static void register() {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        if (container != null) {
            container.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, "primegodling/races.toml");
        }
    }

    public static class Common {
        public final ModConfigSpec.IntValue configVersion;
        public final ModConfigSpec.IntValue primeGodlingMinEp;
        public final ModConfigSpec.IntValue primeGodlingMaxEp;
        public final ModConfigSpec.IntValue primeGodlingMagiculeCap;
        public final ModConfigSpec.IntValue stage2CelestialEssenceEp;
        public final ModConfigSpec.IntValue stage3EclipticWardenEp;
        public final ModConfigSpec.IntValue stage4LuminarchGodEp;
        public final ModConfigSpec.IntValue stage5PrimordialSupremeGodEp;

        public final ModConfigSpec.IntValue flightActivationCost;
        public final ModConfigSpec.IntValue flightMaintenanceCost;
        public final ModConfigSpec.IntValue flightMaintenanceInterval;
        public final ModConfigSpec.IntValue flightActivationCostSub;
        public final ModConfigSpec.IntValue flightMaintenanceCostSub;
        public final ModConfigSpec.IntValue flightMaintenanceIntervalSub;

        // New: random resistance / skill configs
        public final ModConfigSpec.IntValue primeGodlingResistanceCount;
        public final ModConfigSpec.IntValue primeGodlingSkillCount;
        public final ModConfigSpec.IntValue celestialGodlingSkillCount;

        Common(ModConfigSpec.Builder builder) {
            builder.push("_meta").comment("Internal metadata — do not edit");
            configVersion = builder.defineInRange("config_version", CONFIG_VERSION, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("races").comment("Base race stats for primegodling:half_godling");
            primeGodlingMinEp = builder.defineInRange("half_godling_min_ep", 100, Integer.MIN_VALUE, Integer.MAX_VALUE);
            primeGodlingMaxEp = builder.defineInRange("half_godling_max_ep", 4000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            primeGodlingMagiculeCap = builder.defineInRange("half_godling_magicule_cap", 1_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();

            builder.push("evolution").comment("EP thresholds for each evolution stage");
            stage2CelestialEssenceEp = builder.defineInRange("stage1_demi_godling_ep", 50_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            stage3EclipticWardenEp = builder.defineInRange("stage2_prime_godling_ep", 100_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            stage4LuminarchGodEp = builder.defineInRange("stage3_celestial_godling_ep", 200_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            stage5PrimordialSupremeGodEp = builder.defineInRange("stage4_ecliptic_godling_ep", 400_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();

            builder.push("random_rewards").comment("Random resistance and skill grants at evolution milestones");
            primeGodlingResistanceCount = builder
                    .comment("Number of random resistances granted when reaching Prime Godling (stage 2)")
                    .defineInRange("prime_godling_resistance_count", 3, 0, 22);
            primeGodlingSkillCount = builder
                    .comment("Number of random Intrinsic/Common/Extra skills granted when reaching Prime Godling (stage 2)")
                    .defineInRange("prime_godling_skill_count", 3, 0, 20);
            celestialGodlingSkillCount = builder
                    .comment("Number of random Intrinsic/Common/Extra skills granted/mastered when reaching Celestial Godling (stage 3)")
                    .defineInRange("celestial_godling_skill_count", 2, 0, 20);
            builder.pop();

            builder.push("creative_flight").comment("Magicule costs for creative flight");
            flightActivationCost = builder.defineInRange("activation_cost", 40, 0, 10000);
            flightMaintenanceCost = builder.defineInRange("maintenance_cost", 10, 0, 10000);
            flightMaintenanceInterval = builder.defineInRange("maintenance_interval_ticks", 10, 1, 200);
            flightActivationCostSub = builder.defineInRange("activation_cost_subordinate", 20, 0, 10000);
            flightMaintenanceCostSub = builder.defineInRange("maintenance_cost_subordinate", 2, 0, 10000);
            flightMaintenanceIntervalSub = builder.defineInRange("maintenance_interval_ticks_subordinate", 100, 1, 200);
            builder.pop();
        }
    }

    public static void onLoad(ModConfigEvent.Loading event) {
        if (COMMON.configVersion.get() < CONFIG_VERSION) {
            PrimeGodling.LOGGER.warn(
                    "[{}] config/primegodling/races.toml is outdated (version {} < {}). " +
                    "Delete the file to regenerate with current defaults.",
                    PrimeGodling.MOD_ID, COMMON.configVersion.get(), CONFIG_VERSION);
        }
    }
}
