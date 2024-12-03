package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMClientPlayNetworkHandler {
    static boolean elytraCancelSyncFlying(PlayerAbilities instance, boolean value, PlayerAbilitiesS2CPacket packet, MinecraftClient client) {
        if (!value && !packet.allowFlying() && TCConfigs.A().isElytraCancelSyncFlying()) {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
                if (stack.isOf(Items.ELYTRA) && ElytraItem.isUsable(stack)) {
                    return false;
                }
            }
        }
        return true;
    }
    static boolean elytraCancelSyncAllowFlying(PlayerAbilities instance, boolean value, PlayerAbilitiesS2CPacket packet, MinecraftClient client) {
        if (!value && TCConfigs.A().isElytraCancelSyncFlying()) {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
                if (stack.isOf(Items.ELYTRA) && ElytraItem.isUsable(stack)) {
                    return false;
                }
            }
        }
        return true;
    }
}
