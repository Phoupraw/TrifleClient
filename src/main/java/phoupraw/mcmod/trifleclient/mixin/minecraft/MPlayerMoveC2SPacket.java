package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerMoveC2SPacket.class)
abstract class MPlayerMoveC2SPacket {
}
