package com.primegodling.primegodling.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.primegodling.primegodling.client.render.RaceModelRegistry;

public class ClientProxy {
    public static void init(IEventBus bus) {
        bus.addListener(RaceModelRegistry::registerLayerDefinitions);
        bus.addListener(RaceModelRegistry::registerRenderers);
        bus.addListener(ClientProxy::registerScreens);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        // Intrinsic skills are not screens — kept as extension point.
    }
}