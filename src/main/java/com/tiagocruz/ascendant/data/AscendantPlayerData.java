package com.tiagocruz.ascendant.data;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;

public class AscendantPlayerData {

    // Codec para persistência via Fabric Attachment API
    public static final Codec<AscendantPlayerData> CODEC =
        CompoundTag.CODEC.xmap(
            AscendantPlayerData::fromNbt,
            AscendantPlayerData::toNbt
        );

    // Identidade
    private PlayerClass playerClass = PlayerClass.NONE;
    private int level = 1;
    private long xp = 0;
    private int statPoints = 0;

    // Stats base (8 atributos)
    private int strength = 5;      // FOR — dano corpo a corpo
    private int agility = 5;       // AGI — velocidade, cooldown esquiva
    private int endurance = 5;     // RES — redução de dano, knockback
    private int intelligence = 5;  // INT — dano mágico, mana máxima
    private int perception = 5;    // PER — alcance, crítico à distância
    private int vitality = 5;      // VIT — vida máxima, regen de vida
    private int dexterity = 5;     // DES — crítico corpo a corpo, precisão projéteis
    private int wisdom = 5;        // SAB — cooldowns de habilidades, regen de mana

    // Mana — recurso para habilidades (não persistido; regenera ao entrar)
    private float currentMana = 100f;
    // Cooldowns de habilidades (não persistidos — transitórios em memória)
    private final java.util.Map<String, Long> abilityCooldowns = new java.util.HashMap<>();

    // Tracking de comportamento (tutorial)
    private int meleeKills = 0;
    private int rangedKills = 0;
    private int damageAbsorbed = 0;
    private int snuckTicks = 0;
    private int blocksMinedSlow = 0;
    private int observationTicks = 0;

    public AscendantPlayerData() {}

    // --- Getters ---
    public PlayerClass getPlayerClass() { return playerClass; }
    public void setPlayerClass(PlayerClass c) { this.playerClass = c; }

    public int getLevel() { return level; }
    public long getXp() { return xp; }
    public int getStatPoints() { return statPoints; }

    public int getStrength() { return strength; }
    public int getAgility() { return agility; }
    public int getEndurance() { return endurance; }
    public int getIntelligence() { return intelligence; }
    public int getPerception() { return perception; }
    public int getVitality() { return vitality; }
    public int getDexterity() { return dexterity; }
    public int getWisdom() { return wisdom; }

    // ── Mana ──────────────────────────────────────────────────────────────────

    /** Mana máxima: base 100 + INT*10 + SAB*5 */
    public int getMaxMana() {
        return 100 + (intelligence - 5) * 10 + (wisdom - 5) * 5;
    }

    public float getCurrentMana() { return currentMana; }

    public void setCurrentMana(float mana) {
        this.currentMana = Math.max(0, Math.min(mana, getMaxMana()));
    }

    /** Consome mana. Retorna false se não houver suficiente. */
    public boolean consumeMana(int amount) {
        if (currentMana < amount) return false;
        currentMana -= amount;
        return true;
    }

    /** Regenera mana. Rate base: 0.5/tick + 0.02*SAB/tick. */
    public void regenMana() {
        float rate = 0.5f + (wisdom - 5) * 0.02f;
        setCurrentMana(currentMana + rate);
    }

    // ── Cooldowns ─────────────────────────────────────────────────────────────

    public boolean isOnCooldown(String abilityId) {
        Long endTime = abilityCooldowns.get(abilityId);
        return endTime != null && System.currentTimeMillis() < endTime;
    }

    public long getCooldownRemaining(String abilityId) {
        Long endTime = abilityCooldowns.get(abilityId);
        if (endTime == null) return 0;
        return Math.max(0, endTime - System.currentTimeMillis());
    }

    public void setCooldown(String abilityId, long durationMs) {
        abilityCooldowns.put(abilityId, System.currentTimeMillis() + durationMs);
    }

    // Tracking
    public void addMeleeKill() { meleeKills++; }
    public void addRangedKill() { rangedKills++; }
    public void addDamageAbsorbed(int dmg) { damageAbsorbed += dmg; }
    public void addSnuckTick() { snuckTicks++; }
    public void addObservationTick() { observationTicks++; }
    public void addBlockMinedSlow() { blocksMinedSlow++; }

    public int getMeleeKills() { return meleeKills; }
    public int getRangedKills() { return rangedKills; }
    public int getDamageAbsorbed() { return damageAbsorbed; }
    public int getSnuckTicks() { return snuckTicks; }
    public int getObservationTicks() { return observationTicks; }
    public int getBlocksMinedSlow() { return blocksMinedSlow; }

    // --- spendStatPoint ---
    public boolean spendStatPoint(String stat) {
        if (statPoints <= 0) return false;
        statPoints--;
        switch (stat) {
            case "strength"     -> strength++;
            case "agility"      -> agility++;
            case "endurance"    -> endurance++;
            case "intelligence" -> intelligence++;
            case "perception"   -> perception++;
            case "vitality"     -> vitality++;
            case "dexterity"    -> dexterity++;
            case "wisdom"       -> wisdom++;
            default -> { statPoints++; return false; }
        }
        return true;
    }

    // --- XP / Level ---
    public long xpForNextLevel() {
        return (long)(100 * Math.pow(1.15, level - 1));
    }

    public boolean addXp(long amount) {
        xp += amount;
        boolean leveledUp = false;
        while (xp >= xpForNextLevel()) {
            xp -= xpForNextLevel();
            level++;
            statPoints += 3;
            leveledUp = true;
        }
        return leveledUp;
    }

    // --- NBT ---
    public static AscendantPlayerData fromNbt(CompoundTag tag) {
        AscendantPlayerData data = new AscendantPlayerData();
        try { data.playerClass = PlayerClass.valueOf(tag.getString("class")); } catch (Exception ignored) {}
        if (tag.contains("level"))          data.level          = tag.getInt("level");
        if (tag.contains("xp"))             data.xp             = tag.getLong("xp");
        if (tag.contains("statPoints"))     data.statPoints     = tag.getInt("statPoints");
        if (tag.contains("strength"))       data.strength       = tag.getInt("strength");
        if (tag.contains("agility"))        data.agility        = tag.getInt("agility");
        if (tag.contains("endurance"))      data.endurance      = tag.getInt("endurance");
        if (tag.contains("intelligence"))   data.intelligence   = tag.getInt("intelligence");
        if (tag.contains("perception"))     data.perception     = tag.getInt("perception");
        if (tag.contains("vitality"))       data.vitality       = tag.getInt("vitality");
        if (tag.contains("dexterity"))      data.dexterity      = tag.getInt("dexterity");
        if (tag.contains("wisdom"))         data.wisdom         = tag.getInt("wisdom");
        if (tag.contains("meleeKills"))     data.meleeKills     = tag.getInt("meleeKills");
        if (tag.contains("rangedKills"))    data.rangedKills    = tag.getInt("rangedKills");
        if (tag.contains("damageAbsorbed")) data.damageAbsorbed = tag.getInt("damageAbsorbed");
        if (tag.contains("snuckTicks"))     data.snuckTicks     = tag.getInt("snuckTicks");
        if (tag.contains("blocksMinedSlow"))data.blocksMinedSlow= tag.getInt("blocksMinedSlow");
        if (tag.contains("observationTicks"))data.observationTicks=tag.getInt("observationTicks");
        return data;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("class",           playerClass.name());
        tag.putInt("level",              level);
        tag.putLong("xp",                xp);
        tag.putInt("statPoints",         statPoints);
        tag.putInt("strength",           strength);
        tag.putInt("agility",            agility);
        tag.putInt("endurance",          endurance);
        tag.putInt("intelligence",       intelligence);
        tag.putInt("perception",         perception);
        tag.putInt("vitality",           vitality);
        tag.putInt("dexterity",          dexterity);
        tag.putInt("wisdom",             wisdom);
        tag.putInt("meleeKills",         meleeKills);
        tag.putInt("rangedKills",        rangedKills);
        tag.putInt("damageAbsorbed",     damageAbsorbed);
        tag.putInt("snuckTicks",         snuckTicks);
        tag.putInt("blocksMinedSlow",    blocksMinedSlow);
        tag.putInt("observationTicks",   observationTicks);
        return tag;
    }
}