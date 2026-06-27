package com.tiagocruz.ascendant.client.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class AscendantClientMixin {
    @Inject(at = @At("HEAD"), method = "run")
    private void onRun(CallbackInfo info) {
        // Ascendant client-side mixin placeholder
    }
}
