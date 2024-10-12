package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

@FunctionalInterface
public interface OnUseKeyPress {
    void onUseKeyPress(MinecraftClient client, KeyBinding useKey);
    Event<OnUseKeyPress> EVENT = EventFactory.createArrayBacked(OnUseKeyPress.class, callbacks -> (client, useKey) -> {
        for (OnUseKeyPress callback : callbacks) {
            callback.onUseKeyPress(client, useKey);
        }
    });
}
