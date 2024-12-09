package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.events.AutoPickCallback;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;
import phoupraw.mcmod.trifleclient.util.MCUtils;

@Environment(EnvType.CLIENT)
@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MMEntity {
    static boolean cancelPoseSync(Entity instance) {
        return !(instance instanceof PlayerEntity player && FreeElytraFlying.isFlying(player));
    }
    static void onBlockPosSet(Entity self, BlockPos prevPos) {
        if (!TCConfigs.A().isAutoPick() || self.getBlockPos().equals(prevPos) || self != MinecraftClient.getInstance().player) {
            return;
        }
        var player = (ClientPlayerEntity) self;
        var interactor = MCUtils.getInteractor();
        int range = (int) Math.ceil(player.getBlockInteractionRange());
        for (BlockPos pos : BlockPos.iterateOutwards(BlockPos.ofFloored(self.getEyePos()), range, range, range)) {
            if (player.canInteractWithBlockAt(pos, 0)) {
                BlockState state = player.getWorld().getBlockState(pos);
                Hand hand = AutoPickCallback.EVENT.invoker().shouldPick(player, pos, state);
                if (hand != null) {
                    interactor.interactBlock(player, hand, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos.toImmutable(), false));
                }
            }
        }
    }
}
