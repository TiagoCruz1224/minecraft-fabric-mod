package com.tiagocruz.ascendant.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Overlay de ecrã cheio quando o Sistema atribui uma classe.
 * Aparece durante ~5 segundos com animação gradual.
 */
public class ClassRevealOverlay {

    private static String className = "";
    private static String displayName = "";
    private static boolean isRare = false;
    private static long startTime = -1;
    private static final long DURATION_MS = 6000;

    public static void trigger(String cls, String display, boolean rare) {
        className = cls;
        displayName = display;
        isRare = rare;
        startTime = System.currentTimeMillis();
    }

    public static boolean isActive() {
        return startTime > 0 && (System.currentTimeMillis() - startTime) < DURATION_MS;
    }

    public static void render(GuiGraphics graphics) {
        if (!isActive()) return;

        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1f, elapsed / 1000f); // fade in 1s
        float fadeOut = elapsed > 5000 ? 1f - (elapsed - 5000) / 1000f : 1f; // fade out last 1s
        float alpha = Math.min(progress, fadeOut);

        // Fundo negro com alpha
        int bgAlpha = (int)(alpha * 0xBB);
        graphics.fill(0, 0, w, h, (bgAlpha << 24));

        // Linha decorativa horizontal
        int lineY = h / 2 - 30;
        graphics.fill(w / 4, lineY, 3 * w / 4, lineY + 1, 0xFFFFFFFF);
        graphics.fill(w / 4, h / 2 + 28, 3 * w / 4, h / 2 + 29, 0xFFFFFFFF);

        // "O SISTEMA RECONHECE" (pequeno, acima)
        String header = "§7O Sistema reconhece em ti...";
        int headerW = mc.font.width(header);
        graphics.drawString(mc.font, Component.literal(header),
            (w - headerW) / 2, h / 2 - 20, 0x88FFFFFF, false);

        // Nome da classe (grande, central)
        String title = isRare ? "§d✦ " + displayName + " ✦" : "§e" + displayName;
        int titleW = mc.font.width(title);
        // Escalar 2x manualmente desenhando 2 vezes com offset (simular bold/big)
        graphics.drawString(mc.font, Component.literal(title),
            (w - titleW) / 2, h / 2 - 4, 0xFFFFFF, true);

        // Tipo (raro ou base)
        String sub = isRare ? "§d[ Classe Rara ]" : "§7[ Classe Atribuída ]";
        int subW = mc.font.width(sub);
        graphics.drawString(mc.font, Component.literal(sub),
            (w - subW) / 2, h / 2 + 12, 0xAAAAAA, false);
    }
}
