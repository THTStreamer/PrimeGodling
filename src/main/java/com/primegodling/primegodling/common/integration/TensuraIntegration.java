package com.primegodling.primegodling.common.integration;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.ModRaces;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class TensuraIntegration {

    public static void register(IEventBus bus) {
        PrimeGodlingConfig.init(bus);
        bus.addListener(TensuraIntegration::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            PrimeGodling.LOGGER.info("[PrimeGodling] Linking race evolution chain…");
            ModRaces.linkEvolutions();

            PrimeGodling.LOGGER.info("[PrimeGodling] Common setup complete.");
        });
    }
}
