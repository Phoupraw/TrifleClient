package phoupraw.mcmod.trifleclient.v0.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface RegistryFreezeCallback {
    Event<RegistryFreezeCallback> EVENT = EventFactory.createArrayBacked(RegistryFreezeCallback.class, callbacks -> () -> {
        for (var callback : callbacks) {
            callback.afterAllFreezed();
        }
    });
    void afterAllFreezed();
}
