package phoupraw.mcmod.trifleclient.misc;

import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPredicateArgument;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
public class BlockFinder {
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    //private static final Set<BlockPos> iterated = new ObjectOpenHashSet<>();
    private volatile static @NotNull Iterator<BlockPos> iterator = Collections.emptyIterator();
    private volatile static @NotNull Predicate<CachedBlockPosition> predicate = Predicates.alwaysFalse();
    private volatile static @Nullable BlockPos found;
    private boolean sameSpace;
    private volatile boolean stopping, stopped = true;
    //private static Thread thread;
    static {
        ClientCommandRegistrationCallback.EVENT.register(BlockFinder::register);
        ClientTickEvents.END_WORLD_TICK.register(BlockFinder::onEndTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(BlockFinder::onClientStopping);
        //thread.start();
    }
    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(TrifleClient.ID)
          .then(ClientCommandManager.literal("find")
            .then(ClientCommandManager.literal("samespace")
              .executes(context -> {
                  sameSpace ^= true;
                  context.getSource().sendFeedback(Text.of("相同空间已" + (sameSpace ? "开启" : "关闭") + "。"));
                  return 1;
              })
            )
            .then(ClientCommandManager.literal("block")
              .requires(source -> TCConfigs.A().isBlockFinder())
              .executes(BlockFinder::clearSearching)
              .then(ClientCommandManager.argument("block", CBlockPredicateArgument.blockPredicate(registryAccess))
                .executes(BlockFinder::setSearching)
              )
            )
          )
        );
    }
    private static int setSearching(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        try {
            synchronized (BlockFinder.class) {
                //iterator = BlockPos.iterateOutwards(source.getPlayer().getBlockPos(), range, range, range).iterator();
                predicate = CBlockPredicateArgument.getBlockPredicate(context, "block");
                source.sendFeedback(Text.of("开始搜索……"));
                start(source.getWorld(), source.getPlayer().getBlockPos());
                return 1;
            }
        } catch (Throwable e) {
            source.sendError(Text.of("开始搜索时发生错误：" + e));
            LOGGER.catching(e);
            return 0;
        }
    }
    /**
     需要外部同步
     */
    private static void start(World world, BlockPos origin) throws InterruptedException {
        clearFound();
        while (!stopped) {
            Thread.yield();
        }
        stopping = false;
        int range = (MinecraftClient.getInstance().options.getViewDistance().getValue() * 2 + 1) * 16 / 2;
        if (sameSpace) {
            iterator = new AbstractIterator<>() {
                final Set<BlockPos> set = new ObjectOpenHashSet<>();
                final Queue<BlockPos> queue = new ArrayDeque<>();
                {
                    queue.add(origin);
                }
                @Override
                protected BlockPos computeNext() {
                    while (!queue.isEmpty()) {
                        BlockPos pos = queue.poll();
                        if (!set.add(pos) /*|| pos.getSquaredDistance(origin) > 64 * 64*/) {
                            continue;
                        }
                        BlockState state = world.getBlockState(pos);
                        if (state.isAir() || state.isOf(Blocks.WATER) || (!state.isSolidBlock(world, pos) && state.getCollisionShape(world, pos).isEmpty())) {
                            for (Direction direction : RedstoneView.DIRECTIONS) {
                                queue.add(pos.offset(direction));
                            }
                        }
                        return pos;
                    }
                    endOfData();
                    return null;
                }
            };
        } else {
            iterator = BlockPos.iterateOutwards(origin, range, range, range).iterator();
        }
        THREAD_POOL.execute(BlockFinder::run);
    }
    private static void run() {
        stopped = false;
        boolean failed = true;
        while (iterator.hasNext()) {
            if (stopping) {
                stopped = true;
                return;
            }
            try {
                ClientWorld world = MinecraftClient.getInstance().world;
                if (world == null) break;
                BlockPos pos = iterator.next();
                if (predicate.test(new CachedBlockPosition(world, pos, false))) {
                    synchronized (BlockFinder.class) {
                        BlockPos found = pos.toImmutable();
                        BlockFinder.found = found;
                        iterator = Collections.emptyIterator();
                        TargetPointer.POSITIONS.add(pos.toCenterPos());
                        BlockHighlighter.BLOCK_BOXES.add(new BlockBox(found));
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (player != null) {
                            player.sendMessage(Text
                              .empty()
                              .fillStyle(Style.EMPTY.withColor(0xFF88FF88))
                              .append("找到[")
                              .append(world.getBlockState(pos).getBlock().getName())
                              .append("]，位于(%s)。".formatted(pos.toShortString()))
                            );
                        }
                        failed = false;
                    }
                    //predicate = Predicates.alwaysFalse();
                }
            } catch (Throwable e) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.sendMessage(Text.empty().append("搜索时发生错误：" + e).formatted(Formatting.RED));
                }
                LOGGER.catching(e);
            }
        }
        if (failed) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.empty().append("未能找到方块。").fillStyle(Style.EMPTY.withColor(0xFFFF8888)));
                iterator = Collections.emptyIterator();
            }
        }
        stopped = true;
    }
    private static void onClientStopping(MinecraftClient client) {
        try {
            THREAD_POOL.close();
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }
    private static void onEndTick(ClientWorld world) {
        if (!TCConfigs.A().isBlockFinder()) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        BlockPos found = BlockFinder.found;
        if (found == null || predicate.test(new CachedBlockPosition(world, found, false))) return;
        synchronized (BlockFinder.class) {
            found = BlockFinder.found;
            if (found == null || predicate.test(new CachedBlockPosition(world, found, false))) return;
            try {
                start(world, player.getBlockPos());
            } catch (InterruptedException e) {
                player.sendMessage(Text.empty().append("开始新一轮搜索时发生错误：" + e).formatted(Formatting.RED));
                LOGGER.catching(e);
            }
        }
    }
    private static int clearSearching(CommandContext<FabricClientCommandSource> context) {
        synchronized (BlockFinder.class) {
            FabricClientCommandSource source = context.getSource();
            if (found != null) {
                clearFound();
                source.sendFeedback(Text.literal("已清除搜索结果。"));
                return 1;
            }
            source.sendFeedback(Text.literal("已中止搜索。"));
            return 1;
        }
    }
    /**
     @apiNote 需要外部同步<br>
     不需要外部检查{@link #found}
     */
    private static void clearFound() {
        BlockPos found = BlockFinder.found;
        if (found == null) return;
        TargetPointer.POSITIONS.remove(found.toCenterPos());
        BlockHighlighter.BLOCK_BOXES.remove(new BlockBox(found));
        BlockFinder.found = null;
        stopping = true;
    }
}
