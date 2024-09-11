package phoupraw.mcmod.fast_step_down.mixins.minecraft;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import phoupraw.mcmod.fast_step_down.events.AfterStepDown;
import phoupraw.mcmod.fast_step_down.events.BeforeStepDown;
import phoupraw.mcmod.fast_step_down.events.OnStepDown;

import java.util.Comparator;

@Environment(EnvType.CLIENT)
public interface MMClientPlayerEntity {
    Comparator<Box> COMPARATOR = Comparator.comparingDouble(MMClientPlayerEntity::getMaxY);
    static void stepDown(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, LocalBooleanRef prevOnGround, Runnable sendMovementPackets) {
        if (Boolean.FALSE.equals(BeforeStepDown.EVENT.invoker().apply(self, movementType, movement, client, prevOnGround.get()))) {
            return;
        }
        //if (!FastStepDown.isFastStepDown() || movementType != MovementType.SELF /*|| self.hasVehicle()*/ || self.getAbilities().flying /*|| !prevOnGround.get() || self.isOnGround()*/ /*|| self.getVelocity().getY() > 0*/ || movement.getY() > 0) return;
        Entity vehicle = self.getRootVehicle();
        Box maxBox = null;
        double maxMaxY = Double.NEGATIVE_INFINITY;
        Box stretched = vehicle.getBoundingBox().stretch(0, vehicle.fallDistance - vehicle.getStepHeight(), 0);
        for (VoxelShape shape : self.getWorld().getCollisions(vehicle, stretched)) {
            for (Box box : shape.getBoundingBoxes()) {
                if (box.intersects(stretched)) {
                    double maxY = box.maxY;
                    if (maxMaxY < maxY) {
                        maxMaxY = maxY;
                        maxBox = box;
                    }
                }
            }
        }
        if (maxBox != null) {
            double dy = maxMaxY - vehicle.getY();
            if (!Boolean.FALSE.equals(OnStepDown.EVENT.invoker().apply(self, movementType, movement, client, prevOnGround.get(), maxBox, dy))) {
                sendMovementPackets.run();
                self.move(movementType, new Vec3d(0, dy, 0));//由于FastStepDownClient.checkStepHeight，所以不会无限递归
                sendMovementPackets.run();
                AfterStepDown.EVENT.invoker().accept(self, movementType, movement, client, prevOnGround.get(), maxBox, dy);
            }
        }
    }
    static double getMaxY(Box self) {
        return self.maxY;
    }
    static void storePrevStates(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, LocalBooleanRef prevOnGround) {
        prevOnGround.set(self.isOnGround());
    }
}
