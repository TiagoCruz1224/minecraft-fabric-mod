package com.tiagocruz.ascendant.ability;

/**
 * Rank de habilidade — determina o nível mínimo do jogador para desbloquear.
 * Escala: E (básico) → SS (lendário).
 */
public enum AbilityRank {
    E  (1,  "§7E",  "Latente"),
    D  (5,  "§aD",  "Despertar"),
    C  (10, "§bC",  "Forjado"),
    B  (20, "§9B",  "Élite"),
    A  (30, "§5A",  "Exaltado"),
    S  (50, "§6S",  "Soberano"),
    SS (70, "§cSS", "Ascendente");

    private final int minLevel;
    private final String colored;      // com código de cor Minecraft
    private final String rankRequired; // nome do rank Ascendant equivalente

    AbilityRank(int minLevel, String colored, String rankRequired) {
        this.minLevel = minLevel;
        this.colored = colored;
        this.rankRequired = rankRequired;
    }

    public int getMinLevel() { return minLevel; }
    public String getColored() { return colored; }
    public String getRankRequired() { return rankRequired; }

    public boolean isUnlocked(int playerLevel) {
        return playerLevel >= minLevel;
    }
}
