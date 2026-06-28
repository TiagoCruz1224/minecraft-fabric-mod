# Ascendant — Dev Log

Documento de progresso técnico do mod. Atualizado a cada sessão de desenvolvimento.

---

## Estado Atual

**Última atualização:** 27/06/2026 — 22:15  
**Versão do mod:** 1.0.0 (pré-alpha)  
**Branch:** master

### O que está feito

| # | Tarefa | Estado |
|---|--------|--------|
| 0 | Ambiente (JDK 21, VS Code, Fabric, runClient) | ✅ |
| 1 | Rename para Ascendant (estrutura, packages, fabric.mod.json) | ✅ |
| 1 | README com setup | ✅ |
| 1 | `docs/DEVLOG.md` e `build-and-run.bat` | ✅ |
| 2 | Sistema de dados do jogador (stats, nível, classe, XP) | ✅ |
| 2 | Persistência via Fabric Attachment API (Codec) | ✅ |
| 3 | HUD overlay (nível, classe, barra XP, stats, level up) | ✅ |
| 4 | Packets S2C (servidor → cliente sync) | ✅ |
| 4 | Tracking de kills (melee vs ranged), XP por kill | ✅ |
| 5 | BehaviorTracker (observação, stealth, por segundo) | ✅ |
| 6 | Atribuição de classe por algoritmo de score | ✅ |
| 6 | Classes raras (Invocador, Espectro) com critérios especiais | ✅ |
| 7 | Habilidades das classes | 🔲 |
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

- **Habilidades das 6 classes** — cada classe tem 3 habilidades ativas e 1 passiva
- **Sistema de Gates** — dungeons com mobs escalados e boss
- **Items especiais** — equipamento de cada classe
- **Sistema de fusões** — Natural / Forçada / Proibida


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
