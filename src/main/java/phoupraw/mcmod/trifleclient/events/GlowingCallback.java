package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface GlowingCallback {
    private static GlowingCallback apply(GlowingCallback[] callbacks) {
        return entity -> {
            for (GlowingCallback callback : callbacks) {
                var r = callback.shouldGlow(entity);
                if (r != null) return r;
            }
            return null;
        };
    }
    @Nullable Boolean shouldGlow(Entity entity);
    Event<GlowingCallback> BEFORE = EventFactory.createArrayBacked(GlowingCallback.class, GlowingCallback::apply);
    Event<GlowingCallback> AFTER = EventFactory.createArrayBacked(GlowingCallback.class, GlowingCallback::apply);
}
