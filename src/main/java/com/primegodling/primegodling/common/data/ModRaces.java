package com.primegodling.primegodling.common.data;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.race.PrimeGodlingRace;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.impl.RaceRegistry;

import java.util.List;

public class ModRaces {

    private static final DeferredRegister<ManasRace> RACES =
            DeferredRegister.create(PrimeGodling.MOD_ID, RaceRegistry.KEY);

    public static final RegistrySupplier<ManasRace> HALF_GODLING;
    public static final RegistrySupplier<ManasRace> DEMI_GODLING;
    public static final RegistrySupplier<ManasRace> PRIME_GODLING;
    public static final RegistrySupplier<ManasRace> CELESTIAL_GODLING;
    public static final RegistrySupplier<ManasRace> ECLIPTIC_GODLING;
    public static final RegistrySupplier<ManasRace> NEW_GOD;
    public static final RegistrySupplier<ManasRace> PRIMORDIAL_SUPREME_GOD;

    static {
        HALF_GODLING = RACES.register("half_godling",
                () -> RaceData.createStage(0));
        DEMI_GODLING = RACES.register("demi_godling",
                () -> RaceData.createStage(1));
        PRIME_GODLING = RACES.register("prime_godling",
                () -> RaceData.createStage(2));
        CELESTIAL_GODLING = RACES.register("celestial_godling",
                () -> RaceData.createStage(3));
        ECLIPTIC_GODLING = RACES.register("ecliptic_godling",
                () -> RaceData.createStage(4));
        NEW_GOD = RACES.register("new_god",
                () -> RaceData.createStage(5));
        PRIMORDIAL_SUPREME_GOD = RACES.register("primordial_supreme_god",
                () -> RaceData.createStage(6));
    }

    public static void init() {
        RACES.register();
    }

    public static void linkEvolutions() {
        List<PrimeGodlingRace> list = getInstances();
        for (int i = 0; i < list.size() - 1; i++) {
            list.get(i).setNextEvolution(list.get(i + 1));
        }
    }

    private static List<PrimeGodlingRace> getInstances() {
        return List.of(
                (PrimeGodlingRace) HALF_GODLING.get(),
                (PrimeGodlingRace) DEMI_GODLING.get(),
                (PrimeGodlingRace) PRIME_GODLING.get(),
                (PrimeGodlingRace) CELESTIAL_GODLING.get(),
                (PrimeGodlingRace) ECLIPTIC_GODLING.get(),
                (PrimeGodlingRace) NEW_GOD.get(),
                (PrimeGodlingRace) PRIMORDIAL_SUPREME_GOD.get()
        );
    }
}
