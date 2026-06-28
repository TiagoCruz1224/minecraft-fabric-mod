package com.tiagocruz.ascendant.item;

import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Arma de classe do Ascendant.
 *
 * - Se usado pela classe certa: bónus completo
 * - Se usado por outra classe: 35% chance de falha no ataque + fraqueza temporária
 * - Se usado pela classe oposta (rare): 60% falha + penalidade maior
 *
 * A penalidade é aplicada via ItemClassEvents (evento de ataque).
 */
public class ClassWeapon extends Item {

    private final PlayerClass requiredClass;
    private final String weaponName;
    private final String weaponLore;

    public ClassWeapon(PlayerClass requiredClass, String weaponName, String weaponLore, Item.Properties properties) {
        super(properties.stacksTo(1));
        this.requiredClass = requiredClass;
        this.weaponName    = weaponName;
        this.weaponLore    = weaponLore;
    }

    public PlayerClass getRequiredClass() { return requiredClass; }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.literal("§7Classe: " + requiredClass.getDisplayName()));
        lines.add(Component.literal("§8" + weaponLore));
        lines.add(Component.empty());
        lines.add(Component.literal("§c⚠ Penalidade se usada fora da classe"));
    }

    /** Verifica se o jogador tem a classe certa para esta arma. */
    public static boolean hasCorrectClass(ServerPlayer player, ClassWeapon weapon) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        return data.getPlayerClass() == weapon.getRequiredClass();
    }

    /** Calcula a penalidade de falha (0.0 = sem penalidade, 1.0 = sempre falha). */
    public static float getMissPenalty(PlayerClass playerClass, PlayerClass weaponClass) {
        if (playerClass == PlayerClass.NONE)   return 0.15f; // sem classe — penalidade leve
        if (playerClass == weaponClass)        return 0.0f;  // classe certa
        // Opostos temáticos (mais penalidade)
        if (isOpposite(playerClass, weaponClass)) return 0.60f;
        return 0.35f; // classe errada normal
    }

    private static boolean isOpposite(PlayerClass a, PlayerClass b) {
        return (a == PlayerClass.MAGE     && b == PlayerClass.TITAN)    ||
               (a == PlayerClass.TITAN    && b == PlayerClass.MAGE)     ||
               (a == PlayerClass.ASSASSIN && b == PlayerClass.GUARDIAN) ||
               (a == PlayerClass.GUARDIAN && b == PlayerClass.ASSASSIN) ||
               (a == PlayerClass.HEALER   && b == PlayerClass.SPECTER)  ||
               (a == PlayerClass.SPECTER  && b == PlayerClass.HEALER);
    }
}
