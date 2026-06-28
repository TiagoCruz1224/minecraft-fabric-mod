package com.tiagocruz.ascendant.item;

import com.tiagocruz.ascendant.data.PlayerClass;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Peça de armadura de classe do Ascendant.
 *
 * Por agora é um Item comum com atributos de armadura.
 * No futuro pode tornar-se ArmorItem com textura própria.
 *
 * Equipar uma armadura da classe errada dá debuffs passivos
 * enquanto estiver equipada (verificado em tick events).
 */
public class ClassArmor extends Item {

    private final PlayerClass requiredClass;
    private final String armorName;
    private final String armorLore;
    private final int armorValue; // pontos de armadura

    public ClassArmor(PlayerClass requiredClass, String armorName, String armorLore, int armorValue, Item.Properties properties) {
        super(properties.stacksTo(1));
        this.requiredClass = requiredClass;
        this.armorName     = armorName;
        this.armorLore     = armorLore;
        this.armorValue    = armorValue;
    }

    public PlayerClass getRequiredClass() { return requiredClass; }
    public int         getArmorValue()    { return armorValue; }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.literal("§7Classe: " + requiredClass.getDisplayName()));
        lines.add(Component.literal("§8" + armorLore));
        lines.add(Component.literal("§7Armadura: +" + armorValue));
        lines.add(Component.empty());
        lines.add(Component.literal("§c⚠ Debuff se equipada fora da classe"));
    }
}
