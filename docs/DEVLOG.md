# Ascendant — Dev Log

Documento de progresso técnico do mod. Atualizado a cada sessão de desenvolvimento.

---

## Estado Atual

**Última atualização:** 29/06/2026 — 00:15  
**Versão do mod:** 1.0.0 (pré-alpha)  
**Branch:** master

### O que está feito

| # | Tarefa | Estado |
|---|--------|--------|
| 0 | Ambiente (JDK 21, VS Code, Fabric, runClient) | ✅ |
| 1 | Rename para Ascendant (estrutura, packages, fabric.mod.json) | ✅ |
| 1 | README com setup | ✅ |
| 1 | `docs/DEVLOG.md` e `restart-mc.bat` | ✅ |
| 2 | Sistema de dados do jogador (stats, nível, classe, XP) | ✅ |
| 2 | Persistência via Fabric Attachment API (Codec) | ✅ |
| 3 | HUD overlay: HP bar, MP bar, stat icons, level-up flash | ✅ |
| 4 | Packets S2C (servidor → cliente sync) | ✅ |
| 4 | Tracking de kills (melee vs ranged), XP por kill | ✅ |
| 5 | BehaviorTracker (observação, stealth, por segundo) | ✅ |
| 6 | Atribuição de classe por algoritmo de score | ✅ |
| 6 | Classes raras (Invocador, Espectro) com critérios especiais | ✅ |
| 7 | Habilidades gerais (Dash, Salto Duplo, Escudo, Esquivar) | ✅ |
| 7 | Animação Escudo de Energia — esfera 3D real (paralelos + meridianos animados) | ✅ |
| 7 | HUD de Habilidades de Classe (sistema de slots, 9+ habilidades, dual-class) | 🔲 |
| 7 | Habilidades de classe (3 ativas + 1 passiva por classe) | 🔲 |
| 8 | Sistema de fusões | 🔲 |
| 9 | Gates / Dungeons | 🔲 |
| 10 | Items & Armaduras | 🔲 |

---

## Sessão 1 — Setup e Rename (27/06/2026)

### Ambiente

- **JDK 21** — Eclipse Temurin via `winget`
  - Path: `C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot`
  - `JAVA_HOME` permanente via `setx /M`
- **VS Code** — `.vscode/settings.json` com `java.jdt.ls.java.home` e `gradle.build.server.javaHome`
- **Build** — `gradlew.bat build` ✅ BUILD SUCCESSFUL
- **runClient** — Minecraft 1.21.4/Fabric abre ✅

### Como fazer build

> **IMPORTANTE**: Usar `build-and-run.bat` (duplo clique no File Explorer).  
> **NÃO usar PowerShell** — permissões conflituam com cache do Gradle.

O bat faz automaticamente:
```
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
cd C:\Users\Tiago\Documents\GitHub\minecraft-fabric-mod
gradlew.bat build
gradlew.bat runClient
```

---

## Sessão 2 — Sistema de Dados e HUD (27/06/2026)

### Ficheiros criados/modificados

**Main source set** (`src/main/java/com/tiagocruz/ascendant/`):

| Ficheiro | Descrição |
|----------|-----------|
| `Ascendant.java` | Registar networking, attachments, events, tick |
| `data/PlayerClass.java` | Enum: NONE, ASSASSIN, GUARDIAN, MAGE, TITAN, ARCHER, HEALER, SUMMONER, SPECTER |
| `data/AscendantPlayerData.java` | Stats (FOR/AGI/RES/INT/PER), nível, XP, classe, stat points, tracking |
| `data/PlayerDataManager.java` | Helper: `get()`, `set()`, `addXp()` via Fabric Attachment |
| `registry/AscendantAttachments.java` | `AttachmentType<AscendantPlayerData>` com `CODEC` para persistência |
| `network/SyncPlayerDataPacket.java` | Record packet S2C com `StreamCodec` |
| `network/ServerNetworking.java` | Registar packet, enviar sync ao cliente |
| `event/PlayerEvents.java` | ENTITY_LOAD (sync login), AFTER_KILLED_OTHER_ENTITY (tracking + XP) |
| `event/AscendantServerTickEvents.java` | Tick por segundo → BehaviorTracker |
| `system/BehaviorTracker.java` | Algoritmo de atribuição de classe por score |

**Client source set** (`src/client/java/com/tiagocruz/ascendant/`):

| Ficheiro | Descrição |
|----------|-----------|
| `client/AscendantClient.java` | Registar packet handler e HUD |
| `client/data/ClientPlayerData.java` | Cache local dos dados (atualizado por packet) |
| `client/hud/AscendantHud.java` | Overlay: nível, classe, XP bar, stats, level-up notif |

### Algoritmo de atribuição de classe

Cada comportamento gera pontos para classes:

| Comportamento | Classe beneficiada |
|--------------|-------------------|
| Kills melee | Assassino, Titã |
| Kills ranged | Arqueiro |
| Tempo em stealth (agachar) | Assassino |
| Tempo parado a observar | Mago, Arqueiro, Curandeiro |
| Dano absorvido | Guardião, Titã |
| Evitar combate | Curandeiro |
| Equilíbrio de tudo + observação | Invocador (raro) |
| Muito stealth + ranged, sem melee | Espectro (raro) |

---

## Erros conhecidos e resoluções

| Erro | Causa | Solução |
|------|-------|---------|
| `persistent()` — wrong types | API Fabric mudou: recebe `Codec<T>` | Adicionar `CODEC = CompoundTag.CODEC.xmap(...)` |
| `cannot find symbol SyncPlayerDataPacket` | Packet estava no client source set | Mover para main source set |
| `ServerTickEvents.java` — nome errado | Classe `AscendantServerTickEvents` em ficheiro errado | Renomear para `AscendantServerTickEvents.java` |
| `isOnGround()` not found | Em 1.21.4, removido ou diferente | Substituir por `isAlive()` + speed check |
| PowerShell `Acesso negado` | Conflito permissões com cmd.exe admin | Usar CMD admin ou `build-and-run.bat` |

---

## Estrutura de packages

```
com.tiagocruz.ascendant (main)
├── Ascendant.java
├── data/
│   ├── PlayerClass.java          ← enum 6 classes + 2 raras
│   ├── AscendantPlayerData.java  ← stats + XP + tracking + CODEC
│   └── PlayerDataManager.java    ← get/set/addXp via Attachment
├── registry/
│   └── AscendantAttachments.java ← AttachmentType com persistência
├── network/
│   ├── SyncPlayerDataPacket.java ← record packet S2C
│   └── ServerNetworking.java     ← registar + enviar sync
├── event/
│   ├── PlayerEvents.java         ← login + kill tracking
│   └── AscendantServerTickEvents.java ← tick por segundo
└── system/
    └── BehaviorTracker.java      ← algoritmo de atribuição de classe

com.tiagocruz.ascendant (client)
├── client/
│   ├── AscendantClient.java      ← inicialização cliente
│   ├── data/
│   │   └── ClientPlayerData.java ← cache local
│   └── hud/
│       └── AscendantHud.java     ← overlay HUD
```

---

## Próximos passos

- **HUD de Habilidades de Classe** — novo sistema de slots para 9+ habilidades, compatível com dual-class (ver secção de ideias abaixo)
- **Habilidades das 8 classes** — cada classe tem 3 habilidades ativas e 1 passiva
- **Sistema de Gates** — dungeons com mobs escalados e boss
- **Items especiais** — equipamento de cada classe
- **Sistema de fusões** — Natural / Forçada / Proibida

---

## Ideias em aberto (para decisão)

### HUD de Habilidades — Como gerir 9+ habilidades

O jogador vai ter até 9 habilidades possíveis (4 gerais + 3 ativas de classe primária + 3 ativas de classe secundária se dual-class). Precisa de um sistema que seja ergonómico e não sobrecarregue o ecrã.

| Ideia | Inspiração | Descrição | Prós | Contras |
|-------|-----------|-----------|------|---------|
| **Action Bar estilo WoW** | World of Warcraft | Barra horizontal com 9–12 slots numerados (1–9, 0, -, =), keybinds diretos, ícones com cooldown circular | Familiar, rápido, muito customizável | Ocupa espaço no ecrã, muitas teclas |
| **Roda de Habilidades** | Baldur's Gate 3, Skyrim | Manter tecla especial (ex: Q) abre roda radial com 8 slots, soltar ativa a selecionada | Limpo, elegante, não polui o HUD | Requer pausa/slow-mo, menos responsivo em combate |
| **Dual Bar (classe A + classe B)** | Final Fantasy XIV | Duas barras separadas — uma por classe. Trocar de "stance" muda qual barra está ativa | Muito organizado para dual-class | Complexo de implementar, confuso para novos jogadores |
| **Deck de Cartas (escolher 6 de N)** | Hades, Slay the Spire | O jogador escolhe quais 6 habilidades quer equipar antes de entrar em combate | Muito estratégico, build diversity alta | Precisa de ecrã de gestão adicional |
| **Slots de Acesso Rápido sobrepostos** | Path of Exile | 6 slots visíveis no HUD (Q, E, R, F, C, V), resto acessível num painel lateral | Equilíbrio entre acesso e limpeza | Menos habilidades imediatamente acessíveis |

**Estado:** 🔲 aguarda decisão do Tiago (exemplo de HUD prometido)

---

### Outras ideias RPG (para decisão futura)

| Ideia | Inspiração | Descrição |
|-------|-----------|-----------|
| **Mapa de Talentos/Passive Tree** | Path of Exile | Árvore visual de nós passivos onde o jogador gasta pontos de stat para desbloquear. Extremamente profundo. |
| **Sistema de Combos** | Devil May Cry, Monster Hunter | Encadear habilidades em sequências específicas multiplica o dano ou ativa efeitos especiais. |
| **Resistências Elementais** | Dark Souls, Minecraft Dungeons | Mobs e jogadores têm resistências/fraquezas a fogo, gelo, relâmpago, trevas. Classes com afinidades. |
| **Stamina bar separada da mana** | Elden Ring, Dark Souls | Corrida, ataques pesados e esquivas consomem stamina. Mana apenas para habilidades de magia. |
| **Sistema de Morte com penalidade** | Path of Exile, Dark Souls | Ao morrer, perde XP ou dropa alguns itens no chão. Aumenta stakes sem ser frustrante demais. |
| **Buff/Debuff visible icons no HUD** | WoW, FFXIV | Ícones dos efeitos ativos (veneno, força, lentidão) visíveis no HUD do Ascendant em vez dos vanilla. |
| **Party System** | MMOs em geral | Grupo de até 4 jogadores com stats partilhados, aura de curandeiro em aliados, aggro de tanque. |
| **Reputação com Fações** | Skyrim, WoW | NPCs de fações diferentes reagem ao jogador com base na classe e ações. Permite missões exclusivas. |
| **Títulos desbloqueáveis** | WoW, FFXIV | Ao atingir certos marcos (nível 50, matar X bosses, completar Y dungeons) desbloqueia títulos visíveis no HUD. |


---

## Sessão 2 (continuação) — Habilidades, Comandos, Overlay (27/06/2026)

### Novos ficheiros

| Ficheiro | Descrição |
|----------|-----------|
| `ability/ClassAbilities.java` | Passivas das 8 classes (efeitos por 5s, partículas, stat bonuses) |
| `command/AscendantCommands.java` | `/ascendant stats\|class\|assign\|addxp\|setclass\|reset` |
| `network/ClassAssignedPacket.java` | Packet S2C quando classe é atribuída |
| `client/hud/ClassRevealOverlay.java` | Overlay dramático de ecrã cheio com fade in/out |

### Comandos disponíveis em jogo

```
/ascendant stats         — ver nível, XP, stats, stat points
/ascendant class         — ver classe e tracking de comportamento
/ascendant assign        — forçar atribuição baseada no comportamento
/ascendant addxp <n>     — adicionar XP (dev)
/ascendant setclass <c>  — definir classe diretamente (dev)
/ascendant reset         — resetar todos os dados
```

### Habilidades passivas por classe

| Classe | Passiva |
|--------|---------|
| Assassino | Invisibilidade + Speed ao agachar |
| Guardião | Absorção escalonada + Resistência |
| Mago | Regeneração + partículas de encanto |
| Titã | Força + Resistência |
| Arqueiro | Speed 2 + Night Vision (se PER ≥ 15) |
| Curandeiro | Regeneração pessoal + regeneração de aliados próximos (8 blocos) |
| Invocador | Speed + Força + Regen + partículas de fogo da alma |
| Espectro | Invisibilidade + Speed 3 ao agachar, Speed 2 normal |

### Correções de API 1.21.4

| Erro | Correção |
|------|----------|
| `MobEffects.RESISTANCE` | → `MobEffects.DAMAGE_RESISTANCE` |
| `AttachmentRegistry.persistent(toNbt, fromNbt)` | → `.persistent(Codec<T>)` |
| `SyncPlayerDataPacket` em client source set | Movido para main source set |


---

## Sessão 3 — Ecrã K, Ranks, Itens de Classe (28/06/2026)

### Resumo

Remoção do HUD permanente, substituído por um ecrã dedicado na tecla K. Adição do sistema de ranks, distribuição de stat points, framework completo de itens e armaduras por classe com penalidades cross-class.

### Novos ficheiros

**Main source set:**

| Ficheiro | Descrição |
|----------|-----------|
| `data/PlayerRank.java` | Enum 7 ranks: Latente → Despertar → Forjado → Élite → Exaltado → Soberano → Ascendente |
| `network/SpendStatPointPacket.java` | Packet C2S para gastar stat point (nome do stat como string) |
| `network/ServerNetworking.java` | Atualizado: registar C2S + handler `spendStatPoint`, método `sendClassAssigned` |
| `item/ClassWeapon.java` | Item com penalidade de ataque 0/35/60% por classe (pares opostos) |
| `item/ClassArmor.java` | Item de armadura com armorValue e tooltip de debuff |
| `item/AscendantItems.java` | 8 armas + 8 armaduras, creative tab `[Ascendant]` |
| `event/ItemClassEvents.java` | `AttackEntityCallback`: falha o ataque se classe errada, aplica Fraqueza 3s |

**Client source set:**

| Ficheiro | Descrição |
|----------|-----------|
| `client/AscendantKeyBindings.java` | Tecla K — `key.ascendant.stats` |
| `client/screen/AscendantStatsScreen.java` | Screen com 2 abas: ATRIBUTOS (rank, nível, XP, 5 stats com botões +) e PODERES (placeholder por classe) |
| `client/hud/AscendantHud.java` | Reescrito: removido painel permanente, mini-indicador `CLS Lv.X [+N]` e notificação de level-up |
| `client/AscendantClient.java` | Atualizado: registo de keybindings + tick handler para abrir/fechar ecrã K |

**Assets:**

| Ficheiro | Descrição |
|----------|-----------|
| 16× `assets/ascendant/models/item/*.json` | Modelos para armas (handheld) e armaduras (generated) |
| `assets/ascendant/lang/en_us.json` | Nomes dos itens (PT) + keybinding |

### Sistema de Ranks

| Rank | Nível | Cor |
|------|-------|-----|
| Latente | 1–9 | Cinzento §7 |
| Despertar | 10–24 | Verde claro §a |
| Forjado | 25–49 | Verde §2 |
| Élite | 50–74 | Azul claro §b |
| Exaltado | 75–99 | Azul §9 |
| Soberano | 100–149 | Roxo §5 |
| Ascendente | 150+ | Dourado bold §6§l |

### Itens de Classe

**Armas (uma por classe):**

| Item | Classe |
|------|--------|
| Adaga das Sombras | ASSASSIN |
| Maça do Baluarte | GUARDIAN |
| Cajado Arcano | MAGE |
| Martelo do Titã | TITAN |
| Arco do Caçador | ARCHER |
| Cetro da Vida | HEALER |
| Tomo das Sombras | SUMMONER |
| Lâmina Espectral | SPECTER |

**Sistema de penalidades cross-class:**
- Classe correta: 0% penalidade
- Classe errada (geral): 35% chance de falhar o ataque
- Classes opostas (MAGE↔TITAN, ASSASSIN↔GUARDIAN, HEALER↔SPECTER): 60% chance de falhar

### Correções de API 1.21.4

| Erro | Correção |
|------|----------|
| `Item.Properties` sem ID | → `new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))` antes de `new Item(props)` |
| `applyStatBonuses(player, data)` | → `applyStatBonuses(player)` (método só aceita ServerPlayer) |
| `ServerNetworking.java` truncado | Ficheiro reescrito por completo |
| `sendClassAssigned(player)` não encontrado | Adicionado método `sendClassAssigned(ServerPlayer)` que lê a classe dos dados |

---

## Sessão 4 — HUD Redesign, Fonte Global, Habilidades Gerais (28/06/2026)

### Resumo

Reescrita completa do HUD (barras HP/MP com texto, icons de stats), sistema de fonte customizada global, habilidades gerais funcionais com mana/cooldowns, ecrã de habilidades, e fix de bug nos IDs das habilidades.

### Novos ficheiros

**Main source set:**

| Ficheiro | Descrição |
|----------|-----------|
| `ability/AscendantAbility.java` | Record: id, displayName, desc, rank, manaCost, cooldownMs, isGeneral, icon |
| `ability/AbilityRank.java` | Enum E→SS com nível de desbloqueio e cor |
| `ability/AbilityRegistry.java` | Registo central: DASH, DOUBLE_JUMP, ENERGY_SHIELD, DODGE + placeholders de classe |
| `ability/GeneralAbilityHandler.java` | Execução server-side das 4 habilidades gerais (dash, salto duplo, escudo, esquivar) |
| `network/UseAbilityPacket.java` | Packet C2S: cliente envia ID da habilidade ao servidor |

**Client source set:**

| Ficheiro | Descrição |
|----------|-----------|
| `client/hud/AscendantFont.java` | Gestão centralizada de fontes: `text(str)` aplica `ascendant:hud` via `Style.withFont()` |
| `client/hud/AbilityHud.java` | HUD das habilidades (4 slots com cooldown, mana, ícone) |
| `client/screen/MainMenuScreen.java` | Ecrã "Sistema Ascendant" (ESC em jogo): botões Perfil e Habilidades |
| `client/screen/AbilitiesScreen.java` | Ecrã de habilidades com lista, mana/cooldown, teclas |
| `client/AscendantKeyBindings.java` | Teclas: K=menu, H=toggle HUD habilidades, G=Dash, F=Salto Duplo, R=Escudo, Z=Esquivar |

**Assets:**

| Ficheiro | Descrição |
|----------|-----------|
| `assets/ascendant/font/hud.json` | Definição da fonte `ascendant:hud` (LiberationSerif-Bold TTF, type: ttf) |
| `assets/ascendant/font/hud.ttf` | Ficheiro TTF (LiberationSerif-Bold, 144KB) |
| `assets/minecraft/font/default.json` | Aplica a mesma fonte globalmente em todo o Minecraft |

### HUD atual

- **Esquerda (baixo):** Barra HP vermelha com texto `HP/MaxHP` centrado
- **Direita (baixo):** Barra MP azul com texto `Mana/MaxMana` centrado
- **Acima da barra MP:** 4 icons (comida, XP, água, saturação) com valores numéricos
- **Canto sup. esq.:** Classe + Nível (se classe atribuída)
- **Centro ecrã:** Flash "LEVEL UP!" ao subir de nível

### Fonte

- **Tipo correto:** `"type": "ttf"` (não `"truetype"` — não existe em 1.21.4)
- **Path correto:** `"file": "ascendant:hud.ttf"` (sem `font/` — o Minecraft já prefixe automaticamente)
- **Global:** `assets/minecraft/font/default.json` aplica ao `minecraft:default`
- **Override:** `AscendantFont.text(str)` força `ascendant:hud` em sítios específicos

### Habilidades Gerais

| ID | Nome | Tecla | Mana | CD | Efeito |
|----|------|-------|------|----|--------|
| `dash` | Dash | G | 20 | 8s | Propulsão explosiva na direção do olhar |
| `double_jump` | Salto Duplo | F | 15 | 3s | Impulso vertical se estiver no ar |
| `energy_shield` | Escudo de Energia | R | 30 | 15s | Resistência IV invisível por 3s + animação de partículas client-side |
| `dodge` | Esquivar | Z | 10 | 5s | Esquiva lateral + i-frames (10 ticks) |

### Bug fixes

| Bug | Causa | Correção |
|-----|-------|----------|
| Fonte mostra caixas (□) | `"type": "truetype"` não existe em 1.21.4 | Mudar para `"type": "ttf"` |
| Fonte não carrega TTF | Path `"ascendant:font/hud.ttf"` duplicava `font/` | Mudar para `"ascendant:hud.ttf"` |
| Habilidades não funcionam | IDs enviados com namespace (`"ascendant:dash"`) mas registo usa apenas `"dash"` | Remover namespace nos `UseAbilityPacket` |
| Escudo não funcionava | ID enviado era `"ascendant:shield"` mas correto é `"energy_shield"` | Corrigido em `AscendantClient.java` |

---

## Sessão 5 — Animação Escudo de Energia (28/06/2026)

### Resumo

Substituição do efeito visual básico do Escudo de Energia (partículas server-side pontuais + ícone de Resistência HUD visível) por uma animação client-side contínua com partículas em esfera giratória à volta do jogador. O efeito de Resistência IV mantém-se para a mecânica de dano, mas fica completamente invisível.

### Novos ficheiros

| Ficheiro | Descrição |
|----------|-----------|
| `network/SyncShieldPacket.java` | Packet S2C: `active` (bool) + `ticks` (int) — informa o cliente do estado do escudo |
| `client/shield/ShieldRenderer.java` | Animação client-side: 3 anéis de `DustParticleOptions` azuis a rodar em planos diferentes + faíscas `END_ROD` aleatórias na superfície da esfera |

### Ficheiros modificados

| Ficheiro | Alteração |
|----------|-----------|
| `network/ServerNetworking.java` | Registou `SyncShieldPacket` S2C; adicionou `syncShieldToClient(player, active, ticks)` |
| `ability/GeneralAbilityHandler.java` | `executeEnergyShield`: substituiu `MobEffectInstance` visível por invisível (`visible=false, showIcon=false`) + chama `syncShieldToClient(player, true, 60)` |
| `client/AscendantClient.java` | Registou handler de `SyncShieldPacket`; chama `ShieldRenderer.tick()` em `ClientTickEvents.END_CLIENT_TICK` |

### Detalhes da animação (`ShieldRenderer`)

- **Anel 1 (plano XZ, horizontal):** 10 pontos azuis (`#338CFF`), roda 6°/tick
- **Anel 2 (plano XY, vertical):** 10 pontos azul-ciano (`#66D9FF`), roda no sentido contrário
- **Anel 3 (plano ZY, diagonal):** 6 pontos azuis, roda a 1.5× a velocidade
- **Faíscas END_ROD:** 3 partículas aleatórias na superfície da esfera a cada 2 ticks
- **Duração:** 60 ticks (3s), sincronizada com a Resistência IV no servidor
- **Raio:** 1.1 blocos, centrado ao nível do peito do jogador

### Bug fix

| Bug | Causa | Correção |
|-----|-------|----------|
| `BUILD FAILED` — `Vector3f cannot be converted to int` | Em Minecraft 1.21.4, `DustParticleOptions(Vector3f, float)` foi substituído por `DustParticleOptions(int argbColor, float size)` | Mudar `new Vector3f(r,g,b)` para literal ARGB hex (`0xFF338CFF`) |

---

## Sessão 6 — Escudo 3D Final (29/06/2026)

### Resumo

O visual de partículas do escudo foi substituído por uma **esfera 3D real** desenhada diretamente no pipeline de render do Minecraft. A esfera envolve completamente o personagem com: volume azul translúcido, 9 paralelos animados (pulsam individualmente) e 12 meridianos animados. Efeito de força-campo sci-fi, visível de forma clara.

### Mudanças

| Ficheiro | Alteração |
|----------|-----------|
| `client/shield/ShieldRenderer.java` | Reescrito: usa `WorldRenderEvents.AFTER_ENTITIES` + `Tesselator` + `TRIANGLES` + `POSITION_COLOR`. Esfera de raio **1.35** blocos com posição interpolada (`Mth.lerp` com partialTick). `disableDepthTest` para visibilidade garantida. |

### Detalhes técnicos

- **Evento:** `WorldRenderEvents.AFTER_ENTITIES` (mais fiável que `LAST` para objetos à volta de entidades)
- **Raio:** `1.35f * pulse` (pulso suave 4% a 0.12 rad/tick)
- **Volume interior:** 18×24 triângulos, azul `(0.15, 0.55, 1.0)`, alpha 14%
- **Paralelos:** 9 anéis horizontais como bandas triangulares finas (`halfWidth=0.013`), pulsam em ritmos diferentes `0.50 + 0.38*sin(animTick*0.13 + ring*0.95)`
- **Meridianos:** 12 linhas verticais como bandas triangulares, pulsam em `0.50 + 0.38*sin(animTick*0.11 + m*0.75)`
- **Posição:** `Mth.lerp(partialTick, player.xOld, player.getX())` para movimento suave sem jitter
- **Estado OpenGL:** `enableBlend + defaultBlendFunc + disableCull + depthMask(false) + disableDepthTest`
- **Shader:** `CoreShaders.POSITION_COLOR` (API correta para 1.21.4)

### Bug fixes desta sessão

| Bug | Causa | Correção |
|-----|-------|----------|
| Esfera não visível (raio 0.88 — muito pequena) | Raio menor que o player (1.8 blocos) | Aumentado para `1.35f` |
| Esfera possivelmente ocultada pelo depth buffer | `depthTest` ativo recusava pixels atrás do terreno | Adicionado `RenderSystem.disableDepthTest()` |
| Esfera com posição errática durante movimento | Usar `getX()` dá posição do tick atual sem interpolação | Usar `Mth.lerp(partialTick, xOld, getX())` |

---
