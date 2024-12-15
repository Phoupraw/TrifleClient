package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.v0.api.AutoSwitchToolCallback;

@UtilityClass
public class AutoSwitchTools {
    private int prevSelected = -1;
    private boolean toSync;
    static {
        AttackBlockCallback.EVENT.register(AutoSwitchTools::interact);
        ClientPlayerBlockBreakEvents.AFTER.register(AutoSwitchTools::afterBlockBreak);
        ClientTickEvents.END_WORLD_TICK.register(AutoSwitchTools::onEndTick);
        AutoSwitchToolCallback.EVENT.register(AutoSwitchTools::check);
    }
    @ApiStatus.Internal
    public static void onStopBreaking(ClientPlayerEntity player, boolean value) {
        if (value || prevSelected < 0 || player == null) return;
        setBack(player);
    }
    private static void setBack(ClientPlayerEntity player) {
        player.getInventory().selectedSlot = prevSelected;
        prevSelected = -1;
        toSync = true;
    }
    private static ActionResult interact(PlayerEntity player0, World world, Hand hand, BlockPos pos, Direction side) {
        if (player0 instanceof ClientPlayerEntity player) {
            BlockState state = world.getBlockState(pos);
            if (AutoSwitchToolCallback.EVENT.invoker().check(world, pos, state, side, player, hand)) {
                int prevSelected = player.getInventory().selectedSlot;
                float maxProgress = state.calcBlockBreakingDelta(player, world, pos);
                double priority = 0;
                int selected = -1;
                for (var iter = player.getInventory().main.listIterator(9); iter.hasPrevious(); ) {
                    int i = iter.previousIndex();
                    ItemStack stack = iter.previous();
                    if (i == prevSelected || stack.isEmpty()) continue;
                    player.getInventory().selectedSlot = i;
                    float progress = state.calcBlockBreakingDelta(player, world, pos);
                    if (maxProgress < progress) {
                        if (player.getMainHandStack().isIn(ItemTags.SWORDS)) {
                            priority = -1;
                        }
                        maxProgress = progress;
                        selected = i;
                    } else if (maxProgress - 0.05f < progress || maxProgress >= 1 && progress >= 1) {
                        if (player.getMainHandStack().isIn(ConventionalItemTags.SHEAR_TOOLS)) {
                            if (priority < 2) {
                                maxProgress = progress;
                                selected = i;
                                priority = 2;
                            }
                        } else {
                            Registry<Enchantment> registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
                            if (EnchantmentHelper.getLevel(registry.getEntry(Enchantments.SILK_TOUCH).orElseThrow(), player.getMainHandStack()) > 0) {
                                if (priority < 2) {
                                    priority = 2;
                                    maxProgress = progress;
                                    selected = i;
                                }
                            } else {
                                RegistryEntry<Enchantment> enchEntry = registry.getEntry(Enchantments.FORTUNE).orElseThrow();
                                int level = EnchantmentHelper.getLevel(enchEntry, player.getMainHandStack());
                                if (level > 0) {
                                    double priority1 = (double) level / enchEntry.value().getMaxLevel();
                                    if (priority < priority1) {
                                        priority = priority1;
                                        maxProgress = progress;
                                        selected = i;
                                    }
                                }
                            }
                        }
                    }
                }
                if (selected >= 0) {
                    AutoSwitchTools.prevSelected = prevSelected;
                    player.getInventory().selectedSlot = selected;
                    player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(selected));
                } else {
                    player.getInventory().selectedSlot = prevSelected;
                }
            }
        }
        return ActionResult.PASS;
    }
    private static void onEndTick(ClientWorld world) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (prevSelected >= 0 && !MinecraftClient.getInstance().options.attackKey.isPressed()) {
            setBack(player);
        }
        if (toSync) {
            player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(player.getInventory().selectedSlot));
            toSync = false;
        }
    }
    private static void afterBlockBreak(ClientWorld world, ClientPlayerEntity player, BlockPos pos, BlockState state) {
        if (prevSelected >= 0) {
            setBack(player);
        }
    }
    private static Boolean check(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, Hand hand) {
        return Screen.hasShiftDown() ? false : null;
    }
}
