package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMPlayerInputC2SPacket {
    @Environment(EnvType.CLIENT)
    static boolean notSendSneakingWhenFlying(boolean sneaking) {
        return sneaking && !FreeElytraFlying.isFlying(MinecraftClient.getInstance().player);
    }
}
