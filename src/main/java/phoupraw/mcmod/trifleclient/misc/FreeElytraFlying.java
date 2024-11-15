package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@UtilityClass
public class FreeElytraFlying {
    @Contract(pure = true, value = "null->false")
    public static boolean isFlying(@Nullable PlayerEntity player) {
        //ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        //return stack.isOf(Items.ELYTRA) && ElytraItem.isUsable(stack) && player.isFallFlying();
        return TCConfigs.A.isFreeElytraFlying() && player != null && player.getWorld().isClient() && player.isFallFlying() && !player.getAbilities().allowFlying;
    }
    @ApiStatus.Internal
    public static void onEndTick(ClientWorld world) {
        var player = MinecraftClient.getInstance().player;
        if (!isFlying(player)) return;
        if (landing == 0) {
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            landing = 5;
        } else {
            //player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
            landing--;
        }
    }
    private static int landing;
}
