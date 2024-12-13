package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMPlayerEntity {
    static boolean elytraFreeFlying(PlayerEntity instance, int index, boolean value) {
        return !FreeElytraFlying.isFlying(instance);
    }
    static double limitSpeed(PlayerEntity self, double original) {
        return Math.min(original, TCConfigs.A().getLimitSpeed());
    }
}
