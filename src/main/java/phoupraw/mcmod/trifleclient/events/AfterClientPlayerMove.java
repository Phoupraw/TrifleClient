package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface AfterClientPlayerMove {
    Event<AfterClientPlayerMove> EVENT = EventFactory.createArrayBacked(AfterClientPlayerMove.class, callbacks -> (player, movementType, movement) -> {
        for (AfterClientPlayerMove callback : callbacks) {
            callback.afterClientPlayerMove(player, movementType, movement);
        }
    });
    void afterClientPlayerMove(ClientPlayerEntity player, MovementType movementType, Vec3d movement);
}
