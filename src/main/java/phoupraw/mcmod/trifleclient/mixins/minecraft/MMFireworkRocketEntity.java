package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMFireworkRocketEntity {
    static boolean freeElytraFlying(LivingEntity instance, Vec3d vec3d) {
        return !(instance instanceof PlayerEntity player && FreeElytraFlying.isFlying(player));
    }
}
