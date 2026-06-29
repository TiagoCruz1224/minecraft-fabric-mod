package com.tiagocruz.ascendant.ability;

/**
 * Definição de uma habilidade do sistema Ascendant.
 *
 * @param id           identificador único (ex: "dash")
 * @param displayName  nome em português (ex: "Dash")
 * @param description  descrição curta do efeito
 * @param rank         rank mínimo para desbloquear (E → SS)
 * @param manaCost     custo em mana para activar
 * @param cooldownMs   cooldown em milissegundos
 * @param isGeneral    true = disponível a todas as classes; false = exclusiva de classe
 * @param icon         ícone Unicode para mostrar no HUD
 */
public record AscendantAbility(
    String id,
    String displayName,
    String description,
    AbilityRank rank,
    int manaCost,
    long cooldownMs,
    boolean isGeneral,
    String icon
) {
    /** Tecla de activação padrão (pode ser sobrescrita nas keybindings). */
    public String getDefaultKey() {
        return switch (id) {
            case "dash"          -> "G";
            case "double_jump"   -> "F";
            case "energy_shield" -> "R";
            case "dodge"         -> "Z";
            default              -> "?";
        };
    }
}
