package phoupraw.mcmod.trifleclient.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Texts;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.util.MCUtils;
import phoupraw.mcmod.trifleclient.util.PublicAxeItem;

@ApiStatus.NonExtendable
public abstract class LogStripper {
    private static boolean on;
    @ApiStatus.Internal
    public static void lambda_register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(TrifleClient.ID)
          .then(ClientCommandManager.literal("strip")
            .executes(LogStripper::runStartOrStop)));
    }
    @ApiStatus.Internal
    public static void onStartAndEndTick(ClientWorld world) {
        if (!on) return;
        var interactor = MinecraftClient.getInstance().interactionManager;
        if (interactor == null) return;
        var player = MinecraftClient.getInstance().player;
        if (player == null || !player.getMainHandStack().isIn(ItemTags.AXES)) {
            return;
        }
        int radius = (int) Math.ceil(player.getBlockInteractionRange() + 1.5);
        for (BlockPos mutable : BlockPos.iterateOutwards(BlockPos.ofFloored(player.getEyePos()), radius, radius, radius)) {
            if (player.canInteractWithBlockAt(mutable, 1)) {
                BlockState state = world.getBlockState(mutable);
                if (PublicAxeItem.STRIPPED_BLOCKS.containsKey(state.getBlock())) {
                    interactor.interactBlock(player, Hand.MAIN_HAND, MCUtils.getHitResult(world, mutable.toImmutable(), state, player));
                }
            }
        }
    }
    private static int runStartOrStop(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        if (on) {
            on = false;
            source.sendFeedback(Texts.bracketed(TrifleClient.name()).append("停止持续给周围原木去皮。"));
            return 1;
        } else {
            on = true;
            source.sendFeedback(Texts.bracketed(TrifleClient.name()).append("开始持续给周围原木去皮……"));
            return 0;
        }
    }
}
