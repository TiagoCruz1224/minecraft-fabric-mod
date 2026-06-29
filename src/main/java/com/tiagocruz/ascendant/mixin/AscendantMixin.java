package com.tiagocruz.ascendant.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class AscendantMixin {
    @Inject(at = @At("HEAD"), method = "loadLevel")
    private void onLoadLevel(CallbackInfo info) {
        // Ascendant server-side mixin placeholder
    }
}
