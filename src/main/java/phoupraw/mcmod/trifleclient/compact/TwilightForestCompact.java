package phoupraw.mcmod.trifleclient.compact;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.v0.api.AutoHarvestCallback;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
public class TwilightForestCompact {
    public static final String MOD_ID = "twilightforest";
    public static final Identifier TORCH_BERRY = Identifier.of(MOD_ID, "torchberry_plant");
    public static AutoHarvestCallback findTorchBerry(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        for (var entry : state.getEntries().entrySet()) {
            if (entry.getKey().getName().equals("has_torchberries")&&(Boolean) entry.getValue()) {
                return AutoHarvestCallback::simpleUse;
            }
        }
        return null;
    }
    public static void init() {
        LOGGER.info("检测到《暮色森林》，将加载相关兼容。");
        AutoHarvestCallback.LOOKUP.registerForBlocks(TwilightForestCompact::findTorchBerry, Registries.BLOCK.get(TwilightForestCompact.TORCH_BERRY));
    }
}
