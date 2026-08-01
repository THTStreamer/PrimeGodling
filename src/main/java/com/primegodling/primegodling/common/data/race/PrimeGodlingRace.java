package com.primegodling.primegodling.common.data.race;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.RaceRegistry;
import com.primegodling.primegodling.common.data.ResistanceHelper;
import com.primegodling.primegodling.common.integration.FTBIntegration;
import com.primegodling.primegodling.network.SyncNexusCoresPayload;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.EnergyHelper.GainType;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class PrimeGodlingRace extends DefaultRace {

    private final List<Supplier<ManasSkill>> intrinsicSkillSuppliers;
    private final long epThreshold;
    private final double minMagicule;
    private final double maxMagicule;
    private final double minAura;
    private final double maxAura;
    private ManasRace nextEvolution;

    private final List<ExtraModifier> extraModifiers = new ArrayList<>();

    private record ExtraModifier(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {}

    public PrimeGodlingRace(Difficulty difficulty, long epThreshold, double minMagicule, double maxMagicule, double minAura, double maxAura, List<Supplier<ManasSkill>> intrinsicSkillSuppliers) {
        super(difficulty);
        this.epThreshold = epThreshold;
        this.minMagicule = minMagicule;
        this.maxMagicule = maxMagicule;
        this.minAura = minAura;
        this.maxAura = maxAura;
        this.intrinsicSkillSuppliers = new ArrayList<>(intrinsicSkillSuppliers);
    }

    @Override
    public RaceConfig.Default getDefaultConfig() {
        return new RaceConfig.Default() {
            @Override public double getMinAura() { return minAura; }
            @Override public double getMaxAura() { return maxAura; }
            @Override public double getMinMagicule() { return minMagicule; }
            @Override public double getMaxMagicule() { return maxMagicule; }
            @Override public double getSize() { return 0; }
            @Override public double getMaxHealth() { return 0; }
            @Override public double getMaxSpiritualHealth() { return 0; }
            @Override public double getAttack() { return 0; }
            @Override public double getAttackSpeed() { return 0; }
            @Override public double getKnockbackResistance() { return 0; }
            @Override public double getMovementSpeed() { return 0; }
            @Override public double getSwimSpeed() { return 0; }
        };
    }

    public void setNextEvolution(ManasRace next) {
        this.nextEvolution = next;
    }

    public long getEpThreshold() {
        return epThreshold;
    }

    public void addAttr(Holder<Attribute> attribute, String name, double amount, AttributeModifier.Operation operation) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("primegodling", name);
        if (this.attributeModifiers.containsKey(attribute)) {
            extraModifiers.add(new ExtraModifier(attribute, id, amount, operation));
        } else {
            addAttributeModifier(attribute, id, amount, operation);
        }
    }

    @Override
    public void addAttributeModifiers(ManasRaceInstance instance, LivingEntity entity) {
        super.addAttributeModifiers(instance, entity);
        for (ExtraModifier em : extraModifiers) {
            AttributeInstance attr = entity.getAttribute(em.attribute);
            if (attr != null) {
                attr.removeModifier(em.id);
                attr.addPermanentModifier(new AttributeModifier(em.id, em.amount, em.operation));
            }
        }
    }

    @Override
    public void removeAttributeModifiers(ManasRaceInstance instance, LivingEntity entity) {
        super.removeAttributeModifiers(instance, entity);
        List<AttributeInstance> dirty = new ArrayList<>();
        for (ExtraModifier em : extraModifiers) {
            AttributeInstance attr = entity.getAttribute(em.attribute);
            if (attr != null) {
                attr.removeModifier(em.id);
                dirty.add(attr);
            }
        }
        if (!dirty.isEmpty() && entity instanceof ServerPlayer player) {
            player.connection.send(new ClientboundUpdateAttributesPacket(player.getId(), dirty));
        }
    }

    @Override
    public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
        if (nextEvolution != null) {
            return new ArrayList<>(List.of(nextEvolution));
        }
        return new ArrayList<>();
    }

    @Override
    public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance instance, LivingEntity entity) {
        Map<EvolutionRequirement, Float> reqs = new LinkedHashMap<>();

        for (int i = 1; i < RaceRegistry.EP_THRESHOLDS.length; i++) {
            if (epThreshold != RaceRegistry.EP_THRESHOLDS[i]) continue;

            reqs.put(new FixedEPRequirement(RaceRegistry.EP_THRESHOLDS[i]), i <= 3 ? 70.0f : 60.0f);
            reqs.put(new NexusCoreRequirement(RaceRegistry.CORES_REQUIRED[i]), 30.0f);
            if (i == 4 || i == 5) {
                reqs.put(new EvolutionRequirement.NamedRequirement(), 10.0f);
            }
            if (i == 6) {
                reqs.put(new AwakenedOrTDLOrHeroRequirement(), 20.0f);
            }
            return reqs;
        }

        return reqs;
    }

    private static final ResourceLocation EVOLUTION_BONUS_HP = ResourceLocation.fromNamespaceAndPath("primegodling", "evolution_bonus_hp");
    private static final ResourceLocation EVOLUTION_BONUS_SHP = ResourceLocation.fromNamespaceAndPath("primegodling", "evolution_bonus_shp");
    private static final ResourceLocation EVOLUTION_BONUS_MP = ResourceLocation.fromNamespaceAndPath("primegodling", "evolution_bonus_mp");
    private static final ResourceLocation EVOLUTION_BONUS_AP = ResourceLocation.fromNamespaceAndPath("primegodling", "evolution_bonus_ap");

    @Override
    public void triggerEvolutionRewards(ManasRaceInstance instance, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)) return;

        int evolutionCount = player.getPersistentData().getInt("primegodling:evolution_count") + 1;
        player.getPersistentData().putInt("primegodling:evolution_count", evolutionCount);

        IExistence existence = TensuraStorages.getExistenceFrom(player);
        if (existence != null) {
            double savedEP = player.getPersistentData().getDouble("primegodling:pre_evolution_ep");
            double currentEP = existence.getEP();
            double epToUse = Math.max(savedEP, currentEP);
            double newAura = epToUse;
            double newMagicule = epToUse;

            AttributeInstance auraAttr = player.getAttribute(TensuraAttributes.MAX_AURA);
            if (auraAttr != null) {
                auraAttr.setBaseValue(newAura);
            }
            AttributeInstance magiculeAttr = player.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (magiculeAttr != null) {
                magiculeAttr.setBaseValue(newMagicule);
            }

            existence.setAura(newAura);
            existence.setMagicule(newMagicule);
            existence.markDirty();

            player.getPersistentData().remove("primegodling:pre_evolution_ep");
        }

        double bonus = evolutionCount * 100.0;

        AttributeInstance hpAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.removeModifier(EVOLUTION_BONUS_HP);
            hpAttr.addPermanentModifier(new AttributeModifier(EVOLUTION_BONUS_HP, bonus, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance shpAttr = player.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
        if (shpAttr != null) {
            shpAttr.removeModifier(EVOLUTION_BONUS_SHP);
            shpAttr.addPermanentModifier(new AttributeModifier(EVOLUTION_BONUS_SHP, bonus, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance mpAttr = player.getAttribute(TensuraAttributes.MAX_MAGICULE);
        if (mpAttr != null) {
            mpAttr.removeModifier(EVOLUTION_BONUS_MP);
            mpAttr.addPermanentModifier(new AttributeModifier(EVOLUTION_BONUS_MP, bonus, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance apAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR);
        if (apAttr != null) {
            apAttr.removeModifier(EVOLUTION_BONUS_AP);
            apAttr.addPermanentModifier(new AttributeModifier(EVOLUTION_BONUS_AP, bonus, AttributeModifier.Operation.ADD_VALUE));
        }

        player.sendSystemMessage(Component.literal("§6✦ Evolution bonus: +100 HP, SHP, MP, AP | EP x2!"));
    }

    @Override
    public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
        return nextEvolution;
    }

    @Override
    public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
        List<ManasSkill> skills = new ArrayList<>();
        for (Supplier<ManasSkill> supplier : intrinsicSkillSuppliers) {
            ManasSkill skill = supplier.get();
            if (skill != null) {
                skills.add(skill);
            }
        }

        if (entity instanceof ServerPlayer player) {
            if (epThreshold == RaceRegistry.EP_STAGE_2) {
                readSkills(player, "primegodling:granted_resistances", skills);
                readSkills(player, "primegodling:granted_skills", skills);
            } else if (epThreshold == RaceRegistry.EP_STAGE_3) {
                readSkills(player, "primegodling:granted_nullifications", skills);
                readSkills(player, "primegodling:granted_skills_stage3", skills);
            } else if (epThreshold == RaceRegistry.EP_STAGE_6) {
                readSkills(player, "primegodling:granted_nullifications_all", skills);
                String unique = player.getPersistentData().getString("primegodling:granted_unique_skill");
                if (!unique.isEmpty()) {
                    ManasSkill s = SkillRegistry.SKILLS.get(ResourceLocation.parse(unique));
                    if (s != null) skills.add(s);
                }
            }
        }

        return skills;
    }

    private static void readSkills(ServerPlayer player, String key, List<ManasSkill> list) {
        String raw = player.getPersistentData().getString(key);
        if (raw.isEmpty()) return;
        for (String id : raw.split(",")) {
            id = id.trim();
            if (!id.isEmpty()) {
                ManasSkill skill = SkillRegistry.SKILLS.get(ResourceLocation.parse(id));
                if (skill != null) list.add(skill);
            }
        }
    }

    private static void storeSkills(ServerPlayer player, String key, List<ResourceLocation> ids) {
        List<String> idStrs = new ArrayList<>();
        for (ResourceLocation id : ids) {
            idStrs.add(id.toString());
        }
        player.getPersistentData().putString(key, String.join(",", idStrs));
    }

    @Override
    public com.mojang.datafixers.util.Pair<Double, Double> getBaseAuraRange() {
        return com.mojang.datafixers.util.Pair.of(minAura, maxAura);
    }

    @Override
    public com.mojang.datafixers.util.Pair<Double, Double> getBaseMagiculeRange() {
        return com.mojang.datafixers.util.Pair.of(minMagicule, maxMagicule);
    }

    @Override
    public Alignment getAlignment() {
        return Alignment.HOLY;
    }

    @Override
    public void onRaceEvolution(ManasRaceInstance oldInstance, LivingEntity entity, ManasRaceInstance newInstance) {
        if (entity instanceof ServerPlayer player) {
            IExistence existence = TensuraStorages.getExistenceFrom(player);
            if (existence != null) {
                double currentEP = existence.getEP();
                player.getPersistentData().putDouble("primegodling:pre_evolution_ep", currentEP);
            }

            ManasRace newRace = newInstance.getRace();
            if (newRace instanceof PrimeGodlingRace pgr) {
                long newEp = pgr.getEpThreshold();
                for (int i = 0; i < RaceRegistry.EP_THRESHOLDS.length; i++) {
                    if (RaceRegistry.EP_THRESHOLDS[i] == newEp) {
                        var tag = oldInstance.getOrCreateTag();
                        int spent = tag.getInt("nexus_cores_spent");
                        spent += RaceRegistry.CORES_REQUIRED[i];
                        tag.putInt("nexus_cores_spent", spent);
                        int eaten = tag.getInt("nexus_cores_eaten");
                        PacketDistributor.sendToPlayer(player, new SyncNexusCoresPayload(player.getUUID(), eaten, spent));
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onRaceSet(ManasRaceInstance instance, LivingEntity entity) {
        super.onRaceSet(instance, entity);

        resetExistenceData(entity);

        EnergyHelper.gainMagicule(entity, EnergyHelper.getMaxMagicule(entity), GainType.NORMAL);

        if (entity instanceof ServerPlayer player) {
            int stageIndex = stageIndex();
            FTBIntegration.onEvolve(player, stageIndex);
            grantStageAdvancement(player, stageIndex);

            if (epThreshold == RaceRegistry.EP_STAGE_6) {
                EnergyHelper.gainMagicule(entity, EnergyHelper.getMaxMagicule(entity), GainType.NORMAL);
            }

            Random random = new Random();

            if (epThreshold == RaceRegistry.EP_STAGE_2 && ResistanceHelper.isInitialized()) {
                String existingRes = player.getPersistentData().getString("primegodling:granted_resistances");
                String existingSkills = player.getPersistentData().getString("primegodling:granted_skills");
                int resCount = com.primegodling.primegodling.common.config.RaceConfig.COMMON.primeGodlingResistanceCount.get();
                int skillCount = com.primegodling.primegodling.common.config.RaceConfig.COMMON.primeGodlingSkillCount.get();
                if (resCount > 0 && existingRes.isEmpty()) {
                    List<ResourceLocation> res = ResistanceHelper.getRandomResistances(resCount, random);
                    storeSkills(player, "primegodling:granted_resistances", res);
                    player.sendSystemMessage(Component.literal("§b✦ " + resCount + " random resistances granted!"));
                }
                if (skillCount > 0 && existingSkills.isEmpty()) {
                    List<ResourceLocation> skills = ResistanceHelper.getRandomNonResistanceSkills(skillCount, random);
                    storeSkills(player, "primegodling:granted_skills", skills);
                    player.sendSystemMessage(Component.literal("§b✦ " + skillCount + " random skills granted!"));
                }
            }

            if (epThreshold == RaceRegistry.EP_STAGE_3 && ResistanceHelper.isInitialized()) {
                String existingNulls = player.getPersistentData().getString("primegodling:granted_nullifications");
                if (existingNulls.isEmpty()) {
                    String stored = player.getPersistentData().getString("primegodling:granted_resistances");
                    List<ResourceLocation> nullIds = new ArrayList<>();
                    for (String resStr : stored.split(",")) {
                        resStr = resStr.trim();
                        if (resStr.isEmpty()) continue;
                        ResourceLocation nullId = ResistanceHelper.getNullificationFor(ResourceLocation.parse(resStr));
                        if (nullId != null) nullIds.add(nullId);
                    }
                    storeSkills(player, "primegodling:granted_nullifications", nullIds);
                    player.getPersistentData().remove("primegodling:granted_resistances");
                    player.sendSystemMessage(Component.literal("§d✦ Resistances evolved to nullifications!"));
                }

                String existingStage3Skills = player.getPersistentData().getString("primegodling:granted_skills_stage3");
                int skillCount = com.primegodling.primegodling.common.config.RaceConfig.COMMON.celestialGodlingSkillCount.get();
                if (skillCount > 0 && existingStage3Skills.isEmpty()) {
                    List<ResourceLocation> chosen = ResistanceHelper.getRandomNonResistanceSkills(skillCount, random);
                    List<ResourceLocation> granted = new ArrayList<>();
                    for (ResourceLocation skillId : chosen) {
                        if (ResistanceHelper.grantOrMasterSkill(player, skillId, random)) {
                            granted.add(skillId);
                        }
                    }
                    storeSkills(player, "primegodling:granted_skills_stage3", granted);
                    player.sendSystemMessage(Component.literal("§d✦ " + granted.size() + " skills granted or mastered!"));
                }
            }

            if (epThreshold == RaceRegistry.EP_STAGE_6 && ResistanceHelper.isInitialized()) {
                String existingAllNulls = player.getPersistentData().getString("primegodling:granted_nullifications_all");
                if (existingAllNulls.isEmpty()) {
                    List<ResourceLocation> allNulls = ResistanceHelper.getAllNullifications();
                    storeSkills(player, "primegodling:granted_nullifications_all", allNulls);
                    player.sendSystemMessage(Component.literal("§5✦ All nullifications granted!"));
                }

                Skills skills = SkillAPI.getSkillsFrom(player);
                if (skills != null && skills.getSkill(com.primegodling.primegodling.common.data.SkillRegistry.CREATION_AUTHORITY).isEmpty()) {
                    skills.learnSkill(com.primegodling.primegodling.common.data.SkillRegistry.CREATION_AUTHORITY);
                    player.sendSystemMessage(Component.literal("§5✦ Ultimate Skill: Creation Authority acquired!"));
                }

                String existingUnique = player.getPersistentData().getString("primegodling:granted_unique_skill");
                if (existingUnique.isEmpty()) {
                    List<ResourceLocation> uniques = ResistanceHelper.getRandomUniqueSkills(1, random);
                    if (!uniques.isEmpty()) {
                        String uniqueStr = uniques.get(0).toString();
                        player.getPersistentData().putString("primegodling:granted_unique_skill", uniqueStr);
                        player.sendSystemMessage(Component.literal("§5✦ A unique skill has been bestowed!"));
                    }
                }
            }
        }
    }

    private int stageIndex() {
        for (int i = 0; i < RaceRegistry.EP_THRESHOLDS.length; i++) {
            if (epThreshold == RaceRegistry.EP_THRESHOLDS[i]) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Requirement: player must be awakened as Divine Nexus, OR be a True Demon Lord, OR be a Hero.
     */
    private static class AwakenedOrTDLOrHeroRequirement extends EvolutionRequirement {

        @Override
        public float getProgress(ManasRaceInstance instance, LivingEntity entity) {
            if (!(entity instanceof ServerPlayer player)) return 0;

            boolean awakened = player.getPersistentData().getBoolean("primegodling:awakened_nexus");
            if (awakened) return 1.0f;

            IExistence existence = TensuraStorages.getExistenceFrom(player);
            if (existence != null) {
                if (existence.isTrueDemonLord()) return 1.0f;
                if (existence.isTrueHero()) return 1.0f;
            }

            return 0;
        }

        @Override
        public Component getRequirementComponent(ManasRaceInstance instance, LivingEntity entity) {
            boolean met = getProgress(instance, entity) >= 1.0f;
            if (met) return Component.literal("§a✔ Awakening: Divine Nexus / TDL / Hero");

            if (entity instanceof ServerPlayer player) {
                IExistence existence = TensuraStorages.getExistenceFrom(player);
                if (existence != null) {
                    if (existence.isTrueDemonLord()) return Component.literal("§a✔ True Demon Lord");
                    if (existence.isTrueHero()) return Component.literal("§a✔ True Hero");
                }
            }
            return Component.literal("§cRequires: Divine Nexus Awakening §7OR§c True Demon Lord §7OR§c True Hero");
        }
    }

    private static final String[] STAGE_ADVANCEMENT_IDS = {
        "half_godling", "demi_godling", "prime_godling", "celestial_godling",
        "ecliptic_godling", "new_god", "primordial_supreme_god"
    };

    private static final String[] STAGE_ADVANCEMENT_CRITERIA = {
        "become_half_godling", "evolve_demi_godling", "evolve_prime_godling", "evolve_celestial_godling",
        "evolve_ecliptic_godling", "evolve_new_god", "evolve_primordial_supreme_god"
    };

    private static void grantStageAdvancement(ServerPlayer player, int stageIndex) {
        if (stageIndex < 0 || stageIndex >= STAGE_ADVANCEMENT_IDS.length) return;
        AdvancementHolder adv = player.getServer().getAdvancements()
            .get(ResourceLocation.parse("primegodling:" + STAGE_ADVANCEMENT_IDS[stageIndex]));
        if (adv != null) {
            player.getAdvancements().award(adv, STAGE_ADVANCEMENT_CRITERIA[stageIndex]);
        }
    }
}
