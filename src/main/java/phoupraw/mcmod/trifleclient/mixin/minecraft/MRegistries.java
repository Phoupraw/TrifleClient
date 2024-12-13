package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Registries.class)
abstract class MRegistries {
    //@Inject(method = "bootstrap", at = @At("RETURN"))
    //private static void afterFreeze(CallbackInfo ci) {
    //    LOGGER.info("RegistryFreezeCallback");
    //    RegistryFreezeCallback.EVENT.invoker().afterAllFreezed();
    //}
}
