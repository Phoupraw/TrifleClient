package phoupraw.mcmod.trifleclient.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@ApiStatus.NonExtendable
public interface ItemGlowing {
    static boolean isInRange(Entity self) {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        double range = TCConfigs.A.getItemGlowingRange() * client.options.getEntityDistanceScaling().getValue();
        return self.squaredDistanceTo(camera.getPos()) <= range * range;
    }
    static boolean glow(Entity self, boolean original) {
        return original || isInRange(self);
    }
    static int glintColor(Entity self, int original, boolean halfWave) {
        if (!isInRange(self)) return original;
        long time = self.getWorld().getTime();
        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        int period = 20;
        float delta = ((halfWave ? -1 : 1) * MathHelper.sin((time % period + tickDelta) / period * MathHelper.PI * 2) + 1) / 2;
        return MathHelper.hsvToRgb(1 / 9f, delta, (float) 1);
    }
}
