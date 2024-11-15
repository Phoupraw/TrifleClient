package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@Mixin(PlayerMoveC2SPacket.class)
abstract class MPlayerMoveC2SPacket {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static float lookUp(float pitch) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && FreeElytraFlying.isFlying(player)) {
            return -90;
        }
        return pitch;
    }
}
