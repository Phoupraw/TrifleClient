package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMClientCommandC2SPacket {
    static void startFlying(Entity entity, ClientCommandC2SPacket.Mode mode, int mountJumpHeight) {
        if (mode == ClientCommandC2SPacket.Mode.START_FALL_FLYING && entity instanceof PlayerEntity player && FreeElytraFlying.canFly(player)) {
            player.getAbilities().flying = true;
        }
    }
}
