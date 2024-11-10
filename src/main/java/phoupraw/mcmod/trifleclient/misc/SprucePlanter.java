package phoupraw.mcmod.trifleclient.misc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.TrifleClient;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

@ApiStatus.NonExtendable
public interface SprucePlanter {
    Collection<BlockPos> POSITIONS = new ObjectArrayList<>();
    @ApiStatus.Internal
    static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal(TrifleClient.ID)
          .then(ClientCommandManager.literal("spruce")
            .executes(SprucePlanter::runStartOrStop)));
    }
    private static int runStartOrStop(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        ClientWorld world = source.getWorld();
        if (!POSITIONS.isEmpty()) {
            source.sendFeedback(Text.literal("停止种植云杉树苗。"));
            POSITIONS.clear();
            return 1;
        }
        if (!(MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hitResult) || hitResult.getType() == HitResult.Type.MISS) {
            source.sendError(Text.literal("准心未选中方块！"));
            return 0;
        }
        BlockPos pos = hitResult.getBlockPos().up();
        fail:
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos pos2 = pos.offset(direction);
            Direction direction2 = direction.rotateYClockwise();
            List<BlockPos> posList = Arrays.asList(pos, pos2, pos.offset(direction2), pos2.offset(direction2));
            for (BlockPos pos1 : posList) {
                if (!world.getBlockState(pos1).isOf(Blocks.PODZOL)) {
                    continue fail;
                }
            }
            POSITIONS.clear();
            POSITIONS.addAll(posList);
            var sb = new StringJoiner(", ", "[", "]");
            for (BlockPos blockPos : posList) {
                sb.add("(" + blockPos.toShortString() + ")");
            }
            source.sendFeedback(Text.literal("在%s上持续种植云杉树苗……".formatted(sb)));
            return 1;
        }
        source.sendError(Text.literal("在%s找不到2×2灰化土！".formatted("[" + pos.toShortString() + "]")));
        return 0;
    }
    @ApiStatus.Internal
    static void onStartAndEndTick(ClientWorld world) {
        if (POSITIONS.isEmpty()) return;
        var interactor = MinecraftClient.getInstance().interactionManager;
        if (interactor == null) return;
        var player = MinecraftClient.getInstance().player;
        if (player == null || !player.getMainHandStack().isOf(Items.SPRUCE_SAPLING)) {
            return;
        }
        for (BlockPos pos : POSITIONS) {
            if (!world.getBlockState(pos).isOf(Blocks.PODZOL)) {
                return;
            }
            BlockPos up = pos.up();
            BlockState stateUp = world.getBlockState(up);
            if (!(stateUp.isAir() || stateUp.isOf(Blocks.SPRUCE_SAPLING))) {
                return;
            }
        }
        for (BlockPos pos : POSITIONS) {
            interactor.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos, 1), Direction.UP, pos, false));
        }
    }
}
