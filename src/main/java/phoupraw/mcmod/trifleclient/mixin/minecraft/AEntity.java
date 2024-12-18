package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface AEntity {
    @Invoker
    Vec3d invokeAdjustMovementForCollisions(Vec3d movement);
    @Invoker
    float invokeGetVelocityMultiplier();
}
