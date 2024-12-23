package phoupraw.mcmod.trifleclient.v0.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@FunctionalInterface
public interface AutoSwitchToolComparator {
    Event<AutoSwitchToolComparator> EVENT = EventFactory.createArrayBacked(AutoSwitchToolComparator.class, callbacks -> (world, pos, state, side, player, slot1, slot2) -> {
        for (AutoSwitchToolComparator callback : callbacks) {
            var r = callback.compare(world, pos, state, side, player, slot1, slot2);
            if (r != null) return r;
        }
        return false;
    });
    static float getMiningDelta(World world, BlockPos pos, BlockState state, PlayerEntity player, int hotbar) {
        PlayerInventory inv = player.getInventory();
        int prev = inv.selectedSlot;
        inv.selectedSlot = hotbar;
        float delta = state.calcBlockBreakingDelta(player, world, pos);
        inv.selectedSlot = prev;
        return delta;
    }
    /// @return `true`表示`candidate`优先于`current`；`false`表示`candidate`不优先于`current`；`null`表示交给后续比较器判断。如果所有比较器都返回`null`，则视为`candidate`不优先于`current`。
    Boolean compare(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, int candidate, int current);
}
