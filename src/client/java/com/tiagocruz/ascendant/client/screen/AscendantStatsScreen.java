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
 *
 * Layout:
 * ┌──────────────────────────────────┐
 * │  ⚔ SISTEMA ASCENDANT            │
 * │  [  ATRIBUTOS  ] [  PODERES  ]  │
 * ├──────────────────────────────────┤
 * │  Tab ATRIBUTOS:                  │
 * │   Nome  |  Rank: Latente I       │
 * │   Nível 1 | MAGE                 │
 * │   XP ████░░░░ 0/100              │
 * │                                  │
 * │   FOR  5  [+]   AGI  5  [+]      │
 * │   RES  5  [+]   INT  5  [+]      │
 * │   PER  5  [+]                    │
 * │                                  │
 * │   Pontos disponíveis: 3          │
 * └──────────────────────────────────┘
 *
 * Tab PODERES:
 * │   [ÍCONE CLASSE]   MAGE          │
 * │                                  │
 * │   PASSIVA                        │
 * │   ▸ Regeneração constante...     │
 * │                                  │
 * │   ACTIVA  (Em Desenvolvimento)   │
 * │   ▸ ??? Em breve                 │
 * └──────────────────────────────────┘
 */
public class AscendantStatsScreen extends Screen {

    // Dimensões do painel central
    private static final int PANEL_W = 280;
    private static final int PANEL_H = 210;

    // Cores
    private static final int C_BG        = 0xE0080810;
    private static final int C_BORDER    = 0xFF1A3A6A;
    private static final int C_HEADER    = 0xFF0D1B2A;
    private static final int C_XP_BG     = 0xFF1C2C3C;
    private static final int C_XP_FILL   = 0xFF00AAFF;
    private static final int C_TAB_ACT   = 0xFF1A3A6A;
    private static final int C_TAB_INACT = 0xFF0A1520;
    private static final int C_STAT_BG   = 0xFF0D1B2A;
    private static final int C_GOLD      = 0xFFFFD700;
    private static final int C_WHITE     = 0xFFFFFFFF;
    private static final int C_GRAY      = 0xFFAAAAAA;
    private static final int C_GREEN     = 0xFF44FF44;
    private static final int C_CYAN      = 0xFF00CCFF;

    // Estado das tabs
    private int activeTab = 0; // 0 = ATRIBUTOS, 1 = PODERES

    // Botões de stat points (só visíveis quando há pontos)
    private Button btnFor, btnAgi, btnRes, btnInt, btnPer;
    private Button btnTabStats, btnTabPowers;

    // Posição do painel (calculada em init)
    private int panelX, panelY;

    public AscendantStatsScreen() {
        super(Component.literal("Sistema Ascendant"));
    }

    @Override
    protected void init() {
        panelX = (this.width - PANEL_W) / 2;
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

        // Stat point buttons — posição fixa, visibilidade gerida em render
        int statStartY = panelY + 110;
        int col1 = panelX + 58;
        int col2 = panelX + 168;

        btnFor = makeStatButton("strength", col1, statStartY);
        btnAgi = makeStatButton("agility",  col2, statStartY);
        btnRes = makeStatButton("endurance",    col1, statStartY + 22);
        btnInt = makeStatButton("intelligence", col2, statStartY + 22);
        btnPer = makeStatButton("perception",   col1, statStartY + 44);

        addRenderableWidget(btnFor);
        addRenderableWidget(btnAgi);
        addRenderableWidget(btnRes);
        addRenderableWidget(btnInt);
        addRenderableWidget(btnPer);

        refreshStatButtons();
    }

    private Button makeStatButton(String stat, int x, int y) {
        return Button.builder(Component.literal("§a+"), btn -> {
            ClientPlayNetworking.send(new SpendStatPointPacket(stat));
        }).pos(x, y).size(14, 12).build();
    }

    private void refreshStatButtons() {
        boolean showStats = activeTab == 0;
        boolean hasPoints = ClientPlayerData.getStatPoints() > 0;
        btnFor.visible = showStats && hasPoints;
        btnAgi.visible = showStats && hasPoints;
        btnRes.visible = showStats && hasPoints;
        btnInt.visible = showStats && hasPoints;
        btnPer.visible = showStats && hasPoints;
        // Ocultar tab buttons em PODERES — não, deixar sempre visíveis
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        // Fundo escuro sobre o jogo
        g.fill(0, 0, this.width, this.height, 0x88000000);

        // Painel principal
        drawPanel(g);

        // Conteúdo da tab activa
        if (activeTab == 0) renderStatsTab(g);
        else                 renderPowersTab(g);

        // Actualizar visibilidade dos botões + antes de render
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

        // Borda exterior
        drawBorder(g, x, y, w, h, C_BORDER);

        // Header
        g.fill(x, y, x + w, y + 26, C_HEADER);
        drawBorder(g, x, y, w, 26, C_BORDER);

        // Título
        String title = "§b⚔ §fSISTEMA §bASCENDANT";
        g.drawString(font, Component.literal(title), x + 6, y + 9, C_WHITE, true);

        // Linha separadora das tabs
        g.fill(x, y + 44, x + w, y + 45, C_BORDER);

        // Highlight da tab activa
        int tabY = y + 28;
        if (activeTab == 0) g.fill(x + 4,   tabY, x + 94,  tabY + 14, C_TAB_ACT);
        else                g.fill(x + 100,  tabY, x + 182, tabY + 14, C_TAB_ACT);
    }

    private void drawBorder(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x,         y,         x + w,     y + 1,     color); // top
        g.fill(x,         y + h - 1, x + w,     y + h,     color); // bottom
        g.fill(x,         y,         x + 1,     y + h,     color); // left
        g.fill(x + w - 1, y,         x + w,     y + h,     color); // right
    }

    // ── TAB ATRIBUTOS ────────────────────────────────────────────────────────

    private void renderStatsTab(GuiGraphics g) {
        int x = panelX + 6;
        int y = panelY + 48;

        // Dados do jogador
        int    level    = ClientPlayerData.getLevel();
        long   xp       = ClientPlayerData.getXp();
        long   xpMax    = ClientPlayerData.getXpToNext();
        int    sp       = ClientPlayerData.getStatPoints();
        String cls      = ClientPlayerData.getPlayerClass();
        PlayerRank rank = PlayerRank.fromLevel(level);
        String clsName  = ClientPlayerData.isClassAssigned() ? getClassDisplayName(cls) : "???";

        // ─── Linha 1: Rank ───────────────────────────────────────────────────
        g.drawString(font,
            Component.literal("§7Rank: " + rank.getColored() + " §8[" + rank.getRoman() + "]"),
            x, y, C_WHITE, false);

        // Nível para próximo rank (se não for máximo)
        if (rank != PlayerRank.ASCENDENTE) {
            int toNext = rank.levelsToNext(level);
            g.drawString(font,
                Component.literal("§8(+" + toNext + " nv para " + PlayerRank.values()[rank.ordinal()+1].getDisplayName() + ")"),
                x + 140, y, C_GRAY, false);
        }

        // ─── Linha 2: Nível + Classe ─────────────────────────────────────────
        y += 11;
        g.drawString(font,
            Component.literal("§fNível §e" + level + "  §7|  §b" + clsName),
            x, y, C_WHITE, false);

        // ─── Barra de XP ─────────────────────────────────────────────────────
        y += 13;
        int barW = PANEL_W - 14;
        int barH = 5;
        float frac = xpMax > 0 ? Math.min(1f, (float) xp / xpMax) : 0f;
        g.fill(x, y, x + barW, y + barH, C_XP_BG);
        if (frac > 0) g.fill(x, y, x + (int)(barW * frac), y + barH, C_XP_FILL);
        drawBorder(g, x, y, barW, barH, C_BORDER);
        y += barH + 2;
        g.drawString(font,
            Component.literal("§7XP §f" + xp + " §8/ §f" + xpMax),
            x, y, C_GRAY, false);

        // ─── Separador ────────────────────────────────────────────────────────
        y += 13;
        g.fill(panelX + 4, y, panelX + PANEL_W - 4, y + 1, C_BORDER);
        y += 5;

        // ─── Stats (2 colunas) ────────────────────────────────────────────────
        int col1X = panelX + 6;
        int col2X = panelX + 120;

        drawStat(g, "§cFOR", ClientPlayerData.getStrength(), col1X, y, btnFor);
        drawStat(g, "§aAGI", ClientPlayerData.getAgility(),  col2X, y, btnAgi);
        y += 22;
        drawStat(g, "§6RES", ClientPlayerData.getEndurance(),    col1X, y, btnRes);
        drawStat(g, "§9INT", ClientPlayerData.getIntelligence(), col2X, y, btnInt);
        y += 22;
        drawStat(g, "§dPER", ClientPlayerData.getPerception(),   col1X, y, btnPer);

        // ─── Pontos disponíveis ────────────────────────────────────────────────
        if (sp > 0) {
            y += 22;
            g.fill(panelX + 4, y - 3, panelX + PANEL_W - 4, y + 13, 0x44FFDD00);
            g.drawString(font,
                Component.literal("§e✦ §f" + sp + " ponto(s) de atributo disponíveis §e✦"),
                panelX + 6, y, C_GOLD, true);
        }

        // ─── Rodapé ───────────────────────────────────────────────────────────
        g.drawString(font,
            Component.literal("§8[ESC] Fechar  |  [K] Fechar"),
            panelX + 6, panelY + PANEL_H - 11, C_GRAY, false);
    }

    private void drawStat(GuiGraphics g, String label, int value, int x, int y, Button btn) {
        // Fundo do stat
        g.fill(x, y, x + 110, y + 18, C_STAT_BG);
        drawBorder(g, x, y, 110, 18, C_BORDER);

        // Label e valor
        g.drawString(font, Component.literal(label), x + 4, y + 5, C_WHITE, false);
        g.drawString(font, Component.literal("§f" + value), x + 38, y + 5, C_WHITE, true);

        // Actualizar posição do botão (pode ter mudado por resize)
        if (btn != null) {
            btn.setX(x + 56);
            btn.setY(y + 3);
        }
    }

    // ── TAB PODERES ──────────────────────────────────────────────────────────

    private void renderPowersTab(GuiGraphics g) {
        int x = panelX + 10;
        int y = panelY + 50;

        String cls = ClientPlayerData.getPlayerClass();
        String clsDisplay = ClientPlayerData.isClassAssigned() ? getClassDisplayName(cls) : "???";
        String clsColor   = getClassColor(cls);

        // Classe em destaque
        g.drawString(font,
            Component.literal("§7Classe activa: " + clsColor + "§l" + clsDisplay),
            x, y, C_WHITE, true);
        y += 14;

        // ─── PASSIVA ────────────────────────────────────────────────────────
        g.fill(panelX + 6, y, panelX + PANEL_W - 6, y + 1, C_BORDER);
        y += 4;
        g.drawString(font, Component.literal("§bPASSIVA"), x, y, C_CYAN, true);
        y += 12;

        List<String> passiveLines = getPassiveDescription(cls);
        for (String line : passiveLines) {
            g.drawString(font, Component.literal("§7▸ §f" + line), x + 4, y, C_WHITE, false);
            y += 10;
        }

        // ─── ACTIVA ─────────────────────────────────────────────────────────
        y += 6;
        g.fill(panelX + 6, y, panelX + PANEL_W - 6, y + 1, C_BORDER);
        y += 4;
        g.drawString(font, Component.literal("§6ACTIVA"), x, y, C_GOLD, true);
        y += 12;

        List<String> activeLines = getActiveDescription(cls);
        for (String line : activeLines) {
            g.drawString(font, Component.literal("§7▸ §8" + line), x + 4, y, C_GRAY, false);
            y += 10;
        }

        // Nota de WIP
        y += 6;
        g.fill(x, y, panelX + PANEL_W - 10, y + 18, 0x33FFAA00);
        drawBorder(g, x, y, PANEL_W - 20, 18, 0xFF664400);
        g.drawString(font,
            Component.literal("§e⚠ §8As habilidades activas estão em desenvolvimento"),
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
                "Regeneração constante escalada com Inteligência",
                "Partículas arcanas visíveis a aliados");
            case "TITAN" -> List.of(
                "Força aumentada com Strength",
                "Resistência a dano. Corpo impassível");
            case "ARCHER" -> List.of(
                "Velocidade II constante",
                "Visão Nocturna com Percepção ≥ 15");
            case "HEALER" -> List.of(
                "Regeneração pessoal constante",
                "Cura aliados a 8 blocos ao redor a cada 5s");
            case "SUMMONER" -> List.of(
                "Velocidade + Força + Regeneração simultâneas",
                "Aura de fogo de almas. Raros e imprevisíveis");
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
            case "MAGE"     -> List.of("Canalização — Dispara projéctil arcano de alto dano", "[Em desenvolvimento]");
            case "TITAN"    -> List.of("Impacto Sísmico — Dano em área ao redor do jogador", "[Em desenvolvimento]");
            case "ARCHER"   -> List.of("Flecha Penetrante — Atravessa múltiplos inimigos", "[Em desenvolvimento]");
            case "HEALER"   -> List.of("Pulso Sagrado — Cura em área. Dano a mortos-vivos", "[Em desenvolvimento]");
            case "SUMMONER" -> List.of("Invocação — Convoca um servo das sombras temporário", "[Em desenvolvimento]");
            case "SPECTER"  -> List.of("Forma Espectral — Atravessa blocos por 5 segundos", "[Em desenvolvimento]");
            default         -> List.of("[Classe não atribuída]");
        };
    }

    // ── CONTROLO ─────────────────────────────────────────────────────────────

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // K ou ESC fecham o ecrã
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_K ||
            keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
