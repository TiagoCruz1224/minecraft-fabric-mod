package com.tiagocruz.ascendant.client.mixin;

import com.tiagocruz.ascendant.client.hud.AbilityHotbar;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancela os elementos vanilla do HUD que o Ascendant substitui.
 *
 * Assinaturas verificadas via bytecode da Gui.class (Yarn 1.21.4):
 *   renderPlayerHealth(GuiGraphics)V
 *   renderFood(GuiGraphics, Player, int, int)V
 *   renderHotbarAndDecorations(GuiGraphics, DeltaTracker)V
 *   renderItemHotbar(GuiGraphics, DeltaTracker)V
 */
@Mixin(Gui.class)
public class GuiMixin {

    /**
     * Cancela renderPlayerHealth — o método de topo que orquestra corações,
     * corações de absorção e armadura.
     */
    @Inject(method = "renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At("HEAD"), cancellable = true)
    private void ascendant$cancelPlayerHealth(GuiGraphics g, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * Cancela renderFood.
     */
    @Inject(method = "renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V",
            at = @At("HEAD"), cancellable = true)
    private void ascendant$cancelFood(GuiGraphics g, Player player, int x, int y, CallbackInfo ci) {
        ci.cancel();
    }

    /**
     * Cancela o fundo + decorações do hotbar vanilla quando a barra de
     * habilidades está activa. R toggle → volta ao hotbar normal.
     */
    @Inject(method = "renderHotbarAndDecorations(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At("HEAD"), cancellable = true)
    private void ascendant$cancelHotbar(GuiGraphics g, DeltaTracker dt, CallbackInfo ci) {
        if (AbilityHotbar.isVisible()) ci.cancel();
    }

    /**
     * Cancela a renderização dos items dentro do hotbar vanilla quando a barra
     * de habilidades está activa.
     */
    @Inject(method = "renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
            at = @At("HEAD"), cancellable = true)
    private void ascendant$cancelItemHotbar(GuiGraphics g, DeltaTracker dt, CallbackInfo ci) {
        if (AbilityHotbar.isVisible()) ci.cancel();
    }
}
