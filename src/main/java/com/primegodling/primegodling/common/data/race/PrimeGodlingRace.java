package com.primegodling.primegodling.common.data.race;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.RaceRegistry;
import com.primegodling.primegodling.common.data.SkillRegistry;
import com.primegodling.primegodling.common.integration.FTBIntegration;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PrimeGodlingRace extends DefaultRace {

    private final List<Supplier<ManasSkill>> intrinsicSkillSuppliers;
    private final long epThreshold;
    private final double minMagicule;
    private final double maxMagicule;
    private final double auraCap;
    private ManasRace nextEvolution;

    public PrimeGodlingRace(Difficulty difficulty, long epThreshold, double minMagicule, double maxMagicule, double auraCap, List<Supplier<ManasSkill>> intrinsicSkillSuppliers) {
        super(difficulty);
        this.epThreshold = epThreshold;
        this.minMagicule = minMagicule;
        this.maxMagicule = maxMagicule;
        this.auraCap = auraCap;
        this.intrinsicSkillSuppliers = new ArrayList<>(intrinsicSkillSuppliers);
    }

    @Override
    public RaceConfig.Default getDefaultConfig() {
        return new RaceConfig.Default() {
            @Override public double getMinAura() { return auraCap; }
            @Override public double getMaxAura() { return auraCap; }
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
        addAttributeModifier(attribute, ResourceLocation.fromNamespaceAndPath("primegodling", name), amount, operation);
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
        if (epThreshold <= 0) return Map.of();
        if (epThreshold == 30_000_000) {
            return Map.of(
                new ScaledEPRequirement(5.0), 70.0f,
                new EvolutionRequirement.NamedRequirement(), 15.0f,
                new NexusCoreRequirement(), 15.0f
            );
        }
        return Map.of(new ScaledEPRequirement(5.0), 100.0f);
    }

    @Override
    public void triggerEvolutionRewards(ManasRaceInstance instance, LivingEntity entity) {
        super.triggerEvolutionRewards(instance, entity);
        if (entity instanceof ServerPlayer player) {
            int stageIndex = 0;
            for (int i = 0; i < RaceRegistry.EP_THRESHOLDS.length; i++) {
                if (epThreshold == RaceRegistry.EP_THRESHOLDS[i]) {
                    stageIndex = i;
                    break;
                }
            }
            FTBIntegration.onEvolve(player, stageIndex);
            if (epThreshold == RaceRegistry.EP_STAGE_4) {
                awakenNexus(player);
            }
        }
    }

    private void awakenNexus(ServerPlayer player) {
        double nexusEpCost = SkillConfig.COMMON.nexusCoreEpCost.get();
        double currentMaxMagicule = EnergyHelper.getBaseMaxMagicule(player);
        double currentMaxAura = EnergyHelper.getBaseMaxAura(player);

        double newMagicule = (currentMaxMagicule - nexusEpCost) * 3.0;
        double newAura = currentMaxAura * 4.0 - newMagicule;
        EnergyHelper.setMaxMagicule(player, newMagicule);
        EnergyHelper.setMaxAura(player, newAura);

        Skills skills = SkillAPI.getSkillsFrom(player);
        skills.learnSkill(SkillRegistry.CREATION_AUTHORITY);

        player.getPersistentData().remove("primegodling:nexus_cores_eaten");
        player.getPersistentData().putBoolean("primegodling:awakened_nexus", true);

        var nexusAdv = player.getServer().getAdvancements()
                .get(ResourceLocation.parse("primegodling:divine_nexus"));
        if (nexusAdv != null) {
            player.getAdvancements().award(nexusAdv, "awaken_divine_nexus");
        }

        ServerLevel level = player.serverLevel();
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0, player.getZ(),
                40, 1.0, 1.0, 1.0, 0.5);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.FLASH,
                player.getX(), player.getY() + 1.5, player.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                net.minecraft.sounds.SoundSource.PLAYERS, 1.5f, 1.0f);

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6✦ Divine Nexus Awakening Complete!"));
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§eYou are now connected to the primordial source."));

        FTBIntegration.onNexusAwakening(player);

        PrimeGodling.LOGGER.info("[{}] Player {} achieved Divine Nexus Awakening!",
                PrimeGodling.MOD_ID, player.getGameProfile().getName());
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
        return com.mojang.datafixers.util.Pair.of(auraCap, auraCap);
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
        ScaledEPRequirement.storeEntryEP(entity);
        if (nextEvolution == null) {
            EnergyHelper.gainMagicule(entity, EnergyHelper.getMaxMagicule(entity), EnergyHelper.GainType.NORMAL);
        }
    }
}
