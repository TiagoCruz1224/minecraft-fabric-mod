package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class ServerNetworking {

    public static void register() {
        PayloadTypeRegistry.playS2C().register(SyncPlayerDataPacket.TYPE, SyncPlayerDataPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ClassAssignedPacket.TYPE, ClassAssignedPacket.CODEC);
    }

    public static void syncToClient(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        SyncPlayerDataPacket packet = new SyncPlayerDataPacket(
            data.getLevel(), data.getXp(), data.xpForNextLevel(),
            data.getStatPoints(), data.getStrength(), data.getAgility(),
            data.getEndurance(), data.getIntelligence(), data.getPerception(),
            data.getPlayerClass().name()
        );
        ServerPlayNetworking.send(player, packet);
    }

    public static void sendClassAssigned(ServerPlayer player) {
        var cls = PlayerDataManager.get(player).getPlayerClass();
        ClassAssignedPacket packet = new ClassAssignedPacket(
            cls.name(), cls.getDisplayName(), cls.isRare()
        );
        ServerPlayNetworking.send(player, packet);
    }
}
