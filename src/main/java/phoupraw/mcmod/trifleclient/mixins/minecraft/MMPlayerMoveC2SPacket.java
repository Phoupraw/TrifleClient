package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMPlayerMoveC2SPacket {
    @Environment(EnvType.CLIENT)
    static float lookUp(float pitch) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (FreeElytraFlying.isFlying(player)) {
            return -90;
        }
        return pitch;
    }
    @Environment(EnvType.CLIENT)
    static double slowlyDown(double y) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (FreeElytraFlying.isFlying(player)) {
            FreeElytraFlying.y -= 0.2;
            if (y < FreeElytraFlying.y) {
                return FreeElytraFlying.y;
            }
        }
        return FreeElytraFlying.y = y;
    }
}
