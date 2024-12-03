package phoupraw.mcmod.trifleclient.misc;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.xpple.clientarguments.arguments.CBlockPredicateArgument;
import lombok.experimental.UtilityClass;
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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
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
              .requires(source -> TCConfigs.A().isBlockFinder())
              .executes(BlockFinder::clearSearching)
              .then(ClientCommandManager.argument("block", CBlockPredicateArgument.blockPredicate(registryAccess))
                .executes(BlockFinder::setSearching)))));
    }
    private static int setSearching(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        try {
            
            synchronized (BlockFinder.class) {
                //iterator = BlockPos.iterateOutwards(source.getPlayer().getBlockPos(), range, range, range).iterator();
                predicate = CBlockPredicateArgument.getBlockPredicate(context, "block");
                source.sendFeedback(Text.of("开始搜索……"));
                start(source.getPlayer().getBlockPos());
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
    private static void start(BlockPos origin) throws InterruptedException {
        clearFound();
        if (thread != null) {
            thread.interrupt();
        }
        int range = (MinecraftClient.getInstance().options.getViewDistance().getValue() * 2 + 1) * 16 / 2;
        iterator = BlockPos.iterateOutwards(origin, range, range, range).iterator();
        thread = new Thread(BlockFinder::run);
        thread.start();
        thread.join(1);
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
                        BlockPos found = pos.toImmutable();
                        BlockFinder.found = found;
                        iterator = Collections.emptyIterator();
                        TargetPointer.POSITIONS.add(pos.toCenterPos());
                        BlockHighlighter.BLOCK_BOXES.add(new BlockBox(found));
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
                LOGGER.catching(e);
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
        try {
            if (thread != null) {
                thread.interrupt();
            }
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }
    private static void onEndTick(ClientWorld world) {
        if (!TCConfigs.A().isBlockFinder()) return;
        BlockPos found = BlockFinder.found;
        if (found == null || predicate.test(new CachedBlockPosition(world, found, false))) return;
        synchronized (BlockFinder.class) {
            found = BlockFinder.found;
            if (found == null || predicate.test(new CachedBlockPosition(world, found, false))) return;
            try {
                start(found);
            } catch (InterruptedException e) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.sendMessage(Text.empty().append("开始新一轮搜索时发生错误：" + e).formatted(Formatting.RED));
                }
                LOGGER.catching(e);
            }
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
                clearFound();
                source.sendFeedback(Text.literal("已清除搜索结果。"));
                return 1;
            } else {
                source.sendError(Text.literal("没有搜索结果或进行中的搜索！"));
                return 0;
            }
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
    }
}
