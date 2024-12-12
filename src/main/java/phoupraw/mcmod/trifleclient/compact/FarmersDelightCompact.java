package phoupraw.mcmod.trifleclient.compact;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.v0.api.AutoHarvestCallback;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
public class FarmersDelightCompact {
    public static final String MOD_ID = "farmersdelight";
    private static final Identifier TOMATO = Identifier.of(MOD_ID, "tomatoes");
    private static final Identifier RICE = Identifier.of(MOD_ID, "rice_panicles");
    private static AutoHarvestCallback findTomatoes(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        var property = Properties.AGE_3;
        if (state.get(property) == Properties.AGE_3_MAX) {
            return AutoHarvestCallback::simpleUse;
        }
        return null;
    }
    static {
        LOGGER.info("检测到《农夫乐事》，将加载相关兼容。");
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            AutoHarvestCallback.LOOKUP.registerForBlocks(FarmersDelightCompact::findTomatoes, Registries.BLOCK.get(FarmersDelightCompact.TOMATO), Registries.BLOCK.get(FarmersDelightCompact.RICE));
        });
    }
}
