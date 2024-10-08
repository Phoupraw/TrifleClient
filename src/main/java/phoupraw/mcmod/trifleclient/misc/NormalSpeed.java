package phoupraw.mcmod.trifleclient.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface NormalSpeed {
    @ApiStatus.Internal
    static Vec3d onClientPlayerMove(ClientPlayerEntity player, Vec3d velocity) {
        return velocity.multiply(1.75);
    }
}
