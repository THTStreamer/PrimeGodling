package com.primegodling.primegodling.common.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

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
            container.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, "primegodling/skills.toml");
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
        public final ModConfigSpec.IntValue creationAuthorityMasteredCooldown;
        public final ModConfigSpec.DoubleValue creationAuthorityEnergyCost;

        // Divine Nexus Awakening requirements
        public final ModConfigSpec.IntValue awakeningEpRequired;
        public final ModConfigSpec.IntValue awakeningCoresRequired;
        public final ModConfigSpec.IntValue awakeningDemonLordKills;
        public final ModConfigSpec.IntValue awakeningHostileMobKills;
        public final ModConfigSpec.BooleanValue awakeningRequireHinata;
        public final ModConfigSpec.ConfigValue<List<? extends String>> awakeningBossMobs;

        // Divine Devour settings
        public final ModConfigSpec.BooleanValue devourAllowUnique;
        public final ModConfigSpec.BooleanValue devourAllowUltimate;
        public final ModConfigSpec.ConfigValue<List<? extends String>> devourSkillBlacklist;

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

            builder.push("divine_nexus_awakening").comment("Divine Nexus — Full awakening requirements");
            awakeningEpRequired = builder
                    .comment("Minimum EP required to attempt Divine Nexus awakening")
                    .defineInRange("ep_required", 1_000_000, 0, 1_000_000_000);
            awakeningCoresRequired = builder
                    .comment("Number of Nexus Cores required to attempt Divine Nexus awakening")
                    .defineInRange("cores_required", 1000, 0, 100_000);
            awakeningDemonLordKills = builder
                    .comment("Number of Awakened Demon Lords required (Path A)")
                    .defineInRange("demon_lord_kills_required", 3, 0, 100);
            awakeningHostileMobKills = builder
                    .comment("Number of hostile mobs required (Path B)")
                    .defineInRange("hostile_mob_kills_required", 50000, 0, 10_000_000);
            awakeningRequireHinata = builder
                    .comment("Whether killing Hinata Sakaguchi is required for Path B")
                    .define("require_hinata_kill", true);
            awakeningBossMobs = builder
                    .comment(
                            "List of specific boss mobs that count for Path B alternative kill requirement.",
                            "Format: \"modid:entity_id\" (e.g. \"tensura:hinata_sakaguchi\")",
                            "These mobs must ALL be killed as part of Path B.",
                            "Hinata is controlled by require_hinata_kill above; add her here only if you want双重 counting."
                    )
                    .defineListAllowEmpty("boss_mobs", SkillConfig::defaultBossMobs, obj -> obj instanceof String s && s.contains(":"));
            builder.pop();

            builder.push("divine_devour").comment("Divine Devour — Unique Skill settings");
            devourAllowUnique = builder
                    .comment("Allow Divine Devour to copy UNIQUE skills from targets",
                            "Default: false (only Common/Intrinsic/Extra skills can be copied)")
                    .define("allow_unique_skills", false);
            devourAllowUltimate = builder
                    .comment("Allow Divine Devour to copy ULTIMATE skills from targets",
                            "Default: false (only Common/Intrinsic/Extra skills can be copied)")
                    .define("allow_ultimate_skills", false);
            devourSkillBlacklist = builder
                    .comment("List of skills that Divine Devour cannot copy.",
                            "Format: \"modid:skill_id\" (e.g. \"tensura:predation\")",
                            "These skills will never be stolen regardless of allow_unique_skills or allow_ultimate_skills.")
                    .defineListAllowEmpty("skill_blacklist", SkillConfig::defaultDevourBlacklist, obj -> obj instanceof String s && s.contains(":"));
            builder.pop();

            builder.push("luminarch_blessing").comment("Luminarch Blessing — Toggleable Intrinsic Skill settings");
            luminarchBlessingCost = builder.defineInRange("energy_cost_per_tick", 200, 0, 10000);
            builder.pop();

            builder.push("creation_authority").comment("Creation Authority — Ultimate Skill settings");
            creationAuthorityCooldown = builder.defineInRange("cooldown_ticks", 200, 10, 1200);
            creationAuthorityMasteredCooldown = builder.defineInRange("mastered_cooldown_ticks", 60, 10, 1200);
            creationAuthorityEnergyCost = builder.defineInRange("energy_cost", 5000.0, 0.0, 100000.0);
            builder.pop();
        }
    }

    private static List<String> defaultBossMobs() {
        List<String> list = new ArrayList<>();
        return list;
    }

    private static List<String> defaultDevourBlacklist() {
        List<String> list = new ArrayList<>();
        return list;
    }
}
