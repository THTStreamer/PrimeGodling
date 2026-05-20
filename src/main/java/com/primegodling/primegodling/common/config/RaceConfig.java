package com.primegodling.primegodling.common.config;

import com.primegodling.primegodling.PrimeGodling;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class RaceConfig {
    public static final int CONFIG_VERSION = 2;

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
            container.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, "primegodling-races.toml");
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

        Common(ModConfigSpec.Builder builder) {
            builder.push("_meta").comment("Internal metadata — do not edit");
            configVersion = builder.defineInRange("config_version", CONFIG_VERSION, 1, Integer.MAX_VALUE);
            builder.pop();

            builder.push("races").comment("Base race stats for primegodling:prime_godling");
            primeGodlingMinEp = builder.defineInRange("prime_godling_min_ep", 5000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            primeGodlingMaxEp = builder.defineInRange("prime_godling_max_ep", 25000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            primeGodlingMagiculeCap = builder.defineInRange("prime_godling_magicule_cap", 1_500_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            builder.pop();

            builder.push("evolution").comment("EP thresholds for each evolution stage");
            stage2CelestialEssenceEp = builder.defineInRange("stage2_celestial_essence_ep", 50_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            stage3EclipticWardenEp = builder.defineInRange("stage3_ecliptic_warden_ep", 250_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            stage4LuminarchGodEp = builder.defineInRange("stage4_luminarch_god_ep", 1_000_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
            stage5PrimordialSupremeGodEp = builder.defineInRange("stage5_primordial_supreme_god_ep", 3_000_000, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
                    "[{}] primegodling-races.toml is outdated (version {} < {}). " +
                    "Delete the file to regenerate with current defaults.",
                    PrimeGodling.MOD_ID, COMMON.configVersion.get(), CONFIG_VERSION);
        }
    }
}
