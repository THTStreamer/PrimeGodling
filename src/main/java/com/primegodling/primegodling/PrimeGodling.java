package com.primegodling.primegodling;

import com.primegodling.primegodling.client.ClientProxy;
import com.primegodling.primegodling.common.ModItems;
import com.primegodling.primegodling.common.awakening.NexusAwakening;
import com.primegodling.primegodling.common.config.NexusDropsConfig;
import com.primegodling.primegodling.common.command.DivineNexusCommand;
import com.primegodling.primegodling.common.config.RaceConfig;
import com.primegodling.primegodling.network.PrimeGodlingNetwork;
import com.primegodling.primegodling.common.config.ServerConfig;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.ModRaces;
import com.primegodling.primegodling.common.data.ModSkills;
import com.primegodling.primegodling.common.data.ResistanceHelper;
import com.primegodling.primegodling.common.integration.TensuraIntegration;
import com.mojang.logging.LogUtils;
import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.RaceEvents;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Mod(PrimeGodling.MOD_ID)
public class PrimeGodling {
    public static final String MOD_ID = "primegodling";
    public static final ResourceLocation MOD_RL = ResourceLocation.fromNamespaceAndPath(MOD_ID, "root");
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<UUID, FlightData> FLIGHT_DATA = new HashMap<>();
    /** Tracks each player's last-known base max EP and race for applying the gain multiplier. */
    private static final Map<UUID, EpSnapshot> LAST_EP = new HashMap<>();

    private record EpSnapshot(double ep, ResourceLocation raceId) {};

    public PrimeGodling(IEventBus bus) {
        RaceConfig.register();
        SkillConfig.register();
        ServerConfig.register();
        NexusDropsConfig.register();

        bus.addListener(RaceConfig::onLoad);

        ModItems.ITEMS.register(bus);
        ModRaces.init();
        ModSkills.init();
        ResistanceHelper.init();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            ClientProxy.init(bus);
        }
        TensuraIntegration.register(bus);

        bus.addListener(PrimeGodlingNetwork::onRegisterPayloadHandlers);

        NeoForge.EVENT_BUS.addListener(PrimeGodling::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(PrimeGodling::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(PrimeGodling::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(DivineNexusCommand::onRegisterCommands);

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
        UUID uuid = player.getUUID();

        NexusAwakening.handleRitualTick(player);

        // EP gain multiplier: reduce EP accumulation for Prime Godling races
        ResourceLocation currentRaceId = raceInstance.getRaceId();
        if (currentRaceId.getNamespace().equals(MOD_ID)) {
            double currentMaxEP = EnergyHelper.getBaseMaxEP(player);
            EpSnapshot snap = LAST_EP.get(uuid);
            if (snap != null && currentMaxEP > snap.ep && snap.raceId.equals(currentRaceId)) {
                double gained = currentMaxEP - snap.ep;
                double multiplier = RaceConfig.COMMON.epGainMultiplier.get();
                double allowed = snap.ep + gained * multiplier;
                double maxMagicule = EnergyHelper.getBaseMaxMagicule(player);
                double maxAura = EnergyHelper.getBaseMaxAura(player);
                double total = maxMagicule + maxAura;
                if (total > 0) {
                    double mRatio = maxMagicule / total;
                    double aRatio = maxAura / total;
                    double reduction = currentMaxEP - allowed;
                    EnergyHelper.setMaxMagicule(player, maxMagicule - reduction * mRatio);
                    EnergyHelper.setMaxAura(player, maxAura - reduction * aRatio);
                    currentMaxEP = EnergyHelper.getBaseMaxEP(player);
                }
            }
            LAST_EP.put(uuid, new EpSnapshot(currentMaxEP, currentRaceId));
        }

        if (!raceInstance.is(TensuraRaceTags.HAS_CREATIVE_FLIGHT)) {
            FLIGHT_DATA.remove(player.getUUID());
            return;
        }

        FlightData fd = FLIGHT_DATA.computeIfAbsent(uuid, k -> new FlightData());

        boolean hasSub = hasNamedSubordinate(player);
        int activationCost = hasSub ? RaceConfig.COMMON.flightActivationCostSub.get() : RaceConfig.COMMON.flightActivationCost.get();
        int maintenanceCost = hasSub ? RaceConfig.COMMON.flightMaintenanceCostSub.get() : RaceConfig.COMMON.flightMaintenanceCost.get();
        int maintenanceInterval = hasSub ? RaceConfig.COMMON.flightMaintenanceIntervalSub.get() : RaceConfig.COMMON.flightMaintenanceInterval.get();

        boolean isFlying = player.getAbilities().mayfly && player.getAbilities().flying;

        if (fd.flightLocked) {
            if (getCurrentMagicule(player) >= activationCost) {
                fd.flightLocked = false;
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (!player.getAbilities().mayfly && getCurrentMagicule(player) >= activationCost) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        if (isFlying) {
            if (!fd.wasFlying) {
                if (getCurrentMagicule(player) < activationCost) {
                    disableFlight(player);
                    fd.flightLocked = true;
                } else {
                    EnergyHelper.isOutOfMagiculeConsuming(player, activationCost, 0.0);
                }
            }

            fd.maintenanceCounter++;
            if (fd.maintenanceCounter >= maintenanceInterval) {
                fd.maintenanceCounter = 0;
                if (getCurrentMagicule(player) < maintenanceCost) {
                    disableFlight(player);
                    fd.flightLocked = true;
                } else {
                    EnergyHelper.isOutOfMagiculeConsuming(player, maintenanceCost, 0.0);
                }
            }
        }

        fd.wasFlying = isFlying;
    }

    private static final Random RANDOM = new Random();

    private static void onLivingDrops(LivingDropsEvent event) {
        if (!NexusDropsConfig.COMMON.dropsEnabled.get()) return;
        if (!(event.getSource().getEntity() instanceof ServerPlayer)) return;

        LivingEntity killed = event.getEntity();
        if (killed == null) return;

        ResourceLocation killedId = net.minecraft.world.entity.EntityType.getKey(killed.getType());
        String killedKey = killedId.getNamespace() + ":" + killedId.getPath();

        List<? extends String> entries = NexusDropsConfig.COMMON.mobDrops.get();
        for (String entry : entries) {
            String[] parts = entry.split(";");
            if (parts.length < 3) continue;
            if (!parts[0].equals(killedKey)) continue;
            try {
                double chance = Double.parseDouble(parts[1]);
                if (RANDOM.nextDouble() >= chance) break;

                int minAmount = Integer.parseInt(parts[2]);
                int maxAmount = parts.length >= 4 ? Integer.parseInt(parts[3]) : minAmount;
                int amount = minAmount >= maxAmount ? minAmount
                        : minAmount + RANDOM.nextInt(maxAmount - minAmount + 1);

                for (int i = 0; i < amount; i++) {
                    event.getDrops().add(event.getEntity().spawnAtLocation(
                            new ItemStack(ModItems.NEXUS_CORE.get()), 0.0f));
                }
            } catch (NumberFormatException ignored) {
            }
            break;
        }
    }

    private static boolean hasNamedSubordinate(ServerPlayer player) {
        for (LivingEntity e : player.serverLevel().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(32),
                e -> e != player && e.isAlive() && e.hasCustomName() && SubordinateHelper.isSubordinate(e, player))) {
            return true;
        }
        return false;
    }

    private static double getCurrentMagicule(ServerPlayer player) {
        IExistence existence = TensuraStorages.getExistenceFrom(player);
        return existence != null ? existence.getMagicule() : 0;
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
                IExistence targetExistence = TensuraStorages.getExistenceFrom(targetPlayer);
                if (targetExistence != null && targetExistence.isTrueDemonLord()) {
                    int count = killer.getPersistentData().getInt("primegodling:demon_lord_kills") + 1;
                    killer.getPersistentData().putInt("primegodling:demon_lord_kills", count);
                    killer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§dYou killed an Awakened Demon Lord! §7(" + count + "/3)"));
                }
            }

            if (killed == null) return;
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
