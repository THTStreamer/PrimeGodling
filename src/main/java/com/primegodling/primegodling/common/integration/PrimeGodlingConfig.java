package com.primegodling.primegodling.common.integration;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.RaceRegistry;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.ReincarnationConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class PrimeGodlingConfig {

    private static final String STARTING_RACE_ID = "primegodling:" + RaceRegistry.HALF_GODLING;
    private static final List<String> EVOLVED_IDS = RaceRegistry.ALL_STAGES_KEY.stream()
            .filter(s -> !s.equals(RaceRegistry.HALF_GODLING))
            .map(s -> "primegodling:" + s)
            .toList();
    public static void init(IEventBus modBus) {
        tryInject("init");
        modBus.addListener(PrimeGodlingConfig::onConfigLoad);
    }

    private static void onConfigLoad(ModConfigEvent.Loading event) {
        tryInject("config_load");
    }

    static void applyInjection() {
        tryInject("common_setup");
    }

    private static void tryInject(String phase) {
        try {
            ReincarnationConfig rc = ConfigRegistry.getConfig(ReincarnationConfig.class);
            if (rc == null) {
                PrimeGodling.LOGGER.info("[{}] tryInject({}): ReincarnationConfig not available yet",
                        PrimeGodling.MOD_ID, phase);
                return;
            }

            List<String> before = new ArrayList<>(rc.Races.startingRaces);

            LinkedHashSet<String> startingSet = new LinkedHashSet<>(rc.Races.startingRaces);
            LinkedHashSet<String> randomSet = new LinkedHashSet<>(rc.Races.randomRaces);
            LinkedHashSet<String> reincarnationSet = new LinkedHashSet<>(rc.Races.reincarnationRaces);
            LinkedHashSet<String> masteredSet = new LinkedHashSet<>(rc.Races.reincarnationRacesMastered);
            LinkedHashSet<String> masteredRandomSet = new LinkedHashSet<>(rc.Races.reincarnationRandomRacesMastered);

            startingSet.removeAll(EVOLVED_IDS);
            randomSet.removeAll(EVOLVED_IDS);
            reincarnationSet.removeAll(EVOLVED_IDS);
            masteredSet.removeAll(EVOLVED_IDS);
            masteredRandomSet.removeAll(EVOLVED_IDS);
            startingSet.add(STARTING_RACE_ID);
            randomSet.add(STARTING_RACE_ID);
            reincarnationSet.add(STARTING_RACE_ID);
            masteredSet.add(STARTING_RACE_ID);
            masteredRandomSet.add(STARTING_RACE_ID);

            rc.Races.startingRaces = new ArrayList<>(startingSet);
            rc.Races.randomRaces = new ArrayList<>(randomSet);
            rc.Races.reincarnationRaces = new ArrayList<>(reincarnationSet);
            rc.Races.reincarnationRacesMastered = new ArrayList<>(masteredSet);
            rc.Races.reincarnationRandomRacesMastered = new ArrayList<>(masteredRandomSet);

            // Persist to the TOML file so that @SyncToClient syncs our races, not the defaults
            rc.save();

            PrimeGodling.LOGGER.info("[{}] Injected into Tensura config (phase={}). startingRaces: {} -> {}",
                    PrimeGodling.MOD_ID, phase, before, rc.Races.startingRaces);

        } catch (Exception e) {
            PrimeGodling.LOGGER.error("[{}] tryInject({}) failed", PrimeGodling.MOD_ID, phase, e);
        }
    }
}
