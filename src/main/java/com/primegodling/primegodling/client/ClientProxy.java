package com.primegodling.primegodling.client;

import com.primegodling.primegodling.client.render.RaceModelRegistry;
import com.primegodling.primegodling.client.render.halo.HaloMeshRenderer;
import net.neoforged.bus.api.IEventBus;

public class ClientProxy {
    public static void init(IEventBus bus) {
        bus.addListener(RaceModelRegistry::registerLayerDefinitions);
        bus.addListener(RaceModelRegistry::registerRenderers);
        bus.addListener(ClientProxy::registerScreens);

        HaloMeshRenderer.register();
    }

    private static void registerScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
    }
}
