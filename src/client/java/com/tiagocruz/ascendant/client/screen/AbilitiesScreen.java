package com.tiagocruz.ascendant.client.screen;

import com.tiagocruz.ascendant.ability.AbilityRegistry;
import com.tiagocruz.ascendant.ability.AscendantAbility;
import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Ecrã de Habilidades — mostra todas as habilidades com estado de desbloqueio.
 * Acedido via Menu Principal → Habilidades.
 */
public class AbilitiesScreen extends Screen {

    private static final int PANEL_W = 300;
    private static final int PANEL_H = 230;

    private static final int C_BG      = 0xE0080810;
    private static final int C_BORDER  = 0xFF1A3A6A;
    private static final int C_HEADER  = 0xFF0D1B2A;
    private static final int C_WHITE   = 0xFFFFFFFF;
    private static final int C_GRAY    = 0xFF888888;
    private static final int C_GOLD    = 0xFFFFD700;
    private static final int C_LOCKED  = 0xFF333344;
    private static final int C_UNLOCKED= 0xFF0D2040;

    public AbilitiesScreen() {
        super(Component.literal("Habilidades"));
    }

    @Override
    protected void init() {
        int cx = (this.width  - PANEL_W) / 2;
        int cy = (this.height - PANEL_H) / 2;

        addRenderableWidget(Button.builder(
            Component.literal("§8◄ Voltar"),
            btn -> this.minecraft.setScreen(new MainMenuScreen())
        ).pos(cx + PANEL_W - 70, cy + PANEL_H - 18).size(65, 14).build());
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        g.fill(0, 0, this.width, this.height, 0x88000000);

        int x = (this.width  - PANEL_W) / 2;
        int y = (this.height - PANEL_H) / 2;

        // Painel
        g.fill(x + 3, y + 3, x + PANEL_W + 3, y + PANEL_H + 3, 0x66000000);
        g.fill(x, y, x + PANEL_W, y + PANEL_H, C_BG);
        drawBorder(g, x, y, PANEL_W, PANEL_H, C_BORDER);

        // Header
        g.fill(x, y, x + PANEL_W, y + 26, C_HEADER);
        drawBorder(g, x, y, PANEL_W, 26, C_BORDER);
        g.drawString(font, Component.literal("§b✦ §fHABILIDADES"), x + 8, y + 9, C_WHITE, true);

        int playerLevel = ClientPlayerData.getLevel();
        g.drawString(font, Component.literal("§7Nível " + playerLevel + "  —  desbloqueadas pelo rank"),
            x + 8, y + 30, C_GRAY, false);

        // Separador
        g.fill(x + 4, y + 41, x + PANEL_W - 4, y + 42, C_BORDER);

        // Lista de habilidades
        List<AscendantAbility> abilities = AbilityRegistry.ALL_ABILITIES;
        int rowY = y + 46;
        int rowH = 26;

        for (AscendantAbility ab : abilities) {
            boolean unlocked = ab.rank().isUnlocked(playerLevel);
            drawAbilityRow(g, ab, unlocked, x + 6, rowY, PANEL_W - 12, rowH - 2);
            rowY += rowH;
            if (rowY + rowH > y + PANEL_H - 20) break; // não ultrapassar painel
        }

        super.render(g, mx, my, pt);
    }

    private void drawAbilityRow(GuiGraphics g, AscendantAbility ab, boolean unlocked,
                                 int x, int y, int w, int h) {
        int bg = unlocked ? C_UNLOCKED : C_LOCKED;
        int borderCol = unlocked ? 0xFF1A3A6A : 0xFF2A2A44;

        g.fill(x, y, x + w, y + h, bg);
        drawBorder(g, x, y, w, h, borderCol);

        // Ícone + nome
        String rankStr = ab.rank().getColored() + " §r";
        String nameStr = unlocked ? "§f" + ab.displayName() : "§8" + ab.displayName();
        String iconStr = (unlocked ? "§e" : "§8") + ab.icon();

        g.drawString(font, Component.literal(iconStr), x + 4, y + 4, C_WHITE, true);
        g.drawString(font, Component.literal("[" + rankStr + "] " + nameStr), x + 16, y + 4, C_WHITE, false);

        // Descrição ou "Bloqueado"
        if (unlocked) {
            g.drawString(font, Component.literal("§7" + ab.description()), x + 16, y + 14, C_GRAY, false);
            // Custo de mana e cooldown
            String info = "§9" + ab.manaCost() + " mana  §8|  §7" + (ab.cooldownMs() / 1000) + "s CD";
            g.drawString(font, Component.literal(info), x + w - 100, y + 4, C_GRAY, false);
        } else {
            g.drawString(font,
                Component.literal("§8Requer nível " + ab.rank().getMinLevel() + " (" + ab.rank().getRankRequired() + ")"),
                x + 16, y + 14, C_GRAY, false);
        }
    }

    private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y, x + 1, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean keyPressed(int keyCode, int scan, int mods) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.setScreen(new MainMenuScreen());
            return true;
        }
        return super.keyPressed(keyCode, scan, mods);
    }

    @Override public boolean isPauseScreen() { return false; }
    @Override public void renderBackground(GuiGraphics g, int mx, int my, float pt) {}
}
