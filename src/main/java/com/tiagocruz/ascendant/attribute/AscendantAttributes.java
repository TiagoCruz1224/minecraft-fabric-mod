package com.tiagocruz.ascendant.attribute;

import com.tiagocruz.ascendant.Ascendant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

/**
 * Atributos customizados do Ascendant.
 *
 * Registados no registo de atributos do Minecraft para uso futuro
 * (data packs, outros sistemas). Os bónus de stats são aplicados directamente
 * por ClassAbilities via setMod(), que já tem null-check para atributos não
 * presentes no AttributeMap do jogador.
 *
 * Nota: EntityAttributeModificationCallback não está disponível em
 * fabric-api 0.119.4+1.21.4. Os custom attrs são guardados no registo
 * mas não adicionados ao supplier do Player — ClassAbilities.setMod() ignora
 * automaticamente atributos com getAttribute() == null.
 */
public class AscendantAttributes {

    /** INT — poder mágico (escala dano de habilidades mágicas) */
    public static final Holder<Attribute> SPELL_POWER = register(
        "spell_power", 0.0, 0.0, 2048.0);

    /** PER — chance de crítico base (0.0 a 1.0 = 0% a 100%) */
    public static final Holder<Attribute> CRIT_CHANCE = register(
        "crit_chance", 0.0, 0.0, 1.0);

    /** DES — multiplicador de dano crítico (1.5 = +50% dano no crítico) */
    public static final Holder<Attribute> CRIT_DAMAGE = register(
        "crit_damage", 1.5, 1.0, 5.0);

    /** SAB — redução de cooldown (0.0 a 0.75 = 0% a 75%) */
    public static final Holder<Attribute> COOLDOWN_REDUCTION = register(
        "cooldown_reduction", 0.0, 0.0, 0.75);

    /** VIT+SAB — regeneração de vida por segundo */
    public static final Holder<Attribute> LIFE_REGEN = register(
        "life_regen", 0.0, 0.0, 20.0);

    /** INT+SAB — mana máxima adicional */
    public static final Holder<Attribute> MAX_MANA_BONUS = register(
        "max_mana_bonus", 0.0, 0.0, 5000.0);

    // ── Registo ──────────────────────────────────────────────────────────────

    private static Holder<Attribute> register(String name, double def, double min, double max) {
        return Registry.registerForHolder(
            BuiltInRegistries.ATTRIBUTE,
            Ascendant.id(name),
            new RangedAttribute("attribute.ascendant." + name, def, min, max).setSyncable(true)
        );
    }

    /**
     * Inicializa os atributos (força a avaliação dos campos estáticos).
     * Chamar em Ascendant.onInitialize() antes de qualquer uso.
     */
    public static void register() {
        // Os campos estáticos já fazem o registo ao serem inicializados.
        // Este método existe apenas para garantir que a classe é carregada
        // (triggering static initializers) no momento certo do boot.
        Ascendant.LOGGER.info("[Ascendant] Atributos customizados registados.");
    }
}
