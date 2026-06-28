package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.Ascendant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Packet S2C — sincroniza dados do jogador do servidor para o cliente.
 */
public record SyncPlayerDataPacket(
    int level,
    long xp,
    long xpToNext,
    int statPoints,
    int strength,
    int agility,
    int endurance,
    int intelligence,
    int perception,
    int vitality,
    int dexterity,
    int wisdom,
    String playerClass
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncPlayerDataPacket> TYPE =
        new CustomPacketPayload.Type<>(Ascendant.id("sync_player_data"));

    public static final StreamCodec<FriendlyByteBuf, SyncPlayerDataPacket> CODEC =
        StreamCodec.of(
            (buf, pkt) -> {
                buf.writeInt(pkt.level());
                buf.writeLong(pkt.xp());
                buf.writeLong(pkt.xpToNext());
                buf.writeInt(pkt.statPoints());
                buf.writeInt(pkt.strength());
                buf.writeInt(pkt.agility());
                buf.writeInt(pkt.endurance());
                buf.writeInt(pkt.intelligence());
                buf.writeInt(pkt.perception());
                buf.writeInt(pkt.vitality());
                buf.writeInt(pkt.dexterity());
                buf.writeInt(pkt.wisdom());
                buf.writeUtf(pkt.playerClass());
            },
            buf -> new SyncPlayerDataPacket(
                buf.readInt(),
                buf.readLong(),
                buf.readLong(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.