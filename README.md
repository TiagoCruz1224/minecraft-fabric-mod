# Ascendant — Minecraft Fabric Mod

Mod de Minecraft inspirado no universo de Solo Leveling, com identidade própria.  
**Minecraft 1.21.4 · Fabric Loader 0.19.3 · Fabric Loom 1.17.12 · Java 21**

---

## Pré-requisitos

### 1. JDK 21
Instalar **Eclipse Temurin JDK 21** (obrigatório — versões anteriores não funcionam com Fabric Loom 1.17.12):

```
winget install EclipseAdoptium.Temurin.21.JDK
```

Após instalar, definir `JAVA_HOME` permanentemente (como Administrador):

```cmd
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot" /M
```

Verificar: `java -version` deve mostrar `openjdk 21`.

### 2. VS Code
Instalar extensões:
- **Extension Pack for Java** (Microsoft)
- **Gradle for Java**

Criar (ou verificar) `.vscode/settings.json` na raiz do projeto:

```json
{
  "java.jdt.ls.java.home": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.11.10-hotspot",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.11.10-hotspot",
      "default": true
    }
  ],
  "gradle.build.server.javaHome": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.11.10-hotspot",
  "java.configuration.updateBuildConfiguration": "interactive"
}
```

---

## Setup do Projeto

```cmd
git clone https://github.com/TiagoCruz1224/minecraft-fabric-mod.git
cd minecraft-fabric-mod
```

---

## Compilar

**Usar sempre CMD como Administrador** (PowerShell tem problemas de permissões com o Gradle no Windows):

```cmd
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
gradlew.bat build
```

Output esperado: `BUILD SUCCESSFUL`  
Jar gerado em: `build/libs/ascendant-1.0.0.jar`

---

## Testar (lançar Minecraft com o mod)

```cmd
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
gradlew.bat runClient
```

Na primeira execução descarrega os assets do Minecraft (~300 MB). As seguintes são rápidas.

---

## Estrutura do Projeto

```
ascendant/
├── src/
│   ├── main/
│   │   ├── java/com/tiagocruz/ascendant/
│   │   │   ├── Ascendant.java              ← entrypoint principal
│   │   │   └── mixin/
│   │   │       └── AscendantMixin.java
│   │   └── resources/
│   │       ├── fabric.mod.json
│   │       └── ascendant.mixins.json
│   └── client/
│       ├── java/com/tiagocruz/ascendant/client/
│       │   ├── AscendantClient.java         ← entrypoint cliente
│       │   └── mixin/
│       │       └── AscendantClientMixin.java
│       └── resources/
│           └── ascendant.client.mixins.json
├── gradle.properties                        ← versões do Minecraft/Fabric/mod
├── build.gradle
├── settings.gradle
└── .vscode/settings.json                   ← configuração VS Code (JDK 21)
```

---

## Problemas Conhecidos

### `JAVA_HOME is set to an invalid directory`
O JAVA_HOME aponta para um JDK que não existe. Verificar e corrigir:
```cmd
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot
```

### `Dependency requires at least JVM runtime version 21`
Significa que o Gradle está a usar Java 8 ou 17 em vez de 21. Confirmar o JAVA_HOME acima.

### `java.io.IOException: Acesso negado` (FileHasher)
Problema de permissões no Windows. Solução: **usar CMD como Administrador** (não PowerShell).  
Se persistir, limpar a cache:
```cmd
rmdir /s /q .gradle
rmdir /s /q build
gradlew.bat build
```

### VS Code mostra erros mesmo após build funcionar
Abrir VS Code, fechar e reabrir — o Java Language Server inicializa com JDK 21 automaticamente.  
Verificar barra de status em baixo: deve mostrar **"Java: Ready"** e **0 erros**.

---

## Configuração `gradle.properties`

| Propriedade | Valor |
|---|---|
| `minecraft_version` | 1.21.4 |
| `loader_version` | 0.19.3 |
| `loom_version` | 1.17.12 |
| `fabric_api_version` | 0.119.4+1.21.4 |
| `mod_version` | 1.0.0 |
| `maven_group` | com.tiagocruz.ascendant |
