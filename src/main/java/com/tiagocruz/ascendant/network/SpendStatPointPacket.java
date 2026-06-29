package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.Ascendant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Packet C2S — jogador pede para gastar 1 stat point num atributo.
 * stat: "strength" | "agility" | "endurance" | "intelligence" | "perception"
 */
public record SpendStatPointPacket(String stat) implements CustomPacketPayload {

    public static final Type<SpendStatPointPacket> TYPE =
        new Type<>(Ascendant.id("spend_stat_point"));

    public static final StreamCodec<FriendlyByteBuf, SpendStatPointPacket> CODEC =
        StreamCodec.of(
            (buf, pkt) -> buf.writeUtf(pkt.stat()),
            buf -> new SpendStatPointPacket(buf.readUtf())
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
