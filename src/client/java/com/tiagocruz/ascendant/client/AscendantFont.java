package com.tiagocruz.ascendant.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * Sistema de fontes do Ascendant.
 *
 * FONTE ACTUAL: mesma do Minecraft (referência a minecraft:default em ascendant:ascendant).
 *
 * ─── Como mudar a fonte do HUD do Ascendant ───────────────────────────────
 * 1. Coloca um ficheiro .ttf em:
 *      src/main/resources/assets/ascendant/font/
 *
 * 2. Edita o ficheiro:
 *      src/main/resources/assets/ascendant/font/ascendant.json
 *    Substitui o provider 'reference' por um provider 'ttf' com o teu ficheiro.
 *
 * ─── Como mudar a fonte de TODO o Minecraft ───────────────────────────────
 * Cria/edita:  src/main/resources/assets/minecraft/font/default.json
 * Conteúdo:
 *   {
 *     "providers": [
 *       { "type": "reference", "id": "ascendant:ascendant" }
 *     ]
 *   }
 *
 * ─── Uso no código ────────────────────────────────────────────────────────
 * Em vez de escrever:
 *   g.drawString(mc.font, "texto", x, y, cor);
 *
 * Escreve:
 *   g.drawString(mc.font, AscendantFont.text("texto"), x, y, cor);
 *
 * Isto aplica a fonte ascendant:ascendant automaticamente.
 */
public class AscendantFont {

    /**
     * ResourceLocation da nossa fonte personalizada.
     * Muda apenas este valor para trocar a fonte em todo o mod de uma vez.
     */
    public static final ResourceLocation FONT = ResourceLocation.fromNamespaceAndPath("ascendant", "ascendant");

    /**
     * Cria um Component com a fonte do Ascendant aplicada.
     * Usar em todas as chamadas drawString do nosso HUD e screens.
     *
     * Exemplo:
     *   g.drawString(mc.font, AscendantFont.text("20/20 HP"), x, y, 0xFFFFFF);
     */
    public static Component text(String raw) {
        return Component.literal(raw)
            .withStyle(Style.EMPTY.withFont(FONT));
    }

    /**
     * Versão com formatação § já incluída no texto.
     * O estilo de fonte é aplicado por cima da formatação vanilla.
     */
    public static Component fmt(String formattedText) {
        // Component.translatable não processa §, mas literal sim para cores.
        return Component.literal(formattedText)
            .withStyle(s -> s.withFont(FONT));
    }
}
