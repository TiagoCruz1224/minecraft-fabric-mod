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
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return;
        if (mc.player == null) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int baseY = sh - BAR_Y_OFF;
        int leftX = sw / 2 - 91;

        // ── HP Bar ────────────────────────────────────────────────────────────
        float maxHp  = mc.player.getMaxHealth();
        float hp     = mc.player.getHealth();
        float hpFrac = maxHp > 0 ? Math.min(1f, hp / maxHp) : 0f;
        boolean lowHp = hpFrac < 0.3f;

        g.fill(leftX, baseY, leftX + BAR_W, baseY + BAR_H, C_HP_BG);
        g.fill(leftX, baseY, leftX + (int)(BAR_W * hpFrac), baseY + BAR_H, lowHp ? C_HP_LOW : C_HP_FILL);
        g.fill(leftX, baseY, leftX + (int)(BAR_W * hpFrac), baseY + 1, C_HP_SHINE);
        g.fill(leftX, baseY, leftX + BAR_W, baseY, C_BAR_BORDER);
        g.fill(leftX, baseY + BAR_H, leftX + BAR_W, baseY + BAR_H + 1, C_BAR_BORDER);
        g.drawString(mc.font, Component.literal("§c❤ " + (int)hp + "/" + (int)maxHp),
            leftX, baseY - 9, C_WHITE, true);

        // ── MP Bar ────────────────────────────────────────────────────────────
        int rightX   = sw / 2 + 10;
        float mana   = ClientPlayerData.getCurrentMana();
        int maxMana  = ClientPlayerData.getMaxMana();
        float mpFrac = maxMana > 0 ? Math.min(1f, mana / maxMana) : 0f;

        g.fill(rightX, baseY, rightX + BAR_W, baseY + BAR_H, C_MP_BG);
        g.fill(rightX, baseY, rightX + (int)(BAR_W * mpFrac), baseY + BAR_H, C_MP_FILL);
        g.fill(rightX, baseY, rightX + (int)(BAR_W * mpFrac), baseY + 1, C_MP_SHINE);
        g.fill(rightX, baseY, rightX + BAR_W, baseY, C_BAR_BORDER);
        g.fill(rightX, baseY + BAR_H, rightX + BAR_W, baseY + BAR_H + 1, C_BAR_BORDER);
        g.drawString(mc.font, Component.literal("§9✦ " + (int)mana + "/" + maxMana),
            rightX, baseY - 9, C_WHITE, true);

        // ── Stats row ─────────────────────────────────────────────────────────
        int foodLvl  = mc.player.getFoodData().getFoodLevel();
        int armorVal = mc.player.getArmorValue();
        int waterLvl = ClientPlayerData.getWaterLevel();
        g.drawString(mc.font,
            Component.literal("§f🍗 " + foodLvl + "  §b💧 " + waterLvl + "  §7⚔ " + armorVal),
            leftX, sh - 49, C_GRAY, false);

        // ── Classe / Nível (canto superior esquerdo) ──────────────────────────
        String cls = ClientPlayerData.getPlayerClass();
        int lvl = ClientPlayerData.getLevel();
        if (!"NONE".equals(cls)) {
            g.drawString(mc.font, Component.literal("§b" + cls + " §7Lv." + lvl),
                6, 6, C_WHITE, true);
        }

        // ── Level-up flash ────────────────────────────────────────────────────
        if (ClientPlayerData.isLevelUpDisplayActive()) {
            String msg = "✦  LEVEL UP!  ✦";
            int msgW = mc.font.width(msg);
            float progress = ClientPlayerData.getLevelUpProgress();
            int alpha = (int)(Math.min(1f, progress * 2f) * 255);
            int color = (alpha << 24) | 0xFFDD00;
            g.drawString(mc.font, Component.literal("§e" + msg),
                (sw - msgW) / 2, sh / 2 - 20, color, true);
        }
    }
}