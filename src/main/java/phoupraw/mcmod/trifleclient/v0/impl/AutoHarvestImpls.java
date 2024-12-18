package phoupraw.mcmod.trifleclient.v0.impl;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.util.MCUtils;
import phoupraw.mcmod.trifleclient.v0.api.AutoHarvestCallback;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Environment(EnvType.CLIENT)
@UtilityClass
public class AutoHarvestImpls {
    public static final Table<ChunkPos, BlockPos, AutoHarvestCallback> CACHE = Tables.newCustomTable(new ConcurrentHashMap<>(), Object2ObjectOpenHashMap::new);
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    /**
     @see phoupraw.mcmod.trifleclient.mixin.minecraft.MClientWorld#onBlockChanged(BlockPos, BlockState, BlockState, CallbackInfo)
     */
    public static void onBlockChanged(ClientWorld world, BlockPos pos, BlockState oldBlock, BlockState newBlock) {
        ChunkPos chunkPos = new ChunkPos(pos);
        //var action = EVENT.invoker().getAction(world, pos, newBlock);
        var action = AutoHarvestCallback.LOOKUP.find(world, pos, newBlock, null, null);
        if (action == null) {
            CACHE.remove(chunkPos, pos);
        } else {
            CACHE.put(chunkPos, pos, action);
        }
    }
    public static void init() {
        ClientTickEvents.END_WORLD_TICK.register(AutoHarvestImpls::onEndTick);
        ClientChunkEvents.CHUNK_LOAD.register(AutoHarvestImpls::onChunkLoad);
        ClientChunkEvents.CHUNK_UNLOAD.register(AutoHarvestImpls::onChunkUnload);
        AutoHarvestCallback.LOOKUP.registerForBlocks(AutoHarvestImpls::findBamboo, Blocks.BAMBOO);
        AutoHarvestCallback.LOOKUP.registerForBlocks(AutoHarvestImpls::findCaveVines, Blocks.CAVE_VINES_PLANT, Blocks.CAVE_VINES);
        AutoHarvestCallback.LOOKUP.registerForBlocks(AutoHarvestImpls::findSugarCane, Blocks.SUGAR_CANE);
        AutoHarvestCallback.LOOKUP.registerForBlocks(AutoHarvestImpls::findSweetBerry, Blocks.SWEET_BERRY_BUSH);
        AutoHarvestCallback.LOOKUP.registerForBlocks(AutoHarvestImpls::findCactus, Blocks.CACTUS);
        AutoHarvestCallback.LOOKUP.registerFallback(AutoHarvestImpls::findCrop);
    }
    private static void onChunkLoad(ClientWorld world, WorldChunk chunk) {
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
                            //Action action = EVENT.invoker().getAction(world, pos, state);
                            var action = AutoHarvestCallback.LOOKUP.find(world, pos, state, null, null);
                            if (action != null) {
                                CACHE.put(chunkPos, pos.toImmutable(), action);
                            }
                        }
                    }
                }
            }
        });
    }
    private static void onChunkUnload(ClientWorld world, WorldChunk chunk) {
        CACHE.rowMap().remove(chunk.getPos());
    }
    private static void onEndTick(ClientWorld world) {
        if (!TCConfigs.A().isAutoHarvest()) return;
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        var interactor = MCUtils.getInteractor();
        int range = (int) Math.ceil(player.getBlockInteractionRange());
        for (BlockPos pos : BlockPos.iterateOutwards(BlockPos.ofFloored(player.getEyePos()), range, range, range)) {
            if (!player.canInteractWithBlockAt(pos, 0)) {
                continue;
            }
            var action = CACHE.get(new ChunkPos(pos), pos);
            if (action == null) {
                continue;
            }
            BlockState state = player.getWorld().getBlockState(pos);
            action.act(player, interactor, world, pos, state);
        }
    }
    private static void reapCrop(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        if (!(player.canInteractWithBlockAt(pos.down(), 0) && state.calcBlockBreakingDelta(player, world, pos) >= 1)) {
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
        int preCount = stack.getCount();
        interactor.interactBlock(player, Hand.OFF_HAND, new BlockHitResult(pos.toBottomCenterPos(), Direction.UP, pos.down(), false));
        int count = stack.getCount();
        if (count == preCount) {
            stack.decrementUnlessCreative(1, player);
        }
    }
    private static AutoHarvestCallback findBamboo(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        if (world.getBlockState(pos.down()).isOf(Blocks.BAMBOO)) {
            BlockState state1 = world.getBlockState(pos.down(2));
            if (!state1.isOf(Blocks.BAMBOO) && !state1.isAir()) {
                return AutoHarvestCallback::simpleInstaMine;
            }
        }
        return null;
    }
    private static AutoHarvestCallback findCaveVines(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        if (state.get(CaveVines.BERRIES)) {
            return AutoHarvestCallback::simpleUse;
        }
        return null;
    }
    private static AutoHarvestCallback findSugarCane(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        if (world.getBlockState(pos.down()).isOf(Blocks.SUGAR_CANE) && !world.getBlockState(pos.down(2)).isOf(Blocks.SUGAR_CANE)
        ) {
            return AutoHarvestCallback::simpleAttack;
        }
        return null;
    }
    private static AutoHarvestCallback findSweetBerry(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        if (state.get(SweetBerryBushBlock.AGE) == SweetBerryBushBlock.MAX_AGE) {
            return AutoHarvestCallback::simpleUse;
        }
        return null;
    }
    private static AutoHarvestCallback findCactus(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        if (world.getBlockState(pos.down()).isOf(Blocks.CACTUS) && !world.getBlockState(pos.down(2)).isOf(Blocks.CACTUS)
        ) {
            return AutoHarvestCallback::simpleInstaMine;
        }
        return null;
    }
    private static AutoHarvestCallback findCrop(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        if (state.getBlock() instanceof CropBlock block && block.isMature(state)
          || state.isOf(Blocks.NETHER_WART) && state.get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE
        ) {
            return AutoHarvestImpls::reapCrop;
        }
        return null;
    }
}
