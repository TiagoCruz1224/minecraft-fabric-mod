package com.tiagocruz.ascendant.ability;

import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.network.ServerNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Habilidades passivas de cada classe.
 * Aplicadas periodicamente ou em eventos.
 *
 * Ativas: a implementar depois via tecla de atalho ou comando /skill
 */
public class ClassAbilities {

    /**
     * Aplica a passiva da classe do jogador.
     * Chamado a cada 5 segundos pelo tick.
     */
    public static void applyPassive(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        PlayerClass cls = data.getPlayerClass();
        int level = data.getLevel();

        switch (cls) {
            case ASSASSIN -> passiveAssassin(player, data, level);
            case GUARDIAN -> passiveGuardian(player, data, level);
            case MAGE -> passiveMage(player, data, level);
            case TITAN -> passiveTitan(player, data, level);
            case ARCHER -> passiveArcher(player, data, level);
            case HEALER -> passiveHealer(player, data, level);
            case SUMMONER -> passiveSummoner(player, data, level);
            case SPECTER -> passiveSpecter(player, data, level);
            default -> {} // NONE — sem passiva
        }
    }

    // --- ASSASSINO ---
    // Passiva: enquanto agachado, ganha Invisibilidade e velocidade leve
    private static void passiveAssassin(ServerPlayer player, AscendantPlayerData data, int level) {
        if (player.isCrouching()) {
            int duration = 30 * 20; // 30 segundos
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0, false, false));
        }
    }

    // --- GUARDIÃO ---
    // Passiva: absorção de dano extra (aumenta com nível)
    private static void passiveGuardian(ServerPlayer player, AscendantPlayerData data, int level) {
        int absorptionLevel = Math.min(level / 5, 4); // 0 a 4 (máximo nível 20)
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 30 * 20, absorptionLevel, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 0, false, false));
    }

    // --- MAGO ---
    // Passiva: regeneração de magia (por agora, regen de vida rápida para simular mana)
    private static void passiveMage(ServerPlayer player, AscendantPlayerData data, int level) {
        int regenLevel = Math.min(level / 8, 2);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30 * 20, regenLevel, false, false));
        // Partículas mágicas
        if (player.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.ENCHANT,
                player.getX(), player.getY() + 1, player.getZ(),
                3, 0.3, 0.5, 0.3, 0.05);
        }
    }

    // --- TITÃ ---
    // Passiva: força e resistência aumentadas
    private static void passiveTitan(ServerPlayer player, AscendantPlayerData data, int level) {
        int strengthLevel = Math.min(level / 5, 3);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, strengthLevel, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 0, false, false));
    }

    // --- ARQUEIRO ---
    // Passiva: velocidade de movimento e visão aumentadas
    private static void passiveArcher(ServerPlayer player, AscendantPlayerData data, int level) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1, false, false));
        // Night vision leve (para simular "perception")
        if (data.getPerception() >= 15) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40 * 20, 0, false, false));
        }
    }

    // --- CURANDEIRO ---
    // Passiva: regeneração constante + cura aliados próximos
    private static void passiveHealer(ServerPlayer player, AscendantPlayerData data, int level) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30 * 20, 1, false, false));
        // Curar aliados próximos (outros jogadores)
        AABB range = player.getBoundingBox().inflate(8);
        List<ServerPlayer> nearby = player.level().getEntitiesOfClass(
            ServerPlayer.class, range, p -> p != player);
        for (ServerPlayer ally : nearby) {
            ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 0, false, false));
        }
    }

    // --- INVOCADOR ---
    // Passiva: regeneração + speed (representa o equilíbrio)
    private static void passiveSummoner(ServerPlayer player, AscendantPlayerData data, int level) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30 * 20, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 0, false, false));
        if (player.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                player.getX(), player.getY() + 1, player.getZ(),
                5, 0.5, 0.5, 0.5, 0.02);
        }
    }

    // --- ESPECTRO ---
    // Passiva: sempre invisível enquanto agachado + velocidade alta
    private static void passiveSpecter(ServerPlayer player, AscendantPlayerData data, int level) {
        if (player.isCrouching()) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30 * 20, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 2, false, false));
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1, false, false));
        }
    }

    /**
     * Bónus de stats aplicados como atributos Minecraft.
     * Chamado quando o jogador muda de nível ou distribui stat points.
     */
    public static void applyStatBonuses(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        // Endurance → max health (10 HP base + 0.5 por ponto)
        double bonusHealth = data.getEndurance() * 0.5;
        var maxHealthAttr = player.getAttribute(
            net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            var modifier = new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ascendant", "endurance_bonus"),
                bonusHealth,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
            );
            maxHealthAttr.removeModifier(
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("ascendant", "endurance_bonus"));
            maxHealthAttr.addPermanentModifier(modifier);
        }
    }
}
