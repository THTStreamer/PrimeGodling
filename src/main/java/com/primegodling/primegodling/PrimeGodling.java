package com.primegodling.primegodling;

import com.primegodling.primegodling.client.ClientProxy;
import com.primegodling.primegodling.common.ModItems;
import com.primegodling.primegodling.common.config.RaceConfig;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.ModRaces;
import com.primegodling.primegodling.common.data.ModSkills;
import com.primegodling.primegodling.common.integration.PrimeGodlingConfig;
import com.primegodling.primegodling.common.integration.TensuraIntegration;
import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.RaceEvents;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mod(PrimeGodling.MOD_ID)
public class PrimeGodling {
    public static final String MOD_ID = "primegodling";
    public static final ResourceLocation MOD_RL = ResourceLocation.fromNamespaceAndPath(MOD_ID, "root");
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<UUID, FlightData> FLIGHT_DATA = new HashMap<>();

    public PrimeGodling(IEventBus bus) {
        RaceConfig.register();
        SkillConfig.register();

        ModItems.ITEMS.register(bus);
        ModRaces.init();
        ModSkills.init();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            ClientProxy.init(bus);
        }
        TensuraIntegration.register(bus);

        NeoForge.EVENT_BUS.addListener(PrimeGodling::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(PrimeGodling::onPlayerTick);

        RaceEvents.ACTIVATE_ABILITY.register((raceInstance, entity) -> {
            if (entity instanceof ServerPlayer player && raceInstance.is(TensuraRaceTags.HAS_CREATIVE_FLIGHT)) {
                if (player.getAbilities().flying) {
                    player.getAbilities().flying = false;
                } else {
                    player.getAbilities().mayfly = true;
                    player.getAbilities().flying = true;
                }
                player.onUpdateAbilities();
                return EventResult.interruptFalse();
            }
            return EventResult.pass();
        });
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isCreative() || player.isSpectator()) return;

        Races races = RaceAPI.getRaceFrom(player);
        Optional<ManasRaceInstance> opt = races.getRace();
        if (opt.isEmpty()) return;
        ManasRaceInstance raceInstance = opt.get();

        if (!raceInstance.is(TensuraRaceTags.HAS_CREATIVE_FLIGHT)) {
            FLIGHT_DATA.remove(player.getUUID());
            return;
        }

        UUID uuid = player.getUUID();
        FlightData fd = FLIGHT_DATA.computeIfAbsent(uuid, k -> new FlightData());

        boolean hasSub = false;
        for (LivingEntity e : player.serverLevel().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(32),
                e -> e != player && e.isAlive() && e.hasCustomName() && SubordinateHelper.isSubordinate(e, player))) {
            hasSub = true;
            break;
        }

        int activationCost = hasSub ? RaceConfig.COMMON.flightActivationCostSub.get() : RaceConfig.COMMON.flightActivationCost.get();
        int maintenanceCost = hasSub ? RaceConfig.COMMON.flightMaintenanceCostSub.get() : RaceConfig.COMMON.flightMaintenanceCost.get();
        int maintenanceInterval = hasSub ? RaceConfig.COMMON.flightMaintenanceIntervalSub.get() : RaceConfig.COMMON.flightMaintenanceInterval.get();

        boolean sufficientMagicule = !EnergyHelper.isOutOfEnergy(player, activationCost, 0.0);
        boolean isFlying = player.getAbilities().mayfly && player.getAbilities().flying;

        if (fd.flightLocked) {
            if (sufficientMagicule) {
                fd.flightLocked = false;
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (!player.getAbilities().mayfly && sufficientMagicule) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        if (isFlying) {
            if (!fd.wasFlying) {
                if (EnergyHelper.isOutOfEnergy(player, activationCost, 0.0)) {
                    disableFlight(player);
                    fd.flightLocked = true;
                } else {
                    EnergyHelper.gainMagicule(player, -activationCost, EnergyHelper.GainType.NORMAL);
                }
            }

            fd.maintenanceCounter++;
            if (fd.maintenanceCounter >= maintenanceInterval) {
                fd.maintenanceCounter = 0;
                if (EnergyHelper.isOutOfEnergy(player, maintenanceCost, 0.0)) {
                    disableFlight(player);
                    fd.flightLocked = true;
                } else {
                    EnergyHelper.gainMagicule(player, -maintenanceCost, EnergyHelper.GainType.NORMAL);
                }
            }
        }

        fd.wasFlying = isFlying;
    }

    private static void disableFlight(ServerPlayer player) {
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }

    private static class FlightData {
        int maintenanceCounter = 0;
        boolean wasFlying = false;
        boolean flightLocked = false;
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer killer) {
            LivingEntity killed = event.getEntity();

            if (killed instanceof ServerPlayer targetPlayer) {
                io.github.manasmods.tensura.storage.ep.IExistence targetExistence = io.github.manasmods.tensura.storage.TensuraStorages.getExistenceFrom(targetPlayer);
                if (targetExistence != null && targetExistence.isTrueDemonLord()) {
                    int count = killer.getPersistentData().getInt("primegodling:demon_lord_kills") + 1;
                    killer.getPersistentData().putInt("primegodling:demon_lord_kills", count);
                    killer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§dYou killed an Awakened Demon Lord! §7(" + count + "/3)"));
                }
            }

            ResourceLocation killedId = net.minecraft.world.entity.EntityType.getKey(killed.getType());
            if (killedId.equals(ResourceLocation.parse("tensura:hinata_sakaguchi"))) {
                killer.getPersistentData().putBoolean("primegodling:hinata_killed", true);
                killer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§dYou killed Hinata Sakaguchi!"));
            }
            if (killedId.equals(ResourceLocation.parse("tensura:rimuru"))
                    || killedId.equals(ResourceLocation.parse("tensura:rimuru_tempest"))
                    || killedId.equals(ResourceLocation.parse("tensura:true_dragon_rimuru"))) {
                killer.getPersistentData().putBoolean("primegodling:rimuru_killed", true);
                killer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§dYou killed Rimuru Tempest!"));
            }

            if (killed instanceof Monster) {
                int count = killer.getPersistentData().getInt("primegodling:hostile_mob_kills") + 1;
                killer.getPersistentData().putInt("primegodling:hostile_mob_kills", count);
            }
        }
    }
}
