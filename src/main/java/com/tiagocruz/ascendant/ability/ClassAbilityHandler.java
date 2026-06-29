package com.tiagocruz.ascendant.ability;

import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.network.ServerNetworking;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * Handler server-side para as 24 habilidades de classe (3 por classe x 8 classes).
 *
 * Fluxo:
 *   1. Verificar que a habilidade pertence a classe do jogador
 *   2. Verificar rank / nivel
 *   3. Verificar cooldown e mana
 *   4. Executar efeito
 *   5. Aplicar cooldown + sincronizar mana
 */
public class ClassAbilityHandler {

    public static void handle(ServerPlayer player, String abilityId) {
        AscendantAbility def = AbilityRegistry.getById(abilityId);
        if (def == null) return;

        AscendantPlayerData data = PlayerDataManager.get(player);

        // Verificar que e habilidade da classe do jogador
        List<AscendantAbility> classAbils = AbilityRegistry.getClassAbilities(data.getPlayerClass());
        if (classAbils.stream().noneMatch(a -> a.id().equals(abilityId))) {
            player.sendSystemMessage(Component.literal(
                "§cEsta habilidade nao pertence a tua classe."));
            return;
        }

        // Verificar nivel de desbloqueio
        if (!def.rank().isUnlocked(data.getLevel())) {
            player.sendSystemMessage(Component.literal(
                "§cNivel insuficiente para " + def.displayName() + "."));
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
            player.sendSystemMessage(Component.literal(
                "§9Mana insuficiente para " + def.displayName() + "."));
            return;
        }

        boolean executed = executeAbility(player, data, abilityId);

        if (executed) {
            double cdReduction = Math.min(0.75, (data.getWisdom() - 5) * 0.02);
            long effectiveCd = (long)(def.cooldownMs() * (1.0 - cdReduction));
            data.setCooldown(abilityId, effectiveCd);
        } else {
            data.setCurrentMana(data.getCurrentMana() + def.manaCost());
        }

        ServerNetworking.syncManaToClient(player);
    }

    // ── Dispatcher ────────────────────────────────────────────────────────────

    private static boolean executeAbility(ServerPlayer p, AscendantPlayerData d, String id) {
        return switch (id) {
            // -- ASSASSINO
            case "assassin_shadow_step" -> execShadowStep(p);
            case "assassin_stealth"     -> execStealth(p);
            case "assassin_blade_storm" -> execBladeStorm(p);
            // -- GUARDIAO
            case "guardian_shield_wall" -> execShieldWall(p);
            case "guardian_taunt"       -> execTaunt(p);
            case "guardian_ground_slam" -> execGroundSlam(p);
            // -- MAGO
            case "mage_arcane_bolt"     -> execArcaneBolt(p);
            case "mage_blink"           -> execBlink(p);
            case "mage_arcane_burst"    -> execArcaneBurst(p);
            // -- TITA
            case "titan_charge"         -> execTitanCharge(p);
            case "titan_war_cry"        -> execWarCry(p);
            case "titan_seismic_slam"   -> execSeismicSlam(p);
            // -- ARQUEIRO
            case "archer_multi_shot"    -> execMultiShot(p);
            case "archer_hawk_eye"      -> execHawkEye(p);
            case "archer_rain_arrows"   -> execRainArrows(p);
            // -- CURANDEIRO
            case "healer_pulse"         -> execHealPulse(p);
            case "healer_purify"        -> execPurify(p);
            case "healer_blessing"      -> execBlessing(p);
            // -- INVOCADOR
            case "summoner_familiar"    -> execFamiliar(p);
            case "summoner_soul_drain"  -> execSoulDrain(p);
            case "summoner_void_rift"   -> execVoidRift(p);
            // -- ESPECTRO
            case "specter_phantom_dash" -> execPhantomDash(p);
            case "specter_shadow_clone" -> execShadowClone(p);
            case "specter_void_strike"  -> execVoidStrike(p);
            default -> false;
        };
    }

    // ══════════════════════════ ASSASSINO ════════════════════════════════════

    private static boolean execShadowStep(ServerPlayer p) {
        Vec3 look = p.getLookAngle().normalize();
        double dist = 8.0;
        p.teleportTo(p.getX() + look.x * dist, p.getY(), p.getZ() + look.z * dist);
        spawnParticles(p, ParticleTypes.SMOKE, 15);
        return true;
    }

    private static boolean execStealth(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,        100, 1, false, false));
        spawnParticles(p, ParticleTypes.ASH, 15);
        return true;
    }

    private static boolean execBladeStorm(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 2, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,    60, 2, false, false));
        spawnParticles(p, ParticleTypes.CRIT, 20);
        return true;
    }

    // ══════════════════════════ GUARDIAO ══════════════════════════════════════

    private static boolean execShieldWall(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,         120, 4, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,  120, 1, false, false));
        spawnParticles(p, ParticleTypes.ENCHANT, 20);
        return true;
    }

    private static boolean execTaunt(ServerPlayer p) {
        AABB area = p.getBoundingBox().inflate(10);
        List<Mob> mobs = p.level().getEntitiesOfClass(Mob.class, area);
        mobs.forEach(m -> m.setTarget(p));
        p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 160, 0, false, false));
        spawnParticles(p, ParticleTypes.ENCHANT, 10);
        return true;
    }

    private static boolean execGroundSlam(ServerPlayer p) {
        AABB area = p.getBoundingBox().inflate(3);
        List<LivingEntity> targets = p.level().getEntitiesOfClass(
            LivingEntity.class, area, e -> e != p);
        for (LivingEntity t : targets) {
            t.hurt(p.damageSources().playerAttack(p), 8.0f);
            Vec3 kb = t.position().subtract(p.position()).normalize();
            t.setDeltaMovement(kb.x * 1.5, 0.5, kb.z * 1.5);
        }
        if (p.level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.EXPLOSION, p.getX(), p.getY(), p.getZ(), 2, 0.5, 0, 0.5, 0);
        return true;
    }

    // ══════════════════════════ MAGO ══════════════════════════════════════════

    private static boolean execArcaneBolt(ServerPlayer p) {
        Vec3 look = p.getLookAngle();
        SmallFireball fb = new SmallFireball(p.level(), p, look);
        fb.setPos(p.getX() + look.x, p.getEyeY() - 0.1, p.getZ() + look.z);
        p.level().addFreshEntity(fb);
        return true;
    }

    private static boolean execBlink(ServerPlayer p) {
        Vec3 look = p.getLookAngle().normalize();
        spawnParticles(p, ParticleTypes.PORTAL, 15);
        p.teleportTo(
            p.getX() + look.x * 12,
            p.getY() + look.y * 3,
            p.getZ() + look.z * 12);
        spawnParticles(p, ParticleTypes.PORTAL, 15);
        return true;
    }

    private static boolean execArcaneBurst(ServerPlayer p) {
        AABB area = p.getBoundingBox().inflate(4);
        List<LivingEntity> targets = p.level().getEntitiesOfClass(
            LivingEntity.class, area, e -> e != p);
        for (LivingEntity t : targets) {
            t.hurt(p.damageSources().magic(), 12.0f);
            Vec3 kb = t.position().subtract(p.position()).normalize();
            t.setDeltaMovement(kb.x * 1.5, 0.5, kb.z * 1.5);
        }
        if (p.level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.ENCHANT,
                p.getX(), p.getY() + 1, p.getZ(), 50, 2, 1, 2, 0.2);
        return true;
    }

    // ══════════════════════════ TITA ══════════════════════════════════════════

    private static boolean execTitanCharge(ServerPlayer p) {
        Vec3 look = p.getLookAngle().normalize();
        p.setDeltaMovement(look.x * 2.5, 0.2, look.z * 2.5);
        p.hurtMarked = true;
        AABB front = p.getBoundingBox().inflate(2).move(look.x * 2, 0, look.z * 2);
        List<LivingEntity> hit = p.level().getEntitiesOfClass(
            LivingEntity.class, front, e -> e != p);
        for (LivingEntity t : hit) {
            t.hurt(p.damageSources().playerAttack(p), 6.0f);
            t.setDeltaMovement(look.x * 2, 0.4, look.z * 2);
        }
        return true;
    }

    private static boolean execWarCry(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,         160, 2, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,160, 0, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION,      80, 0, false, false));
        if (p.level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.EXPLOSION, p.getX(), p.getY(), p.getZ(), 4, 0.5, 0.1, 0.5, 0);
        return true;
    }

    private static boolean execSeismicSlam(ServerPlayer p) {
        p.setDeltaMovement(p.getDeltaMovement().x, 1.2, p.getDeltaMovement().z);
        p.hurtMarked = true;
        AABB area = p.getBoundingBox().inflate(4);
        List<LivingEntity> targets = p.level().getEntitiesOfClass(
            LivingEntity.class, area, e -> e != p);
        for (LivingEntity t : targets) {
            t.hurt(p.damageSources().playerAttack(p), 10.0f);
            Vec3 kb = t.position().subtract(p.position()).normalize();
            t.setDeltaMovement(kb.x * 2, 0.6, kb.z * 2);
        }
        if (p.level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.EXPLOSION, p.getX(), p.getY(), p.getZ(), 5, 2, 0.1, 2, 0);
        return true;
    }

    // ══════════════════════════ ARQUEIRO ══════════════════════════════════════

    private static boolean execMultiShot(ServerPlayer p) {
        float[] offsets = {-20f, -10f, 0f, 10f, 20f};
        for (float yaw : offsets) {
            Arrow arrow = new Arrow(p.level(), p,
                new ItemStack(Items.ARROW), null);
            arrow.shootFromRotation(p, p.getXRot(), p.getYRot() + yaw, 0f, 2.5f, 0f);
            arrow.setBaseDamage(4.0);
            p.level().addFreshEntity(arrow);
        }
        return true;
    }

    private static boolean execHawkEye(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,  80, 0, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,  80, 0, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,         80, 0, false, false));
        spawnParticles(p, ParticleTypes.CRIT, 8);
        return true;
    }

    private static boolean execRainArrows(ServerPlayer p) {
        Vec3 look = p.getLookAngle().normalize();
        double cx = p.getX() + look.x * 10;
        double cz = p.getZ() + look.z * 10;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Arrow arrow = new Arrow(p.level(), p,
                    new ItemStack(Items.ARROW), null);
                arrow.setPos(cx + i, p.getY() + 18, cz + j);
                arrow.shoot(0, -1, 0, 2.5f, 0f);
                arrow.setBaseDamage(3.5);
                p.level().addFreshEntity(arrow);
            }
        }
        return true;
    }

    // ══════════════════════════ CURANDEIRO ════════════════════════════════════

    private static boolean execHealPulse(ServerPlayer p) {
        p.heal(8.0f);
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, false));
        AABB range = p.getBoundingBox().inflate(6);
        List<ServerPlayer> allies = p.level().getEntitiesOfClass(
            ServerPlayer.class, range, a -> a != p);
        for (ServerPlayer ally : allies) {
            ally.heal(6.0f);
            ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, false, false));
        }
        spawnParticles(p, ParticleTypes.HEART, 12);
        return true;
    }

    private static boolean execPurify(ServerPlayer p) {
        List.of(MobEffects.POISON, MobEffects.WITHER, MobEffects.MOVEMENT_SLOWDOWN,
                MobEffects.WEAKNESS, MobEffects.BLINDNESS, MobEffects.HUNGER,
                MobEffects.CONFUSION, MobEffects.LEVITATION)
            .forEach(e -> p.removeEffect(e));
        spawnParticles(p, ParticleTypes.HAPPY_VILLAGER, 15);
        return true;
    }

    private static boolean execBlessing(ServerPlayer p) {
        AABB range = p.getBoundingBox().inflate(8);
        List<ServerPlayer> allies = p.level().getEntitiesOfClass(ServerPlayer.class, range);
        for (ServerPlayer ally : allies) {
            ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2, false, true));
            ally.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,   200, 1, false, false));
        }
        spawnParticles(p, ParticleTypes.HEART, 20);
        return true;
    }

    // ══════════════════════════ INVOCADOR ═════════════════════════════════════

    private static boolean execFamiliar(ServerPlayer p) {
        net.minecraft.world.entity.animal.Wolf wolf = new net.minecraft.world.entity.animal.Wolf(net.minecraft.world.entity.EntityType.WOLF, p.level());
        wolf.setPos(p.getX() + 1, p.getY(), p.getZ() + 1);
        wolf.setOwnerUUID(p.getUUID());
        wolf.setTame(true, false);
        wolf.setHealth(wolf.getMaxHealth());
        p.level().addFreshEntity(wolf);
        spawnParticles(p, ParticleTypes.SOUL_FIRE_FLAME, 15);
        return true;
    }

    private static boolean execSoulDrain(ServerPlayer p) {
        AABB range = p.getBoundingBox().inflate(8);
        List<LivingEntity> mobs = p.level().getEntitiesOfClass(
            LivingEntity.class, range, e -> e != p && !(e instanceof ServerPlayer));
        mobs.stream()
            .min(Comparator.comparingDouble(m -> m.distanceTo(p)))
            .ifPresent(target -> {
                target.hurt(p.damageSources().magic(), 6.0f);
                p.heal(4.0f);
            });
        spawnParticles(p, ParticleTypes.SOUL, 12);
        return !mobs.isEmpty();
    }

    private static boolean execVoidRift(ServerPlayer p) {
        AABB range = p.getBoundingBox().inflate(8);
        List<LivingEntity> mobs = p.level().getEntitiesOfClass(
            LivingEntity.class, range, e -> e != p);
        for (LivingEntity mob : mobs) {
            Vec3 pull = p.position().subtract(mob.position()).normalize();
            mob.setDeltaMovement(pull.x * 2.0, 0.5, pull.z * 2.0);
        }
        if (p.level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.PORTAL,
                p.getX(), p.getY() + 1, p.getZ(), 40, 3, 1, 3, 0.1);
        return true;
    }

    // ══════════════════════════ ESPECTRO ══════════════════════════════════════

    private static boolean execPhantomDash(ServerPlayer p) {
        Vec3 look = p.getLookAngle().normalize();
        p.setDeltaMovement(look.x * 2.2, look.y * 0.5 + 0.1, look.z * 2.2);
        p.hurtMarked = true;
        p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 8, 0, false, false));
        AABB path = p.getBoundingBox().inflate(0.6).move(look.x * 3, 0, look.z * 3);
        List<LivingEntity> hit = p.level().getEntitiesOfClass(
            LivingEntity.class, path, e -> e != p);
        hit.forEach(t -> t.hurt(p.damageSources().playerAttack(p), 7.0f));
        spawnParticles(p, ParticleTypes.ASH, 15);
        return true;
    }

    private static boolean execShadowClone(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false));
        spawnParticles(p, ParticleTypes.ASH, 20);
        return true;
    }

    private static boolean execVoidStrike(ServerPlayer p) {
        AABB range = p.getBoundingBox().inflate(5);
        List<LivingEntity> mobs = p.level().getEntitiesOfClass(
            LivingEntity.class, range, e -> e != p);
        mobs.stream()
            .min(Comparator.comparingDouble(m -> m.distanceTo(p)))
            .ifPresent(t -> t.hurt(p.damageSources().magic(), 15.0f));
        if (mobs.isEmpty()) {
            // Sem alvo: buff de Forca IV por 2s
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 3, false, false));
        }
        spawnParticles(p, ParticleTypes.CRIT, 10);
        return true;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static <T extends ParticleOptions> void spawnParticles(
        ServerPlayer player, T type, int count) {
        if (player.level() instanceof ServerLevel sl)
            sl.sendParticles(type,
                player.getX(), player.getY() + 1, player.getZ(),
                count, 0.5, 0.5, 0.5, 0.05);
    }
}
