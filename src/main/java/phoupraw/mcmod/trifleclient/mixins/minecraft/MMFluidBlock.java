package phoupraw.mcmod.trifleclient.mixins.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@UtilityClass
public class MMFluidBlock {
    public @Nullable VoxelShape walkOn(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (TCConfigs.A().isWalkOnFluid()&& context instanceof EntityShapeContext shapeContext && shapeContext.getEntity() instanceof ClientPlayerEntity player && !(player.isSneaking() || player.isInFluid())) {
            float height = state.getFluidState().getHeight(world, pos);
            return VoxelShapes.cuboid(0, 0, 0, 1, height, 1);
        }
        return null;
    }
}
