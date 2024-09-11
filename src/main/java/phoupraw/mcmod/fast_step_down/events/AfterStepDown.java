package phoupraw.mcmod.fast_step_down.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface AfterStepDown {
    Event<AfterStepDown> EVENT = EventFactory.createArrayBacked(AfterStepDown.class, callbacks -> (self, movementType, movement, client, prevOnGround, highestBox, deltaY) -> {
        for (AfterStepDown callback : callbacks) {
            callback.accept(self, movementType, movement, client, prevOnGround, highestBox, deltaY);
        }
        
    });
    void accept(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround, Box highestBox, double deltaY);
}
