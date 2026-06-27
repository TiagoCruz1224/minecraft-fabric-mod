package com.tiagocruz.ascendant.event;

import com.tiagocruz.ascendant.ability.ClassAbilities;
import com.tiagocruz.ascendant.system.BehaviorTracker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public class AscendantServerTickEvents {

    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(AscendantServerTickEvents::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;

        // A cada 20 ticks (1 segundo): BehaviorTracker
        if (tickCounter % 20 == 0) {
            for (var player : server.getPlayerList().getPlayers()) {
                BehaviorTracker.tick(player);
            }
        }

        // A cada 100 ticks (5 segundos): passivas das classes
        if (tickCounter % 100 == 0) {
            for (var player : server.getPlayerList().getPlayers()) {
                ClassAbilities.applyPassive(player);
            }
        }
    }
}
