package com.tiagocruz.ascendant.event;

import com.tiagocruz.ascendant.Ascendant;
import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.network.ServerNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class PlayerEvents {

    public static void register() {
        // Sincronizar dados ao entrar no mundo
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayer player) {
                AscendantPlayerData data = PlayerDataManager.get(player);
                Ascendant.LOGGER.info("[Ascendant] {} entrou — Nível {} | Classe: {}",
                    player.getName().getString(),
                    data.getLevel(),
                    data.getPlayerClass().getDisplayName()
                );
                // Pequeno delay para garantir que o cliente está pronto
                ServerNetworking.syncToClient(player);
            }
        });

        // Tracking: kill de entidade
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayer player && killedEntity instanceof LivingEntity) {
                AscendantPlayerData data = PlayerDataManager.get(player);

                // Detectar melee vs ranged
                String item = player.getMainHandItem().getItem().toString();
                boolean ranged = item.contains("bow") || item.contains("crossbow") || item.contains("trident");

                if (ranged) {
                    data.addRangedKill();
                } else {
                    data.addMeleeKill();
                }

                // XP por kill
                boolean leveledUp = data.addXp(10);
                if (leveledUp) {
                    Ascendant.LOGGER.info("[Ascendant] {} → Nível {}!",
                        player.getName().getString(), data.getLevel());
                }

                // Sincronizar com o cliente
                ServerNetworking.syncToClient(player);
            }
        });
    }
}
