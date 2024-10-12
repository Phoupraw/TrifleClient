package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.events.OnUseKeyPress;

@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MMMinecraftClient {
    static void onUseKeyPress(MinecraftClient self, KeyBinding instance, boolean returnValue) {
        if (returnValue && instance == self.options.useKey) {
            OnUseKeyPress.EVENT.invoker().onUseKeyPress(self, instance);
        }
    }
}
