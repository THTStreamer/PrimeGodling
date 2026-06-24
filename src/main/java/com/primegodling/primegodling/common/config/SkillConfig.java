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
        // Primordial Bloom
        public final ModConfigSpec.IntValue primordialBloomRegenRate;

        // Cosmic Awareness
        public final ModConfigSpec.IntValue cosmicAwarenessRange;

        // Stellar Ascension
        public final ModConfigSpec.DoubleValue stellarAscensionAttackBonus;
        public final ModConfigSpec.DoubleValue stellarAscensionHealthBonus;
        public final ModConfigSpec.DoubleValue stellarAscensionDamageBonus;
        public final ModConfigSpec.DoubleValue stellarAscensionMasteredDamageBonus;
        public final ModConfigSpec.DoubleValue stellarAscensionEnergyCost;

        // Ecliptic Mastery
        public final ModConfigSpec.DoubleValue eclipticMasteryArmor;
        public final ModConfigSpec.DoubleValue eclipticMasteryToughness;
        public final ModConfigSpec.DoubleValue eclipticMasteryReflectDamage;
        public final ModConfigSpec.DoubleValue eclipticMasteryMasteredReflectDamage;
        public final ModConfigSpec.DoubleValue eclipticMasteryNegateChance;
        public final ModConfigSpec.DoubleValue eclipticMasteryMasteredNegateChance;

        // Luminarch Blessing
        public final ModConfigSpec.IntValue luminarchBlessingCost;
        public final ModConfigSpec.IntValue luminarchBlessingRange;
        public final ModConfigSpec.IntValue luminarchBlessingMasteredRange;
        public final ModConfigSpec.DoubleValue luminarchBlessingHealRange;

        // Primordial Fortitude
        public final ModConfigSpec.DoubleValue primordialFortitudeAttackBonus;
        public final ModConfigSpec.DoubleValue primordialFortitudeHealthBonus;
        public final ModConfigSpec.DoubleValue primordialFortitudeArmor;
        public final ModConfigSpec.DoubleValue primordialFortitudeToughness;
        public final ModConfigSpec.DoubleValue primordialFortitudeSpeedMultiplier;
        public final ModConfigSpec.DoubleValue primordialFortitudeDamageReduction;
        public final ModConfigSpec.DoubleValue primordialFortitudeMasteredDamageReduction;
        public final ModConfigSpec.DoubleValue primordialFortitudeLearningGain;
        public final ModConfigSpec.DoubleValue primordialFortitudeMasteryGain;
        public final ModConfigSpec.DoubleValue primordialFortitudeChantSpeed;

        // Creation Authority
        public final ModConfigSpec.IntValue creationAuthorityCooldown;
        public final ModConfigSpec.IntValue creationAuthorityMasteredCooldown;
        public final ModConfigSpec.DoubleValue creationAuthorityEnergyCost;
        public final ModConfigSpec.DoubleValue creationAuthorityExplosionRadius;
        public final ModConfigSpec.DoubleValue creationAuthorityMasteredExplosionRadius;
        public final ModConfigSpec.IntValue creationAuthorityImmunityDuration;

        // Divine Devour
        public final ModConfigSpec.DoubleValue devourSuccessChance;
        public final ModConfigSpec.DoubleValue devourUniqueSuccessChance;
        public final ModConfigSpec.DoubleValue devourUltimateSuccessChance;
        public final ModConfigSpec.BooleanValue devourAllowUnique;
        public final ModConfigSpec.BooleanValue devourAllowUltimate;
        public final ModConfigSpec.ConfigValue<List<? extends String>> devourSkillBlacklist;

        // Divine Nexus (Awakening prerequisites)
        public final ModConfigSpec.IntValue divineNexusMinSkills;
        public final ModConfigSpec.IntValue divineNexusMinEp;
        public final ModConfigSpec.IntValue nexusCoreEpCost;

        // Divine Nexus Awakening (full requirements)
        public final ModConfigSpec.IntValue awakeningEpRequired;
        public final ModConfigSpec.IntValue awakeningCoresRequired;
        public final ModConfigSpec.IntValue awakeningDemonLordKills;
        public final ModConfigSpec.IntValue awakeningHostileMobKills;
        public final ModConfigSpec.BooleanValue awakeningRequireHinata;
        public final ModConfigSpec.ConfigValue<List<? extends String>> awakeningBossMobs;

        Common(ModConfigSpec.Builder builder) {
            // ==================== Primordial Bloom ====================
            builder.push("primordial_bloom").comment("Primordial Bloom — Unique Skill (Stage 2)");
            primordialBloomRegenRate = builder
                    .comment("Magicule regeneration rate per second (percent of max)")
                    .defineInRange("regen_rate_percent_per_second", 3, 1, 50);
            builder.pop();

            // ==================== Cosmic Awareness ====================
            builder.push("cosmic_awareness").comment("Cosmic Awareness — Intrinsic Skill (Stage 2)");
            cosmicAwarenessRange = builder
                    .comment("Detection range for invisible entities and presence sense")
                    .defineInRange("detection_range", 32, 8, 128);
            builder.pop();

            // ==================== Stellar Ascension ====================
            builder.push("stellar_ascension").comment("Stellar Ascension — Intrinsic Skill (Stage 3)");
            stellarAscensionAttackBonus = builder
                    .comment("Bonus attack damage when toggled on")
                    .defineInRange("attack_bonus", 4.0, 0.0, 100.0);
            stellarAscensionHealthBonus = builder
                    .comment("Bonus max health when toggled on")
                    .defineInRange("health_bonus", 20.0, 0.0, 1000.0);
            stellarAscensionDamageBonus = builder
                    .comment("Bonus magic damage dealt on attack (non-mastered)")
                    .defineInRange("damage_bonus", 2.0, 0.0, 50.0);
            stellarAscensionMasteredDamageBonus = builder
                    .comment("Bonus magic damage dealt on attack (mastered)")
                    .defineInRange("mastered_damage_bonus", 4.0, 0.0, 50.0);
            stellarAscensionEnergyCost = builder
                    .comment("Energy cost per tick while toggled on")
                    .defineInRange("energy_cost", 5.0, 0.0, 1000.0);
            builder.pop();

            // ==================== Ecliptic Mastery ====================
            builder.push("ecliptic_mastery").comment("Ecliptic Mastery — Intrinsic Skill (Stage 3)");
            eclipticMasteryArmor = builder
                    .comment("Bonus armor when skill is learned")
                    .defineInRange("armor", 6.0, 0.0, 100.0);
            eclipticMasteryToughness = builder
                    .comment("Bonus armor toughness when skill is learned")
                    .defineInRange("toughness", 4.0, 0.0, 100.0);
            eclipticMasteryReflectDamage = builder
                    .comment("Damage reflected back to attackers (non-mastered)")
                    .defineInRange("reflect_damage", 1.0, 0.0, 100.0);
            eclipticMasteryMasteredReflectDamage = builder
                    .comment("Damage reflected back to attackers (mastered)")
                    .defineInRange("mastered_reflect_damage", 3.0, 0.0, 100.0);
            eclipticMasteryNegateChance = builder
                    .comment("Chance to negate incoming damage (non-mastered, 0.05 = 5%)")
                    .defineInRange("negate_chance", 0.05, 0.0, 1.0);
            eclipticMasteryMasteredNegateChance = builder
                    .comment("Chance to negate incoming damage (mastered, 0.10 = 10%)")
                    .defineInRange("mastered_negate_chance", 0.10, 0.0, 1.0);
            builder.pop();

            // ==================== Luminarch Blessing ====================
            builder.push("luminarch_blessing").comment("Luminarch Blessing — Intrinsic Skill (Stage 4)");
            luminarchBlessingCost = builder
                    .comment("Energy cost per tick while toggled on")
                    .defineInRange("energy_cost_per_tick", 200, 0, 10000);
            luminarchBlessingRange = builder
                    .comment("Glowing detection range (non-mastered)")
                    .defineInRange("glow_range", 24, 4, 128);
            luminarchBlessingMasteredRange = builder
                    .comment("Glowing detection range (mastered)")
                    .defineInRange("mastered_glow_range", 48, 4, 128);
            luminarchBlessingHealRange = builder
                    .comment("Range for healing nearby allies (mastered only)")
                    .defineInRange("heal_range", 4.0, 1.0, 32.0);
            builder.pop();

            // ==================== Primordial Fortitude ====================
            builder.push("primordial_fortitude").comment("Primordial Fortitude — Intrinsic Skill (Stage 5)");
            primordialFortitudeAttackBonus = builder
                    .comment("Bonus attack damage")
                    .defineInRange("attack_bonus", 10.0, 0.0, 100.0);
            primordialFortitudeHealthBonus = builder
                    .comment("Bonus max health")
                    .defineInRange("health_bonus", 40.0, 0.0, 1000.0);
            primordialFortitudeArmor = builder
                    .comment("Bonus armor")
                    .defineInRange("armor", 12.0, 0.0, 100.0);
            primordialFortitudeToughness = builder
                    .comment("Bonus armor toughness")
                    .defineInRange("toughness", 8.0, 0.0, 100.0);
            primordialFortitudeSpeedMultiplier = builder
                    .comment("Movement speed multiplier (0.1 = 10% faster)")
                    .defineInRange("speed_multiplier", 0.1, 0.0, 5.0);
            primordialFortitudeDamageReduction = builder
                    .comment("Damage reduction factor (non-mastered, 0.9 = 90% reduction)")
                    .defineInRange("damage_reduction", 0.9, 0.0, 0.99);
            primordialFortitudeMasteredDamageReduction = builder
                    .comment("Damage reduction factor (mastered, 0.95 = 95% reduction)")
                    .defineInRange("mastered_damage_reduction", 0.95, 0.0, 0.99);
            primordialFortitudeLearningGain = builder
                    .comment("Bonus to ability learning rate")
                    .defineInRange("learning_gain", 50.0, 0.0, 1000.0);
            primordialFortitudeMasteryGain = builder
                    .comment("Bonus to ability mastery rate")
                    .defineInRange("mastery_gain", 50.0, 0.0, 1000.0);
            primordialFortitudeChantSpeed = builder
                    .comment("Chant speed multiplier")
                    .defineInRange("chant_speed", 5.0, 1.0, 50.0);
            builder.pop();

            // ==================== Creation Authority ====================
            builder.push("creation_authority").comment("Creation Authority — Ultimate Skill (Stage 6)");
            creationAuthorityCooldown = builder
                    .comment("Cooldown in ticks after use (non-mastered)")
                    .defineInRange("cooldown_ticks", 200, 10, 1200);
            creationAuthorityMasteredCooldown = builder
                    .comment("Cooldown in ticks after use (mastered)")
                    .defineInRange("mastered_cooldown_ticks", 60, 10, 1200);
            creationAuthorityEnergyCost = builder
                    .comment("Energy cost to activate")
                    .defineInRange("energy_cost", 5000.0, 0.0, 100000.0);
            creationAuthorityExplosionRadius = builder
                    .comment("Explosion radius (non-mastered)")
                    .defineInRange("explosion_radius", 12.0, 1.0, 64.0);
            creationAuthorityMasteredExplosionRadius = builder
                    .comment("Explosion radius (mastered)")
                    .defineInRange("mastered_explosion_radius", 18.0, 1.0, 64.0);
            creationAuthorityImmunityDuration = builder
                    .comment("Invulnerability duration in ticks after activation")
                    .defineInRange("immunity_duration", 40, 0, 200);
            builder.pop();

            // ==================== Divine Devour ====================
            builder.push("divine_devour").comment("Divine Devour — Unique Skill (Universal)");
            devourSuccessChance = builder
                    .comment("Chance to steal Common/Intrinsic/Extra skills (0.10 = 10%)")
                    .defineInRange("success_chance", 0.10, 0.01, 1.0);
            devourUniqueSuccessChance = builder
                    .comment("Chance to steal UNIQUE skills when allow_unique_skills is true (0.05 = 5%)")
                    .defineInRange("unique_success_chance", 0.05, 0.01, 1.0);
            devourUltimateSuccessChance = builder
                    .comment("Chance to steal ULTIMATE skills when allow_ultimate_skills is true (0.01 = 1%)")
                    .defineInRange("ultimate_success_chance", 0.01, 0.01, 1.0);
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

            // ==================== Divine Nexus ====================
            builder.push("divine_nexus").comment("Divine Nexus — Awakening prerequisites (New God only)");
            divineNexusMinSkills = builder
                    .comment("Minimum unique skills required to unlock Divine Nexus path")
                    .defineInRange("min_unique_skills", 5, 1, 20);
            divineNexusMinEp = builder
                    .comment("Minimum EP required to see Divine Nexus option")
                    .defineInRange("min_ep_required", 150_000, 0, 10_000_000);
            nexusCoreEpCost = builder
                    .comment("EP cost per Nexus Core consumed")
                    .defineInRange("nexus_core_ep_cost", 10_000, 0, 1_000_000);
            builder.pop();

            // ==================== Divine Nexus Awakening ====================
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
                            "Hinata is controlled by require_hinata_kill above; add her here only if you want dual counting."
                    )
                    .defineListAllowEmpty("boss_mobs", SkillConfig::defaultBossMobs, obj -> obj instanceof String s && s.contains(":"));
            builder.pop();
        }
    }

    private static List<String> defaultBossMobs() {
        return new ArrayList<>();
    }

    private static List<String> defaultDevourBlacklist() {
        return new ArrayList<>();
    }
}
