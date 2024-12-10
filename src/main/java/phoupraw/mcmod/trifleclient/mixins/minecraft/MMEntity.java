package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.AutoPicks;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@Environment(EnvType.CLIENT)
@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MMEntity {
    static boolean cancelPoseSync(Entity instance) {
        return !(instance instanceof PlayerEntity player && FreeElytraFlying.isFlying(player));
    }
    static void onBlockPosSet(Entity self, BlockPos prevPos) {
        AutoPicks.onBlockPosSet(self, prevPos);
    }
}
