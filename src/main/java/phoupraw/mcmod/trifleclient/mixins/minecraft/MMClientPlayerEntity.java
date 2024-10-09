package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
@ApiStatus.NonExtendable
public interface MMClientPlayerEntity {
    /**
     @deprecated 这个方法关联太多，无论怎么改都无法做到在不接触流体时保持原样，还是直接去{@link LivingEntity#travel}里面改吧。
     */
    @Deprecated
    static void updateVelocity(ClientPlayerEntity self, float speed, Vec3d movementInput, BiConsumer<Float, Vec3d> invokeSuper, float offGroundSpeed) {
        float slipperiness = 0.6f;
        invokeSuper.accept(Math.max(speed,/*self.isOnGround() ? */self.getMovementSpeed() * (0.216f / (slipperiness * slipperiness * slipperiness)) /*: offGroundSpeed*/), movementInput);
    }
    static boolean noUsingItemSlow(ClientPlayerEntity self, boolean original) {
        return original && !TCConfigs.A.isNoUsingItemSlow();
    }
}
