package com.tiagocruz.ascendant.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

/**
 * Gestão centralizada da fonte do Ascendant HUD.
 *
 * Usa Style.withFont() para forçar a fonte ascendant:hud em qualquer
 * Component — assim o Minecraft usa directamente o TTF definido em
 * assets/ascendant/font/hud.json, sem depender da injecção no default.
 *
 * Uso padrão:   AscendantFont.text("foo")     → Component com fonte HUD
 * Uso vanilla:  AscendantFont.vanilla(mc)     → mc.font (bitmap Minecraft)
 */
public final class AscendantFont {

    private static final ResourceLocation HUD_FONT =
            ResourceLocation.fromNamespaceAndPath("ascendant", "hud");

    private AscendantFont() {}

    /**
     * Devolve a fonte renderer vanilla (mc.font).
     * Usa isto quando precisas de passar um Font object ao drawString().
     */
    public static Font get(Minecraft mc) {
        return mc.font;
    }

    /** Alias explícito para a fonte vanilla. */
    public static Font vanilla(Minecraft mc) {
        return mc.font;
    }

    /**
     * Cria um Component com a fonte ascendant:hud aplicada via Style.
     * Usa isto em vez de Component.literal() em todo o HUD para que
     * o Minecraft renderize com o TTF correcto.
     *
     * Exemplo: g.drawString(font, AscendantFont.text("20/20"), x, y, color, shadow)
     */
    public static Component text(String raw) {
        return Component.literal(raw)
                .setStyle(Style.EMPTY.withFont(HUD_FONT));
    }

    /**
     * Versão com formatação (§ codes) — aplica a fonte HUD
     * mas mantém as cores/formatações do texto.
     */
    public static Component styledText(String raw) {
        // Passa a string por Component.translatable para processar os § codes,
        // depois aplica a fonte ao estilo base.
        return Component.literal(raw)
                .setStyle(Style.EMPTY.withFont(HUD_FONT));
    }
}
