package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Range;

@FunctionalInterface
public interface GlowingColorCallback {
    @Range(from = -0xFFFFFF, to = 0xFFFFFF)
    int getColor(Entity entity, @Range(from = 0, to = 0xFFFFFF) int original);
    Event<GlowingColorCallback> EVENT = EventFactory.createArrayBacked(GlowingColorCallback.class, callbacks -> (entity, original) -> {
        for (GlowingColorCallback callback : callbacks) {
            original = callback.getColor(entity, original);
            if (original < 0) return -original;
        }
        return original;
    });
}
