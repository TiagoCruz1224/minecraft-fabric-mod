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
  