package com.primegodling.primegodling.common.data;

import com.primegodling.primegodling.PrimeGodling;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class ResistanceHelper {

    private static final Map<ResourceLocation, ResourceLocation> RESISTANCE_TO_NULLIFICATION = new LinkedHashMap<>();
    private static List<ResourceLocation> allResistanceIds = new ArrayList<>();
    private static List<ResourceLocation> allNullificationIds = new ArrayList<>();
    private static List<ResourceLocation> nonResistanceSkillPool = new ArrayList<>();
    private static List<ResourceLocation> uniqueSkillPool = new ArrayList<>();
    private static boolean initialized = false;
    private static boolean poolsBuilt = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void init() {
        if (initialized) return;
        initialized = true;

        addPair(ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE, ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.MAGIC_RESISTANCE, ResistanceSkills.MAGIC_NULLIFICATION);
        addPair(ResistanceSkills.FLAME_ATTACK_RESISTANCE, ResistanceSkills.FLAME_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.WATER_ATTACK_RESISTANCE, ResistanceSkills.WATER_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.WIND_ATTACK_RESISTANCE, ResistanceSkills.WIND_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.EARTH_ATTACK_RESISTANCE, ResistanceSkills.EARTH_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.LIGHT_ATTACK_RESISTANCE, ResistanceSkills.LIGHT_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.DARKNESS_ATTACK_RESISTANCE, ResistanceSkills.DARKNESS_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE, ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.HOLY_ATTACK_RESISTANCE, ResistanceSkills.HOLY_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.GRAVITY_ATTACK_RESISTANCE, ResistanceSkills.GRAVITY_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.SPATIAL_ATTACK_RESISTANCE, ResistanceSkills.SPATIAL_ATTACK_NULLIFICATION);
        addPair(ResistanceSkills.POISON_RESISTANCE, ResistanceSkills.POISON_NULLIFICATION);
        addPair(ResistanceSkills.PARALYSIS_RESISTANCE, ResistanceSkills.PARALYSIS_NULLIFICATION);
        addPair(ResistanceSkills.PAIN_RESISTANCE, ResistanceSkills.PAIN_NULLIFICATION);
        addPair(ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE, ResistanceSkills.ABNORMAL_CONDITION_NULLIFICATION);
        addPair(ResistanceSkills.HEAT_RESISTANCE, ResistanceSkills.HEAT_NULLIFICATION);
        addPair(ResistanceSkills.COLD_RESISTANCE, ResistanceSkills.COLD_NULLIFICATION);
        addPair(ResistanceSkills.ELECTRICITY_RESISTANCE, ResistanceSkills.ELECTRICITY_NULLIFICATION);
        addPair(ResistanceSkills.CORROSION_RESISTANCE, ResistanceSkills.CORROSION_NULLIFICATION);
        addPair(ResistanceSkills.THERMAL_FLUCTUATION_RESISTANCE, ResistanceSkills.THERMAL_FLUCTUATION_NULLIFICATION);
        addPair(ResistanceSkills.PIERCE_RESISTANCE, ResistanceSkills.PIERCE_NULLIFICATION);

        PrimeGodling.LOGGER.info("[{}] ResistanceHelper initialized: {} resistance pairs",
                PrimeGodling.MOD_ID, allResistanceIds.size());
    }

    private static void addPair(RegistrySupplier<? extends ManasSkill> resistance,
                                RegistrySupplier<? extends ManasSkill> nullification) {
        ResourceLocation resId = resistance.getId();
        ResourceLocation nullId = nullification.getId();
        RESISTANCE_TO_NULLIFICATION.put(resId, nullId);
        allResistanceIds.add(resId);
        allNullificationIds.add(nullId);
    }

    private static void buildSkillPools() {
        Set<ResourceLocation> resistanceSet = new HashSet<>(allResistanceIds);
        resistanceSet.addAll(allNullificationIds);

        nonResistanceSkillPool = new ArrayList<>();
        uniqueSkillPool = new ArrayList<>();

        for (ResourceLocation id : SkillRegistry.SKILLS.getIds()) {
            if (resistanceSet.contains(id)) continue;

            ManasSkill skill = SkillRegistry.SKILLS.get(id);
            if (skill == null) continue;

            if (skill instanceof Skill tensuraSkill) {
                Skill.SkillType type = tensuraSkill.getType();
                if (type == Skill.SkillType.UNIQUE || type == Skill.SkillType.ULTIMATE) {
                    uniqueSkillPool.add(id);
                } else if (type == Skill.SkillType.INTRINSIC || type == Skill.SkillType.COMMON || type == Skill.SkillType.EXTRA) {
                    nonResistanceSkillPool.add(id);
                }
            }
        }
    }

    private static void ensurePoolsBuilt() {
        if (poolsBuilt) return;
        poolsBuilt = true;

        Set<ResourceLocation> resistanceSet = new HashSet<>(allResistanceIds);
        resistanceSet.addAll(allNullificationIds);

        nonResistanceSkillPool = new ArrayList<>();
        uniqueSkillPool = new ArrayList<>();

        for (ResourceLocation id : SkillRegistry.SKILLS.getIds()) {
            if (resistanceSet.contains(id)) continue;

            ManasSkill skill = SkillRegistry.SKILLS.get(id);
            if (skill == null) continue;

            if (skill instanceof Skill tensuraSkill) {
                Skill.SkillType type = tensuraSkill.getType();
                if (type == Skill.SkillType.UNIQUE || type == Skill.SkillType.ULTIMATE) {
                    uniqueSkillPool.add(id);
                } else if (type == Skill.SkillType.INTRINSIC || type == Skill.SkillType.COMMON || type == Skill.SkillType.EXTRA) {
                    nonResistanceSkillPool.add(id);
                }
            }
        }

        PrimeGodling.LOGGER.info("[{}] Skill pools built: {} non-resistance, {} unique",
                PrimeGodling.MOD_ID, nonResistanceSkillPool.size(), uniqueSkillPool.size());
    }

    public static List<ResourceLocation> getRandomResistances(int count, Random random) {
        List<ResourceLocation> pool = new ArrayList<>(allResistanceIds);
        Collections.shuffle(pool, random);
        return pool.subList(0, Math.min(count, pool.size()));
    }

    public static ResourceLocation getNullificationFor(ResourceLocation resistanceId) {
        return RESISTANCE_TO_NULLIFICATION.get(resistanceId);
    }

    public static List<ResourceLocation> getAllNullifications() {
        return new ArrayList<>(allNullificationIds);
    }

    public static List<ResourceLocation> getRandomNonResistanceSkills(int count, Random random) {
        ensurePoolsBuilt();
        List<ResourceLocation> pool = new ArrayList<>(nonResistanceSkillPool);
        Collections.shuffle(pool, random);
        return pool.subList(0, Math.min(count, pool.size()));
    }

    public static List<ResourceLocation> getRandomUniqueSkills(int count, Random random) {
        ensurePoolsBuilt();
        List<ResourceLocation> pool = new ArrayList<>(uniqueSkillPool);
        Collections.shuffle(pool, random);
        return pool.subList(0, Math.min(count, pool.size()));
    }

    public static boolean grantSkill(ServerPlayer player, ResourceLocation skillId) {
        ManasSkill skill = SkillRegistry.SKILLS.get(skillId);
        if (skill == null) return false;
        Skills skills = SkillAPI.getSkillsFrom(player);
        if (skills == null) return false;
        skills.learnSkill(skillId);
        return true;
    }

    public static boolean grantOrMasterSkill(ServerPlayer player, ResourceLocation skillId, Random random) {
        ManasSkill skill = SkillRegistry.SKILLS.get(skillId);
        if (skill == null) return false;
        Skills skills = SkillAPI.getSkillsFrom(player);
        if (skills == null) return false;

        Optional<io.github.manasmods.manascore.skill.api.ManasSkillInstance> instanceOpt = skills.getSkill(skillId);
        if (instanceOpt.isPresent()) {
            var instance = instanceOpt.get();
            if (skill.isMastered(instance, player)) {
                return grantRandomNonResistanceSkill(player, random);
            }
            skill.addMasteryPoint(instance, player);
            return true;
        }
        skills.learnSkill(skillId);
        return true;
    }

    public static boolean grantRandomNonResistanceSkill(ServerPlayer player, Random random) {
        List<ResourceLocation> pool = getRandomNonResistanceSkills(1, random);
        if (pool.isEmpty()) return false;
        return grantSkill(player, pool.get(0));
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
