package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;

@UtilityClass
public class SpeedSpeed {
    //public static final int STEPS = 20;
    //private static int step = 0;
    private static boolean looping;
    /**
     基本是从meteor抄的
     */
    @ApiStatus.Internal
    public static Vec3d onClientPlayerMove(ClientPlayerEntity player, MovementType movementType, Vec3d velocity) {
        if (!TCConfigs.A().isSpeedSpeed() || !TCKeyBindings.SPEED.isPressed() || movementType != MovementType.SELF || looping) {
            return velocity;
        }
        //if (step >= STEPS) {
        //    step = 0;
        //    return velocity;
        //} else {
        //    step++;
        //    player.move(movementType,Vec3d.ZERO);
        //}
        Input input = player.input;
        if (input == null) {
            return velocity;
        }
        float yaw = player.getYaw();
        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velZ = 0;
        boolean isForward = false;
        double factor = TCConfigs.A().getSpeedFactor();
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
        int steps = TCConfigs.A().getSpeedSteps();
        Vec3d motion = new Vec3d(velX, velocity.getY() / steps, velZ);
        looping = true;
        for (int i = 1; i < steps; i++) {
            player.move(movementType, motion);
            player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY(), player.getZ(), player.isOnGround()));
        }
        looping = false;
        return motion;
    }
}
