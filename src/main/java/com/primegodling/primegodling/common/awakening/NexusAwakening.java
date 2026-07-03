package com.primegodling.primegodling.common.awakening;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.RaceRegistry;
import com.primegodling.primegodling.common.data.SkillRegistry;
import com.primegodling.primegodling.common.integration.FTBIntegration;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class NexusAwakening {

    private static final int RITUAL_TICKS = 200;

    public static String startRitual(ServerPlayer player) {
        int nexusEpCost = SkillConfig.COMMON.nexusCoreEpCost.get();
        int epRequired = SkillConfig.COMMON.awakeningEpRequired.get();
        int coresRequired = SkillConfig.COMMON.awakeningCoresRequired.get();
        int demonLordKillsRequired = SkillConfig.COMMON.awakeningDemonLordKills.get();
        int hostileMobKillsRequired = SkillConfig.COMMON.awakeningHostileMobKills.get();
        boolean requireHinata = SkillConfig.COMMON.awakeningRequireHinata.get();

        ResourceLocation currentRaceId = RaceAPI.getRaceFrom(player)
                .getRace()
                .map(instance -> instance.getRaceId())
                .orElse(null);

        if (currentRaceId == null) {
            return "§cYou have no race!";
        }

        if (!currentRaceId.equals(RaceRegistry.ID_NEW_GOD)) {
            return "§cYou must be New God to attempt Divine Nexus awakening.";
        }

        double currentEP = EnergyHelper.getBaseMaxEP(player);
        int minEp = SkillConfig.COMMON.divineNexusMinEp.get();
        if (currentEP < minEp) {
            return "§cYou need at least " + minEp + " EP to access the Divine Nexus path. You have " + (int) currentEP + " EP.";
        }
        if (currentEP < epRequired) {
            return "§cYou need at least " + epRequired + " EP to attempt the awakening. You have " + (int) currentEP + " EP.";
        }

        IExistence existence = TensuraStorages.getExistenceFrom(player);
        if (existence == null || existence.getName() == null || existence.getName().isBlank()) {
            return "§cYou must obtain a name before attempting the awakening.";
        }

        CompoundTag nexusTag = RaceAPI.getRaceFrom(player)
                .getRace()
                .map(ManasRaceInstance::getOrCreateTag)
                .orElse(null);
        int eaten = nexusTag != null ? nexusTag.getInt("nexus_cores_eaten") : 0;
        if (eaten < coresRequired) {
            return "§cYou have consumed " + eaten + "/" + coresRequired + " Nexus Cores.";
        }

        int demonLordKills = player.getPersistentData().getInt("primegodling:demon_lord_kills");
        boolean hinataKilled = player.getPersistentData().getBoolean("primegodling:hinata_killed");
        int hostileMobKills = player.getPersistentData().getInt("primegodling:hostile_mob_kills");

        // Check boss mobs from config
        List<String> bossMobs = new java.util.ArrayList<>(SkillConfig.COMMON.awakeningBossMobs.get());
        boolean allBossMobsKilled = true;
        for (String mobId : bossMobs) {
            if (!player.getPersistentData().getBoolean("primegodling:killed_" + mobId)) {
                allBossMobsKilled = false;
                break;
            }
        }

        boolean hinataReqMet = !requireHinata || hinataKilled;
        boolean killReqMet = demonLordKills >= demonLordKillsRequired
                || (hinataReqMet && allBossMobsKilled && hostileMobKills >= hostileMobKillsRequired);

        if (!killReqMet) {
            if (demonLordKills < demonLordKillsRequired) {
                player.sendSystemMessage(Component.literal("§cRequirement: Kill " + demonLordKillsRequired + " Awakened Demon Lords (" + demonLordKills + "/" + demonLordKillsRequired + ")"));
            }
            if (requireHinata && !hinataKilled) {
                player.sendSystemMessage(Component.literal("§cRequirement: Kill Hinata Sakaguchi"));
            }
            for (String mobId : bossMobs) {
                if (!player.getPersistentData().getBoolean("primegodling:killed_" + mobId)) {
                    player.sendSystemMessage(Component.literal("§cRequirement: Kill " + mobId));
                }
            }
            if (hostileMobKills < hostileMobKillsRequired) {
                player.sendSystemMessage(Component.literal("§cRequirement: Kill " + hostileMobKillsRequired + " hostile mobs (" + hostileMobKills + "/" + hostileMobKillsRequired + ")"));
            }
            return "§cYou have not fulfilled the kill requirements.";
        }

        double currentMaxMagicule = EnergyHelper.getBaseMaxMagicule(player);
        if (currentMaxMagicule < nexusEpCost) {
            return "§cYou need at least " + nexusEpCost + " magicule to fuel the awakening. You have " + (int) currentMaxMagicule + ".";
        }

        int minSkills = SkillConfig.COMMON.divineNexusMinSkills.get();
        Skills skillStorage = SkillAPI.getSkillsFrom(player);
        if (skillStorage != null) {
            int uniqueCount = skillStorage.getLearnedSkills().size();
            if (uniqueCount < minSkills) {
                return "§cYou need at least " + minSkills + " unique skills. You have " + uniqueCount + ".";
            }
        }

        if (nexusTag != null) nexusTag.remove("nexus_cores_eaten");
        player.getPersistentData().putInt("primegodling:nexus_ritual", RITUAL_TICKS);
        player.getPersistentData().putDouble("primegodling:ritual_max_magicule", currentMaxMagicule);
        player.getPersistentData().putDouble("primegodling:ritual_max_aura", EnergyHelper.getBaseMaxAura(player));

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, RITUAL_TICKS + 40, 255, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, RITUAL_TICKS + 40, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, RITUAL_TICKS + 40, 4, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, RITUAL_TICKS + 40, 0, false, false, true));

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 3.0f, 1.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2.0f, 0.8f);

        return "§6✦ The Divine Nexus ritual has begun...";
    }

    public static void handleRitualTick(ServerPlayer player) {
        int ticks = player.getPersistentData().getInt("primegodling:nexus_ritual");
        if (ticks <= 0) return;

        ticks--;
        player.getPersistentData().putInt("primegodling:nexus_ritual", ticks);

        ServerLevel level = player.serverLevel();
        double px = player.getX();
        double py = player.getY() + 1.0;
        double pz = player.getZ();

        int remaining = RITUAL_TICKS - ticks;

        // Phase 1 (first 60 ticks): Rising golden particles
        if (remaining < 60) {
            level.sendParticles(ParticleTypes.WAX_ON,
                    px, py, pz, 3, 0.8, 0.3, 0.8, 0.02);
            level.sendParticles(ParticleTypes.GLOW,
                    px, py + 0.5, pz, 2, 0.5, 0.5, 0.5, 0.01);
        }
        // Phase 2 (60-140 ticks): Swirling divine dome
        else if (remaining < 140) {
            for (int i = 0; i < 4; i++) {
                double angle = (ticks * 0.1 + i * Math.PI / 2);
                double radius = 2.0 + Math.sin(ticks * 0.05) * 0.5;
                double sx = px + Math.cos(angle) * radius;
                double sz = pz + Math.sin(angle) * radius;
                level.sendParticles(ParticleTypes.END_ROD, sx, py + Math.sin(ticks * 0.08 + i) * 2.0, sz,
                        1, 0, 0, 0, 0);
            }
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    px, py + 1.5, pz, 2, 0.5, 0.5, 0.5, 0.03);
            if (remaining % 20 == 0) {
                level.playSound(null, px, py, pz,
                        SoundEvents.BEACON_AMBIENT, SoundSource.PLAYERS, 0.8f, 1.2f);
            }
        }
        // Phase 3 (140-200 ticks): Intensifying light
        else {
            level.sendParticles(ParticleTypes.FIREWORK,
                    px, py + 2.0, pz, 4, 1.0, 1.5, 1.0, 0.05);
            level.sendParticles(ParticleTypes.END_ROD,
                    px, py, pz, 6, 1.2, 2.0, 1.2, 0.04);
            if (remaining % 10 == 0) {
                level.sendParticles(ParticleTypes.FLASH,
                        px, py + 2.0, pz, 1, 0, 0, 0, 0);
                level.playSound(null, px, py, pz,
                        SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.2f, 1.5f);
            }
        }

        if (ticks <= 0) {
            completeRitual(player);
        }
    }

    private static void completeRitual(ServerPlayer player) {
        double currentMaxMagicule = player.getPersistentData().getDouble("primegodling:ritual_max_magicule");
        double currentMaxAura = player.getPersistentData().getDouble("primegodling:ritual_max_aura");
        int nexusEpCost = SkillConfig.COMMON.nexusCoreEpCost.get();

        player.getPersistentData().remove("primegodling:nexus_ritual");
        player.getPersistentData().remove("primegodling:ritual_max_magicule");
        player.getPersistentData().remove("primegodling:ritual_max_aura");

        Skills skills = SkillAPI.getSkillsFrom(player);

        double newMagicule = (currentMaxMagicule - nexusEpCost) * 3.0;
        double newAura = currentMaxAura * 4.0 - newMagicule;
        EnergyHelper.setMaxMagicule(player, newMagicule);
        EnergyHelper.setMaxAura(player, newAura);

        skills.learnSkill(SkillRegistry.CREATION_AUTHORITY);

        player.getPersistentData().putBoolean("primegodling:awakened_nexus", true);

        RaceAPI.getRaceFrom(player).evolveRace(RaceRegistry.ID_PRIMORDIAL_SUPREME_GOD);

        IExistence existence = TensuraStorages.getExistenceFrom(player);
        if (existence != null) {
            existence.setAlignment(Alignment.MAJIN);
            existence.setOriginalAlignment(Alignment.MAJIN);
        }

        AdvancementHolder nexusAdv = player.getServer().getAdvancements()
                .get(ResourceLocation.parse("primegodling:divine_nexus"));
        if (nexusAdv != null) {
            player.getAdvancements().award(nexusAdv, "awaken_divine_nexus");
        }

        ServerLevel level = player.serverLevel();
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        level.playSound(null, px, py, pz,
                SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 3.0f, 0.5f);

        // Spiral ascent
        for (int i = 0; i < 180; i++) {
            double t = i / 60.0;
            double angle = i * 0.35;
            double radius = 2.5 + Math.sin(t * Math.PI) * 0.5;
            double sx = px + Math.cos(angle) * radius;
            double sz = pz + Math.sin(angle) * radius;
            double sy = py + t * 4.0;
            level.sendParticles(ParticleTypes.END_ROD, sx, sy, sz, 1, 0, 0, 0, 0);
            level.sendParticles(ParticleTypes.GLOW, sx, sy + 0.3, sz, 1, 0, 0, 0, 0);
        }

        // Converging sparks
        for (int i = 0; i < 80; i++) {
            double angle = i * 0.25;
            double radius = 6.0 * (1.0 - i / 80.0);
            double sx = px + Math.cos(angle) * radius;
            double sz = pz + Math.sin(angle) * radius;
            double sy = py + 3.0 + Math.sin(angle * 2) * 1.5;
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, sx, sy, sz, 1, 0, 0, 0, 0);
        }

        // Grand finale explosion
        for (int i = 0; i < 100; i++) {
            double angle = i * 0.4;
            double radius = 8.0 + Math.sin(i * 0.3) * 2.0;
            double sx = px + Math.cos(angle) * radius;
            double sz = pz + Math.sin(angle) * radius;
            double sy = py + 2.0 + Math.sin(i * 0.2) * 3.0;
            level.sendParticles(ParticleTypes.FIREWORK, sx, sy, sz, 1, 0, 0, 0, 0);
            level.sendParticles(ParticleTypes.FLASH, sx, sy, sz, 1, 0, 0, 0, 0);
        }

        level.sendParticles(ParticleTypes.FLASH, px, py + 3.0, pz, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.END_ROD, px, py + 3.0, pz, 60, 5.0, 3.0, 5.0, 0.2);
        level.sendParticles(ParticleTypes.GLOW, px, py + 3.0, pz, 40, 4.0, 2.0, 4.0, 0.1);

        level.playSound(null, px, py, pz,
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.5f, 1.0f);
        level.playSound(null, px, py, pz,
                SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 2.0f, 0.5f);

        player.sendSystemMessage(Component.literal("§6✦ Divine Nexus Awakening Complete!"));
        player.sendSystemMessage(Component.literal("§eYou are now a Divine Nexus — one with the primordial source."));

        FTBIntegration.onNexusAwakening(player);

        PrimeGodling.LOGGER.info("[{}] Player {} achieved Divine Nexus Awakening!",
                PrimeGodling.MOD_ID, player.getGameProfile().getName());
    }

    public static boolean isAwakened(ServerPlayer player) {
        return player.getPersistentData().getBoolean("primegodling:awakened_nexus");
    }

    public static int getRitualTicks() {
        return RITUAL_TICKS;
    }
}
