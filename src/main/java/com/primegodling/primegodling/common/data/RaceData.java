package com.primegodling.primegodling.common.data;

import com.primegodling.primegodling.common.data.race.PrimeGodlingRace;
import com.primegodling.primegodling.common.data.ModSkills;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class RaceData {

    private RaceData() {}

    public static PrimeGodlingRace createStage(int index) {
        long epThreshold = RaceRegistry.EP_THRESHOLDS[index];
        PrimeGodlingRace race = switch (index) {
            case 0 -> new PrimeGodlingRace(Difficulty.HARD, epThreshold,
                    100, 1_000, 50, 500,
                    List.of());
            case 1 -> new PrimeGodlingRace(Difficulty.INTERMEDIATE, epThreshold,
                    5_000, 20_000, 2_500, 10_000,
                    List.of());
            case 2 -> new PrimeGodlingRace(Difficulty.HARD, epThreshold,
                    20_000, 80_000, 10_000, 40_000,
                    List.<Supplier<ManasSkill>>of(
                            () -> ModSkills.PRIMORDIAL_BLOOM.get(),
                            () -> ModSkills.COSMIC_AWARENESS.get()
                    ));
            case 3 -> new PrimeGodlingRace(Difficulty.HARD, epThreshold,
                    100_000, 500_000, 50_000, 250_000,
                    List.<Supplier<ManasSkill>>of(
                            () -> ModSkills.STELLAR_ASCENSION.get(),
                            () -> ModSkills.ECLIPTIC_MASTERY.get()
                    ));
            case 4 -> new PrimeGodlingRace(Difficulty.EXTREME, epThreshold,
                    500_000, 2_000_000, 250_000, 1_000_000,
                    List.<Supplier<ManasSkill>>of(
                            () -> ModSkills.LUMINARCH_BLESSING.get()
                    ));
            case 5 -> new PrimeGodlingRace(Difficulty.EXTREME, epThreshold,
                    2_000_000, 8_000_000, 1_000_000, 4_000_000,
                    List.<Supplier<ManasSkill>>of(
                            () -> ModSkills.PRIMORDIAL_FORTITUDE.get()
                    ));
            case 6 -> new PrimeGodlingRace(Difficulty.EXTREME, epThreshold,
                    8_000_000, 20_000_000, 4_000_000, 10_000_000,
                    List.<Supplier<ManasSkill>>of(
                            () -> ModSkills.DIVINE_DEVOUR.get()
                    ));
            default -> throw new IllegalArgumentException("Unknown stage index: " + index);
        };
        applyModifiers(race, index);
        return race;
    }

    private static void applyModifiers(PrimeGodlingRace race, int index) {
        switch (index) {
            case 0 -> {
                race.addAttr(Attributes.MAX_HEALTH, "hg_health", 3.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "hg_damage", 0.5, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.MOVEMENT_SPEED, "hg_speed", 0.01, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "hg_armor", 1.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case 1 -> {
                race.addAttr(Attributes.MAX_HEALTH, "dg_health", 6.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "dg_damage", 1.5, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "dg_mult", 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "dg_speed", 0.03, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "dg_armor", 3.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case 2 -> {
                race.addAttr(Attributes.MAX_HEALTH, "pg_health", 10.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "pg_damage", 3.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "pg_mult", 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "pg_speed", 0.05, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "pg_armor", 5.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "pg_toughness", 2.0, AttributeModifier.Operation.ADD_VALUE);
            }
            case 3 -> {
                race.addAttr(Attributes.MAX_HEALTH, "cg_health", 18.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "cg_damage", 5.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "cg_mult", 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "cg_speed", 0.08, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "cg_armor", 9.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "cg_toughness", 3.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "cg_dodge_negate", 0.20, AttributeModifier.Operation.ADD_VALUE);
            }
            case 4 -> {
                race.addAttr(Attributes.MAX_HEALTH, "eg_health", 28.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "eg_damage", 8.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "eg_mult", 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "eg_speed", 0.10, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "eg_armor", 13.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "eg_toughness", 6.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.RESISTANCE_DEGRADATION, "eg_res_degradation", 1.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "eg_dodge_negate", 0.35, AttributeModifier.Operation.ADD_VALUE);
            }
            case 5 -> {
                race.addAttr(Attributes.MAX_HEALTH, "ng_health", 45.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "ng_damage", 12.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "ng_mult", 0.30, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "ng_speed", 0.15, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "ng_armor", 17.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "ng_toughness", 10.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.RESISTANCE_DEGRADATION, "ng_res_degradation", 1.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "ng_dodge_negate", 0.55, AttributeModifier.Operation.ADD_VALUE);
            }
            case 6 -> {
                race.addAttr(Attributes.MAX_HEALTH, "psg_health", 70.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "psg_damage", 20.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ATTACK_DAMAGE, "psg_mult", 0.40, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
                race.addAttr(Attributes.MOVEMENT_SPEED, "psg_speed", 0.22, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR, "psg_armor", 24.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(Attributes.ARMOR_TOUGHNESS, "psg_toughness", 14.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.RESISTANCE_DEGRADATION, "psg_res_degradation", 1.0, AttributeModifier.Operation.ADD_VALUE);
                race.addAttr(TensuraAttributes.DODGE_NEGATE_CHANCE, "psg_dodge_negate", 0.75, AttributeModifier.Operation.ADD_VALUE);
            }
        }
    }
}
