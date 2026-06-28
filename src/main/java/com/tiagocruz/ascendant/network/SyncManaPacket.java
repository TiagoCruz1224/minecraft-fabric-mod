package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.Ascendant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Packet S2C — sincroniza mana actual e mana máxima ao cliente.
 * Enviado após cada consumo/regen significativo.
 */
public record SyncManaPacket(float currentMana, int maxMana) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncManaPacket> TYPE =
        new CustomPacketPayload.Type<>(Ascendant.id("sync_mana"));

    public static final StreamCodec<FriendlyByteBuf, SyncManaPacket> CODEC =
        StreamCodec.of(
            (buf, pkt) -> { buf.writeFloat(pkt.currentMana()); buf.writeInt(pkt.maxMana()); },
            buf -> new SyncManaPacket(buf.readFloat(), buf.readInt())
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
