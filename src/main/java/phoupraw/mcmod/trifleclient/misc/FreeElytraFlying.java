package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AEntity;

@UtilityClass
public class FreeElytraFlying {
    @Contract(pure = true, value = "null->false")
    public static boolean canFly(@Nullable PlayerEntity player) {
        //ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        //return stack.isOf(Items.ELYTRA) && ElytraItem.isUsable(stack) && player.isFallFlying();
        return TCConfigs.A.isFreeElytraFlying() && player != null && player.getWorld().isClient() && player.isFallFlying() && !player.getAbilities().allowFlying;
    }
    @Contract(pure = true, value = "null->false")
    public static boolean isFlying(@Nullable PlayerEntity player) {
        //ItemStack stack = player.getEquippedStack(EquipmentSlot.CHEST);
        //return stack.isOf(Items.ELYTRA) && ElytraItem.isUsable(stack) && player.isFallFlying();
        return canFly(player) && player.getAbilities().flying;
    }
    @ApiStatus.Internal
    public static void onEndTick(ClientWorld world) {
        var player = (ClientPlayerEntity & AEntity) MinecraftClient.getInstance().player;
        if (!isFlying(player)) return;
        if (landing == 0) {
            //for (int i = 0; i < 3; i++) {
            //    Vec3d movement = player.invokeAdjustMovementForCollisions(new Vec3d(0, -2*(i+1), 0));
            //    Vec3d pos = player.getPos().add(movement);
            //    player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(),pos.getY(),pos.getZ(),true));
            //}
           /* if (player.getVelocity().getY() < 0) {
                Vec3d movement = player.invokeAdjustMovementForCollisions(player.getVelocity().multiply(-1));
                Vec3d pos = player.getPos().add(movement);
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), true));
            } else */
            Vec3d movement = player.invokeAdjustMovementForCollisions(new Vec3d(0, -5, 0));
            //Vec3d pos = player.getPos().add(movement);
            //player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), true));
            if (movement.getY() > -5) {
                Vec3d pos = player.getPos().add(movement);
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), true));
            } else {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            }
            landing = 5;
        } else {
            //player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
            landing--;
        }
    }
    private static int landing;
    public static double y;
}