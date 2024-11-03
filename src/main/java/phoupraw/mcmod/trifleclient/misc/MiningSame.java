package phoupraw.mcmod.trifleclient.misc;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;

public class MiningSame {
    private static Block block = Blocks.AIR;
    static {
        AttackBlockCallback.EVENT.register(MiningSame::interact);
    }
    public static Block getBlock() {
        return block;
    }
    public static void setBlock(Block block) {
        MiningSame.block = block;
    }
    private static ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (TCKeyBindings.MINING_SAME.isPressed()) {
            if (getBlock() != Blocks.AIR && block != getBlock()) {
                return ActionResult.FAIL;
            }
        }
        MiningSame.setBlock(block);
        return ActionResult.PASS;
    }
}
