package com.primegodling.primegodling.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record SyncKillCountersPayload(UUID playerUuid, int demonLordKills, boolean rimuruKilled, boolean hinataKilled, int hostileMobKills) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncKillCountersPayload> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("primegodling", "sync_kill_counters"));

    public static final StreamCodec<FriendlyByteBuf, SyncKillCountersPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeUUID(p.playerUuid);
                buf.writeVarInt(p.demonLordKills);
                buf.writeBoolean(p.rimuruKilled);
                buf.writeBoolean(p.hinataKilled);
                buf.writeVarInt(p.hostileMobKills);
            },
            buf -> new SyncKillCountersPayload(buf.readUUID(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean(), buf.readVarInt())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
