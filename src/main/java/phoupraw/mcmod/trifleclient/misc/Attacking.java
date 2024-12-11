package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
@UtilityClass
public class Attacking {
    private boolean force;
    private long lastClick = Long.MIN_VALUE / 2;
    static {
        ClientPreAttackCallback.EVENT.register((client, player, clickCount) -> {
            if (clickCount != 0 && Screen.hasShiftDown()) {
                long time = player.getWorld().getTime();
                if (time - lastClick <= 5) {
                    lastClick = Long.MIN_VALUE / 2;
                    force ^= true;
                    return true;
                }
                lastClick = time;
            }
            return false;
        });
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            if (!force) return;
            var world = MinecraftClient.getInstance().world;
            if (world == null) return;
            MatrixStack matrices = drawContext.getMatrices();
            matrices.push();
            matrices.translate(drawContext.getScaledWindowWidth() / 2f, drawContext.getScaledWindowHeight() / 2f, 0);
            int t = 20;
            matrices.multiply(new Quaternionf().rotateZ((world.getTime() % t + renderTickCounter.getTickDelta(false)) / t * MathHelper.PI * 2));
            int l = 10;
            drawContext.drawSprite(l, -2 * l, 0, l, l, MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(StatusEffects.STRENGTH));
            matrices.pop();
        });
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!(force)) return;
            var player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (!Boolean.TRUE.equals(AutoAttacker.WEAPON.invoker().apply(new AutoAttacker.ItemContext(player.getMainHandStack(), player)))) {
                return;
            }
            AutoAttacker.attack(player);
        });
    }
}
