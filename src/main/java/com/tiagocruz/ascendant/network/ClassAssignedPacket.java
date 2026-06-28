package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.Ascendant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Enviado quando o Sistema atribui uma classe ao jogador.
 * Dispara a animação dramática no cliente.
 */
public record ClassAssignedPacket(String className, String displayName, boolean isRare)
    implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClassAssignedPacket> TYPE =
        new CustomPacketPayload.Type<>(Ascendant.id("class_assigned"));

    public static final StreamCodec<FriendlyByteBuf, ClassAssignedPacket> CODEC =
        StreamCodec.of(
            (buf, pkt) -> {
                buf.writeUtf(pkt.className());
                buf.writeUtf(pkt.displayName());
                buf.writeBoolean(pkt.isRare());
            },
            buf -> new ClassAssignedPacket(
                buf.readUtf(),
                buf.readUtf(),
                buf.readBoolean()
            )
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
