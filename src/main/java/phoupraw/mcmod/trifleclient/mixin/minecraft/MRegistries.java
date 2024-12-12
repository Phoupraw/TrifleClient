package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.trifleclient.v0.api.RegistryFreezeCallback;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@Mixin(Registries.class)
abstract class MRegistries {
    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void afterFreeze(CallbackInfo ci) {
        LOGGER.info("RegistryFreezeCallback");
        RegistryFreezeCallback.EVENT.invoker().afterAllFreezed();
    }
}
