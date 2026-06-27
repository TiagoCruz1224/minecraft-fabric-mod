package com.tiagocruz.ascendant.data;

public enum PlayerClass {
    // Não atribuída ainda
    NONE("none", "???"),

    // Classes Base
    ASSASSIN("assassin", "Assassino"),
    GUARDIAN("guardian", "Guardião"),
    MAGE("mage", "Mago"),
    TITAN("titan", "Titã"),
    ARCHER("archer", "Arqueiro"),
    HEALER("healer", "Curandeiro"),

    // Classes Raras
    SUMMONER("summoner", "Invocador"),
    SPECTER("specter", "Espectro");

    private final String id;
    private final String displayName;

    PlayerClass(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public boolean isRare() { return this == SUMMONER || this == SPECTER; }
    public boolean isAssigned() { return this != NONE; }
}
