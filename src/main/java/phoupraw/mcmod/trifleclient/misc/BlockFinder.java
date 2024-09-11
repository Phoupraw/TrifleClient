package phoupraw.mcmod.trifleclient.misc;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPredicateArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.TrifleClient;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

public class BlockFinder {
    //private static final Set<BlockPos> iterated = new ObjectOpenHashSet<>();
    private volatile static @NotNull Iterator<BlockPos> iterator = Collections.emptyIterator();
    private volatile static @NotNull Predicate<CachedBlockPosition> predicate = Predicates.alwaysFalse();
    private volatile static @Nullable BlockPos found;
    private static Thread thread;
    static {
        ClientCommandRegistrationCallback.EVENT.register(BlockFinder::register);
        ClientTickEvents.END_WORLD_TICK.register(BlockFinder::onEndTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(BlockFinder::onClientStopping);
        //thread.start();
    }
    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(TrifleClient.ID)
          .then(ClientCommandManager.literal("find")
            .then(ClientCommandManager.literal("block")
              .executes(BlockFinder::clearSearching)
              .then(ClientCommandManager.argument("block", CBlockPredicateArgument.blockPredicate(registryAccess))
                .executes(BlockFinder::setSearching)))));
    }
    private static int setSearching(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        try {
            int range = (MinecraftClient.getInstance().options.getViewDistance().getValue() * 2 + 1) * 16 / 2;
            synchronized (BlockFinder.class) {
                iterator = BlockPos.iterateOutwards(source.getPlayer().getBlockPos(), range, range, range).iterator();
                predicate = CBlockPredicateArgument.getBlockPredicate(context, "block");
                if (found != null) {
                    TargetPointer.POSITIONS.remove(found.toCenterPos());
                }
                found = null;
                if (thread != null) {
                    thread.interrupt();
                }
                thread = new Thread(BlockFinder::run);
                source.sendFeedback(Text.of("开始搜索……"));
                thread.start();
                thread.join(1);
                return 1;
            }
        } catch (Throwable e) {
            source.sendError(Text.of("开始搜索时发生错误：" + e));
            TrifleClient.LOGGER.throwing(e);
            return 0;
        }
    }
    private static void run() {
        boolean failed = true;
        while (iterator.hasNext()) {
            try {
                ClientWorld world = MinecraftClient.getInstance().world;
                if (world == null) break;
                BlockPos pos = iterator.next();
                if (predicate.test(new CachedBlockPosition(world, pos, false))) {
                    synchronized (BlockFinder.class) {
                        found = pos.toImmutable();
                        iterator = Collections.emptyIterator();
                        TargetPointer.POSITIONS.add(pos.toCenterPos());
                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                        if (player != null) {
                            player.sendMessage(Text.empty().append("找到方块，位于(%d,%d,%d)。".formatted(pos.getX(), pos.getY(), pos.getZ())).fillStyle(Style.EMPTY.withColor(0xFF88FF88)));
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
                TrifleClient.LOGGER.throwing(e);
            }
        }
        if (failed) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.empty().append("未能找到方块。").fillStyle(Style.EMPTY.withColor(0xFFFF8888)));
            }
        }
    }
    private static void onClientStopping(MinecraftClient client) {
        thread.interrupt();
    }
    private static void onEndTick(ClientWorld world) {
        BlockPos found = BlockFinder.found;
        if (found == null || predicate.test(new CachedBlockPosition(world, BlockFinder.found, false))) return;
        synchronized (BlockFinder.class) {
            found = BlockFinder.found;
            if (found == null || predicate.test(new CachedBlockPosition(world, found, false))) return;
            TargetPointer.POSITIONS.remove(found.toCenterPos());
            BlockFinder.found = null;
        }
    }
    private static int clearSearching(CommandContext<FabricClientCommandSource> context) {
        synchronized (BlockFinder.class) {
            FabricClientCommandSource source = context.getSource();
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
                source.sendFeedback(Text.literal("已中止搜索。"));
                return 1;
            } else if (found != null) {
                found = null;
                source.sendFeedback(Text.literal("已清除搜索结果。"));
                return 1;
            } else {
                source.sendError(Text.literal("没有可执行的操作！"));
                return 0;
            }
        }
    }
}
