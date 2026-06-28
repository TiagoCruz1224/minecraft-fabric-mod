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
    private static int vitality = 5;
    private static int dexterity = 5;
    private static int wisdom = 5;
    private static String playerClass = "NONE";
    private static float currentMana = 100f;
    private static int maxMana = 100;

    // Água/sede — placeholder (sistema futuro). Começa cheio.
    private static int waterLevel = 20;
    private static final int MAX_WATER = 20;

    // Animação de level up
    private static long levelUpTimestamp = -1;
    private static final long LEVEL_UP_DISPLAY_MS = 4000;

    public static void update(int lvl, long currentXp, long nextXp, int sp,
                               int str, int agi, int end, int intel, int per,
                               int vit, int dex, int wis,
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
        vitality = vit;
        dexterity = dex;
        wisdom = wis;
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
    public static int getVitality() { return vitality; }
    public static int getDexterity() { return dexterity; }
    public static int getWisdom() { return wisdom; }
    public static String getPlayerClass() { return playerClass; }
    public static boolean isClassAssigned() { return playerClass != null && !playerClass.equals("NONE"); }
    public static float getCurrentMana() { return currentMana; }
    public static int getMaxMana() { return maxMana; }
    public static int getWaterLevel() { return waterLevel; }
    public static int getMaxWater() { return MAX_WATER; }

    public static void updateMana(float current, int max) {
        currentMana = current;
        maxMana = max;
    }

    public static boolean isLevelUpDisplayActive() {
        return levelUpTimestamp > 0 &&
               (System.currentTimeMillis() - levelUpTimestamp) < LEVEL_UP_DISPLAY_MS;
    }

    public static float getLevelUpProgress() {
        if (levelUpTimestamp < 0) return 0f;
        long elapsed = System.currentTimeMillis() - levelUpTimestamp;
        return 1f - Math.min(1f, (float) elapsed / LEVEL_UP_DISPLAY_MS);
    }
}