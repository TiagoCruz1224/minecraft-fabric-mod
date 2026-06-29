package com.tiagocruz.ascendant.client.hud;

import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * HUD do Ascendant — canto superior esquerdo.
 *
 *  [HP ████ 20/20 ]
 *  [MP ████ 93/100]
 *  [food]20  [sat]100%
 *  [water]20 [xp]5
 */
public class AscendantHud {

    private static final int C_HP_BG    = 0xFF1A0000;
    private static final int C_HP_FILL  = 0xFFCC2222;
    private static final int C_HP_SHINE = 0xFFFF5555;
    private static final int C_HP_LOW   = 0xFFFF2200;
    private static final int C_MP_BG    = 0xFF000022;
    private static final int C_MP_FILL  = 0xFF2244CC;
    private static final int C_MP_SHINE = 0xFF4488FF;
    private static final int C_BORDER   = 0xFF223344;
    private static final int C_WHITE    = 0xFFFFFFFF;

    private static final int BAR_W  = 91;
    private static final int BAR_H  = 9;
    private static final int BAR_X  = 6;   // canto esquerdo
    private static final int HP_Y   = 6;
    private static final int MP_Y   = HP_Y + BAR_H + 4;  // 19
    private static final int ICON_Y = MP_Y + BAR_H + 5;  // 33

    private static final ItemStack ICON_FOOD  = new ItemStack(Items.COOKED_BEEF);
    private static final ItemStack ICON_WATER = new ItemStack(Items.WATER_BUCKET);
    private static final ItemStack ICON_XP    = new ItemStack(Items.EXPERIENCE_BOTTLE);
    private static final ItemStack ICON_SAT   = new ItemStack(Items.APPLE);

    public static void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        net.minecraft.client.gui.Font font = AscendantFont.get(mc);

        // ── HP bar ──────────────────────────────────────────────────────────
        float maxHp  = mc.player.getMaxHealth();
        float hp     = mc.player.getHealth();
        float hpFrac = maxHp > 0 ? Math.min(1f, hp / maxHp) : 0f;
        drawBar(g, font, BAR_X, HP_Y, BAR_W, BAR_H,
            C_HP_BG, hpFrac < 0.3f ? C_HP_LOW : C_HP_FILL, C_HP_SHINE, C_BORDER,
            hpFrac, (int)hp + "/" + (int)maxHp);

        // ── MP bar ──────────────────────────────────────────────────────────
        float mana  = ClientPlayerData.getCurrentMana();
        int maxMana = ClientPlayerData.getMaxMana();
        float mpFrac = maxMana > 0 ? Math.min(1f, mana / maxMana) : 0f;
        drawBar(g, font, BAR_X, MP_Y, BAR_W, BAR_H,
            C_MP_BG, C_MP_FILL, C_MP_SHINE, C_BORDER,
            mpFrac, (int)mana + "/" + maxMana);

        // ── Stat icons (2 colunas × 2 linhas) ──────────────────────────────
        FoodData food  = mc.player.getFoodData();
        int foodLvl    = food.getFoodLevel();
        int waterLvl   = ClientPlayerData.getWaterLevel();
        int xpLvl      = mc.player.experienceLevel;
        int satPct     = (int)(food.getSaturationLevel() / 5f * 100f);

        int iconSize   = 9;
        int colGap     = 44;  // dist entre colunas
        int rowGap     = 2;

        renderSmallItem(g, ICON_FOOD,  BAR_X,         ICON_Y);
        renderSmallItem(g, ICON_SAT,   BAR_X + colGap, ICON_Y);
        renderSmallItem(g, ICON_WATER, BAR_X,          ICON_Y + iconSize + rowGap);
        renderSmallItem(g, ICON_XP,    BAR_X + colGap, ICON_Y + iconSize + rowGap);

        g.drawString(font, AscendantFont.text(String.valueOf(foodLvl)),
            BAR_X + iconSize + 2, ICON_Y + 1, C_WHITE, true);
        g.drawString(font, AscendantFont.text(satPct + "%"),
            BAR_X + colGap + iconSize + 2, ICON_Y + 1, C_WHITE, true);
        g.drawString(font, AscendantFont.text(String.valueOf(waterLvl)),
            BAR_X + iconSize + 2, ICON_Y + iconSize + rowGap + 1, C_WHITE, true);
        g.drawString(font, AscendantFont.text(String.valueOf(xpLvl)),
            BAR_X + colGap + iconSize + 2, ICON_Y + iconSize + rowGap + 1, C_WHITE, true);

        // ── Classe / Nível — canto sup. direito ────────────────────────────
        String cls = ClientPlayerData.getPlayerClass();
        int lvl = ClientPlayerData.getLevel();
        if (!"NONE".equals(cls)) {
            net.minecraft.network.chat.Component clsComp = AscendantFont.text(cls + " Lv." + lvl);
            int clsW = font.width(clsComp);
            g.drawString(font, clsComp, sw - clsW - 6, 6, 0xFF55FFFF, true);
        }

        // ── Level-up flash ──────────────────────────────────────────────────
        if (ClientPlayerData.isLevelUpDisplayActive()) {
            String msg = "LEVEL UP!";
            net.minecraft.network.chat.Component msgComp = AscendantFont.text(msg);
            int msgW = font.width(msgComp);
            float progress = ClientPlayerData.getLevelUpProgress();
            int alpha = (int)(Math.min(1f, progress * 2f) * 255);
            int color = (alpha << 24) | 0xFFDD00;
            g.drawString(font, msgComp, (sw - msgW) / 2, sh / 2 - 20, color, true);
        }
    }

    private static void drawBar(GuiGraphics g, net.minecraft.client.gui.Font font,
                                int x, int y, int w, int h,
                                int bgColor, int fillColor, int shineColor,
                                int borderColor, float frac, String label) {
        int fillW = (int)(w * frac);
        g.fill(x, y, x + w, y + h, bgColor);
        if (fillW > 0) {
            g.fill(x, y, x + fillW, y + h, fillColor);
            g.fill(x, y, x + fillW, y + 1, shineColor);
        }
        g.fill(x - 1, y - 1, x + w + 1, y,         borderColor);
        g.fill(x - 1, y + h, x + w + 1, y + h + 1, borderColor);
        g.fill(x - 1, y - 1, x,         y + h + 1, borderColor);
        g.fill(x + w, y - 1, x + w + 1, y + h + 1, borderColor);
        net.minecraft.network.chat.Component labelComp = AscendantFont.text(label);
        int labelW = font.width(labelComp);
        g.drawString(font, labelComp,
            x + (w - labelW) / 2,
            y + (h - font.lineHeight) / 2,
            C_WHITE, true);
    }

    private static void renderSmallItem(GuiGraphics g, ItemStack stack, int x, int y) {
        float scale = 9f / 16f;
        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(scale, scale, 1f);
        g.renderItem(stack, 0, 0);
        g.pose().popPose();
    }
}
