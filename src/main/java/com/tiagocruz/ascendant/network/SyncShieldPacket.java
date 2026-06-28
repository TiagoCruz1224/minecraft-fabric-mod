package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.Ascendant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Packet S2C — servidor informa o cliente do estado do escudo de energia.
 * active=true → escudo activado, ticks = duração em ticks
 * active=false → escudo desactivado (fim de duração ou dano absorvido)
 */
public record SyncShieldPacket(boolean active, int ticks) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncShieldPacket> TYPE =
        new CustomPacketPayload.Type<>(Ascendant.id("sync_shield"));

    public static final StreamCodec<FriendlyByteBuf, SyncShieldPacket> CODEC =
        StreamCodec.of(
            (buf, pkt) -> { buf.writeBoolean(pkt.active()); buf.writeInt(pkt.ticks()); },
            buf  -> new SyncShieldPacket(buf.readBoolean(), buf.readInt())
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
