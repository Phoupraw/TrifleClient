package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMPlayerEntity {
    static double limitSpeed(PlayerEntity self, double original) {
        return Math.min(original, TCConfigs.A().getLimitSpeed());
    }
}
