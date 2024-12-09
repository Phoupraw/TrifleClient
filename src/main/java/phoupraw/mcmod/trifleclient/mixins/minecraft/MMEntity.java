package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MMEntity {
    static boolean cancelPoseSync(Entity instance) {
        return !(instance instanceof PlayerEntity player && FreeElytraFlying.isFlying(player));
    }
}
