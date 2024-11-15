package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerInputC2SPacket.class)
abstract class MPlayerInputC2SPacket {
    //@Environment(EnvType.CLIENT)
    //@ModifyVariable(method = "<init>(FFZZ)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    //private static boolean notSendSneakingWhenFlying(boolean sneaking) {
    //    return MMPlayerInputC2SPacket.notSendSneakingWhenFlying(sneaking);
    //}
}
