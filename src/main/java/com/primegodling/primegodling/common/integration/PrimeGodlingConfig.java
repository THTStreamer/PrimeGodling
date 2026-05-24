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
    private static final List<String> SKILL_IDS = RaceRegistry.ALL_SKILL_IDS.stream()
            .map(s -> "primegodling:" + s)
            .toList();

    public static void init(IEventBus modBus) {
        modBus.addListener(PrimeGodlingConfig::onConfigLoad);
    }

    private static void onConfigLoad(ModConfigEvent.Loading event) {
        applyInjection();
    }

    static void applyInjection() {
        try {
            ReincarnationConfig rc = ConfigRegistry.getConfig(ReincarnationConfig.class);
            if (rc == null) return;

            LinkedHashSet<String> startingSet = new LinkedHashSet<>(rc.Races.startingRaces);
            LinkedHashSet<String> randomSet = new LinkedHashSet<>(rc.Races.randomRaces);
            LinkedHashSet<String> reincarnationSet = new LinkedHashSet<>(rc.Races.reincarnationRaces);

            startingSet.removeAll(EVOLVED_IDS);
            randomSet.removeAll(EVOLVED_IDS);
            reincarnationSet.removeAll(EVOLVED_IDS);
            startingSet.add(STARTING_RACE_ID);
            randomSet.add(STARTING_RACE_ID);
            reincarnationSet.add(STARTING_RACE_ID);

            rc.Races.startingRaces = new ArrayList<>(startingSet);
            rc.Races.randomRaces = new ArrayList<>(randomSet);
            rc.Races.reincarnationRaces = new ArrayList<>(reincarnationSet);

            LinkedHashSet<String> skillSet = new LinkedHashSet<>(rc.Skills.startingSkills);
            skillSet.addAll(SKILL_IDS);
            rc.Skills.startingSkills = new ArrayList<>(skillSet);

            PrimeGodling.LOGGER.info("[{}] Injected into Tensura config. startingRaces={}, startingSkills={}",
                    PrimeGodling.MOD_ID, rc.Races.startingRaces.size(), rc.Skills.startingSkills.size());

        } catch (Exception e) {
            PrimeGodling.LOGGER.error("[{}] Failed to inject races into Tensura config",
                    PrimeGodling.MOD_ID, e);
        }
    }
}
