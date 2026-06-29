package com.tiagocruz.ascendant.client.hud;

import com.tiagocruz.ascendant.ability.AbilityRegistry;
import com.tiagocruz.ascendant.ability.AscendantAbility;
import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * HUD de habilidades — visível quando o jogador activa com H.
 *
 * Mostra no lado direito as 4 habilidades gerais com:
 *   - Ícone + nome
 *   - Tecla de activação
 *   - Barra de cooldown (verde → preenchida)
 *   - Custo de mana
 */
public class AbilityHud {

    private static boolean visible = false;

    // Tempos de cooldown cliente (estimados — actualizados via feedback do servidor)
    // Guardamos quando a habilidade foi usada e quanto tempo tem de CD
    private static final long[] CD_END   = new long[4]; // timestamp de fim de cooldown
    private static final long[] CD_TOTAL = new long[4]; // duração total do cooldown

    public static boolean isVisible() { return visible; }
    public static void toggle() { visible = !visible; }
    public static void setVisible(boolean v) { visible = v; }

    /** Regista cooldown localmente (chamado após enviar UseAbilityPacket). */
    public static void startCooldown(int slotIndex, long durationMs) {
        CD_END[slotIndex] = System.currentTimeMillis() + durationMs;
        CD_TOTAL[slotIndex] = durationMs;
    }

    public static void render(GuiGraphics g, Minecraft mc) {
        if (!visible) return;
        if (mc.options.hideGui) return;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // Painel direito: 4 habilidades gerais empilhadas verticalmente
        int panelW = 140;
        int rowH   = 30;
        int panelH = 4 * rowH + 8;
        int px = screenW - panelW - 6;
        int py = screenH / 2 - panelH / 2;

        // Fundo do painel
        g.fill(px - 2, py - 2, px + panelW + 2, py + panelH + 2, 0xCC080812);
        g.fill(px - 2, py - 2, px + panelW + 2, py - 1, 0xFF1A3A6A);
        g.fill(px - 2, py + panelH + 1, px + panelW + 2, py + panelH + 2, 0xFF1A3A6A);
        g.fill(px - 2, py - 2, px - 1, py + panelH + 2, 0xFF1A3A6A);

        // Cabeçalho
        g.drawString(mc.font, Component.literal("§b✦ §fHABILIDADES"),
            px + 4, py + 1, 0xFFFFFFFF, true);
        g.fill(px - 2, py + 10, px + panelW + 2, py + 11, 0xFF1A3A6A);

        // Linhas de habilidade
        AscendantAbility[] generals = AbilityRegistry.GENERAL_ABILITIES.toArray(new AscendantAbility[0]);
        int playerLevel = ClientPlayerData.getLevel();

        for (int i = 0; i < generals.length; i++) {
            AscendantAbility ab = generals[i];
            int ry = py + 14 + i * rowH;
            boolean unlocked = ab.rank().isUnlocked(playerLevel);
            boolean onCd = System.currentTimeMillis() < CD_END[i];

            // Fundo da linha
            int bgColor = unlocked ? 0xAA0D1B2A : 0xAA111118;
            g.fill(px, ry, px + panelW, ry + rowH - 2, bgColor);

            // Ícone + nome + tecla
            String iconCol = unlocked ? "§e" : "§8";
            String nameCol = unlocked ? "§f" : "§8";
            g.drawString(mc.font, Component.literal(iconCol + ab.icon()), px + 3, ry + 4, 0xFFFFFFFF, true);
            g.drawString(mc.font, Component.literal(nameCol + ab.displayName()), px + 14, ry + 4, 0xFFFFFFFF, false);

            // Tecla de activação
            String key = "[" + ab.getDefaultKey() + "]";
            int keyW = mc.font.width(key);
            g.drawString(mc.font, Component.literal("§7" + key), px + panelW - keyW - 3, ry + 4, 0xFF888888, false);

            // Custo de mana
            g.drawString(mc.font, Component.literal("§9" + ab.manaCost() + " ✦"), px + 14, ry + 14, 0xFF4488CC, false);

            if (unlocked) {
                if (onCd) {
                    // Barra de cooldown
                    long remaining = CD_END[i] - System.currentTimeMillis();
                    float frac = (float) remaining / CD_TOTAL[i];
                    int barW = panelW - 16;
                    g.fill(px + 14, ry + 14, px + 14 + barW, ry + 20, 0xFF222233);
                    g.fill(px + 14, ry + 14, px + 14 + (int)(barW * (1 - frac)), ry + 20, 0xFF44AA44);
                    String cdStr = (remaining / 1000 + 1) + "s";
                    g.drawString(mc.font, Component.literal("§7" + cdStr),
                        px + panelW - mc.font.width(cdStr) - 4, ry + 14, 0xFF888888, false);
                }
            } else {
                g.drawString(mc.font, Component.literal("§8Nível " + ab.rank().getMinLevel()),
                    px + 14, ry + 14, 0xFF555566, false);
            }
        }
    }
}
