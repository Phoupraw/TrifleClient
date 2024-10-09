package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.events.OnClientPlayerMove;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
    private Vec3d onClientPlayerMove(Vec3d movement) {
        return OnClientPlayerMove.EVENT.invoker().onClientPlayerMove((ClientPlayerEntity) (Object) this, movement);
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
    public float getStepHeight() {
        return Math.max(super.getStepHeight(), TCConfigs.A.getMinStepHeight());
    }
}
