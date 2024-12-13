package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMFluidBlock;

@Mixin(FluidBlock.class)
abstract class MFluidBlock {
    @Inject(method = "getCollisionShape",at = @At("HEAD"), cancellable = true)
    private void walkOn(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        var r = MMFluidBlock.walkOn(state, world, pos, context);
        if (r!=null){
            cir.setReturnValue(r);
        }
    }
}
