package com.tiagocruz.ascendant.client.hud;

import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * HUD do Ascendant.
 *
 * ESQUERDA (corações):  [HP BAR ███ 20/20 ████]
 * DIREITA  (fome):      [food] n  [xp] n        ← 2 icons por linha
 *                       [water] n [sat] n
 *                       [MP BAR ██ 100/100 ███]
 */
public class AscendantHud {

    // ── Cores ─────────────────────────────────────────────────────────────────
    private static final int C_HP_BG    = 0xFF1A0000;
    private static final int C_HP_FILL  = 0xFFCC2222;
    private static final int C_HP_SHINE = 0xFFFF5555;
    private static final int C_HP_LOW   = 0xFFFF2200;

    private static final int C_MP_BG    = 0xFF000022;
    private static final int C_MP_FILL  = 0xFF2244CC;
    private static final int C_MP_SHINE = 0xFF4488FF;

    private static final int C_BORDER   = 0xFF223344;
    private static final int C_WHITE    = 0xFFFFFFFF;

    // ── Dimensões ─────────────────────────────────────────────────────────────
    private static final int HP_W  = 91;  // mesmo que vanilla hearts
    private static final int MP_W  = 81;  // mesmo que vanilla food
    private static final int BAR_H = 9;   // altura suficiente para texto dentro (font=9)

    // Barra de HP/MP fica acima da XP bar vanilla (sh-29 aprox.)
    // barY = sh-42, bar bottom = sh-33, gap para XP bar = 4px
    private static final int BAR_Y_OFF = 42;

    // Icons (item stacks estáticos — sem alocação por frame)
    private static final ItemStack ICON_FOOD  = new ItemStack(Items.COOKED_BEEF);
    private static final ItemStack ICON_WATER = new ItemStack(Items.WATER_BUCKET);
    private static final ItemStack ICON_XP    = new ItemStack(Items.EXPERIENCE_BOTTLE);
    private static final ItemStack ICON_SAT   = new ItemStack(Items.APPLE);

    // ── Render ────────────────────────────────────────────────────────────────
    public static void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        int hpX  = sw / 2 - 91;   // início da barra HP (alinha com hotbar esq.)
        int mpX  = sw / 2 + 10;   // início da barra MP (alinha com hotbar dir.)
        int barY = sh - BAR_Y_OFF;

        // Fonte global do mod (Rajdhani custom; fallback para mc.font)
        net.minecraft.client.gui.Font font = AscendantFont.get(mc);

        // ── HP — esquerda ──────────────────────────────────────────────────────
        float maxHp  = mc.player.getMaxHealth();
        float hp     = mc.player.getHealth();
        float hpFrac = maxHp > 0 ? Math.min(1f, hp / maxHp) : 0f;

        drawBar(g, font, hpX, barY, HP_W, BAR_H,
            C_HP_BG,
            hpFrac < 0.3f ? C_HP_LOW : C_HP_FILL,
            C_HP_SHINE,
            C_BORDER,
            hpFrac,
            (int) hp + "/" + (int) maxHp);

        // ── Stat icons — por cima da barra MP ─────────────────────────────────
        FoodData food    = mc.player.getFoodData();
        int foodLvl      = food.getFoodLevel();
        int waterLvl     = ClientPlayerData.getWaterLevel();
        int xpLvl        = mc.player.experienceLevel;
        int satPct       = (int)(food.getSaturationLevel() / 5f * 100f); // 0-100%

        // Duas linhas de 2 icons cada, acima da barra MP
        // Linha 1 (mais perto da barra):  food | xp
        // Linha 2 (mais acima):           water | sat
        int iconGap  = 2;                        // gap entre icon e número
        int rowGap   = 2;                        // gap vertical entre linhas
        int iconH    = 9;                        // altura do icon (vamos escalar 16→9)
        int row1Y    = barY - iconH - rowGap;    // y da linha 1
        int row2Y    = row1Y - iconH - rowGap;   // y da linha 2

        // Renderizar icons escalados a 9×9 usando pose matrix
        renderSmallItem(g, ICON_FOOD,  mpX,       row1Y);
        renderSmallItem(g, ICON_XP,    mpX + 40,  row1Y);
        renderSmallItem(g, ICON_WATER, mpX,       row2Y);
        renderSmallItem(g, ICON_SAT,   mpX + 40,  row2Y);

        // Valores ao lado dos icons
        int textOffY = 1; // centrar verticalmente nos 9px
        g.drawString(font, AscendantFont.text(String.valueOf(foodLvl)),
            mpX + iconH + iconGap, row1Y + textOffY, C_WHITE, true);
        g.drawString(font, AscendantFont.text(String.valueOf(xpLvl)),
            mpX + 40 + iconH + iconGap, row1Y + textOffY, C_WHITE, true);
        g.drawString(font, AscendantFont.text(String.valueOf(waterLvl)),
            mpX + iconH + iconGap, row2Y + textOffY, C_WHITE, true);
        g.drawString(font, AscendantFont.text(satPct + "%"),
            mpX + 40 + iconH + iconGap, row2Y + textOffY, C_WHITE, true);

        // ── MP — direita ───────────────────────────────────────────────────────
        float mana   = ClientPlayerData.getCurrentMana();
        int maxMana  = ClientPlayerData.getMaxMana();
        float mpFrac = maxMana > 0 ? Math.min(1f, mana / maxMana) : 0f;

        drawBar(g, font, mpX, barY, MP_W, BAR_H,
            C_MP_BG, C_MP_FILL, C_MP_SHINE, C_BORDER,
            mpFrac,
            (int) mana + "/" + maxMana);

        // ── Classe / Nível — canto sup. esq. ──────────────────────────────────
        String cls = ClientPlayerData.getPlayerClass();
        int lvl = ClientPlayerData.getLevel();
        if (!"NONE".equals(cls)) {
            g.drawString(font,
                AscendantFont.text(cls + " Lv." + lvl),
                6, 6, 0xFF55FFFF, true);
        }

        // ── Level-up flash ────────────────────────────────────────────────────
        if (ClientPlayerData.isLevelUpDisplayActive()) {
            String msg = "LEVEL UP!";
            net.minecraft.network.chat.Component msgComp = AscendantFont.text(msg);
            int msgW = font.width(msgComp);
            float progress = ClientPlayerData.getLevelUpProgress();
            int alpha = (int)(Math.min(1f, progress * 2f) * 255);
            int color = (alpha << 24) | 0xFFDD00;
            g.drawString(font, msgComp,
                (sw - msgW) / 2, sh / 2 - 20, color, true);
        }
    }

    // ── Barra com texto centrado dentro ───────────────────────────────────────
    private static void drawBar(GuiGraphics g, net.minecraft.client.gui.Font font,
                                int x, int y, int w, int h,
                                int bgColor, int fillColor, int shineColor,
                                int borderColor, float frac, String label) {
        int fillW = (int)(w * frac);
        // fundo
        g.fill(x, y, x + w, y + h, bgColor);
        // preenchimento
        if (fillW > 0) {
            g.fill(x, y, x + fillW, y + h, fillColor);
            g.fill(x, y, x + fillW, y + 1, shineColor);
        }
        // bordas
        g.fill(x - 1, y - 1, x + w + 1, y,         borderColor);
        g.fill(x - 1, y + h, x + w + 1, y + h + 1, borderColor);
        g.fill(x - 1, y - 1, x,         y + h + 1, borderColor);
        g.fill(x + w, y - 1, x + w + 1, y + h + 1, borderColor);
        // texto centrado dentro da barra (shadow=true para contraste)
        net.minecraft.network.chat.Component labelComp = AscendantFont.text(label);
        int labelW = font.width(labelComp);
        int textX  = x + (w - labelW) / 2;
        int textY  = y + (h - font.lineHeight) / 2;
        g.drawString(font, labelComp, textX, textY, C_WHITE, true);
    }

    // ── Render item escalado a 9×9 px ─────────────────────────────────────────
    private static void renderSmallItem(GuiGraphics g, ItemStack stack, int x, int y) {
        // Item nativo = 16×16. Escalar para 9×9 ≈ 0.5625×
        float scale = 9f / 16f;
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1f);
        g.renderItem(stack, 0, 0);
        g.pose().popPose();
    }
}
