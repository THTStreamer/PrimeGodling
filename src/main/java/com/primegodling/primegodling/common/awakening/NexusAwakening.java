package com.primegodling.primegodling.common.awakening;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.RaceRegistry;
import com.primegodling.primegodling.common.data.SkillRegistry;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class NexusAwakening {

    private static final int RITUAL_TICKS = 120;
    private static final int EP_REQUIRED = 30_000_000;
    private static final int CORES_REQUIRED = 20;

    public static String startRitual(ServerPlayer player) {
        int nexusEpCost = SkillConfig.COMMON.nexusCoreEpCost.get();

        ResourceLocation currentRaceId = RaceAPI.getRaceFrom(player)
                .getRace()
                .map(instance -> instance.getRaceId())
                .orElse(null);

        if (currentRaceId == null) {
            return "§cYou have no race!";
        }

        if (!currentRaceId.equals(RaceRegistry.ID_LUMINARCH_GOD)) {
            return "§cYou must be Luminarch God to attempt Divine Nexus awakening.";
        }

        double currentEP = EnergyHelper.getBaseMaxEP(player);
        if (currentEP < EP_REQUIRED) {
            return "§cYou need at least " + EP_REQUIRED + " EP. You have " + (int) currentEP + " EP.";
        }

        IExistence existence = TensuraStorages.getExistenceFrom(player);
        if (existence == null || existence.getName() == null || existence.getName().isBlank()) {
            return "§cYou must obtain a name before attempting the awakening.";
        }

        int demonLordKills = player.getPersistentData().getInt("primegodling:demon_lord_kills");
        boolean rimuruKilled = player.getPersistentData().getBoolean("primegodling:rimuru_killed");
        boolean hinataKilled = player.getPersistentData().getBoolean("primegodling:hinata_killed");
        int hostileMobKills = player.getPersistentData().getInt("primegodling:hostile_mob_kills");

        boolean killReqMet = demonLordKills >= 3 || (rimuruKilled && hinataKilled && hostileMobKills >= 50000);
        if (!killReqMet) {
            if (demonLordKills < 3) {
                player.sendSystemMessage(Component.literal("§cRequirement: Kill 3 Awakened Demon Lords (" + demonLordKills + "/3)"));
            }
            if (!rimuruKilled) {
                player.sendSystemMessage(Component.literal("§cRequirement: Kill Rimuru Tempest"));
            }
            if (!hinataKilled) {
                player.sendSystemMessage(Component.literal("§cRequirement: Kill Hinata Sakaguchi"));
            }
            if (hostileMobKills < 50000) {
                player.sendSystemMessage(Component.literal("§cRequirement: Kill " + 50000 + " hostile mobs (" + hostileMobKills + "/50000)"));
            }
            return "§cYou have not fulfilled the kill requirements.";
        }

        double currentMaxMagicule = EnergyHelper.getBaseMaxMagicule(player);
        if (currentMaxMagicule < nexusEpCost) {
            return "§cYou need at least " + nexusEpCost + " magicule to fuel the awakening. You have " + (int) currentMaxMagicule + ".";
        }

        player.getPersistentData().remove("primegodling:nexus_cores_eaten");
        player.getPersistentData().putInt("primegodling:nexus_ritual", RITUAL_TICKS);
        player.getPersistentData().putDouble("primegodling:ritual_max_magicule", currentMaxMagicule);
        player.getPersistentData().putDouble("primegodling:ritual_max_aura", EnergyHelper.getBaseMaxAura(player));

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, RITUAL_TICKS + 40, 255, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, RITUAL_TICKS + 40, 0, false, false, true));

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 2.0f, 0.5f);

        return "§6✦ The Divine Nexus ritual has begun...";
    }

    public static void handleRitualTick(ServerPlayer player) {
        int ticks = player.getPersistentData().getInt("primegodling:nexus_ritual");
        if (ticks <= 0) return;

        ticks--;
        player.getPersistentData().putInt("primegodling:nexus_ritual", ticks);

        ServerLevel level = player.serverLevel();

        if (ticks % 15 == 0) {
            level.sendParticles(ParticleTypes.SOUL,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    4, 0.5, 0.5, 0.5, 0.05);
            level.sendParticles(ParticleTypes.FLASH,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    1, 0.5, 0.5, 0.5, 0.0);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.5f, 0.6f);
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

        AdvancementHolder nexusAdv = player.getServer().getAdvancements()
                .get(ResourceLocation.parse("primegodling:divine_nexus"));
        if (nexusAdv != null) {
            player.getAdvancements().award(nexusAdv, "awaken_divine_nexus");
        }

        ServerLevel level = player.serverLevel();
        level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0, player.getZ(),
                40, 1.0, 1.0, 1.0, 0.5);
        level.sendParticles(ParticleTypes.FLASH,
                player.getX(), player.getY() + 1.5, player.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.5f, 1.0f);

        player.sendSystemMessage(Component.literal("§6✦ Divine Nexus Awakening Complete!"));
        player.sendSystemMessage(Component.literal("§eYou are now connected to the primordial source."));

        PrimeGodling.LOGGER.info("[{}] Player {} achieved Divine Nexus Awakening!",
                PrimeGodling.MOD_ID, player.getGameProfile().getName());
    }

    public static boolean isAwakened(ServerPlayer player) {
        return player.getPersistentData().getBoolean("primegodling:awakened_nexus");
    }
}
