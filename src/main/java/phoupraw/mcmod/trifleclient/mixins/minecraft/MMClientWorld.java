package phoupraw.mcmod.trifleclient.mixins.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.events.AutoHarvestCallback;

@UtilityClass
@ApiStatus.Internal
public class MMClientWorld {
    public static void onBlockChanged(ClientWorld self, BlockPos pos, BlockState oldBlock, BlockState newBlock) {
        AutoHarvestCallback.onBlockChanged(self, pos, oldBlock, newBlock);
    }
}
