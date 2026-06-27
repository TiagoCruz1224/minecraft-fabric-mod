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

    // Stats base
    private int strength = 5;
    private int agility = 5;
    private int endurance = 5;
    private int intelligence = 5;
    private int perception = 5;

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

    // XP e level up
    public long xpForNextLevel() {
        return (long)(100 * Math.pow(level, 1.5));
    }

    public boolean addXp(long amount) {
        xp += amount;
        if (xp >= xpForNextLevel()) {
            xp -= xpForNextLevel();
            level++;
            statPoints += 3;
            return true;
        }
        return false;
    }

    public boolean spendStatPoint(String stat) {
        if (statPoints <= 0) return false;
        switch (stat) {
            case "strength"     -> strength++;
            case "agility"      -> agility++;
            case "endurance"    -> endurance++;
            case "intelligence" -> intelligence++;
            case "perception"   -> perception++;
            default -> { return false; }
        }
        statPoints--;
        return true;
    }

    // --- NBT ---
    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("class", playerClass.name());
        tag.putInt("level", level);
        tag.putLong("xp", xp);
        tag.putInt("statPoints", statPoints);
        tag.putInt("strength", strength);
        tag.putInt("agility", agility);
        tag.putInt("endurance", endurance);
        tag.putInt("intelligence", intelligence);
        tag.putInt("perception", perception);
        tag.putInt("meleeKills", meleeKills);
        tag.putInt("rangedKills", rangedKills);
        tag.putInt("damageAbsorbed", damageAbsorbed);
        tag.putInt("snuckTicks", snuckTicks);
        tag.putInt("observationTicks", observationTicks);
        tag.putInt("blocksMinedSlow", blocksMinedSlow);
        return tag;
    }

    public static AscendantPlayerData fromNbt(CompoundTag tag) {
        AscendantPlayerData d = new AscendantPlayerData();
        try { d.playerClass = PlayerClass.valueOf(tag.getString("class")); }
        catch (Exception e) { d.playerClass = PlayerClass.NONE; }
        d.level = tag.getInt("level");
        d.xp = tag.getLong("xp");
        d.statPoints = tag.getInt("statPoints");
        d.strength = tag.getInt("strength");
        d.agility = tag.getInt("agility");
        d.endurance = tag.getInt("endurance");
        d.intelligence = tag.getInt("intelligence");
        d.perception = tag.getInt("perception");
        d.meleeKills = tag.getInt("meleeKills");
        d.rangedKills = tag.getInt("rangedKills");
        d.damageAbsorbed = tag.getInt("damageAbsorbed");
        d.snuckTicks = tag.getInt("snuckTicks");
        d.observationTicks = tag.getInt("observationTicks");
        d.blocksMinedSlow = tag.getInt("blocksMinedSlow");
        return d;
    }
}
