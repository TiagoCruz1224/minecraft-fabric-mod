package com.tiagocruz.ascendant.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Keybindings do mod Ascendant.
 *
 *  K — Menu principal (Perfil / Habilidades)
 *  H — Toggle HUD de habilidades
 *  G — Dash
 *  F — Salto Duplo
 *  R — Escudo de Energia
 *  Z — Esquivar
 */
public class AscendantKeyBindings {

    public static KeyMapping OPEN_MENU;
    public static KeyMapping TOGGLE_ABILITY_HUD;

    // 4 habilidades gerais
    public static KeyMapping ABILITY_DASH;
    public static KeyMapping ABILITY_DOUBLE_JUMP;
    public static KeyMapping ABILITY_SHIELD;
    public static KeyMapping ABILITY_DODGE;

    public static void register() {
        OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.open_menu", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K, "key.categories.ascendant"
        ));
        TOGGLE_ABILITY_HUD = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.toggle_hud", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H, "key.categories.ascendant"
        ));
        ABILITY_DASH = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.dash", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G, "key.categories.ascendant"
        ));
        ABILITY_DOUBLE_JUMP = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.double_jump", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F, "key.categories.ascendant"
        ));
        ABILITY_SHIELD = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.shield", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R, "key.categories.ascendant"
        ));
        ABILITY_DODGE = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.dodge", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z, "key.categories.ascendant"
        ));
    }
}
