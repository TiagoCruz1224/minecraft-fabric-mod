package com.tiagocruz.ascendant.client.screen;

import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerRank;
import com.tiagocruz.ascendant.network.SpendStatPointPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Ecrã de Stats do Sistema Ascendant (tecla K).
 * 8 atributos em grelha 4x2 com ícones Unicode.
 */
public class AscendantStatsScreen extends Screen {

    // Dimensões do painel central
    private static final int PANEL_W = 290;
    private static final int PANEL_H = 240;

    // Cores
    private static final int C_BG        = 0xE0080810;
    private static final int C_BORDER    = 0xFF1A3A6A;
    private static final int C_HEADER    = 0xFF0D1B2A;
    private static final int C_XP_BG     = 0xFF1C2C3C;
    private static final int C_XP_FILL   = 0xFF00AAFF;
    private static final int C_STAT_BG   = 0xFF0D1B2A;
    private static final int C_GOLD      = 0xFFFFD700;
    private static final int C_WHITE     = 0xFFFFFFFF;
    private static final int C_GRAY      = 0xFFAAAAAA;
    private static final int C_CYAN      = 0xFF00CCFF;

    // Estado das tabs
    private int activeTab = 0; // 0 = ATRIBUTOS, 1 = PODERES

    // Botões de stat points — 8 stats
    private Button btnFor, btnAgi, btnRes, btnInt;
    private Button btnPer, btnVit, btnDex, btnWis;
    private Button btnTabStats, btnTabPowers;

    // Posição do painel (calculada em init)
    private int panelX, panelY;

    public AscendantStatsScreen() {
        super(Component.literal("Sistema Ascendant"));
    }

    @Override
    protected void init() {
        panelX = (this.width  - PANEL_W) / 2;
        panelY = (this.height - PANEL_H) / 2;

        int tabY = panelY + 28;

        // Tab buttons
        btnTabStats = Button.builder(Component.literal("⚔ ATRIBUTOS"), btn -> {
            activeTab = 0;
            refreshStatButtons();
        }).pos(panelX + 4, tabY).size(90, 14).build();

        btnTabPowers = Button.builder(Component.literal("✦ PODERES"), btn -> {
            activeTab = 1;
            refreshStatButtons();
        }).pos(panelX + 100, tabY).size(82, 14).build();

        addRenderableWidget(btnTabStats);
        addRenderableWidget(btnTabPowers);

        // Posições dos botões "+" — serão actualizadas em drawStat()
        // Criamos com posição dummy; o drawStat reposiciona a cada frame
        btnFor = makeStatButton("strength");
        btnAgi = makeStatButton("agility");
        btnRes = makeStatButton("endurance");
        btnInt = makeStatButton("intelligence");
        btnPer = makeStatButton("perception");
        btnVit = makeStatButton("vitality");
        btnDex = makeStatButton("dexterity");
        btnWis = makeStatButton("wisdom");

        addRenderableWidget(btnFor);
        addRenderableWidget(btnAgi);
        addRenderableWidget(btnRes);
        addRenderableWidget(btnInt);
        addRenderableWidget(btnPer);
        addRenderableWidget(btnVit);
        addRenderableWidget(btnDex);
        addRenderableWidget(btnWis);

        refreshStatButtons();
    }

    private Button makeStatButton(String stat) {
        return Button.builder(Component.literal("§a+"), btn ->
            ClientPlayNetworking.send(new SpendStatPointPacket(stat))
        ).pos(0, 0).size(14, 12).build();
    }

    private void refreshStatButtons() {
        boolean showStats = activeTab == 0;
        boolean hasPoints = ClientPlayerData.getStatPoints() > 0;
        boolean show = showStats && hasPoints;
        btnFor.visible = show;
        btnAgi.visible = show;
        btnRes.visible = show;
        btnInt.visible = show;
        btnPer.visible = show;
        btnVit.visible = show;
        btnDex.visible = show;
        btnWis.visible = show;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        // Fundo semi-transparente sem blur
        g.fill(0, 0, this.width, this.height, 0x88000000);

        drawPanel(g);

        if (activeTab == 0) renderStatsTab(g);
        else                renderPowersTab(g);

        refreshStatButtons();
        super.render(g, mx, my, pt);
    }

    // ── PAINEL BASE ──────────────────────────────────────────────────────────

    private void drawPanel(GuiGraphics g) {
        int x = panelX, y = panelY, w = PANEL_W, h = PANEL_H;

        // Sombra
        g.fill(x + 4, y + 4, x + w + 4, y + h + 4, 0x66000000);
        // Fundo
        g.fill(x, y, x + w, y + h, C_BG);
        // Borda
        drawBorder(g, x, y, w, h, C_BORDER);
        // Header
        g.fill(x, y, x + w, y + 26, C_HEADER);
        drawBorder(g, x, y, w, 26, C_BORDER);
        // Título
        g.drawString(font, Component.literal("§b⚔ §fSISTEMA §bASCENDANT"), x + 6, y + 9, C_WHITE, true);
        // Separador de tabs
        g.fill(x, y + 44, x + w, y + 45, C_BORDER);
        // Highlight tab activa
        int tabY = y + 28;
        if (activeTab == 0) g.fill(x + 4,  tabY, x + 94,  tabY + 14, 0xFF1A3A6A);
        else                g.fill(x + 100, tabY, x + 182, tabY + 14, 0xFF1A3A6A);
    }

    private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x,         y,         x + w,     y + 1,     color);
        g.fill(x,         y + h - 1, x + w,     y + h,     color);
        g.fill(x,         y,         x + 1,     y + h,     color);
        g.fill(x + w - 1, y,         x + w,     y + h,     color);
    }

    // ── TAB ATRIBUTOS ────────────────────────────────────────────────────────

    private void renderStatsTab(GuiGraphics g) {
        int x = panelX + 6;
        int y = panelY + 48;

        int    level = ClientPlayerData.getLevel();
        long   xp    = ClientPlayerData.getXp();
        long   xpMax = ClientPlayerData.getXpToNext();
        int    sp    = ClientPlayerData.getStatPoints();
        String cls   = ClientPlayerData.getPlayerClass();

        PlayerRank rank    = PlayerRank.fromLevel(level);
        String     clsName = ClientPlayerData.isClassAssigned() ? getClassDisplayName(cls) : "???";
        String     clsCol  = getClassColor(cls);

        // ─── Rank ────────────────────────────────────────────────────────────
        g.drawString(font,
            Component.literal("§7Rank: " + rank.getColored() + " §8[" + rank.getRoman() + "]"),
            x, y, C_WHITE, false);
        if (rank != PlayerRank.ASCENDENTE) {
            int toNext = rank.levelsToNext(level);
            g.drawString(font,
                Component.literal("§8(+" + toNext + " nv para " +
                    PlayerRank.values()[rank.ordinal()+1].getDisplayName() + ")"),
                x + 138, y, 0xFF888888, false);
        }

        // ─── Nível + Classe ───────────────────────────────────────────────────
        y += 11;
        g.drawString(font,
            Component.literal("§fNível §e" + level + "  §7|  " + clsCol + clsName),
            x, y, C_WHITE, false);

        // ─── Barra de XP ─────────────────────────────────────────────────────
        y += 13;
        int barW = PANEL_W - 14;
        float frac = xpMax > 0 ? Math.min(1f, (float) xp / xpMax) : 0f;
        g.fill(x, y, x + barW, y + 5, C_XP_BG);
        if (frac > 0) g.fill(x, y, x + (int)(barW * frac), y + 5, C_XP_FILL);
        drawBorder(g, x, y, barW, 5, C_BORDER);
        y += 7;
        g.drawString(font,
            Component.literal("§7XP §f" + xp + " §8/ §f" + xpMax),
            x, y, 0xFF888888, false);

        // ─── Separador ────────────────────────────────────────────────────────
        y += 12;
        g.fill(panelX + 4, y, panelX + PANEL_W - 4, y + 1, C_BORDER);
        y += 5;

        // ─── Grelha 4×2 de Stats ─────────────────────────────────────────────
        // Cada célula: ícone + abreviatura + valor + botão [+]
        // Layout: [col1] [col2]    col width ~138px each, gap=4
        int col1X = panelX + 4;
        int col2X = panelX + 4 + 143;
        int rowH  = 22;

        //  Linha 1
        drawStat(g, "§c⚔", "FOR", ClientPlayerData.getStrength(),    col1X, y, btnFor);
        drawStat(g, "§a⚡", "AGI", ClientPlayerData.getAgility(),     col2X, y, btnAgi);
        y += rowH;
        //  Linha 2
        drawStat(g, "§6♦", "RES", ClientPlayerData.getEndurance(),    col1X, y, btnRes);
        drawStat(g, "§9✦", "INT", ClientPlayerData.getIntelligence(), col2X, y, btnInt);
        y += rowH;
        //  Linha 3
        drawStat(g, "§e◉", "PER", ClientPlayerData.getPerception(),   col1X, y, btnPer);
        drawStat(g, "§c♥", "VIT", ClientPlayerData.getVitality(),     col2X, y, btnVit);
        y += rowH;
        //  Linha 4
        drawStat(g, "§f✧", "DES", ClientPlayerData.getDexterity(),    col1X, y, btnDex);
        drawStat(g, "§b☆", "SAB", ClientPlayerData.getWisdom(),       col2X, y, btnWis);
        y += rowH;

        // ─── Pontos disponíveis ───────────────────────────────────────────────
        if (sp > 0) {
            y += 2;
            g.fill(panelX + 4, y, panelX + PANEL_W - 4, y + 13, 0x44FFDD00);
            g.drawString(font,
                Component.literal("§e✦ §f" + sp + " ponto(s) disponíveis §e✦"),
                panelX + 8, y + 3, C_GOLD, true);
            y += 13;
        }

        // ─── Rodapé ───────────────────────────────────────────────────────────
        g.drawString(font,
            Component.literal("§8[ESC] Fechar  |  [K] Fechar"),
            panelX + 6, panelY + PANEL_H - 11, 0xFF888888, false);
    }

    /**
     * Desenha uma célula de stat.
     * @param icon  string com código de cor + ícone Unicode (ex: "§c⚔")
     * @param abbr  abreviatura de 3 letras (ex: "FOR")
     * @param value valor actual
     * @param x     canto esquerdo da célula
     * @param y     topo da célula
     * @param btn   botão [+] associado
     */
    private void drawStat(GuiGraphics g, String icon, String abbr, int value,
                           int x, int y, Button btn) {
        int cellW = 139;
        int cellH = 18;

        // Fundo + borda
        g.fill(x, y, x + cellW, y + cellH, C_STAT_BG);
        drawBorder(g, x, y, cellW, cellH, C_BORDER);

        // Ícone (4px de margem)
        g.drawString(font, Component.literal(icon), x + 4, y + 5, C_WHITE, true);

        // Abreviatura (10px à frente do ícone — ícone ocupa ~8px)
        g.drawString(font, Component.literal("§f" + abbr), x + 16, y + 5, C_WHITE, false);

        // Valor (alinhado ~45px)
        g.drawString(font, Component.literal("§f" + value), x + 46, y + 5, C_WHITE, true);

        // Reposicionar botão [+] (55px à frente do início da célula)
        if (btn != null) {
            btn.setX(x + 60);
            btn.setY(y + 3);
        }
    }

    // ── TAB PODERES ──────────────────────────────────────────────────────────

    private void renderPowersTab(GuiGraphics g) {
        int x = panelX + 10;
        int y = panelY + 50;

        String cls        = ClientPlayerData.getPlayerClass();
        String clsDisplay = ClientPlayerData.isClassAssigned() ? getClassDisplayName(cls) : "???";
        String clsColor   = getClassColor(cls);

        g.drawString(font,
            Component.literal("§7Classe activa: " + clsColor + "§l" + clsDisplay),
            x, y, C_WHITE, true);
        y += 14;

        // ─── PASSIVA ────────────────────────────────────────────────────────
        g.fill(panelX + 6, y, panelX + PANEL_W - 6, y + 1, C_BORDER);
        y += 4;
        g.drawString(font, Component.literal("§bPASSIVA"), x, y, C_CYAN, true);
        y += 12;

        for (String line : getPassiveDescription(cls)) {
            g.drawString(font, Component.literal("§7▸ §f" + line), x + 4, y, C_WHITE, false);
            y += 10;
        }

        // ─── ACTIVA ─────────────────────────────────────────────────────────
        y += 6;
        g.fill(panelX + 6, y, panelX + PANEL_W - 6, y + 1, C_BORDER);
        y += 4;
        g.drawString(font, Component.literal("§6ACTIVA"), x, y, C_GOLD, true);
        y += 12;

        for (String line : getActiveDescription(cls)) {
            g.drawString(font, Component.literal("§7▸ §8" + line), x + 4, y, C_GRAY, false);
            y += 10;
        }

        // Nota WIP
        y += 6;
        g.fill(x, y, panelX + PANEL_W - 10, y + 18, 0x33FFAA00);
        drawBorder(g, x, y, PANEL_W - 20, 18, 0xFF664400);
        g.drawString(font,
            Component.literal("§e⚠ §8Habilidades activas em desenvolvimento"),
            x + 2, y + 5, C_GRAY, false);
    }

    // ── DADOS DE CLASSE ──────────────────────────────────────────────────────

    private String getClassDisplayName(String cls) {
        return switch (cls) {
            case "ASSASSIN" -> "Assassino";
            case "GUARDIAN" -> "Guardião";
            case "MAGE"     -> "Mago";
            case "TITAN"    -> "Titã";
            case "ARCHER"   -> "Arqueiro";
            case "HEALER"   -> "Curandeiro";
            case "SUMMONER" -> "Invocador";
            case "SPECTER"  -> "Espectro";
            default         -> "Desconhecida";
        };
    }

    private String getClassColor(String cls) {
        return switch (cls) {
            case "ASSASSIN" -> "§8";
            case "GUARDIAN" -> "§6";
            case "MAGE"     -> "§9";
            case "TITAN"    -> "§c";
            case "ARCHER"   -> "§a";
            case "HEALER"   -> "§d";
            case "SUMMONER" -> "§5";
            case "SPECTER"  -> "§7";
            default         -> "§f";
        };
    }

    private List<String> getPassiveDescription(String cls) {
        return switch (cls) {
            case "ASSASSIN" -> List.of(
                "Ao agachar, ganha Invisibilidade e Velocidade I",
                "Activa durante 30 segundos a cada 5s");
            case "GUARDIAN" -> List.of(
                "Absorção escalada com Endurance",
                "Resistência a dano permanente");
            case "MAGE" -> List.of(
                "Regeneração constante escalada com INT",
                "Partículas arcanas visíveis a aliados");
            case "TITAN" -> List.of(
                "Força aumentada com FOR",
                "Resistência a dano. Corpo impassível");
            case "ARCHER" -> List.of(
                "Velocidade II constante",
                "Visão Nocturna com PER ≥ 15");
            case "HEALER" -> List.of(
                "Regeneração pessoal constante",
                "Cura aliados a 8 blocos a cada 5s");
            case "SUMMONER" -> List.of(
                "Velocidade + Força + Regeneração simultâneas",
                "Aura de fogo de almas");
            case "SPECTER" -> List.of(
                "Invisibilidade + Velocidade III ao agachar",
                "Velocidade II constante mesmo parado");
            default -> List.of("Classe não atribuída.", "Explora o mundo para seres avaliado.");
        };
    }

    private List<String> getActiveDescription(String cls) {
        return switch (cls) {
            case "ASSASSIN" -> List.of("Execução — Teleporta atrás do alvo e aplica dano crítico", "[Em desenvolvimento]");
            case "GUARDIAN" -> List.of("Bastion — Bloqueia todos os danos por 3 segundos", "[Em desenvolvimento]");
            case "MAGE"     -> List.of("Canalização — Projéctil arcano de alto dano", "[Em desenvolvimento]");
            case "TITAN"    -> List.of("Impacto Sísmico — Dano em área ao redor", "[Em desenvolvimento]");
            case "ARCHER"   -> List.of("Flecha Penetrante — Atravessa múltiplos inimigos", "[Em desenvolvimento]");
            case "HEALER"   -> List.of("Pulso Sagrado — Cura em área, dano a mortos-vivos", "[Em desenvolvimento]");
            case "SUMMONER" -> List.of("Invocação — Convoca servo das sombras temporário", "[Em desenvolvimento]");
            case "SPECTER"  -> List.of("Forma Espectral — Atravessa blocos por 5 segundos", "[Em desenvolvimento]");
            default         -> List.of("[Classe não atribuída]");
        };
    }

    // ── CONTROLO ─────────────────────────────────────────────────────────────

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_K ||
            keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    /** Remove o blur automático do Minecraft 1.21.4. */
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // intencional — o nosso render() já trata do fundo
    }
}
                                                                                                                                                                                                                                                                                                                                                                