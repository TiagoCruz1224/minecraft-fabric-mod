package com.tiagocruz.ascendant.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Keybindings do mod Ascendant (lado cliente).
 */
public class AscendantKeyBindings {

    /** Tecla K — abre o ecrã de stats e poderes */
    public static KeyMapping OPEN_STATS;

    public static void register() {
        OPEN_STATS = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.ascendant.stats",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.ascendant"
        ));
    }
}
