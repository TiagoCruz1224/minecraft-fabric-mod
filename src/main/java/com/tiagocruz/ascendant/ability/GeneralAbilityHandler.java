package com.tiagocruz.ascendant.ability;

import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.network.ServerNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

/**
 * Execução server-side das 4 habilidades gerais.
 * Chamado quando o servidor recebe UseAbilityPacket.
 *
 * Fluxo:
 *   1. Verificar cooldown
 *   2. Verificar mana
 *   3. Executar efeito
 *   4. Consumir mana + definir cooldown
 *   5. Sincronizar mana ao cliente
 */
public class GeneralAbilityHandler {

    public static void handle(ServerPlayer player, String abilityId) {
        AscendantAbility def = AbilityRegistry.getById(abilityId);
        if (def == null) return;

        AscendantPlayerData data = PlayerDataManager.get(player);

        // Verificar nível de desbloqueio
        if (!def.rank().isUnlocked(data.getLevel())) {
            player.sendSystemMessage(Component.literal("§cNível insuficiente para usar " + def.displayName() + "."));
            return;
        }

        // Verificar cooldown
        if (data.isOnCooldown(abilityId)) {
            long remaining = data.getCooldownRemaining(abilityId);
            player.sendSystemMessage(Component.literal(
                "§e" + def.displayName() + " §8em cooldown §7(" + (remaining / 1000 + 1) + "s)"));
            return;
        }

        // Verificar mana
        if (!data.consumeMana(def.manaCost())) {
            player.sendSystemMessage(Component.literal("§9Mana insuficiente para " + def.displayName() + "."));
            return;
        }

        // Executar habilidade
        boolean executed = switch (abilityId) {
            case "dash"          -> executeDash(player);
            case "double_jump"   -> executeDoubleJump(player);
            case "energy_shield" -> executeEnergyShield(player);
            case "dodge"         -> executeDodge(player);
            default              -> false;
        };

        if (executed) {
            // Aplicar cooldown reduzido por SAB
            double cdReduction = Math.min(0.75, (data.getWisdom() - 5) * 0.02);
            long effectiveCd = (long)(def.cooldownMs() * (1.0 - cdReduction));
            data.setCooldown(abilityId, effectiveCd);
        } else {
            // Reembolsar mana se falhou
            data.setCurrentMana(data.getCurrentMana() + def.manaCost());
        }

        ServerNetworking.syncManaToClient(player);
    }

    // ── DASH ─────────────────────────────────────────────────────────────────
    // Propulsão explosiva na direcção que o jogador olha.
    private static boolean executeDash(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        double speed = 2.2;
        player.setDeltaMovement(look.x * speed, 0.3, look.z * speed);
        player.hurtMarked = true; // forçar sync de velocidade ao cliente
        return true;
    }

    // ── DOUBLE JUMP ───────────────────────────────────────────────────────────
    // Aplica impulso vertical. Só funciona se o jogador estiver no ar.
    private static boolean executeDoubleJump(ServerPlayer player) {
        if (player.onGround()) return false; // já está no chão — não faz sentido
        Vec3 current = player.getDeltaMovement();
        player.setDeltaMovement(current.x, 0.65, current.z);
        player.hurtMarked = true;
        return true;
    }

    // ── ENERGY SHIELD ─────────────────────────────────────────────────────────
    // Escudo temporário — reduz dano via resistência IV por 3 segundos.
    // Usamos o efeito de resistência APENAS aqui (habilidade activa, não passiva).
    private static boolean executeEnergyShield(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3 * 20, 3, false, true));
        // Partículas de escudo
        if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.8, 0.5, 0.05);
        }
        return true;
    }

    // ── DODGE (ESQUIVAR) ─────────────────────────────────────────────────────
    // Esquiva lateral rápida com i-frames breves.
    private static boolean executeDodge(ServerPlayer player) {
        // Calcular direcção lateral (perpendicular ao look)
        Vec3 look = player.getLookAngle();
        // Esquiva para a esquerda por padrão; podes adicionar lógica de direcção depois
        double dodgeX = -look.z * 1.6;
        double dodgeZ =  look.x * 1.6;
        player.setDeltaMovement(dodgeX, player.getDeltaMovement().y + 0.1, dodgeZ);
        player.hurtMarked = true;
        // Invulnerabilidade breve (10 ticks = 0.5s)
        player.invulnerableTime = 10;
        return true;
    }
}
