package com.tiagocruz.ascendant.event;

import com.tiagocruz.ascendant.data.AscendantPlayerData;
import com.tiagocruz.ascendant.data.PlayerClass;
import com.tiagocruz.ascendant.data.PlayerDataManager;
import com.tiagocruz.ascendant.item.ClassArmor;
import com.tiagocruz.ascendant.item.ClassWeapon;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Eventos de penalidade por usar itens fora da classe correta.
 *
 * ARMAS — ao atacar com arma da classe errada:
 *   - 35% chance de falhar o ataque (classe normal errada)
 *   - 60% chance de falhar (classes opostas: MAGE/TITAN, ASSASSIN/GUARDIAN, HEALER/SPECTER)
 *   - Falha aplica Fraqueza I por 3 segundos ao atacante
 *
 * ARMADURAS — verificado em tick events (AscendantServerTickEvents):
 *   - Armadura errada no inventário activo: Lentidão + Fraqueza leves enquanto equipada
 */
public class ItemClassEvents {

    public static void register() {
        // Evento de ataque com arma de classe errada
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(world instanceof ServerLevel)) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof ClassWeapon weapon)) return InteractionResult.PASS;

            AscendantPlayerData data = PlayerDataManager.get(serverPlayer);
            PlayerClass pClass = data.getPlayerClass();
            float missPenalty = ClassWeapon.getMissPenalty(pClass, weapon.getRequiredClass());

            if (missPenalty > 0f && world.random.nextFloat() < missPenalty) {
                // Falhou — aplica fraqueza e cancela
                serverPlayer.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, true));
                if (entity instanceof LivingEntity living) {
                    // Toca no alvo mas não causa dano (cooldown não repõe)
                    // Apenas enviamos feedback sonoro via partículas
                }
                // Feedback ao jogador
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                        "§c✗ A arma resistiu ao teu controlo..."),
                    true);
                return InteractionResult.FAIL;
            }

            return InteractionResult.PASS;
        });
    }

    /**
     * Verifica se um jogador tem armadura da classe errada equipada no inventário.
     * Retorna a ClassArmor errada se encontrar, null se estiver tudo bem.
     */
    public static ClassArmor getWrongArmorInHotbar(ServerPlayer player) {
        AscendantPlayerData data = PlayerDataManager.get(player);
        PlayerClass pClass = data.getPlayerClass();
        if (pClass == PlayerClass.NONE) return null;

        // Verifica offhand e slots de armor do inventário
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof ClassArmor armor) {
                if (armor.getRequiredClass() != pClass) return armor;
            }
        }
        return null;
    }
}
