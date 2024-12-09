package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
@Deprecated
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface AutoHarvestCallback {
    Event<AutoHarvestCallback> EVENT = EventFactory.createArrayBacked(AutoHarvestCallback.class, callbacks -> (player, pos, state) -> {
        for (AutoHarvestCallback callback : callbacks) {
            var r = callback.shouldHarvest(player, pos, state);
            if (r != null) return r;
        }
        return null;
    });
    @Nullable BlockHitResult shouldHarvest(ClientPlayerEntity player, BlockPos pos, BlockState state);
}
