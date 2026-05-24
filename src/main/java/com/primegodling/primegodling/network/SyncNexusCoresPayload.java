package com.primegodling.primegodling.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncNexusCoresPayload(UUID playerUuid, int eaten, int spent) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncNexusCoresPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("primegodling", "sync_nexus_cores"));

    public static final StreamCodec<FriendlyByteBuf, SyncNexusCoresPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeUUID(p.playerUuid);
                buf.writeVarInt(p.eaten);
                buf.writeVarInt(p.spent);
            },
            buf -> new SyncNexusCoresPayload(buf.readUUID(), buf.readVarInt(), buf.readVarInt())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
