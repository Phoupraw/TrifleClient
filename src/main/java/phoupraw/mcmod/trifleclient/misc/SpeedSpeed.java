package phoupraw.mcmod.trifleclient.misc;

import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;

@ApiStatus.NonExtendable
public interface SpeedSpeed {
    /**
     基本是从meteor抄的
     */
    @ApiStatus.Internal
    static Vec3d mixin(ClientPlayerEntity player, Vec3d velocity) {
        if (!TCKeyBindings.SPEED.isPressed()) {
            return velocity;
        }
        //velocity = velocity.multiply(TClientConfig.getInstance().getNormalSpeed());
        //if (!getCollidingBlocks(player.getWorld(), player.getBoundingBox(), s -> s.isOf(Blocks.COBWEB), true).isEmpty()) {
        //    velocity=Vec3d.ZERO;
        //}
        //GameOptions options = MinecraftClient.getInstance().options;
        //if (/*TClientConfig.getInstance().isAutoClimb() && */player.getPitch() < -80 && player.isClimbing() && options.jumpKey.isPressed() && !options.sneakKey.isPressed()) {
        //    if (velocity.getY() < 1) {
        //        velocity = velocity.withAxis(Direction.Axis.Y, 1);
        //    }
        //}
        //if (options.jumpKey.isPressed() && !((AEntity) player).getSubmergedFluidTag().isEmpty() && player.getPitch() < -80) {
        //    double swimUp = TClientConfig.getInstance().getSwimUp();
        //    if (velocity.getY() < swimUp) {
        //        velocity = velocity.withAxis(Direction.Axis.Y, swimUp);
        //    }
        //}
        Input input = player.input;
        if (/*!TrifleClient.speed || */input == null) {
            return velocity;
        }
        float yaw = player.getYaw();
        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velZ = 0;
        boolean isForward = false;
        double factor = 1.5;
        if (input.pressingForward) {
            velX += forward.x * factor;
            velZ += forward.z * factor;
            isForward = true;
        }
        if (input.pressingBack) {
            velX -= forward.x * factor;
            velZ -= forward.z * factor;
            isForward = true;
        }
        boolean isSide = false;
        if (input.pressingRight) {
            velX += right.x * factor;
            velZ += right.z * factor;
            isSide = true;
        }
        if (input.pressingLeft) {
            velX -= right.x * factor;
            velZ -= right.z * factor;
            isSide = true;
        }
        if (isForward && isSide) {
            double diagonal = 1 / Math.sqrt(2);
            velX *= diagonal;
            velZ *= diagonal;
        }
        return new Vec3d(velX, velocity.getY(), velZ);
    }
}
