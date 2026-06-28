package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.ability.ClassAbilities;
import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class ServerNetworking {

    public static void register() {
        // S2C
        PayloadTypeRegistry.playS2C().register(SyncPlayerDataPacket.TYPE, SyncPlayerDataPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ClassAssignedPacket.TYPE, ClassAssignedPacket.CODEC);

        // C2S: gastar stat point
        PayloadTypeRegistry.playC2S().register(SpendStatPointPacket.TYPE, SpendStatPointPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SpendStatPointPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.server.execute(() -> {
                AscendantPlayerData data = PlayerDataManager.get(player);
                if (data.spendStatPoint(payload.stat())) {
                    ClassAbilities.applyStatBonuses(player);
                    syncToClient(player);
                }
            });
        });
    }

    public static void syncToClient(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        SyncPlayerDataPacket packet = new SyncPlayerDataPacket(
            data.getLevel(),
            data.getXp(),
            data.xpForNextLevel(),
            data.getStatPoints(),
            data.getStrength(),
            data.getAgility(),
            data.getEndurance(),
            data.getIntelligence(),
            data.getPerception(),
            data.getPlayerClass().name()
        );
        ServerPlayNetworking.send(player, packet);
    }

    /** Conveniência: lê a classe do jogador e envia o packet de classe atribuída. */
    public static void sendClassAssigned(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        String className   = data.getPlayerClass().name();
        String displayName = data.getPlayerClass().getDisplayName();
        boolean isRare     = data.getPlayerClass().isRare();
        ClassAssignedPacket packet = new ClassAssignedPacket(className, displayName, isRare);
        ServerPlayNetworking.send(player, packet);
    }

    public static void syncClassAssigned(ServerPlayer player, String className, String displayName, boolean isRare) {
        ClassAssignedPacket packet = new ClassAssignedPacket(className, displayName, isRare);
        ServerPlayNetworking.send(player, packet);
    }
}
