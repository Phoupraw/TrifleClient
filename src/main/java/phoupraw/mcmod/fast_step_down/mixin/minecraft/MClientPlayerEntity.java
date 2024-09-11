package phoupraw.mcmod.fast_step_down.mixin.minecraft;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.fast_step_down.FastStepDown;
import phoupraw.mcmod.fast_step_down.mixins.minecraft.MMClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class, priority = 10000)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    protected abstract void sendMovementPackets();
    @Shadow
    @Final
    protected MinecraftClient client;
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    @Inject(method = "move", at = @At(value = "HEAD"))
    private void storePrevStates(MovementType movementType, Vec3d movement, CallbackInfo ci, @Share(namespace = FastStepDown.ID, value = "prevOnGround") LocalBooleanRef prevOnGround) {
        MMClientPlayerEntity.storePrevStates((ClientPlayerEntity) (Object) this, movementType, movement, client, prevOnGround);
    }
    @Inject(method = "move", at = @At(value = "RETURN"))
    private void stepDown(MovementType movementType, Vec3d movement, CallbackInfo ci, @Share(namespace = FastStepDown.ID, value = "prevOnGround") LocalBooleanRef prevOnGround) {
        MMClientPlayerEntity.stepDown((ClientPlayerEntity) (Object) this, movementType, movement, client, prevOnGround, this::sendMovementPackets);
    }
}
