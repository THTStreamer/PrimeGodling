package com.primegodling.primegodling.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PrimeGodlingNetwork {
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                SyncAwakenedPayload.TYPE,
                SyncAwakenedPayload.STREAM_CODEC,
                PrimeGodlingNetwork::handleSyncAwakened
        );
        registrar.playToClient(
                SyncNexusCoresPayload.TYPE,
                SyncNexusCoresPayload.STREAM_CODEC,
                PrimeGodlingNetwork::handleSyncNexusCores
        );
    }

    private static void handleSyncAwakened(SyncAwakenedPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientAwakenedCache.set(payload.playerUuid(), payload.awakened());
        });
    }

    private static void handleSyncNexusCores(SyncNexusCoresPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientNexusCoresCache.set(payload.playerUuid(), payload.eaten(), payload.spent());
        });
    }
}
