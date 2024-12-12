package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;

@UtilityClass
public class AutoSwitchTools {
    private int prevSelected = -1;
    private boolean toSync;
    static {
        AttackBlockCallback.EVENT.register((player0, world, hand, pos, direction) -> {
            if (player0 instanceof ClientPlayerEntity player) {
                BlockState state = world.getBlockState(pos);
                int prevSelected = player.getInventory().selectedSlot;
                float maxProgress = state.calcBlockBreakingDelta(player, world, pos);
                int selected = -1;
                for (var iter = player.getInventory().main.listIterator(9); iter.hasPrevious(); ) {
                    int i = iter.previousIndex();
                    ItemStack stack = iter.previous();
                    if (i == prevSelected || stack.isEmpty()) continue;
                    player.getInventory().selectedSlot = i;
                    float progress = state.calcBlockBreakingDelta(player, world, pos);
                    if (maxProgress < progress) {
                        maxProgress = progress;
                        selected = i;
                    }
                    //ToolComponent tool = stack.get(DataComponentTypes.TOOL);
                    //if (tool == null) continue;
                    //float speed = tool.getSpeed(state);
                }
                if (selected >= 0) {
                    AutoSwitchTools.prevSelected = prevSelected;
                    player.getInventory().selectedSlot = selected;
                    player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(selected));
                } else {
                    player.getInventory().selectedSlot = prevSelected;
                }
            }
            return ActionResult.PASS;
        });
        ClientPlayerBlockBreakEvents.AFTER.register((world, player, pos, state) -> {
            if (prevSelected >= 0) {
                player.getInventory().selectedSlot = prevSelected;
                prevSelected = -1;
                toSync = true;
            }
        });
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (toSync) {
                var player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(player.getInventory().selectedSlot));
                    toSync = false;
                }
            }
        });
    }
    public static void onStopBreaking(ClientPlayerEntity player, boolean value) {
        if (value || prevSelected < 0 || player == null) return;
        player.getInventory().selectedSlot = prevSelected;
        prevSelected = -1;
        toSync = true;
    }
}
