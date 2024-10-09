package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMLivingEntity {
    static float moveLikeOnLand(LivingEntity self, float speed, float speed06) {
        //if (self instanceof ClientPlayerEntity) {
        //    return speed06;
        //}
        return speed;
    }
}
