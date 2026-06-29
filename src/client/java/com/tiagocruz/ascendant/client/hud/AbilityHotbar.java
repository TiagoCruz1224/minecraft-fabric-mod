package com.tiagocruz.ascendant.client.hud;

import com.tiagocruz.ascendant.ability.AbilityRegistry;
import com.tiagocruz.ascendant.ability.AscendantAbility;
import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Barra de habilidades — substitui o hotbar vanilla quando activa.
 *
 * R  — toggle (mostra/esconde; restaura vanilla quando escondida)
 * 1-9 — selecciona slot (espelha hotbar vanilla)
 * Z  — usa habilidade no slot seleccionado
 */
public class AbilityHotbar {

    private static boolean visible = false;

    private static final Map<String, long[]> COOLDOWNS = new HashMap<>();

    public static boolean isVisible()         { return visible; }
    public static void toggle()               { visible = !visible; }
    public static void setVisible(boolean v)  { visible = v; }

    public static void startCooldown(String abilityId, long durationMs) {
        COOLDOWNS.put(abilityId, new long[]{
            System.currentTimeMillis() + durationMs, durationMs
        });
    }

    public static AscendantAbility getAbilityForSlot(int slot) {
        return switch (slot) {
            case 0 -> AbilityRegistry.DASH;
            case 1 -> AbilityRegistry.DOUBLE_JUMP;
            case 2 -> AbilityRegistry.ENERGY_SHIELD;
            case 3 -> AbilityRegistry.DODGE;
            case 4, 5, 6 -> {
                try {
                    PlayerClass cls = PlayerClass.valueOf(ClientPlayerData.getPlayerClass());
                    yield AbilityRegistry.getClassAbility(cls, slot - 4);
                } catch (Exception e) { yield null; }
            }
            default -> null;
        };
    }

    public static String getSelectedAbilityId(Minecraft mc) {
        if (mc.player == null) return null;
        int slot = mc.player.getInventory().selected;
        AscendantAbility ab = getAbilityForSlot(slot);
        return ab != null ? ab.id() : null;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    public static void render(GuiGraphics g, Minecraft mc) {
        if (!visible) return;
        if (mc.options.hideGui || mc.player == null) return;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        final int SLOT  = 22;   // mesmo tamanho do slot vanilla (20px interior + 1px borda cada lado)
        final int GAP   = 1;
        int totalW = 9 * SLOT + 8 * GAP;   // 206
        int startX = screenW / 2 - totalW / 2;
        int startY = screenH - SLOT - 1;    // alinha com vanilla hotbar

        int selectedSlot = mc.player.getInventory().selected;

        for (int i = 0; i < 9; i++) {
            int sx = startX + i * (SLOT + GAP);
            AscendantAbility ab = getAbilityForSlot(i);
            boolean selected = (i == selectedSlot);

            // ── Fundo do slot ────────────────────────────────────────────────
            int bgColor = selected
                ? 0xCC1A2A3A
                : (ab != null ? 0xCC0D1520 : 0xBB080D14);
            g.fill(sx, startY, sx + SLOT, startY + SLOT, bgColor);

            // ── Borda ────────────────────────────────────────────────────────
            int borderColor = selected
                ? 0xFF66AAFF
                : (ab != null ? 0xFF1E3A5A : 0xFF111A22);
            drawBorder(g, sx, startY, SLOT, borderColor);

            // Borda extra (glow) no slot seleccionado
            if (selected) {
                drawBorder(g, sx - 1, startY - 1, SLOT + 2, 0x4466AAFF);
            }

            // ── Número do slot (canto sup esq) ──────────────────────────────
            String num = String.valueOf(i + 1);
            g.drawString(mc.font, Component.literal("§8" + num), sx + 2, startY + 2, 0xFFFFFFFF, false);

            if (ab == null) continue;

            // ── Ícone da habilidade (centrado) ───────────────────────────────
            int iconColor = ab.isGeneral()
                ? 0xFF44AAFF
                : classIconColor(ClientPlayerData.getPlayerClass());

            // Ícone (texto Unicode)
            String icon = ab.icon();
            int iconW = mc.font.width(icon);
            g.drawString(mc.font, Component.literal(icon),
                sx + SLOT / 2 - iconW / 2,
                startY + SLOT / 2 - 4,
                iconColor, true);

            // ── Cooldown overlay ─────────────────────────────────────────────
            long[] cd = COOLDOWNS.get(ab.id());
            boolean onCd = cd != null && System.currentTimeMillis() < cd[0];
            if (onCd) {
                long remaining = cd[0] - System.currentTimeMillis();
                float frac = (float) remaining / cd[1];
                int overlayH = (int)((SLOT - 2) * frac);
                g.fill(sx + 1, startY + 1, sx + SLOT - 1, startY + 1 + overlayH, 0xBB000033);

                String cdText = String.valueOf(remaining / 1000 + 1);
                int cdW = mc.font.width(cdText);
                g.drawString(mc.font, Component.literal("§f" + cdText),
                    sx + SLOT / 2 - cdW / 2, startY + SLOT / 2 - 4, 0xFFFFFFFF, true);
            }

            // ── Mana insuficiente — tint vermelho ────────────────────────────
            if (!onCd && ClientPlayerData.getCurrentMana() < ab.manaCost()) {
                g.fill(sx + 1, startY + 1, sx + SLOT - 1, startY + SLOT - 1, 0x55AA0000);
            }
        }

        // ── Nome da habilidade seleccionada (tooltip compacto acima do slot) ──
        AscendantAbility sel = getAbilityForSlot(selectedSlot);
        if (sel != null) {
            int slotCenterX = startX + selectedSlot * (SLOT + GAP) + SLOT / 2;
            String name = sel.displayName() + " §8(" + sel.manaCost() + " mana)";
            int nameW = mc.font.width(name);
            int tx = slotCenterX - nameW / 2;
            int ty = startY - 12;
            // Fundo do tooltip
            g.fill(tx - 2, ty - 1, tx + nameW + 2, ty + 9, 0xBB000000);
            g.drawString(mc.font, Component.literal(name), tx, ty, 0xFFFFFFFF, true);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static int classIconColor(String cls) {
        return switch (cls) {
            case "ASSASSIN" -> 0xFFCC77FF;
            case "GUARDIAN" -> 0xFFBBCCDD;
            case "MAGE"     -> 0xFFAA55FF;
            case "TITAN"    -> 0xFFFF6644;
            case "ARCHER"   -> 0xFF55DD77;
            case "HEALER"   -> 0xFF55BBFF;
            case "SUMMONER" -> 0xFFFFCC44;
            case "SPECTER"  -> 0xFFAABBCC;
            default         -> 0xFF44AAFF;
        };
    }

    private static void drawBorder(GuiGraphics g, int x, int y, int size, int color) {
        g.fill(x,          y,              x + size, y + 1,        color);
        g.fill(x,          y + size - 1,  x + size, y + size,      color);
        g.fill(x,          y,              x + 1,    y + size,      color);
        g.fill(x + size - 1, y,           x + size, y + size,      color);
    }
}
