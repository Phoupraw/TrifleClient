package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlock.AbstractBlockState.class)
abstract class MAbstractBlockState {
    //@Inject(method = "onBlockAdded",at = @At("RETURN"))
    //private void onBlockLoad(World world, BlockPos pos, BlockState state, boolean notify, CallbackInfo ci) {
    //
    //}
}
