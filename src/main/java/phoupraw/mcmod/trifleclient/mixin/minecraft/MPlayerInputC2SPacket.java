package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMPlayerInputC2SPacket;

@Mixin(PlayerInputC2SPacket.class)
abstract class MPlayerInputC2SPacket {
    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "<init>(FFZZ)V", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static boolean notSendSneakingWhenFlying(boolean sneaking) {
        return MMPlayerInputC2SPacket.notSendSneakingWhenFlying(sneaking);
    }
}
