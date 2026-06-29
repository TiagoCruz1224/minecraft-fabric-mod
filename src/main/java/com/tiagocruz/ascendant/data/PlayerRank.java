package com.tiagocruz.ascendant.data;

/**
 * Sistema de Ranks do Ascendant.
 * Sete patamares de poder — do Latente ao Ascendente.
 */
public enum PlayerRank {

    //       nome          cor         nível mín   nível máx
    LATENTE   ("Latente",   "§7",          1,   9),
    DESPERTAR ("Despertar", "§a",         10,  24),
    FORJADO   ("Forjado",   "§2",         25,  49),
    ELITE     ("Élite",     "§b",         50,  74),
    EXALTADO  ("Exaltado",  "§9",         75,  99),
    SOBERANO  ("Soberano",  "§5",        100, 149),
    ASCENDENTE("Ascendente","§6§l",      150, Integer.MAX_VALUE);

    private final String displayName;
    private final String colorCode;
    private final int minLevel;
    private final int maxLevel;

    PlayerRank(String displayName, String colorCode, int minLevel, int maxLevel) {
        this.displayName = displayName;
        this.colorCode   = colorCode;
        this.minLevel    = minLevel;
        this.maxLevel    = maxLevel;
    }

    public String getDisplayName() { return displayName; }
    public String getColorCode()   { return colorCode; }
    public String getColored()     { return colorCode + displayName + "§r"; }
    public int    getMinLevel()    { return minLevel; }
    public int    getMaxLevel()    { return maxLevel; }

    /** Numeral romano para exibir ao lado do rank (ex: Élite IV). */
    public String getRoman() {
        return switch (this) {
            case LATENTE    -> "I";
            case DESPERTAR  -> "II";
            case FORJADO    -> "III";
            case ELITE      -> "IV";
            case EXALTADO   -> "V";
            case SOBERANO   -> "VI";
            case ASCENDENTE -> "VII";
        };
    }

    /** Retorna o rank correspondente ao nível fornecido. */
    public static PlayerRank fromLevel(int level) {
        for (PlayerRank rank : values()) {
            if (level >= rank.minLevel && level <= rank.maxLevel) return rank;
        }
        return LATENTE;
    }

    /** Quantos níveis faltam para o próximo rank (0 se já no máximo). */
    public int levelsToNext(int currentLevel) {
        if (this == ASCENDENTE) return 0;
        PlayerRank next = values()[this.ordinal() + 1];
        return Math.max(0, next.minLevel - currentLevel);
    }
}
