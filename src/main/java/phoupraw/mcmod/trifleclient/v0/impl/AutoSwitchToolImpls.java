package phoupraw.mcmod.trifleclient.v0.impl;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.constant.TCIDs;
import phoupraw.mcmod.trifleclient.v0.api.AutoSwitchToolCallback;
import phoupraw.mcmod.trifleclient.v0.api.AutoSwitchToolComparator;

import java.util.Set;

@UtilityClass
public class AutoSwitchToolImpls {
    private static final Set<BlockPos> MINED = new ObjectOpenHashSet<>();
    private static int prevSelected = -1;
    static {
        AttackBlockCallback.EVENT.register(AutoSwitchToolImpls::interact);
        ClientPlayerBlockBreakEvents.AFTER.register(AutoSwitchToolImpls::afterBlockBreak);
        ClientTickEvents.END_WORLD_TICK.register(AutoSwitchToolImpls::onEndTick);
        AutoSwitchToolCallback.EVENT.register(TCIDs.of("shift"), AutoSwitchToolImpls::cancelIfShift);
        AutoSwitchToolCallback.EVENT.register(TCIDs.of("shears"), AutoSwitchToolImpls::cancelIfShears);
        AutoSwitchToolComparator.EVENT.register(TCIDs.of("suitable"), AutoSwitchToolImpls::compareSuitable);
        AutoSwitchToolComparator.EVENT.register(TCIDs.of("insta/silk_touch"), AutoSwitchToolImpls::compareSilkTouchIfInstant);
        AutoSwitchToolComparator.EVENT.register(TCIDs.of("insta/fortune"), AutoSwitchToolImpls::compareFortuneIfInstant);
        AutoSwitchToolComparator.EVENT.register(TCIDs.of("speed"), AutoSwitchToolImpls::compareSpeed);
        AutoSwitchToolComparator.EVENT.register(TCIDs.of("silk_touch"), AutoSwitchToolImpls::compareSilkTouch);
        AutoSwitchToolComparator.EVENT.register(TCIDs.of("fortune"), AutoSwitchToolImpls::compareFortune);
    }
    @ApiStatus.Internal
    public static void onStopBreaking(ClientPlayerEntity player, boolean value) {
        //TODO 这个方法还有用吗？会不会有什么边缘情况没考虑到，是需要这个方法的？
        //if (value || prevSelected < 0 || player == null) return;
        //setBack(player);
    }
    /// 在服务端向客户端同步方块的破坏后再切换回原来的物品
    /// @see phoupraw.mcmod.trifleclient.mixin.minecraft.MClientWorld#afterSyncBlockState
    public static void afterSyncBlockState(ClientWorld world, BlockPos pos, BlockState state, int flags) {
        if ((state.isAir() || state.getBlock() instanceof FluidBlock) && MINED.remove(pos) && MINED.isEmpty() && prevSelected >= 0) {
            var player = MinecraftClient.getInstance().player;
            if (player != null) {
                setBack(player);
            }
        }
    }
    private static boolean isBothInstant(World world, BlockPos pos, BlockState state, ClientPlayerEntity player, int slot1, int slot2) {
        return !(AutoSwitchToolComparator.getMiningDelta(world, pos, state, player, slot1) < 1) && !(AutoSwitchToolComparator.getMiningDelta(world, pos, state, player, slot2) < 1);
    }
    private static boolean compareEnch(ClientPlayerEntity player, int slot1, int slot2, RegistryKey<Enchantment> key) {
        PlayerInventory inv = player.getInventory();
        ItemStack stack1 = inv.getStack(slot1);
        ItemStack stack2 = inv.getStack(slot2);
        RegistryEntry.Reference<Enchantment> holder = player.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(key).orElseThrow();
        return EnchantmentHelper.getLevel(holder, stack1) > EnchantmentHelper.getLevel(holder, stack2);
    }
    private static void setBack(ClientPlayerEntity player) {
        player.getInventory().selectedSlot = prevSelected;
        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(prevSelected));
        prevSelected = -1;
    }
    private static ActionResult interact(PlayerEntity player0, World world, Hand hand, BlockPos pos, Direction side) {
        if (!(TCConfigs.A().isAutoSwitchTool() && player0 instanceof ClientPlayerEntity player)) {
            return ActionResult.PASS;
        }
        BlockState state = world.getBlockState(pos);
        if (!AutoSwitchToolCallback.EVENT.invoker().check(world, pos, state, side, player, hand)) {
            return ActionResult.PASS;
        }
        PlayerInventory inv = player.getInventory();
        int best = inv.selectedSlot;
        for (var iter = inv.main.listIterator(9); iter.hasPrevious(); ) {
            int i = iter.previousIndex();
            ItemStack stack = iter.previous();
            if (i == inv.selectedSlot) continue;
            if (AutoSwitchToolComparator.EVENT.invoker().compare(world, pos, state, side, player, i, best)) {
                best = i;
            }
        }
        if (best != inv.selectedSlot) {
            if (prevSelected < 0) {
                prevSelected = inv.selectedSlot;
            }
            inv.selectedSlot = best;
            player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(best));
        }
        return ActionResult.PASS;
    }
    private static void onEndTick(ClientWorld world) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (prevSelected >= 0 && !MinecraftClient.getInstance().options.attackKey.isPressed()) {
            setBack(player);
        }
    }
    private static void afterBlockBreak(ClientWorld world, ClientPlayerEntity player, BlockPos pos, BlockState state) {
        MINED.add(pos);
    }
    private static Boolean cancelIfShift(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, Hand hand) {
        return Screen.hasShiftDown() ? false : null;
    }
    private static Boolean cancelIfShears(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, Hand hand) {
        return player.getMainHandStack().isIn(ConventionalItemTags.SHEAR_TOOLS) && state.getHardness(world, pos) == 0 ? false : null;
    }
    private static Boolean compareSuitable(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, int slot1, int slot2) {
        PlayerInventory inv = player.getInventory();
        int prev = inv.selectedSlot;
        inv.selectedSlot = slot1;
        boolean can1 = player.canHarvest(state);
        inv.selectedSlot = slot2;
        boolean can2 = player.canHarvest(state);
        inv.selectedSlot = prev;
        return can1 && !can2 ? true : null;
    }
    private static Boolean compareSilkTouchIfInstant(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, int slot1, int slot2) {
        return !isBothInstant(world, pos, state, player, slot1, slot2) ? null : compareEnch(player, slot1, slot2, Enchantments.SILK_TOUCH) ? true : null;
    }
    private static Boolean compareFortuneIfInstant(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, int slot1, int slot2) {
        return !isBothInstant(world, pos, state, player, slot1, slot2) ? null : compareEnch(player, slot1, slot2, Enchantments.FORTUNE) ? true : null;
    }
    private static Boolean compareSpeed(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, int slot1, int slot2) {
        return AutoSwitchToolComparator.getMiningDelta(world, pos, state, player, slot1) > AutoSwitchToolComparator.getMiningDelta(world, pos, state, player, slot2) ? true : null;
    }
    private static Boolean compareSilkTouch(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, int slot1, int slot2) {
        return compareEnch(player, slot1, slot2, Enchantments.SILK_TOUCH) ? true : null;
    }
    private static Boolean compareFortune(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, int slot1, int slot2) {
        return compareEnch(player, slot1, slot2, Enchantments.FORTUNE) ? true : null;
    }
}
