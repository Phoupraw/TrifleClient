package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.trifleclient.events.AfterClientPlayerMove;
import phoupraw.mcmod.trifleclient.events.OnClientPlayerMove;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    //@Unique
    //@Override
    //public float getStepHeight() {
    //    return super.getStepHeight();
    //}
    //@SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "all"})
    //@ModifyReturnValue(method = "getStepHeight()F", at = @At("RETURN"))
    //private float minStepHeight(float original) {
    //    return MMClientPlayerEntity.minStepHeight(original);
    //}
    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    private Vec3d onClientPlayerMove(Vec3d movement, @Local(argsOnly = true) MovementType movementType) {
        return OnClientPlayerMove.EVENT.invoker().onClientPlayerMove((ClientPlayerEntity) (Object) this, movementType, movement);
    }
    @Inject(method = "move", at = @At("RETURN"))
    private void afterClientPlayerMove(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        AfterClientPlayerMove.EVENT.invoker().afterClientPlayerMove((ClientPlayerEntity) (Object) this, movementType, movement);
    }
    //@Override
    //public void updateVelocity(float speed, Vec3d movementInput) {
    //    MMClientPlayerEntity.updateVelocity((ClientPlayerEntity) (Object) this,speed,movementInput,super::updateVelocity,getOffGroundSpeed());
    //}
    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean noUsingItemSlow(boolean original) {
        return MMClientPlayerEntity.noUsingItemSlow((ClientPlayerEntity) (Object) this, original);
    }
    @Override
    protected float getVelocityMultiplier() {
        return super.getVelocityMultiplier();
    }
    @Dynamic
    @SuppressWarnings("target")
    @ModifyReturnValue(method = "getVelocityMultiplier", at = @At("RETURN"))
    private float minSpeedFactor(float original) {
        return MMClientPlayerEntity.getVelocityMultiplier((ClientPlayerEntity) (Object) this, original);
    }
    //@WrapWithCondition(method = "onTrackedDataSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;onTrackedDataSet(Lnet/minecraft/entity/data/TrackedData;)V"))
    //private boolean cancelPoseSync(AbstractClientPlayerEntity instance, TrackedData<?> data) {
    //    return MMClientPlayerEntity.cancelPoseSync(instance, data, POSE);
    //}
    
    //逆天Fabric API，直接把if块截断了，搞得我排查了半天！
    
    //@Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket$Mode;START_FALL_FLYING:Lnet/minecraft/network/packet/c2s/play/ClientCommandC2SPacket$Mode;"))
    //private void startFlying(CallbackInfo ci) {
    //    MMClientPlayerEntity.startFlying((ClientPlayerEntity) (Object) this);
    //}
    //@WrapWithCondition(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z", opcode = Opcodes.PUTFIELD))
    //private boolean stopFlying(PlayerAbilities instance, boolean value) {
    //    return MMClientPlayerEntity.stopFlying((ClientPlayerEntity) (Object) this, instance, value);
    //}
}
