package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.events.AutoPickCallback;
import phoupraw.mcmod.trifleclient.util.MCUtils;

@UtilityClass
public class AutoPicks {
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup(TrifleClient.ID + "-AutoPicks");
    public static void onBlockPosSet(Entity self, BlockPos prevPos) {
        if (!TCConfigs.A().isAutoPick() || self.getBlockPos().equals(prevPos) || self != MinecraftClient.getInstance().player) {
            return;
        }
        if (THREAD_GROUP.activeCount()>0) {
            return;
        }
        var player = (ClientPlayerEntity) self;
        var interactor = MCUtils.getInteractor();
        int range = (int) Math.ceil(player.getBlockInteractionRange());
        Thread thread = new Thread(THREAD_GROUP, () -> {
            for (BlockPos pos : BlockPos.iterateOutwards(BlockPos.ofFloored(self.getEyePos()), range, range, range)) {
                if (player.canInteractWithBlockAt(pos, 0)) {
                    BlockState state = player.getWorld().getBlockState(pos);
                    Hand hand = AutoPickCallback.EVENT.invoker().shouldPick(player, pos, state);
                    if (hand != null) {
                        interactor.interactBlock(player, hand, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos.toImmutable(), false));
                    }
                }
            }
        });
        thread.start();
    }
}
