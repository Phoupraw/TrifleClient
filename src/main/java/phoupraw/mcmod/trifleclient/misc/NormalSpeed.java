package phoupraw.mcmod.trifleclient.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@ApiStatus.NonExtendable
public interface NormalSpeed {
    @ApiStatus.Internal
    static Vec3d onClientPlayerMove(ClientPlayerEntity player, Vec3d velocity) {
        return TCConfigs.A.isNormalSpeed() ? velocity.multiply(1.75) : velocity;
    }
}
