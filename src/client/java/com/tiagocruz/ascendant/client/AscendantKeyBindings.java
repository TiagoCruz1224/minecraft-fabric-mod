package com.tiagocruz.ascendant.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Keybindings do mod Ascendant.
 *
 *  K — Menu principal (Perfil / Habilidades)
 *  H — Toggle painel de habilidades (info lateral)
 *  R — Toggle barra de habilidades (mostra/esconde)
 *  Z — Usar habilidade seleccionada (slot activo no 1-9)
 */
public class AscendantKeyBindings {

    public static KeyMapping OPEN_MENU;
    public static KeyMapping TOGGLE_ABILITY_HUD;
    public static KeyMapping TOGGLE_ABILITY_HOTBAR;  // R
    public static KeyMapping USE_SELECTED_ABILITY;   // Z

    public static void register() {
        OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.open_menu", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K, "key.categories.ascendant"
        ));
        TOGGLE_ABILITY_HUD = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.toggle_hud", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H, "key.categories.ascendant"
        ));
        TOGGLE_ABILITY_HOTBAR = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.toggle_ability_hotbar", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R, "key.categories.ascendant"
        ));
        USE_SELECTED_ABILITY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.use_selected_ability", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z, "key.categories.ascendant"
        ));
    }
}
