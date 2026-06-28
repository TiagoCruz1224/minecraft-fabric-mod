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
    public int getBlocksMinedS