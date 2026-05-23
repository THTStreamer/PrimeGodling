package com.primegodling.primegodling.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncAwakenedPayload(UUID playerUuid, boolean awakened) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncAwakenedPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("primegodling", "sync_awakened"));

    public static final StreamCodec<FriendlyByteBuf, SyncAwakenedPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeUUID(p.playerUuid);
                buf.writeBoolean(p.awakened);
            },
            buf -> new SyncAwakenedPayload(buf.readUUID(), buf.readBoolean())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
