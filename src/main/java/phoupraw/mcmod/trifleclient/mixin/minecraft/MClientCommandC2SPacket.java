package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMClientCommandC2SPacket;

@Mixin(ClientCommandC2SPacket.class)
abstract class MClientCommandC2SPacket {
    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket$Mode;I)V", at = @At("RETURN"))
    private void startFlying(Entity entity, ClientCommandC2SPacket.Mode mode, int mountJumpHeight, CallbackInfo ci) {
        MMClientCommandC2SPacket.startFlying(entity, mode, mountJumpHeight);
    }
}
