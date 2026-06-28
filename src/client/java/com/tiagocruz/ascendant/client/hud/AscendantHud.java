package com.tiagocruz.ascendant.client.hud;

import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * HUD mínimo do Ascendant — visível durante o jogo.
 *
 * Apenas mostra:
 *   - Notificação de level up (4 segundos, centro do ecrã)
 *   - Indicador de rank/classe discreto no canto inferior (se classe atribuída)
 *
 * O painel completo de stats está agora no ecrã K (AscendantStatsScreen).
 */
public class AscendantHud {

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        if (mc.screen != null)  return;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // ─── Notificação de Level Up (centro do ecrã) ────────────────────────
        if (ClientPlayerData.isShowingLevelUp()) {
            int level = ClientPlayerData.getLevel();
            String msg = "§e✦ NÍVEL " + level + " ALCANÇADO ✦";
            int textW = mc.font.width(msg);
            int cx = (screenW - textW) / 2;
            int cy = screenH / 3;
            graphics.fill(cx - 10, cy - 5, cx + textW + 10, cy + 15, 0xCC000000);
            graphics.fill(cx - 10, cy - 5, cx + textW + 10, cy - 4,  0xFFFFDD00); // linha top
            graphics.fill(cx - 10, cy + 15, cx + textW + 10, cy + 16, 0xFFFFDD00); // linha bottom
            graphics.drawString(mc.font, Component.literal(msg), cx, cy, 0xFFFF44, true);
        }

        // ─── Indicador discreto de rank (canto superior esquerdo, pequeno) ───
        if (ClientPlayerData.isClassAssigned()) {
            renderMiniIndicator(graphics, mc);
        }
    }

    private static void renderMiniIndicator(GuiGraphics g, Minecraft mc) {
        String cls   = ClientPlayerData.getPlayerClass();
        int    level = ClientPlayerData.getLevel();
        int    sp    = ClientPlayerData.getStatPoints();

        String clsShort = switch (cls) {
            case "ASSASSIN" -> "§8ASS";
            case "GUARDIAN" -> "§6GRD";
            case "MAGE"     -> "§9MAG";
            case "TITAN"    -> "§cTIT";
            case "ARCHER"   -> "§aARC";
            case "HEALER"   -> "§dCUR";
            case "SUMMONER" -> "§5INV";
            case "SPECTER"  -> "§7ESP";
            default         -> "§7???";
        };

        String indicator = clsShort + "§8 Nv.§f" + level;
        if (sp > 0) indicator += " §e[+" + sp + "]";

        int tw = mc.font.width(indicator);
        g.fill(2, 2, tw + 8, 13, 0x88000000);
        g.drawString(mc.font, Component.literal(indicator), 4, 4, 0xFFFFFF, false);
    }
}
