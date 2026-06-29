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
 *  G — Dash (atalho directo)
 *  F — Salto Duplo (atalho directo)
 *  R — Toggle barra de habilidades (estilo Solo Leveling: Reawakening)
 *  Z — Usar habilidade seleccionada na barra
 */
public class AscendantKeyBindings {

    public static KeyMapping OPEN_MENU;
    public static KeyMapping TOGGLE_ABILITY_HUD;

    // Atalhos directos para habilidades de movimento
    public static KeyMapping ABILITY_DASH;
    public static KeyMapping ABILITY_DOUBLE_JUMP;

    // Barra de habilidades estilo SL:Reawakening
    public static KeyMapping TOGGLE_ABILITY_HOTBAR;  // R — mostra/esconde barra
    public static KeyMapping USE_SELECTED_ABILITY;   // Z — usa habilidade seleccionada

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
