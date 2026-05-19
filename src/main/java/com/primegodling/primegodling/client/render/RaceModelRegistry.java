package com.primegodling.primegodling.client.render;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * RaceModelRegistry — registers custom model layers for Prime Godling races.
 * Extend with Shin's Race Models (shins-tensura-race-models) overlay layers
 * when that library is present as a dependency.
 */
public class RaceModelRegistry {
    private static final ModelLayerLocation PRIME_GODLING_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("primegodling", "prime_godling"), "main");

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // event.registerLayerDefinition(PRIME_GODLING_LAYER, HumanoidModel::createHumanoidModel);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Add entity renderer registrations when entity types are defined.
    }
}