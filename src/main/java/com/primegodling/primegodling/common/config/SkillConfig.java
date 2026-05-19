package com.primegodling.primegodling.common.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SkillConfig {
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
            container.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, "primegodling-skills.toml");
        }
    }

    public static class Common {
        public final ModConfigSpec.IntValue primordialBloomRegenRate;
        public final ModConfigSpec.IntValue cosmicAwarenessRange;
        public final ModConfigSpec.IntValue divineNexusMinSkills;
        public final ModConfigSpec.IntValue divineNexusMinEp;
        public final ModConfigSpec.IntValue nexusCoreEpCost;
        public final ModConfigSpec.IntValue luminarchBlessingCost;
        public final ModConfigSpec.IntValue creationAuthorityCooldown;
        public final ModConfigSpec.DoubleValue creationAuthorityEnergyCost;

        Common(ModConfigSpec.Builder builder) {
            builder.push("primordial_bloom").comment("Primordial Bloom — Unique Skill settings");
            primordialBloomRegenRate = builder.defineInRange("regen_rate_percent_per_second", 3, 1, 50);
            builder.pop();

            builder.push("cosmic_awareness").comment("Cosmic Awareness — Intrinsic Skill settings");
            cosmicAwarenessRange = builder.defineInRange("detection_range", 32, 8, 128);
            builder.pop();

            builder.push("divine_nexus").comment("Divine Nexus — Awakening requirements");
            divineNexusMinSkills = builder.defineInRange("min_unique_skills", 5, 1, 20);
            divineNexusMinEp = builder.defineInRange("min_ep_required", 150_000, 0, 10_000_000);
            nexusCoreEpCost = builder.defineInRange("nexus_core_ep_cost", 10_000, 0, 1_000_000);
            builder.pop();

            builder.push("luminarch_blessing").comment("Luminarch Blessing — Toggleable Intrinsic Skill settings");
            luminarchBlessingCost = builder.defineInRange("energy_cost_per_tick", 200, 0, 10000);
            builder.pop();

            builder.push("creation_authority").comment("Creation Authority — Ultimate Skill settings");
            creationAuthorityCooldown = builder.defineInRange("cooldown_ticks", 100, 10, 1200);
            creationAuthorityEnergyCost = builder.defineInRange("energy_cost", 5000.0, 0.0, 100000.0);
            builder.pop();
        }
    }
}
