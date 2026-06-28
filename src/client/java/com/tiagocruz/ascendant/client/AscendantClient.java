package com.tiagocruz.ascendant.client;

import com.tiagocruz.ascendant.client.data.ClientPlayerData;
import com.tiagocruz.ascendant.client.hud.AscendantHud;
import com.tiagocruz.ascendant.client.hud.ClassRevealOverlay;
import com.tiagocruz.ascendant.client.screen.AscendantStatsScreen;
import com.tiagocruz.ascendant.network.ClassAssignedPacket;
import com.tiagocruz.ascendant.network.SyncPlayerDataPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class AscendantClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registar keybinding
        AscendantKeyBindings.register();

        // Sync dados normais
        ClientPlayNetworking.registerGlobalReceiver(
            SyncPlayerDataPacket.TYPE,
            (payload, context) -> context.client().execute(() ->
                ClientPlayerData.update(
                    payload.level(), payload.xp(), payload.xpToNext(),
                    payload.statPoints(), payload.strength(), payload.agility(),
                    payload.endurance(), payload.intelligence(), payload.perception(),
                    payload.playerClass()
                )
            )
        );

        // Classe atribuída — overlay dramático
        ClientPlayNetworking.registerGlobalReceiver(
            ClassAssignedPacket.TYPE,
            (payload, context) -> context.client().execute(() ->
                ClassRevealOverlay.trigger(
                    payload.className(), payload.displayName(), payload.isRare()
                )
            )
        );

        // HUD: nível up + mini indicador + overlay de classe
        HudRenderCallback.EVENT.register((graphics, delta) -> {
            AscendantHud.render(graphics, delta);
            ClassRevealOverlay.render(graphics);
        });

        // Tick: verificar tecla K
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (AscendantKeyBindings.OPEN_STATS.consumeClick()) {
                if (client.screen == null) {
                    Minecraft.getInstance().setScreen(new AscendantStatsScreen());
                } else if (client.screen instanceof AscendantStatsScreen) {
                    client.screen.onClose();
                }
            }
        });
    }
}
