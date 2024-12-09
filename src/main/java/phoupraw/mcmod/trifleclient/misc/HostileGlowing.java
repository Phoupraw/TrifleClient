package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@UtilityClass
public class HostileGlowing {
    @Contract(pure = true)
    public static boolean isNearHostile(Entity entity) {
        double range0 = TCConfigs.A().getHostileGlowRange();
        if (range0 > 0) {
            var player = MinecraftClient.getInstance().player;
            if (player != null && entity instanceof Monster) {
                double range = range0 + Math.max(entity.getWidth(), entity.getHeight());
                if (entity.squaredDistanceTo(player) < range * range) {
                    return true;
                }
            }
        }
        return false;
    }
    @ApiStatus.Internal
    public static Boolean shouldGlow(Entity entity) {
        return isNearHostile(entity) ? true : null;
    }
    @ApiStatus.Internal
    public static int getColor(Entity entity, @Range(from = 0, to = 0xFFFFFF) int original, RenderTickCounter tickCounter, Camera camera) {
        if (isNearHostile(entity)) {
            int period = 20;
            float delta = (entity.getWorld().getTime() % period + tickCounter.getTickDelta(false)) / period;
            delta = Math.abs(delta - 0.5f);
            //delta = delta*delta;
            float r = MathHelper.lerp(delta, 0.8f, 0f);
            //float gb = MathHelper.lerp(delta,0f,1f);
            return ~MathHelper.packRgb(r, 0, 0);
        }
        return original;
    }
}
