package com.tiagocruz.ascendant.registry;

import com.tiagocruz.ascendant.Ascendant;
import com.tiagocruz.ascendant.data.AscendantPlayerData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class AscendantAttachments {

    public static final AttachmentType<AscendantPlayerData> PLAYER_DATA =
        AttachmentRegistry.<AscendantPlayerData>builder()
            .persistent(AscendantPlayerData.CODEC)
            .initializer(AscendantPlayerData::new)
            .buildAndRegister(Ascendant.id("player_data"));

    public static void register() {
        Ascendant.LOGGER.info("[Ascendant] Attachments registados.");
    }
}
