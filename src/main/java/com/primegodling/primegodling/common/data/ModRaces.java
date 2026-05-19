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

    public static final RegistrySupplier<ManasRace> PRIME_GODLING;
    public static final RegistrySupplier<ManasRace> CELESTIAL_ESSENCE;
    public static final RegistrySupplier<ManasRace> ECLIPTIC_WARDEN;
    public static final RegistrySupplier<ManasRace> LUMINARCH_GOD;
    public static final RegistrySupplier<ManasRace> PRIMORDIAL_SUPREME_GOD;

    static {
        PRIME_GODLING = RACES.register("prime_godling",
                () -> RaceData.createStage(0));
        CELESTIAL_ESSENCE = RACES.register("celestial_essence",
                () -> RaceData.createStage(1));
        ECLIPTIC_WARDEN = RACES.register("ecliptic_warden",
                () -> RaceData.createStage(2));
        LUMINARCH_GOD = RACES.register("luminarch_god",
                () -> RaceData.createStage(3));
        PRIMORDIAL_SUPREME_GOD = RACES.register("primordial_supreme_god",
                () -> RaceData.createStage(4));
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
                (PrimeGodlingRace) PRIME_GODLING.get(),
                (PrimeGodlingRace) CELESTIAL_ESSENCE.get(),
                (PrimeGodlingRace) ECLIPTIC_WARDEN.get(),
                (PrimeGodlingRace) LUMINARCH_GOD.get(),
                (PrimeGodlingRace) PRIMORDIAL_SUPREME_GOD.get()
        );
    }
}
