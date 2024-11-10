package phoupraw.mcmod.trifleclient.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

/**
 防止摔落伤害
 */
@ApiStatus.NonExtendable
public interface OftenOnGround {
    @ApiStatus.Internal
    static Vec3d onClientPlayerMove(ClientPlayerEntity player, MovementType movementType, Vec3d movement) {
        if (TCConfigs.A.isOftenOnGround() && !player.isClimbing() && (movement.getY() < 0 || player.getAbilities().flying) && !player.isFallFlying()) {
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
        }
        return movement;
    }
}
