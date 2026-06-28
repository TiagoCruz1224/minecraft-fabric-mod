package com.tiagocruz.ascendant.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.network.ServerNetworking;
import com.tiagocruz.ascendant.system.BehaviorTracker;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Comandos do mod Ascendant.
 *
 * /ascendant stats       — ver stats actuais
 * /ascendant class       — ver classe e progresso
 * /ascendant assign      — forçar atribuição de classe (para testar)
 * /ascendant setclass <classe> — definir classe manualmente (dev/admin)
 * /ascendant addxp <amount>  — adicionar XP (dev)
 * /ascendant reset       — resetar dados do jogador (dev)
 */
public class AscendantCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ascendant")
                .then(Commands.literal("stats")
                    .executes(ctx -> cmdStats(ctx.getSource())))
                .then(Commands.literal("class")
                    .executes(ctx -> cmdClass(ctx.getSource())))
                .then(Commands.literal("assign")
                    .executes(ctx -> cmdAssign(ctx.getSource())))
                .then(Commands.literal("addxp")
                    .then(Commands.argument("amount", StringArgumentType.word())
                        .executes(ctx -> cmdAddXp(ctx.getSource(),
                            StringArgumentType.getString(ctx, "amount")))))
                .then(Commands.literal("setclass")
                    .then(Commands.argument("class", StringArgumentType.word())
                        .executes(ctx -> cmdSetClass(ctx.getSource(),
                            StringArgumentType.getString(ctx, "class")))))
                .then(Commands.literal("reset")
                    .executes(ctx -> cmdReset(ctx.getSource())))
        );
    }

    private static int cmdStats(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        AscendantPlayerData data = PlayerDataManager.get(player);

        player.sendSystemMessage(Component.literal(
            "§e=== [SISTEMA] Stats de " + player.getName().getString() + " ===\n" +
            "§fNível: §e" + data.getLevel() + " §7| XP: §f" + data.getXp() + "§7/§f" + data.xpForNextLevel() + "\n" +
            "§cFOR §f" + data.getStrength() +
            "  §aAGI §f" + data.getAgility() +
            "  §6RES §f" + data.getEndurance() +
            "  §9INT §f" + data.getIntelligence() +
            "  §dPER §f" + data.getPerception() + "\n" +
            "§eStat points disponíveis: §f" + data.getStatPoints()
        ));
        return 1;
    }

    private static int cmdClass(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        AscendantPlayerData data = PlayerDataManager.get(player);

        String cls = data.getPlayerClass().getDisplayName();
        String tracking = "§7Kills (melee/ranged): §f" + data.getMeleeKills() + "§7/§f" + data.getRangedKills() + "\n" +
                          "§7Stealth (seg): §f" + data.getSnuckTicks() + "\n" +
                          "§7Observação (seg): §f" + data.getObservationTicks();

        player.sendSystemMessage(Component.literal(
            "§e=== [SISTEMA] Classe ===\n" +
            "§fClasse actual: §b" + cls + "\n" +
            (data.getPlayerClass().isRare() ? "§d✦ Classe Rara\n" : "") +
            tracking
        ));
        return 1;
    }

    private static int cmdAssign(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        AscendantPlayerData data = PlayerDataManager.get(player);

        if (data.getPlayerClass().isAssigned()) {
            player.sendSystemMessage(Component.literal("§c[SISTEMA] Já tens uma classe: §b" + data.getPlayerClass().getDisplayName()));
            return 0;
        }

        PlayerClass cls = BehaviorTracker.determineClass(data);
        data.setPlayerClass(cls);
        ServerNetworking.syncToClient(player);

        player.sendSystemMessage(Component.literal(
            "§e[SISTEMA] §fO Sistema analisou o teu comportamento...\n" +
            "§b✦ Classe atribuída: " + cls.getDisplayName() +
            (cls.isRare() ? " §d(RARA)" : "")
        ));
        return 1;
    }

    private static int cmdAddXp(CommandSourceStack src, String amountStr) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        try {
            long amount = Long.parseLong(amountStr);
            AscendantPlayerData data = PlayerDataManager.get(player);
            boolean leveledUp = data.addXp(amount);
            ServerNetworking.syncToClient(player);
            player.sendSystemMessage(Component.literal(
                "§a[SISTEMA] +" + amount + " XP" + (leveledUp ? " §e— NÍVEL UP! → " + data.getLevel() : "")
            ));
        } catch (NumberFormatException e) {
            player.sendSystemMessage(Component.literal("§c[SISTEMA] Número inválido: " + amountStr));
        }
        return 1;
    }

    private static int cmdSetClass(CommandSourceStack src, String className) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        try {
            PlayerClass cls = PlayerClass.valueOf(className.toUpperCase());
            AscendantPlayerData data = PlayerDataManager.get(player);
            data.setPlayerClass(cls);
            ServerNetworking.syncToClient(player);
            player.sendSystemMessage(Component.literal("§e[SISTEMA] Classe definida: §b" + cls.getDisplayName()));
        } catch (IllegalArgumentException e) {
            player.sendSystemMessage(Component.literal("§c[SISTEMA] Classe inválida: " + className +
                "\nVálidas: ASSASSIN, GUARDIAN, MAGE, TITAN, ARCHER, HEALER, SUMMONER, SPECTER"));
        }
        return 1;
    }

    private static int cmdReset(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        PlayerDataManager.set(player, new AscendantPlayerData());
        ServerNetworking.syncToClient(player);
        player.sendSystemMessage(Component.literal("§c[SISTEMA] Dados resetados."));
        return 1;
    }
}
