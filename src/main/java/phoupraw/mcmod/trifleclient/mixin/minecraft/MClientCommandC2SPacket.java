package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientCommandC2SPacket.class)
abstract class MClientCommandC2SPacket {
}
