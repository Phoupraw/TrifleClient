package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MMEntity {
    static void onBlockPosSet(Entity self, BlockPos prevPos) {
        //AutoPicks.onBlockPosSet(self, prevPos);
    }
}
