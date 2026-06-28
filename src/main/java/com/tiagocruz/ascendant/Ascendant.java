package com.tiagocruz.ascendant;

import com.tiagocruz.ascendant.attribute.AscendantAttributes;
import com.tiagocruz.ascendant.command.AscendantCommands;
import com.tiagocruz.ascendant.event.AscendantServerTickEvents;
import com.tiagocruz.ascendant.event.ItemClassEvents;
import com.tiagocruz.ascendant.event.PlayerEvents;
import com.tiagocruz.ascendant.item.AscendantItems;
import com.tiagocruz.ascendant.network.ServerNetworking;
import com.tiagocruz.ascendant.registry.AscendantAttachments;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ascendant implements ModInitializer {
    public static final String MOD_ID = "ascendant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[Ascendant] The System awakens.");
        AscendantAttributes.register(); // deve ser primeiro (EntityAttributeModificationCallback)
        AscendantItems.register();
        ServerNetworking.register();
        AscendantAttachments.register();
        PlayerEvents.register();
        AscendantServerTickEvents.register();
        AscendantCommands.register();
        Ite