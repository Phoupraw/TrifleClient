package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.events.AfterUseBlockCallback;
import phoupraw.mcmod.trifleclient.misc.MiningDelay;
import phoupraw.mcmod.trifleclient.v0.impl.AutoSwitchToolImpls;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
abstract class MClientPlayerInteractionManager {
    @Shadow
    private int blockBreakingCooldown;
    @Shadow @Final private MinecraftClient client;
    @ModifyExpressionValue(method = {"attackBlock", "updateBlockBreakingProgress"}, at = @At(value = "CONSTANT", args = "intValue=5"))
    private int miningDelay(int original) {
        return MiningDelay.removeDelay((ClientPlayerInteractionManager) (Object) this, original);
    }
    @ModifyReturnValue(method = "interactBlockInternal",at = @At("RETURN"))
    private ActionResult afterUseBlock(ActionResult original, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        return original.isAccepted() ? original : AfterUseBlockCallback.EVENT.invoker().afterVanilla(player, hand, hitResult);
    }
    @WrapWithCondition(method = {"cancelBlockBreaking","updateBlockBreakingProgress"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;breakingBlock:Z",opcode = Opcodes.PUTFIELD))
    private boolean onStopBreaking(ClientPlayerInteractionManager instance, boolean value) {
        AutoSwitchToolImpls.onStopBreaking(client.player,value);
        return true;
    }
}
