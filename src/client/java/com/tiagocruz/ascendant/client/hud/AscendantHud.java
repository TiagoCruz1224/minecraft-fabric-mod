package com.tiagocruz.ascendant.client.hud;

import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * HUD overlay do Sistema Ascendant.
 * Mostra: nível, classe, barra de XP, stats.
 */
public class AscendantHud {

    // Cores
    private static final int COLOR_BG = 0x88000000;
    private static final int COLOR_XP_BAR_BG = 0xFF333333;
    private static final int COLOR_XP_BAR = 0xFF00CCFF;
    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_GRAY = 0xAAAAAA;
    private static final int COLOR_YELLOW = 0xFFFF00;

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        if (mc.screen != null) return;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        String cls = ClientPlayerData.getPlayerClass();
        int level = ClientPlayerData.getLevel();
        long xp = ClientPlayerData.getXp();
        long xpMax = ClientPlayerData.getXpToNext();

        // --- Painel superior esquerdo ---
        int x = 4, y = 4;
        int panelW = 124, panelH = 58;
        graphics.fill(x - 2, y - 2, x + panelW, y + panelH, COLOR_BG);

        // Título com nível e classe
        String classDisplay = ClientPlayerData.isClassAssigned() ? cls : "???";
        graphics.drawString(mc.font,
            Component.literal("§e[SISTEMA]§f Nv." + level + " §7|§b " + classDisplay),
            x, y, COLOR_WHITE, true);

        // Barra de XP
        int barW = 120, barH = 4;
        int barY = y + 12;
        float xpFraction = xpMax > 0 ? Math.min(1f, (float) xp / xpMax) : 0f;
        graphics.fill(x, barY, x + barW, barY + barH, COLOR_XP_BAR_BG);
        if (xpFraction > 0)
            graphics.fill(x, barY, x + (int)(barW * xpFraction), barY + barH, COLOR_XP_BAR);

        // XP numérico
        graphics.drawString(mc.font,
            Component.literal("§7XP §f" + xp + "§7/§f" + xpMax),
            x, barY + 6, COLOR_GRAY, false);

        // Stats em linha
        graphics.drawString(mc.font,
            Component.literal(
                "§cFOR§f" + ClientPlayerData.getStrength() + " " +
                "§aAGI§f" + ClientPlayerData.getAgility() + " " +
                "§6RES§f" + ClientPlayerData.getEndurance() + " " +
                "§9INT§f" + ClientPlayerData.getIntelligence() + " " +
                "§dPER§f" + ClientPlayerData.getPerception()),
            x, barY + 18, COLOR_WHITE, false);

        // Pontos disponíveis
        int sp = ClientPlayerData.getStatPoints();
        if (sp > 0) {
            graphics.drawString(mc.font,
                Component.literal("§e✦ " + sp + " ponto(s) para distribuir"),
                x, barY + 30, COLOR_YELLOW, true);
        }

        // --- Notificação de Level Up ---
        if (ClientPlayerData.isShowingLevelUp()) {
            String msg = "§e✦ NÍVEL " + level + " ALCANÇADO ✦";
            int textW = mc.font.width(msg);
            int cx = (screenW - textW) / 2;
            int cy = screenH / 3;
            graphics.fill(cx - 8, cy - 4, cx + textW + 8, cy + 14, 0xCC000000);
            graphics.drawString(mc.font, Component.literal(msg), cx, cy, 0xFFFF44, true);
        }

        // --- Classe recém atribuída (overlay central) ---
        // (apenas se houver notificação pendente — implementar depois)
    }
}
