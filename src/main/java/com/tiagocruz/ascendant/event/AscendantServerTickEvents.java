package com.tiagocruz.ascendant.event;

import com.tiagocruz.ascendant.ability.ClassAbilities;
import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.network.ServerNetworking;
import com.tiagocruz.ascendant.system.BehaviorTracker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class AscendantServerTickEvents {

    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(AscendantServerTickEvents::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;

        // A cada 20 ticks (1 segundo): BehaviorTracker + regen de mana
        if (tickCounter % 20 == 0) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                BehaviorTracker.tick(player);
                tickManaRegen(player);
            }
        }
    }

    private static void tickManaRegen(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        data.regenMana();
        ServerNetworking.syncManaToClient(player);
    }
}