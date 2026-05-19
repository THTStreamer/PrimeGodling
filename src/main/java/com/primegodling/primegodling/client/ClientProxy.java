package com.primegodling.primegodling.client;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import com.primegodling.primegodling.client.render.RaceModelRegistry;
import com.primegodling.primegodling.client.render.halo.PrimordialHaloLayer;

public class ClientProxy {
    public static void init(IEventBus bus) {
        bus.addListener(RaceModelRegistry::registerLayerDefinitions);
        bus.addListener(RaceModelRegistry::registerRenderers);
        bus.addListener(ClientProxy::registerScreens);
        bus.addListener(ClientProxy::onAddLayers);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        // Intrinsic skills are not screens — kept as extension point.
    }

    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new PrimordialHaloLayer(renderer));
            }
        }
    }
}