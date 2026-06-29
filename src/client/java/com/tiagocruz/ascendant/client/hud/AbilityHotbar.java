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
 * Barra de habilidades estilo Solo Leveling: Reawakening.
 *
 * Layout: 9 slots acima da hotbar vanilla, centrados.
 *   Slots 1-4 : habilidades gerais (Dash, Salto, Escudo, Esquiva)
 *   Slots 5-7 : habilidades da classe do jogador
 *   Slots 8-9 : reservados (futuro: dual-class)
 *
 * Controlos:
 *   R — toggle (mostra/esconde)
 *   1-9 — selecciona slot (espelha hotbar vanilla)
 *   Z — usa habilidade no slot seleccionado
 */
public class AbilityHotbar {

    // ── Estado ────────────────────────────────────────────────────────────────
    private static boolean visible = false;

    /** Cooldowns locais: endTime e totalDuration por abilityId */
    private static final Map<String, long[]> COOLDOWNS = new HashMap<>();

    public static boolean isVisible() { return visible; }
    public static void toggle() { visible = !visible; }
    public static void setVisible(boolean v) { visible = v; }

    /** Inicia um cooldown local (estimativa visual — sem reducao de SAB). */
    public static void startCooldown(String abilityId, long durationMs) {
        COOLDOWNS.put(abilityId, new long[]{
            System.currentTimeMillis() + durationMs,
            durationMs
        });
    }

    /** Habilidade no slot dado, considerando a classe do jogador. Slot 0-8. */
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
                } catch (Exception e) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    /** ID da habilidade actualmente seleccionada (slot = hotbar vanilla). */
    public static String getSelectedAbilityId(Minecraft mc) {
        if (mc.player == null) return null;
        int slot = mc.player.getInventory().selected; // 0-8
        AscendantAbility ab = getAbilityForSlot(slot);
        return ab != null ? ab.id() : null;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    public static void render(GuiGraphics g, Minecraft mc) {
        if (!visible) return;
        if (mc.options.hideGui) return;
        if (mc.player == null) return;

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int slotSize = 22;
        int gap      = 2;
        int totalW   = 9 * slotSize + 8 * gap; // 214
        int startX   = screenW / 2 - totalW / 2;
        int startY   = screenH - 52; // acima da hotbar vanilla

        int selectedSlot = mc.player.getInventory().selected; // 0-8

        // ── Fundo do painel ─────────────────────────────────────────────────
        g.fill(startX - 3, startY - 14,
               startX + totalW + 3, startY + slotSize + 3,
               0xCC05090F);
        // Linha de topo decorativa
        g.fill(startX - 3, startY - 14,
               startX + totalW + 3, startY - 13,
               0xFF1A3A6A);

        // Titulo
        String title = "§b[R] §9HABILIDADES §7— §fZ §7para usar";
        int titleW = mc.font.width(title);
        g.drawString(mc.font, Component.literal(title),
            startX + totalW / 2 - titleW / 2, startY - 11, 0xFFFFFFFF, false);

        // ── Slots ───────────────────────────────────────────────────────────
        for (int i = 0; i < 9; i++) {
            int sx = startX + i * (slotSize + gap);
            AscendantAbility ab = getAbilityForSlot(i);
            boolean selected = (i == selectedSlot);

            if (ab == null) {
                // Slot vazio
                int emptyBg = selected ? 0xAA1A2230 : 0xAA0D1018;
                g.fill(sx, startY, sx + slotSize, startY + slotSize, emptyBg);
                drawBorder(g, sx, startY, slotSize, selected ? 0xFF2A3A50 : 0xFF151E28);
                // Numero do slot
                g.drawString(mc.font, Component.literal("§8" + (i + 1)),
                    sx + 2, startY + 2, 0xFF444455, false);
                continue;
            }

            // Cor de fundo por tipo de habilidade
            int bgColor = ab.isGeneral()
                ? (selected ? 0xAA0D1B2A : 0xAA080F1A)
                : classSlotColor(ClientPlayerData.getPlayerClass(), selected);

            g.fill(sx, startY, sx + slotSize, startY + slotSize, bgColor);
            drawBorder(g, sx, startY, slotSize,
                selected ? 0xFF55AAFF : 0xFF1A3050);

            // Numero do slot (canto superior esquerdo)
            g.drawString(mc.font, Component.literal("§7" + (i + 1)),
                sx + 2, startY + 2, 0xFF888899, false);

            // Icone da habilidade (centrado)
            int iconColor = ab.isGeneral()
                ? 0xFF44AAFF
                : classIconColor(ClientPlayerData.getPlayerClass());
            g.drawString(mc.font, Component.literal(ab.icon()),
                sx + slotSize / 2 - 3, startY + 8, iconColor, true);

            // ── Cooldown overlay ────────────────────────────────────────────
            long[] cd = COOLDOWNS.get(ab.id());
            boolean onCd = cd != null && System.currentTimeMillis() < cd[0];
            if (onCd) {
                long remaining = cd[0] - System.currentTimeMillis();
                float frac = (float) remaining / cd[1];
                int overlayH = (int)(slotSize * frac);
                g.fill(sx + 1, startY + 1, sx + slotSize - 1, startY + 1 + overlayH, 0xBB000022);

                // Texto de contagem
                String cdStr = remaining >= 1000
                    ? (remaining / 1000 + 1) + "s"
                    : remaining / 100 * 100 + "ms";  // fallback
                // Just show seconds
                String cdText = String.valueOf(remaining / 1000 + 1);
                int cdW = mc.font.width(cdText);
                g.drawString(mc.font, Component.literal("§f" + cdText),
                    sx + slotSize / 2 - cdW / 2, startY + 7, 0xFFFFFFFF, true);
            }

            // ── Mana insuficiente — tint vermelho ──────────────────────────
            if (!onCd && ClientPlayerData.getCurrentMana() < ab.manaCost()) {
                g.fill(sx + 1, startY + 1, sx + slotSize - 1, startY + slotSize - 1, 0x55AA0000);
            }
        }

        // ── Info da habilidade seleccionada ─────────────────────────────────
        AscendantAbility sel = getAbilityForSlot(selectedSlot);
        if (sel != null) {
            boolean onCd = COOLDOWNS.containsKey(sel.id())
                && System.currentTimeMillis() < COOLDOWNS.get(sel.id())[0];

            String cdInfo = onCd
                ? " §8(" + (COOLDOWNS.get(sel.id())[0] - System.currentTimeMillis()) / 1000 + "s)"
                : "";
            String manaInfo = " §9" + sel.manaCost() + " §7mana";
            String info = "§f" + sel.displayName() + cdInfo + manaInfo;

            int infoW = mc.font.width(info);
            g.drawString(mc.font, Component.literal(info),
                screenW / 2 - infoW / 2, startY + slotSize + 5, 0xFFFFFFFF, true);
        }
    }

    // ── Helpers de cor ────────────────────────────────────────────────────────

    private static int classSlotColor(String cls, boolean selected) {
        int base = switch (cls) {
            case "ASSASSIN" -> 0x3D1A55;
            case "GUARDIAN" -> 0x2A3040;
            case "MAGE"     -> 0x2A1040;
            case "TITAN"    -> 0x401010;
            case "ARCHER"   -> 0x103020;
            case "HEALER"   -> 0x0D1E35;
            case "SUMMONER" -> 0x302010;
            case "SPECTER"  -> 0x1E1E28;
            default         -> 0x0D1B2A;
        };
        int alpha = selected ? 0xBB000000 : 0x88000000;
        return (int)(alpha | base);
    }

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
        g.fill(x,          y,          x + size,     y + 1,        color); // topo
        g.fill(x,          y + size - 1, x + size,   y + size,     color); // baixo
        g.fill(x,          y,          x + 1,         y + size,     color); // esquerda
        g.fill(x + size - 1, y,        x + size,      y + size,     color); // direita
    }
}
