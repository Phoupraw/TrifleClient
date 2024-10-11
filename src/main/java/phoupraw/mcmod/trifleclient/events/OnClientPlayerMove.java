package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface OnClientPlayerMove {
    Event<OnClientPlayerMove> EVENT = EventFactory.createArrayBacked(OnClientPlayerMove.class, callbacks -> (player, movementType, movement) -> {
        for (OnClientPlayerMove callback : callbacks) {
            movement = callback.onClientPlayerMove(player, movementType, movement);
        }
        return movement;
    });
    Vec3d onClientPlayerMove(ClientPlayerEntity player, MovementType movementType, Vec3d movement);
}
