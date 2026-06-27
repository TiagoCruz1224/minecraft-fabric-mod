package com.tiagocruz.ascendant.client.data;

/**
 * Cache local (cliente) dos dados do jogador.
 * Atualizado pelos packets do servidor.
 */
public class ClientPlayerData {

    private static int level = 1;
    private static long xp = 0;
    private static long xpToNext = 100;
    private static int statPoints = 0;
    private static int strength = 5;
    private static int agility = 5;
    private static int endurance = 5;
    private static int intelligence = 5;
    private static int perception = 5;
    private static String playerClass = "NONE";

    // Animação de level up
    private static long levelUpTimestamp = -1;
    private static final long LEVEL_UP_DISPLAY_MS = 4000;

    public static void update(int lvl, long currentXp, long nextXp, int sp,
                               int str, int agi, int end, int intel, int per,
                               String cls) {
        if (lvl > level) {
            levelUpTimestamp = System.currentTimeMillis();
        }
        level = lvl;
        xp = currentXp;
        xpToNext = nextXp;
        statPoints = sp;
        strength = str;
        agility = agi;
        endurance = end;
        intelligence = intel;
        perception = per;
        playerClass = cls;
    }

    public static int getLevel() { return level; }
    public static long getXp() { return xp; }
    public static long getXpToNext() { return xpToNext; }
    public static int getStatPoints() { return statPoints; }
    public static int getStrength() { return strength; }
    public static int getAgility() { return agility; }
    public static int getEndurance() { return endurance; }
    public static int getIntelligence() { return intelligence; }
    public static int getPerception() { return perception; }
    public static String getPlayerClass() { return playerClass; }

    public static boolean isClassAssigned() { return !playerClass.equals("NONE"); }

    public static boolean isShowingLevelUp() {
        return levelUpTimestamp > 0 &&
               (System.currentTimeMillis() - levelUpTimestamp) < LEVEL_UP_DISPLAY_MS;
    }
}
