package com.tiagocruz.ascendant.ability;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Registo central de todas as habilidades do Ascendant.
 *
 * Habilidades Gerais (rank E) — disponíveis a todos desde o nível 1:
 *   dash, double_jump, energy_shield, dodge
 *
 * Habilidades de Classe (ranks D → SS) — desbloqueadas por nível/rank:
 *   (placeholders — implementadas progressivamente)
 */
public class AbilityRegistry {

    // ── Habilidades Gerais ────────────────────────────────────────────────────

    public static final AscendantAbility DASH = new AscendantAbility(
        "dash",
        "Dash",
        "Propulsão explosiva na direcção que olhas",
        AbilityRank.E, 20, 8_000L, true, "»"
    );

    public static final AscendantAbility DOUBLE_JUMP = new AscendantAbility(
        "double_jump",
        "Salto Duplo",
        "Segundo salto enquanto estás no ar",
        AbilityRank.E, 15, 3_000L, true, "▲"
    );

    public static final AscendantAbility ENERGY_SHIELD = new AscendantAbility(
        "energy_shield",
        "Escudo de Energia",
        "Barreira que reduz o próximo dano em 80%",
        AbilityRank.E, 30, 15_000L, true, "◈"
    );

    public static final AscendantAbility DODGE = new AscendantAbility(
        "dodge",
        "Esquivar",
        "Esquiva rápida lateral com i-frames breves",
        AbilityRank.E, 10, 5_000L, true, "◇"
    );

    // ── Placeholders de Classe (rank D+) ──────────────────────────────────────
    // Serão expandidos à medida que as classes forem desenvolvidas

    public static final AscendantAbility SHADOW_STEP = new AscendantAbility(
        "shadow_step",
        "Passo das Sombras",
        "Teleporta atrás do alvo mais próximo [Assassino]",
        AbilityRank.D, 40, 12_000L, false, "✦"
    );

    public static final AscendantAbility ARCANE_BOLT = new AscendantAbility(
        "arcane_bolt",
        "Projéctil Arcano",
        "Dispara um projéctil de energia mágica [Mago]",
        AbilityRank.D, 45, 6_000L, false, "★"
    );

    // ── Listas de consulta ────────────────────────────────────────────────────

    public static final List<AscendantAbility> GENERAL_ABILITIES =
        List.of(DASH, DOUBLE_JUMP, ENERGY_SHIELD, DODGE);

    public static final List<AscendantAbility> ALL_ABILITIES =
        List.of(DASH, DOUBLE_JUMP, ENERGY_SHIELD, DODGE, SHADOW_STEP, ARCANE_BOLT);

    private static final Map<String, AscendantAbility> BY_ID = new LinkedHashMap<>();

    static {
        for (AscendantAbility a : ALL_ABILITIES) BY_ID.put(a.id(), a);
    }

    public static AscendantAbility getById(String id) {
        return BY_ID.get(id);
    }
}
