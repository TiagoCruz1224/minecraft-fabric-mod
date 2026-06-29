package com.tiagocruz.ascendant.data;

import com.tiagocruz.ascendant.registry.AscendantAttachments;
import net.minecraft.server.level.ServerPlayer;

public class PlayerDataManager {

    public static AscendantPlayerData get(ServerPlayer player) {
        return player.getAttachedOrCreate(AscendantAttachments.PLAYER_DATA);
    }

    public static void set(ServerPlayer player, AscendantPlayerData data) {
        player.setAttached(AscendantAttachments.PLAYER_DATA, data);
    }

    /**
     * Adiciona XP ao jogador e retorna true se fez level up.
     */
    public static boolean addXp(ServerPlayer player, long amount) {
        AscendantPlayerData data = get(player);
        boolean leveledUp = data.addXp(amount);
        return leveledUp;
    }
}
