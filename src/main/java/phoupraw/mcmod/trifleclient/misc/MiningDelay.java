package phoupraw.mcmod.trifleclient.misc;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AClientPlayerInteractionManager;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AClientWorld;

@UtilityClass
public class MiningDelay {
    @Setter
    private static boolean on = true;
    static {
        ClientTickEvents.START_WORLD_TICK.register(MiningDelay::onStartTick);
    }
    private static void onStartTick(ClientWorld world0) {
        if (!TCConfigs.A.isMiningDelay() || !TCKeyBindings.MINING_DELAY.wasPressed()) {
            return;
        }
        while (TCKeyBindings.MINING_DELAY.wasPressed()) {
            //防止长按（？）
        }
        var world = (ClientWorld & AClientWorld) world0;
        setOn(!isOn());
        var interactor = (ClientPlayerInteractionManager & AClientPlayerInteractionManager) world.getClient().interactionManager;
        //PlayerEntity player = MinecraftClient.getInstance().player;
        if (interactor == null /*|| player == null*/) {
            return;
        }
        if (isOn()) {
            interactor.setBlockBreakingCooldown(5);
        } else {
            interactor.setBlockBreakingCooldown(0);
        }
    }
    @ApiStatus.Internal
    public static int removeDelay(ClientPlayerInteractionManager self, int original) {
        return isOn() || !TCConfigs.A.isMiningDelay() ? original : 0;
    }
    public static void setDelay(ClientPlayerInteractionManager self, BlockState blockState, BlockPos blockPos, Direction direction, int sequence) {
        if (MiningDelay.isOn() && TCConfigs.A.isMiningDelay()) {
            var interactor = (ClientPlayerInteractionManager & AClientPlayerInteractionManager) self;
            interactor.setBlockBreakingCooldown(5);
        }
    }
    public static boolean isOn() {
        return on;
    }
}
