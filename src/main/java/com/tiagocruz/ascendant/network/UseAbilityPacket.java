package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.Ascendant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Packet C2S — cliente pede ao servidor para activar uma habilidade.
 */
public record UseAbilityPacket(String abilityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UseAbilityPacket> TYPE =
        new CustomPacketPayload.Type<>(Ascendant.id("use_ability"));

    public static final StreamCodec<FriendlyByteBuf, UseAbilityPacket> CODEC =
        StreamCodec.of(
            (buf, pkt) -> buf.writeUtf(pkt.abilityId()),
            buf -> new UseAbilityPacket(buf.readUtf())
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
