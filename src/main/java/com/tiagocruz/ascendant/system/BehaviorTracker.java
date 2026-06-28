package com.tiagocruz.ascendant.system;

import com.tiagocruz.ascendant.Ascendant;
import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.network.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;

public class BehaviorTracker {

    public static void tick(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        if (data.getPlayerClass().isAssigned()) return;

        if (player.isCrouching()) data.addSnuckTick();

        double speed = player.getDeltaMovement().lengthSqr();
        if (speed < 0.001 && player.isAlive()) data.addObservationTick();

        if (shouldAssignClass(data)) {
            PlayerClass assigned = determineClass(data);
            data.setPlayerClass(assigned);
            ServerNetworking.syncToClient(player);
            ServerNetworking.sendClassAssigned(player);
            Ascendant.LOGGER.info("[Ascendant] {} → Classe: {}", player.getName().getString(), assigned.getDisplayName());
        }
    }

    private static boolean shouldAssignClass(AscendantPlayerData data) {
        int kills = data.getMeleeKills() + data.getRangedKills();
        return kills >= 5 || data.getSnuckTicks() >= 180 || data.getObservationTicks() >= 300;
    }

    public static PlayerClass determineClass(AscendantPlayerData data) {
        int melee = data.getMeleeKills(), ranged = data.getRangedKills();
        int stealth = data.getSnuckTicks(), observe = data.getObservationTicks();
        int slow = data.getBlocksMinedSlow(), absorbed = data.getDamageAbsorbed();

        if (observe > 10 && melee > 2 && ranged > 2 && stealth > 2) return PlayerClass.SUMMONER;
        if (stealth > 15 && ranged > 3 && melee == 0) return PlayerClass.SPECTER;

        int[] s = {
            melee * 3 + stealth * 4,       // ASSASSIN
            absorbed / 2 + melee * 2,       // GUARDIAN
            observe * 4 + slow * 2,         // MAGE
            melee * 4 + absorbed / 3,       // TITAN
            ranged * 5 + observe * 2,       // ARCHER
            observe * 3 + slow * 3 + (melee == 0 && ranged == 0 ? 20 : 0) // HEALER
        };

        int best = 0;
        for (int i = 1; i < s.length; i++) if (s[i] > s[best]) best = i;

        return switch (best) {
            case 0 -> PlayerClass.ASSASSIN;
            case 1 -> PlayerClass.GUARDIAN;
            case 2 -> PlayerClass.MAGE;
            case 3 -> PlayerClass.TITAN;
            case 4 -> PlayerClass.ARCHER;
            default -> PlayerClass.HEALER;
        };
    }
}
