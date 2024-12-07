package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.events.GlowingCallback;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMMinecraftClient;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
abstract class MMinecraftClient {
    @WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z"))
    private boolean onUseKeyPress(KeyBinding instance, Operation<Boolean> original) {
        boolean returnValue = original.call(instance);
        MMMinecraftClient.onUseKeyPress((MinecraftClient) (Object) this, instance, returnValue);
        return returnValue;
    }
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void glowingCallbackBefore(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Boolean r = GlowingCallback.BEFORE.invoker().shouldGlow(entity);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
    @ModifyReturnValue(method = "hasOutline", at = @At("RETURN"))
    private boolean glowingCallbackAfter(boolean original, @Local(argsOnly = true) Entity entity) {
        return original || Boolean.TRUE.equals(GlowingCallback.AFTER.invoker().shouldGlow(entity));
    }
    @ModifyArg(method = "getTargetMillisPerTick", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), index = 0)
    private float noCapMSPT(float millis) {
        return TCConfigs.A().getMinMSPT();
    }
}
