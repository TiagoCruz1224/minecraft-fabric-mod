package com.tiagocruz.ascendant.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Menu principal do Ascendant (tecla K).
 * Duas opções: Perfil (stats) e Habilidades.
 */
public class MainMenuScreen extends Screen {

    private static final int PANEL_W = 200;
    private static final int PANEL_H = 160;

    private static final int C_BG     = 0xE0080810;
    private static final int C_BORDER = 0xFF1A3A6A;
    private static final int C_HEADER = 0xFF0D1B2A;
    private static final int C_WHITE  = 0xFFFFFFFF;
    private static final int C_GRAY   = 0xFF888888;

    public MainMenuScreen() {
        super(Component.literal("Ascendant"));
    }

    @Override
    protected void init() {
        int cx = (this.width  - PANEL_W) / 2;
        int cy = (this.height - PANEL_H) / 2;

        // Botão Perfil
        addRenderableWidget(Button.builder(
            Component.literal("§f◉ PERFIL"),
            btn -> this.minecraft.setScreen(new AscendantStatsScreen())
        ).pos(cx + 20, cy + 50).size(160, 22).build());

        // Botão Habilidades
        addRenderableWidget(Button.builder(
            Component.literal("§b✦ HABILIDADES"),
            btn -> this.minecraft.setScreen(new AbilitiesScreen())
        ).pos(cx + 20, cy + 80).size(160, 22).build());

        // Botão Fechar
        addRenderableWidget(Button.builder(
            Component.literal("§8[ESC] Fechar"),
            btn -> this.onClose()
        ).pos(cx + 55, cy + 120).size(90, 16).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        g.fill(0, 0, this.width, this.height, 0x88000000);

        int x = (this.width  - PANEL_W) / 2;
        int y = (this.height - PANEL_H) / 2;

        // Sombra + fundo + borda
        g.fill(x + 3, y + 3, x + PANEL_W + 3, y + PANEL_H + 3, 0x66000000);
        g.fill(x, y, x + PANEL_W, y + PANEL_H, C_BG);
        drawBorder(g, x, y, PANEL_W, PANEL_H, C_BORDER);

        // Header
        g.fill(x, y, x + PANEL_W, y + 26, C_HEADER);
        drawBorder(g, x, y, PANEL_W, 26, C_BORDER);
        g.drawString(font, Component.literal("§b⚔ §fSISTEMA §bASCENDANT"), x + 8, y + 9, C_WHITE, true);

        // Subtítulo
        g.drawString(font, Component.literal("§8Escolhe uma opção:"), x + 8, y + 36, C_GRAY, false);

        super.render(g, mx, my, pt);
    }

    private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y, x + 1, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean keyPressed(int keyCode, int scan, int mods) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_K ||
            keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scan, mods);
    }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics g, int mx, int my, float pt) {}
}
