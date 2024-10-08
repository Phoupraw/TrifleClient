package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface OnClientPlayerMove {
    @NotNull Vec3d onClientPlayerMove(ClientPlayerEntity player, Vec3d movement);
    Event<OnClientPlayerMove> EVENT = EventFactory.createArrayBacked(OnClientPlayerMove.class, callbacks -> (player, movement) -> {
        for (OnClientPlayerMove callback : callbacks) {
            movement = callback.onClientPlayerMove(player, movement);
        }
        return movement;
    });
}
