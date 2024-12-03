package phoupraw.mcmod.trifleclient.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@ApiStatus.NonExtendable
public abstract class NormalSpeed {
    private static boolean recursion;
    @ApiStatus.Internal
    public static void afterClientPlayerMove(ClientPlayerEntity player, MovementType movementType, Vec3d velocity) {
        if (TCConfigs.A().isNormalSpeed() && !recursion && velocity.lengthSquared() > 1e-7) {
            recursion = true;
            player.move(movementType, velocity.multiply(0.75));
            recursion = false;
        }
    }
}
