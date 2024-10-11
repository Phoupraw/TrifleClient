package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
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
    /**
     为了避免与 eclipse's tweakeroo冲突，在这里修改
     */
    @Environment(EnvType.CLIENT)
    static float minStepHeight(LivingEntity self, float original) {
        return self instanceof ClientPlayerEntity ? MMClientPlayerEntity.minStepHeight(original) : original;
    }
}
