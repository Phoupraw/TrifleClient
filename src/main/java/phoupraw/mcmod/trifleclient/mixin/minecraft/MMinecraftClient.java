package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
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
}
