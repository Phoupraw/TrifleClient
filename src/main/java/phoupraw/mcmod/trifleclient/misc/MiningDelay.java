package phoupraw.mcmod.trifleclient.misc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AClientPlayerInteractionManager;
import phoupraw.mcmod.trifleclient.util.MCUtils;

@UtilityClass
public class MiningDelay {
    @Getter
    @Setter
    private static boolean on = true;
    private long lastBreak = -100;
    static {
        ClientTickEvents.START_WORLD_TICK.register(MiningDelay::onStartTick);
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (isOn() && client.crosshairTarget instanceof BlockHitResult hitResult && hitResult.getType() != HitResult.Type.MISS) {
                long time = player.getWorld().getTime();
                if (time - lastBreak < 5) {
                    return true;
                }
            }
            return false;
        });
        ClientPlayerBlockBreakEvents.AFTER.register((world, player, pos, state) -> {
            lastBreak = world.getTime();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> lastBreak = -100);
    }
    @ApiStatus.Internal
    public static int removeDelay(ClientPlayerInteractionManager self, int original) {
        return !TCConfigs.A().isMiningDelay() ? original : 0;
    }
    private static void onStartTick(ClientWorld world0) {
        if (!TCConfigs.A().isMiningDelay() || !TCKeyBindings.MINING_DELAY.wasPressed()) {
            return;
        }
        while (TCKeyBindings.MINING_DELAY.wasPressed()) {
            //防止长按（？）
        }
        setOn(!isOn());
        ((AClientPlayerInteractionManager) MCUtils.getInteractor()).setBlockBreakingCooldown(0);
    }
}
