package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ClientAttackEntityCallback {
    Event<ClientAttackEntityCallback> EVENT = EventFactory.createArrayBacked(ClientAttackEntityCallback.class, callbacks -> (interactor, player, target) -> {
        for (ClientAttackEntityCallback callback : callbacks) {
            var r = callback.shouldCancel(interactor, player, target);
            if (r != null) return r;
        }
        return null;
    });
    @Nullable Boolean shouldCancel(ClientPlayerInteractionManager interactor, PlayerEntity player, Entity target);
}
