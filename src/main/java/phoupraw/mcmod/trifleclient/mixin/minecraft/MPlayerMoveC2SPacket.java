package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMPlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
abstract class MPlayerMoveC2SPacket {
    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static float lookUp(float pitch) {
        return MMPlayerMoveC2SPacket.lookUp(pitch);
    }
    @Environment(EnvType.CLIENT)
    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static double slowlyDown(double y) {
        return MMPlayerMoveC2SPacket.slowlyDown(y);
    }
}
