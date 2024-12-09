package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoupraw.mcmod.trifleclient.events.AfterUseBlockCallback;
import phoupraw.mcmod.trifleclient.misc.MiningDelay;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
abstract class MClientPlayerInteractionManager {
    @Shadow
    private int blockBreakingCooldown;
    @ModifyExpressionValue(method = {"attackBlock", "updateBlockBreakingProgress"}, at = @At(value = "CONSTANT", args = "intValue=5"))
    private int miningDelay(int original) {
        return MiningDelay.removeDelay((ClientPlayerInteractionManager) (Object) this, original);
    }
    @Inject(method = "method_41930", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"))
    private void miningDelay(BlockState blockState, BlockPos blockPos, Direction direction, int sequence, CallbackInfoReturnable<Packet<ServerPlayPacketListener>> cir) {
        MiningDelay.setDelay((ClientPlayerInteractionManager) (Object) this, blockState, blockPos, direction, sequence);
    }
    @ModifyReturnValue(method = "interactBlockInternal",at = @At("RETURN"))
    private ActionResult afterUseBlock(ActionResult original, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        return original.isAccepted() ? original : AfterUseBlockCallback.EVENT.invoker().afterVanilla(player, hand, hitResult);
    }
}
