package phoupraw.mcmod.trifleclient.events;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.util.MCUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@FunctionalInterface
public interface AutoHarvestCallback {
    Table<ChunkPos, BlockPos, Action> CACHE = Tables.newCustomTable(new Object2ObjectOpenHashMap<>(), Object2ObjectOpenHashMap::new);
    ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    Event<AutoHarvestCallback> EVENT = EventFactory.createArrayBacked(AutoHarvestCallback.class, callbacks -> (world, pos, state) -> {
        for (var callback : callbacks) {
            var r = callback.getAction(world, pos, state);
            if (r != null) return r;
        }
        return null;
    });
    static void simpleUse(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        interactor.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos.toImmutable(), false));
    }
    static void simpleAttack(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        interactor.attackBlock(pos.toImmutable(), Direction.UP);
    }
    @ApiStatus.Internal
    static void onChunkLoad(ClientWorld world, WorldChunk chunk) {
        CACHE.row(chunk.getPos()).clear();
        if (!TCConfigs.A().isAutoHarvest()) return;
        THREAD_POOL.execute(() -> {
            ChunkSection[] sections = chunk.getSectionArray();
            ChunkPos chunkPos = chunk.getPos();
            int size = chunk.getHighestNonEmptySection();
            var pos = new BlockPos.Mutable();
            for (int i = 0; i <= size; i++) {
                ChunkSection section = sections[i];
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            BlockState state = section.getBlockState(x, y, z);
                            if (state.isAir()) continue;
                            pos.set(chunkPos.getStartX() + x, chunk.getBottomY() + i * 16 + y, chunkPos.getStartZ() + z);
                            Action action = EVENT.invoker().getAction(world, pos, state);
                            if (action != null) {
                                CACHE.put(chunkPos, pos.toImmutable(), action);
                            }
                        }
                    }
                }
            }
            System.out.println("finished");
        });
    }
    @ApiStatus.Internal
    static void onChunkUnload(ClientWorld world, WorldChunk chunk) {
        CACHE.rowMap().remove(chunk.getPos());
    }
    @ApiStatus.Internal
    static void onEndTick(ClientWorld world) {
        if (!TCConfigs.A().isAutoHarvest()) return;
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        var interactor = MCUtils.getInteractor();
        int range = (int) Math.ceil(player.getBlockInteractionRange());
        for (BlockPos pos : BlockPos.iterateOutwards(BlockPos.ofFloored(player.getEyePos()), range, range, range)) {
            if (!player.canInteractWithBlockAt(pos, 0)) {
                continue;
            }
            Action action = CACHE.get(new ChunkPos(pos), pos);
            if (action == null) {
                continue;
            }
            BlockState state = player.getWorld().getBlockState(pos);
            action.perform(player, interactor, world, pos, state);
        }
    }
    @ApiStatus.Internal
    static void onBlockChanged(ClientWorld world, BlockPos pos, BlockState oldBlock, BlockState newBlock) {
        ChunkPos chunkPos = new ChunkPos(pos);
        var action = EVENT.invoker().getAction(world, pos, newBlock);
        if (action == null) {
            CACHE.remove(chunkPos, pos);
        } else {
            CACHE.put(chunkPos, pos, action);
        }
    }
    @ApiStatus.Internal
    static void reapCrop(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        if (state.calcBlockBreakingDelta(player, world, pos) < 1) {
            return;
        }
        ItemStack stack = player.getOffHandStack();
        if (!(stack.getItem() instanceof BlockItem item && item.getBlock() == state.getBlock())) {
            return;
        }
        var fortune = player.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.FORTUNE).orElseThrow();
        if (EnchantmentHelper.getLevel(fortune, player.getMainHandStack()) < fortune.value().getMaxLevel()) {
            return;
        }
        interactor.attackBlock(pos.toImmutable(), Direction.UP);
        interactor.interactBlock(player, Hand.OFF_HAND, new BlockHitResult(pos.toBottomCenterPos(), Direction.UP, pos.toImmutable(), false));
        stack.decrementUnlessCreative(1, player);
    }
    static void reapBamboo(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        if (state.calcBlockBreakingDelta(player, world, pos) >= 1) {
            interactor.attackBlock(pos.toImmutable(), Direction.UP);
        }
    }
    @ApiStatus.Internal
    static @Nullable Action checkBamboo(ClientWorld world, BlockPos pos, BlockState state) {
        if (state.isOf(Blocks.BAMBOO) && world.getBlockState(pos.down()).isOf(Blocks.BAMBOO)) {
            BlockState state1 = world.getBlockState(pos.down(2));
            if (!state1.isOf(Blocks.BAMBOO) && !state1.isAir()) {
                return AutoHarvestCallback::reapBamboo;
            }
        }
        return null;
    }
    @ApiStatus.Internal
    static @Nullable Action checkSugarCane(ClientWorld world, BlockPos pos, BlockState state) {
        if (state.isOf(Blocks.SUGAR_CANE)
          && world.getBlockState(pos.down()).isOf(Blocks.SUGAR_CANE)
          && !world.getBlockState(pos.down(2)).isOf(Blocks.SUGAR_CANE)
        ) {
            return AutoHarvestCallback::simpleAttack;
        }
        return null;
    }
    @ApiStatus.Internal
    static @Nullable Action checkCrop(ClientWorld world, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof CropBlock block && block.isMature(state)
          || state.isOf(Blocks.NETHER_WART) && state.get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE
        ) {
            return AutoHarvestCallback::reapCrop;
        }
        return null;
    }
    @ApiStatus.Internal
    static @Nullable Action checkCaveVines(ClientWorld world, BlockPos pos, BlockState state) {
        if ((state.isOf(Blocks.CAVE_VINES) || state.isOf(Blocks.CAVE_VINES_PLANT)) && state.get(CaveVines.BERRIES)) {
            return AutoHarvestCallback::simpleUse;
        }
        return null;
    }
    @ApiStatus.Internal
    static @Nullable Action checkSweetBerry(ClientWorld world, BlockPos pos, BlockState state) {
        if (state.isOf(Blocks.SWEET_BERRY_BUSH) && state.get(SweetBerryBushBlock.AGE) == SweetBerryBushBlock.MAX_AGE) {
            return AutoHarvestCallback::simpleUse;
        }
        return null;
    }
    /**
     @param pos {@link BlockPos.Mutable}
     */
    @Nullable AutoHarvestCallback.Action getAction(ClientWorld world, BlockPos pos, BlockState state);
    @FunctionalInterface
    interface Action {
        /**
         @param pos {@link BlockPos.Mutable}
         */
        void perform(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state);
    }
}
