package com.tiagocruz.ascendant.network;

import com.tiagocruz.ascendant.ability.ClassAbilities;
import com.tiagocruz.ascendant.ability.GeneralAbilityHandler;
import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class ServerNetworking {

    public static void register() {
        // S2C
        PayloadTypeRegistry.playS2C().register(SyncPlayerDataPacket.TYPE, SyncPlayerDataPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(ClassAssignedPacket.TYPE, ClassAssignedPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncManaPacket.TYPE, SyncManaPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncShieldPacket.TYPE, SyncShieldPacket.CODEC);

        // C2S
        PayloadTypeRegistry.playC2S().register(SpendStatPointPacket.TYPE, SpendStatPointPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UseAbilityPacket.TYPE, UseAbilityPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(UseAbilityPacket.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            player.server.execute(() -> GeneralAbilityHandler.handle(player, payload.abilityId()));
        });
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
            data.getVitality(),
            data.getDexterity(),
            data.getWisdom(),
            data.getPlayerClass().name()
        );
        ServerPlayNetworking.send(player, packet);
    }

    /** Conveniência: lê a classe do jogador e envia o packet de classe atribuída. */
    public static void sendClassAssigned(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        PlayerClass pc = data.getPlayerClass();
        ServerPlayNetworking.send(player, new ClassAssignedPacket(pc.name(), pc.getDisplayName(), pc.isRare()));
    }

    public static void syncManaToClient(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        ServerPlayNetworking.send(player, new SyncManaPacket(data.getCurrentMana(), data.getMaxMana()));
    }

    public static void syncShieldToClient(ServerPlayer player, boolean active, int ticks) {
        ServerPlayNetworking.send(player, new SyncShieldPacket(active, ticks));
    }
}