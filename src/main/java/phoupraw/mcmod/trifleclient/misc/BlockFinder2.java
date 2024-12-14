package phoupraw.mcmod.trifleclient.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.v0.api.BlockArgumentType;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
public class BlockFinder2 {
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(1);
    private volatile static @NotNull Iterator<BlockPos> iterator = Collections.emptyIterator();
    private volatile static @NotNull LootCondition condition = AnyOfLootCondition.builder().build();
    private volatile static @Nullable BlockPos found;
    static {
        ClientCommandRegistrationCallback.EVENT.register(BlockFinder2::register);
        ClientTickEvents.END_WORLD_TICK.register(BlockFinder2::onEndTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(BlockFinder2::onClientStopping);
    }
    private static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(TrifleClient.ID)
          .then(ClientCommandManager.literal("find")
            .then(ClientCommandManager.literal("blocks")
              .requires(source -> TCConfigs.A().isBlockFinder())
              .executes(BlockFinder2::clearSearching)
              .then(ClientCommandManager.argument("condition", new BlockArgumentType(registryAccess))
                .executes(BlockFinder2::setSearching)
              )
            )
          )
        );
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
        BlockPos found = BlockFinder2.found;
        if (found == null || BlockArgumentType.test(condition, world, found)) return;
        synchronized (BlockFinder.class) {
            found = BlockFinder2.found;
            if (found == null || BlockArgumentType.test(condition, world, found)) return;
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
    private static int setSearching(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        try {
            synchronized (BlockFinder2.class) {
                condition = context.getArgument("condition", LootCondition.class);
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
    private static int clearSearching(CommandContext<FabricClientCommandSource> context) {
        synchronized (BlockFinder2.class) {
            FabricClientCommandSource source = context.getSource();
            if (found != null) {
                clearFound();
                source.sendFeedback(Text.literal("已清除搜索结果。"));
                return 1;
            }
            THREAD_POOL.shutdownNow();
            source.sendFeedback(Text.literal("已中止搜索。"));
            return 1;
        }
    }
    /**
     @apiNote 需要外部同步<br>
     不需要外部检查{@link #found}
     */
    private static void clearFound() {
        BlockPos found = BlockFinder2.found;
        if (found == null) return;
        TargetPointer.POSITIONS.remove(found.toCenterPos());
        BlockHighlighter.BLOCK_BOXES.remove(new BlockBox(found));
        BlockFinder2.found = null;
    }
    /**
     需要外部同步
     */
    private static void start(BlockPos origin) throws InterruptedException {
        clearFound();
        int range = (MinecraftClient.getInstance().options.getViewDistance().getValue() * 2 + 1) * 16 / 2;
        iterator = BlockPos.iterateOutwards(origin, range, range, range).iterator();
        THREAD_POOL.execute(BlockFinder2::run);
    }
    private static void run() {
        boolean failed = true;
        while (iterator.hasNext()) {
            try {
                ClientWorld world = MinecraftClient.getInstance().world;
                if (world == null) break;
                BlockPos pos = iterator.next();
                if (BlockArgumentType.test(condition, world, pos)) {
                    synchronized (BlockFinder2.class) {
                        BlockPos found = pos.toImmutable();
                        BlockFinder2.found = found;
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
            }
        }
    }
}
