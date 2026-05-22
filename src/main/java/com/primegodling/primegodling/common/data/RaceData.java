package com.primegodling.primegodling.common.data;

import com.primegodling.primegodling.common.data.race.PrimeGodlingRace;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class RaceData {

    private RaceData() {}

    public static PrimeGodlingRace createStage(int index) {
        PrimeGodlingRace race = switch (index) {
            // Stage 0: aura random 200-3,000; magicule 2,500-7,000
            case 0 -> new PrimeGodlingRace(Difficulty.EASY, 0, 2_500, 7_000, 200, 3_000, allResistances());
            // Stages 1-4: aura = half of EP threshold
            case 1 -> new PrimeGodlingRace(Difficulty.INTERMEDIATE, 250_000, 3_000_000, 3_000_000, 125_000, 125_000,
                    withResistances(
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.COSMIC_AWARENESS),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.STELLAR_ASCENSION)));
            case 2 -> new PrimeGodlingRace(Difficulty.HARD, 1_000_000, 7_000_000, 7_000_000, 500_000, 500_000,
                    withResistances(
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.COSMIC_AWARENESS),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.STELLAR_ASCENSION),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.ECLIPTIC_MASTERY)));
            case 3 -> new PrimeGodlingRace(Difficulty.HARD, 5_000_000, 10_000_000, 10_000_000, 2_500_000, 2_500_000,
                    withResistances(
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.COSMIC_AWARENESS),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.STELLAR_ASCENSION),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.ECLIPTIC_MASTERY),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.LUMINARCH_BLESSING)));
            case 4 -> new PrimeGodlingRace(Difficulty.EXTREME, 30_000_000, 10_000_000, 10_000_000, 15_000_000, 15_000_000,
                    withResistances(
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.COSMIC_AWARENESS),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.STELLAR_ASCENSION),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.ECLIPTIC_MASTERY),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.LUMINARCH_BLESSING),
                            skill(com.primegodling.primegodling.common.data.SkillRegistry.PRIMORDIAL_FORTITUDE)));
            default -> throw new IllegalArgumentException("Unknown stage index: " + index);
        };
        applyModifiers(race, index);
        return race;
    }

    private static void applyModifiers(PrimeGodlingRace race, int index) {
        switch (index) {
            case 0 -> {
                race.addAttr(Attributes.MAX_HEALTH, "pg_health", 8.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "pg_damage", 2.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "pg_mult", 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "pg_speed", 0.05, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "pg_armor", 4.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "pg_dodge_negate", 0.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case 1 -> {
                race.addAttr(Attributes.MAX_HEALTH, "ce_health", 16.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "ce_damage", 4.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "ce_mult", 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "ce_speed", 0.08, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "ce_armor", 8.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "ce_dodge_negate", 0.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case 2 -> {
                race.addAttr(Attributes.MAX_HEALTH, "ew_health", 24.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "ew_damage", 6.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "ew_mult", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "ew_speed", 0.10, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "ew_armor", 12.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "ew_toughness", 4.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.RESISTANCE_DEGRADATION, "ew_res_degradation", 1.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "ew_dodge_negate", 0.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case 3 -> {
                race.addAttr(Attributes.MAX_HEALTH, "lg_health", 40.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "lg_damage", 10.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "lg_mult", 0.35, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "lg_speed", 0.15, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "lg_armor", 16.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "lg_toughness", 8.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.RESISTANCE_DEGRADATION, "lg_res_degradation", 1.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "lg_dodge_negate", 0.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case 4 -> {
                race.addAttr(Attributes.MAX_HEALTH, "psg_health", 60.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "psg_damage", 16.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "psg_mult", 0.40, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "psg_speed", 0.20, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "psg_armor", 20.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "psg_toughness", 12.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.RESISTANCE_DEGRADATION, "psg_res_degradation", 1.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "psg_dodge_negate", 0.0, AttributeModifier.Operation.ADD_VALUE);
            }
        }
    }

    private static Supplier<ManasSkill> skill(ResourceLocation id) {
        return () -> SkillRegistry.SKILLS.get(id);
    }

    @SafeVarargs
    private static List<Supplier<ManasSkill>> withResistances(Supplier<ManasSkill>... extras) {
        List<Supplier<ManasSkill>> result = new ArrayList<>(allResistances());
        for (Supplier<ManasSkill> s : extras) {
            if (s != null) result.add(s);
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<Supplier<ManasSkill>> allResistances() {
        return Arrays.asList(
                (Supplier) ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.MAGIC_RESISTANCE,
                (Supplier) ResistanceSkills.FLAME_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.WATER_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.WIND_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.EARTH_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.LIGHT_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.DARKNESS_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.HOLY_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.GRAVITY_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.SPATIAL_ATTACK_RESISTANCE,
                (Supplier) ResistanceSkills.POISON_RESISTANCE,
                (Supplier) ResistanceSkills.PARALYSIS_RESISTANCE,
                (Supplier) ResistanceSkills.PAIN_RESISTANCE,
                (Supplier) ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE,
                (Supplier) ResistanceSkills.HEAT_RESISTANCE,
                (Supplier) ResistanceSkills.COLD_RESISTANCE,
                (Supplier) ResistanceSkills.ELECTRICITY_RESISTANCE,
                (Supplier) ResistanceSkills.CORROSION_RESISTANCE,
                (Supplier) ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE,
                (Supplier) ResistanceSkills.PIERCE_RESISTANCE
        );
    }
}
