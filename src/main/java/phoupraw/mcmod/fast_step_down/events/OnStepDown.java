package phoupraw.mcmod.fast_step_down.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface OnStepDown {
    /**
     若最终结果为{@code null}，则视为{@code true}。
     */
    Event<OnStepDown> EVENT = EventFactory.createArrayBacked(OnStepDown.class, callbacks -> (self, movementType, movement, client, prevOnGround, highestBox, deltaY) -> {
        for (OnStepDown callback : callbacks) {
            Boolean r = callback.apply(self, movementType, movement, client, prevOnGround, highestBox, deltaY);
            if (r != null) return r;
        }
        return null;
    });
    /**
     返回非{@code null}值会导致事件不执行后续回调并立刻返回；返回{@code null}表示继续执行后续回调。
     */
    @Nullable Boolean apply(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround, Box highestBox, double deltaY);
}
