package phoupraw.mcmod.trifleclient.v0.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@FunctionalInterface
public interface AutoSwitchToolCallback {
    Event<AutoSwitchToolCallback> EVENT = EventFactory.createArrayBacked(AutoSwitchToolCallback.class, callbacks -> (world, pos, state, side, player, hand) -> {
        for (AutoSwitchToolCallback callback : callbacks) {
            var r = callback.check(world, pos, state, side, player, hand);
            if (r != null) return r;
        }
        return true;
    });
    /// @param hand 永远为{@link Hand#MAIN_HAND}
    /// @return 可以为`null`
    Boolean check(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, @Deprecated Hand hand);
}
