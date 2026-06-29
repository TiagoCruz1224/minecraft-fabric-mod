package com.tiagocruz.ascendant.ability;

import com.tiagocruz.ascendant.data.PlayerClass;
import java.util.*;

/**
 * Registo central de todas as habilidades do Ascendant.
 *
 * Habilidades Gerais (rank E) — slots 1-4 da barra de habilidades:
 *   dash, double_jump, energy_shield, dodge
 *
 * Habilidades de Classe (rank D) — slots 5-7 da barra:
 *   3 activas por classe × 8 classes = 24 habilidades
 */
public class AbilityRegistry {

    // ── Habilidades Gerais (rank E, slots 1-4) ────────────────────────────────

    public static final AscendantAbility DASH = new AscendantAbility(
        "dash", "Dash",
        "Propulsão explosiva na direcção que olhas",
        AbilityRank.E, 20, 8_000L, true, "»"
    );
    public static final AscendantAbility DOUBLE_JUMP = new AscendantAbility(
        "double_jump", "Salto Duplo",
        "Segundo salto enquanto estás no ar",
        AbilityRank.E, 15, 3_000L, true, "▲"
    );
    public static final AscendantAbility ENERGY_SHIELD = new AscendantAbility(
        "energy_shield", "Escudo de Energia",
        "Barreira que reduz o próximo dano em 80%",
        AbilityRank.E, 30, 15_000L, true, "◈"
    );
    public static final AscendantAbility DODGE = new AscendantAbility(
        "dodge", "Esquivar",
        "Esquiva rápida lateral com i-frames breves",
        AbilityRank.E, 10, 5_000L, true, "◇"
    );

    // ── ASSASSINO ─────────────────────────────────────────────────────────────

    public static final AscendantAbility ASSASSIN_SHADOW_STEP = new AscendantAbility(
        "assassin_shadow_step", "Passo das Sombras",
        "Teleporta 8 blocos na direcção que olhas",
        AbilityRank.D, 25, 6_000L, false, "✦"
    );
    public static final AscendantAbility ASSASSIN_STEALTH = new AscendantAbility(
        "assassin_stealth", "Invisibilidade",
        "Invisibilidade + Velocidade II por 5s",
        AbilityRank.D, 30, 20_000L, false, "◉"
    );
    public static final AscendantAbility ASSASSIN_BLADE_STORM = new AscendantAbility(
        "assassin_blade_storm", "Tempestade de Lâminas",
        "Forca III + Rapidez II por 3s",
        AbilityRank.D, 40, 15_000L, false, "╬"
    );

    // ── GUARDIAO ──────────────────────────────────────────────────────────────

    public static final AscendantAbility GUARDIAN_SHIELD_WALL = new AscendantAbility(
        "guardian_shield_wall", "Muralha de Escudo",
        "Absorcao V + Resistencia II por 6s",
        AbilityRank.D, 35, 20_000L, false, "▣"
    );
    public static final AscendantAbility GUARDIAN_TAUNT = new AscendantAbility(
        "guardian_taunt", "Provocacao",
        "Forca mobs proximos a atacar-te por 8s",
        AbilityRank.D, 20, 15_000L, false, "!"
    );
    public static final AscendantAbility GUARDIAN_GROUND_SLAM = new AscendantAbility(
        "guardian_ground_slam", "Golpe Terrestre",
        "Dano + knockback num raio de 3 blocos",
        AbilityRank.D, 45, 12_000L, false, "◎"
    );

    // ── MAGO ──────────────────────────────────────────────────────────────────

    public static final AscendantAbility MAGE_ARCANE_BOLT = new AscendantAbility(
        "mage_arcane_bolt", "Projectil Arcano",
        "Dispara uma bola de fogo magica",
        AbilityRank.D, 25, 5_000L, false, "★"
    );
    public static final AscendantAbility MAGE_BLINK = new AscendantAbility(
        "mage_blink", "Piscar",
        "Teleporte 12 blocos em frente",
        AbilityRank.D, 30, 10_000L, false, "◆"
    );
    public static final AscendantAbility MAGE_ARCANE_BURST = new AscendantAbility(
        "mage_arcane_burst", "Explosao Arcana",
        "Explosao magica AoE num raio de 4 blocos",
        AbilityRank.D, 55, 25_000L, false, "✧"
    );

    // ── TITA ──────────────────────────────────────────────────────────────────

    public static final AscendantAbility TITAN_CHARGE = new AscendantAbility(
        "titan_charge", "Carga do Tita",
        "Sprint para a frente derrubando inimigos",
        AbilityRank.D, 30, 10_000L, false, "▶"
    );
    public static final AscendantAbility TITAN_WAR_CRY = new AscendantAbility(
        "titan_war_cry", "Grito de Guerra",
        "Forca III + Resistencia I por 8s",
        AbilityRank.D, 35, 20_000L, false, "◐"
    );
    public static final AscendantAbility TITAN_SEISMIC_SLAM = new AscendantAbility(
        "titan_seismic_slam", "Impacto Sismico",
        "Onda de choque num raio de 4 blocos",
        AbilityRank.D, 50, 18_000L, false, "▼"
    );

    // ── ARQUEIRO ──────────────────────────────────────────────────────────────

    public static final AscendantAbility ARCHER_MULTI_SHOT = new AscendantAbility(
        "archer_multi_shot", "Tiro Multiplo",
        "Dispara 5 flechas em leque",
        AbilityRank.D, 20, 8_000L, false, ">"
    );
    public static final AscendantAbility ARCHER_HAWK_EYE = new AscendantAbility(
        "archer_hawk_eye", "Olho de Falcao",
        "Visao nocturna + Queda suave por 4s",
        AbilityRank.D, 15, 12_000L, false, "◎"
    );
    public static final AscendantAbility ARCHER_RAIN_ARROWS = new AscendantAbility(
        "archer_rain_arrows", "Chuva de Flechas",
        "Lanca 9 flechas numa area 3x3",
        AbilityRank.D, 40, 20_000L, false, "↓"
    );

    // ── CURANDEIRO ────────────────────────────────────────────────────────────

    public static final AscendantAbility HEALER_PULSE = new AscendantAbility(
        "healer_pulse", "Pulso de Cura",
        "Cura 4 coracoes + Regen a aliados proximos",
        AbilityRank.D, 25, 12_000L, false, "+"
    );
    public static final AscendantAbility HEALER_PURIFY = new AscendantAbility(
        "healer_purify", "Purificacao",
        "Remove todos os efeitos negativos",
        AbilityRank.D, 15, 15_000L, false, "*"
    );
    public static final AscendantAbility HEALER_BLESSING = new AscendantAbility(
        "healer_blessing", "Bencao da Luz",
        "Regeneracao III a todos os jogadores proximos por 10s",
        AbilityRank.D, 45, 30_000L, false, "~"
    );

    // ── INVOCADOR ─────────────────────────────────────────────────────────────

    public static final AscendantAbility SUMMONER_FAMILIAR = new AscendantAbility(
        "summoner_familiar", "Familiar",
        "Invoca um lobo aliado por 30s",
        AbilityRank.D, 35, 30_000L, false, "^"
    );
    public static final AscendantAbility SUMMONER_SOUL_DRAIN = new AscendantAbility(
        "summoner_soul_drain", "Drenar Alma",
        "Rouba vida do mob mais proximo",
        AbilityRank.D, 20, 10_000L, false, "@"
    );
    public static final AscendantAbility SUMMONER_VOID_RIFT = new AscendantAbility(
        "summoner_void_rift", "Fenda do Vazio",
        "Puxa todos os mobs proximos para si",
        AbilityRank.D, 45, 25_000L, false, "O"
    );

    // ── ESPECTRO ──────────────────────────────────────────────────────────────

    public static final AscendantAbility SPECTER_PHANTOM_DASH = new AscendantAbility(
        "specter_phantom_dash", "Dash Fantasma",
        "Atravessa inimigos causando dano",
        AbilityRank.D, 25, 6_000L, false, "%"
    );
    public static final AscendantAbility SPECTER_SHADOW_CLONE = new AscendantAbility(
        "specter_shadow_clone", "Clone das Sombras",
        "Fica invisivel por 10s",
        AbilityRank.D, 30, 20_000L, false, "$"
    );
    public static final AscendantAbility SPECTER_VOID_STRIKE = new AscendantAbility(
        "specter_void_strike", "Golpe do Vazio",
        "Ataque magico poderoso que ignora armadura",
        AbilityRank.D, 40, 15_000L, false, "#"
    );

    // ── Listas e mapas de consulta ────────────────────────────────────────────

    public static final List<AscendantAbility> GENERAL_ABILITIES =
        List.of(DASH, DOUBLE_JUMP, ENERGY_SHIELD, DODGE);

    private static final Map<PlayerClass, List<AscendantAbility>> CLASS_ABILITIES;
    private static final Map<String, AscendantAbility> BY_ID = new LinkedHashMap<>();

    public static List<AscendantAbility> ALL_ABILITIES;

    static {
        CLASS_ABILITIES = Map.of(
            PlayerClass.ASSASSIN, List.of(ASSASSIN_SHADOW_STEP, ASSASSIN_STEALTH, ASSASSIN_BLADE_STORM),
            PlayerClass.GUARDIAN, List.of(GUARDIAN_SHIELD_WALL, GUARDIAN_TAUNT,    GUARDIAN_GROUND_SLAM),
            PlayerClass.MAGE,     List.of(MAGE_ARCANE_BOLT,     MAGE_BLINK,        MAGE_ARCANE_BURST),
            PlayerClass.TITAN,    List.of(TITAN_CHARGE,         TITAN_WAR_CRY,     TITAN_SEISMIC_SLAM),
            PlayerClass.ARCHER,   List.of(ARCHER_MULTI_SHOT,    ARCHER_HAWK_EYE,   ARCHER_RAIN_ARROWS),
            PlayerClass.HEALER,   List.of(HEALER_PULSE,         HEALER_PURIFY,     HEALER_BLESSING),
            PlayerClass.SUMMONER, List.of(SUMMONER_FAMILIAR,    SUMMONER_SOUL_DRAIN, SUMMONER_VOID_RIFT),
            PlayerClass.SPECTER,  List.of(SPECTER_PHANTOM_DASH, SPECTER_SHADOW_CLONE, SPECTER_VOID_STRIKE)
        );

        GENERAL_ABILITIES.forEach(a -> BY_ID.put(a.id(), a));
        CLASS_ABILITIES.values().forEach(list -> list.forEach(a -> BY_ID.put(a.id(), a)));
        ALL_ABILITIES = java.util.List.copyOf(BY_ID.values());
    }

    public static AscendantAbility getById(String id) { return BY_ID.get(id); }

    public static boolean isGeneralAbility(String id) {
        return GENERAL_ABILITIES.stream().anyMatch(a -> a.id().equals(id));
    }

    public static List<AscendantAbility> getClassAbilities(PlayerClass cls) {
        return CLASS_ABILITIES.getOrDefault(cls, List.of());
    }

    /** Indice 0-2 dentro das habilidades de classe. */
    public static AscendantAbility getClassAbility(PlayerClass cls, int index) {
        List<AscendantAbility> list = getClassAbilities(cls);
        return index < list.size() ? list.get(index) : null;
    }

    /** Devolve todas as habilidades registadas. */
    public static Collection<AscendantAbility> allAbilities() {
        return BY_ID.values();
    }
}
