package com.primegodling.primegodling.common.data.race;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.RaceRegistry;
import com.primegodling.primegodling.common.integration.FTBIntegration;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.EnergyHelper.GainType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        if (epThreshold <= 0) {
            reqs.put(new FixedEPRequirement(50_000), 70.0f);
            reqs.put(new NexusCoreRequirement(10), 30.0f);
            return reqs;
        }

        if (epThreshold == RaceRegistry.EP_STAGE_1) {
            reqs.put(new FixedEPRequirement(100_000), 70.0f);
            reqs.put(new NexusCoreRequirement(40), 30.0f);
        } else if (epThreshold == RaceRegistry.EP_STAGE_2) {
            reqs.put(new FixedEPRequirement(200_000), 70.0f);
            reqs.put(new NexusCoreRequirement(160), 30.0f);
        } else if (epThreshold == RaceRegistry.EP_STAGE_3) {
            reqs.put(new FixedEPRequirement(400_000), 70.0f);
            reqs.put(new NexusCoreRequirement(640), 30.0f);
        } else if (epThreshold == RaceRegistry.EP_STAGE_4) {
            reqs.put(new FixedEPRequirement(800_000), 60.0f);
            reqs.put(new NexusCoreRequirement(2_560), 30.0f);
            reqs.put(new EvolutionRequirement.NamedRequirement(), 10.0f);
        } else if (epThreshold == RaceRegistry.EP_STAGE_5) {
            reqs.put(new FixedEPRequirement(1_600_000), 60.0f);
            reqs.put(new NexusCoreRequirement(10_240), 20.0f);
            reqs.put(new AwakenedOrTDLOrHeroRequirement(), 20.0f);
        }

        return reqs;
    }

    @Override
    public void triggerEvolutionRewards(ManasRaceInstance instance, LivingEntity entity) {
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
        return skills;
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
    public void onRaceSet(ManasRaceInstance instance, LivingEntity entity) {
        super.onRaceSet(instance, entity);

        resetExistenceData(entity);

        EnergyHelper.gainMagicule(entity, EnergyHelper.getMaxMagicule(entity), GainType.NORMAL);

        if (entity instanceof ServerPlayer player) {
            int stageIndex = stageIndex();
            FTBIntegration.onEvolve(player, stageIndex);

            if (epThreshold == RaceRegistry.EP_STAGE_6) {
                EnergyHelper.gainMagicule(entity, EnergyHelper.getMaxMagicule(entity), GainType.NORMAL);
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
}
