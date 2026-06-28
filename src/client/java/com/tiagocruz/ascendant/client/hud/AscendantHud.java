package com.tiagocruz.ascendant.client.hud;

import com.tiagocruz.ascendant.client.AscendantFont;
import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodData;

/**
 * HUD principal do Ascendant — substitui os elementos vanilla cancelados pelo GuiMixin.
 *
 * Layout acima da hotbar (da esquerda para a direita, de baixo para cima):
 *
 *   [HOTBAR]                                                    y = screenH-22
 *   [XP bar vanilla]                                            y = screenH-24
 *   [❤ HP ████████████ 20/20]  [✦ MP ████████████ 100/100]     y = screenH-37
 *   [🍗 20  💧 20  🥾 80%  ⚔ 4]                               y = screenH-49
 *
 * Canto superior esquerdo: mini indicador de classe/nível
 * Centro (temporário): notificação de level-up
 * Lado direito (H toggle): HUD de habilidades
 */
public class AscendantHud {

    // ── Cores ─────────────────────────────────────────────────────────────────
    private static final int C_HP_BG     = 0xFF1A0000;
    private static final int C_HP_FILL   = 0xFFCC2222;
    private static final int C_HP_SHINE  = 0xFFFF5555;
    private static final int C_HP_LOW    = 0xFFFF2200; // < 30%

    private static final int C_MP_BG     = 0xFF00001A;
    private static final int C_MP_FILL   = 0xFF2244CC;
    private static final int C_MP_SHINE  = 0xFF4488FF;

    private static final int C_BAR_BORDER = 0xFF334466;

    private static final int C_FOOD      = 0xFFDDAA44;
    private static final int C_WATER     = 0xFF44AADD;
    private static final int C_FATIGUE   = 0xFF888844;
    private static final int C_ARMOR     = 0xFFAAAAAA;
    private static final int C_WHITE     = 0xFFFFFFFF;
    private static final int C_GRAY      = 0xFF888888;

    // ── Dimensões base ────────────────────────────────────────────────────────
    // Replicam a posição das barras vanilla (mesma largura que os corações/fome)
    private static final int BAR_W  = 81;   // largura de cada barra (idem vanilla hearts)
    private static final int BAR_H  = 6;    // espessura da barra
    private static final int BAR_Y_OFF = 37; // pixels acima da base do ecrã

    // ── Render principal ──────────────────────────────────────────────────────

    public static void render(GuiGraphics g, DeltaTracker delta) {
        Minecra