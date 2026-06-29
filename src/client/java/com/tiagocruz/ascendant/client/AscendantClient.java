package com.tiagocruz.ascendant.client;

import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import com.tiagocruz.ascendant.client.hud.AbilityHotbar;
import com.tiagocruz.ascendant.client.hud.AbilityHud;
import com.tiagocruz.ascendant.client.hud.AscendantHud;
import com.tiagocruz.ascendant.client.hud.ClassRevealOverlay;
import com.tiagocruz.ascendant.client.screen.MainMenuScreen;
import com.tiagocruz.ascendant.client.shield.ShieldRenderer;
import com.tiagocruz.ascendant.network.ClassAssignedPacket;
import com.tiagocruz.ascendant.network.SyncManaPacket;
import com.tiagocruz.ascendant.network.SyncPlayerDataPacket;
import com.tiagocruz.ascendant.network.SyncShieldPacket;
import com.tiagocruz.ascendant.network.UseAbilityPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class AscendantClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AscendantKeyBindings.register();
        ShieldRenderer.register(); // render hook da esfera 3D

        // ─── Packets recebidos do servidor ────────────────────────────────────

        // Sync stats
        ClientPlayNetworking.registerGlobalReceiver(
            SyncPlayerDataPacket.TYPE,
            (payload, context) -> context.client().execute(() ->
                ClientPlayerData.update(
                    payload.level(), payload.xp(), payload.xpToNext(),
                    payload.statPoints(), payload.strength(), payload.agility(),
                    payload.endurance(), payload.intelligence(), payload.perception(),
                    payload.vitality(), payload.dexterity(), payload.wisdom(),
                    payload.playerClass()
                )
            )
        );

        // Sync mana
        ClientPlayNetworking.registerGlobalReceiver(
            SyncManaPacket.TYPE,
            (payload, context) -> context.client().execute(() ->
                ClientPlayerData.updateMana(payload.currentMana(), payload.maxMana())
            )
        );

        // Classe atribuida — overlay dramatico
        ClientPlayNetworking.registerGlobalReceiver(
            ClassAssignedPacket.TYPE,
            (payload, context) -> context.client().execute(() ->
                ClassRevealOverlay.trigger(payload.className(), payload.displayName(), payload.isRare())
            )
        );

        // Escudo de energia — activar/desactivar animacao cliente
        ClientPlayNetworking.registerGlobalReceiver(
            SyncShieldPacket.TYPE,
            (payload, context) -> context.client().execute(() -> {
                if (payload.active()) ShieldRenderer.activate(payload.ticks());
                else                  ShieldRenderer.deactivate();
            })
        );

        // ─── HUD ─────────────────────────────────────────────────────────────
        HudRenderCallback.EVENT.register((g, delta) -> {
            AscendantHud.render(g, delta);
            AbilityHud.render(g, Minecraft.getInstance());
            AbilityHotbar.render(g, Minecraft.getInstance());
        });

        // ─── Tick: escudo + teclas ───────────────────────────────────────────
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ShieldRenderer.tick();

            // K — abrir menu
            while (AscendantKeyBindings.OPEN_MENU.consumeClick()) {
                client.setScreen(new MainMenuScreen());
            }

            // H — toggle painel info de habilidades (lateral)
            while (AscendantKeyBindings.TOGGLE_ABILITY_HUD.consumeClick()) {
                AbilityHud.toggle();
            }

            // G — Dash (atalho directo)
            while (AscendantKeyBindings.ABILITY_DASH.consumeClick()) {
                ClientPlayNetworking.send(new UseAbilityPacket("dash"));
                AbilityHotbar.startCooldown("dash", 8_000L);
            }

            // F — Salto Duplo (atalho directo)
            while (AscendantKeyBindings.ABILITY_DOUBLE_JUMP.consumeClick()) {
                ClientPlayNetworking.send(new UseAbilityPacket("double_jump"));
                AbilityHotbar.startCooldown("double_jump", 3_000L);
            }

            // R — toggle barra de habilidades
            while (AscendantKeyBindings.TOGGLE_ABILITY_HOTBAR.consumeClick()) {
                AbilityHotbar.toggle();
            }

            // Z — usar habilidade seleccionada na barra
            while (AscendantKeyBindings.USE_SELECTED_ABILITY.consumeClick()) {
                String abilityId = AbilityHotbar.getSelectedAbilityId(client);
                if (abilityId != null) {
                    ClientPlayNetworking.send(new UseAbilityPacket(abilityId));
                    // Cooldown local (visual — estimativa sem reducao de SAB)
                    var ab = com.tiagocruz.ascendant.ability.AbilityRegistry.getById(abilityId);
                    if (ab != null) AbilityHotbar.startCooldown(abilityId, ab.cooldownMs());
                    // Shield especial: activar renderer imediatamente (feedback rapido)
                    if ("energy_shield".equals(abilityId)) ShieldRenderer.activate(60);
                }
            }
        });
    }
}
