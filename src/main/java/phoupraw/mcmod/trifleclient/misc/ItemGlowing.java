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
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double range = TCConfigs.A.getItemGlowingRange();
        return self.squaredDistanceTo(camera.getPos()) <= range * range;
    }
    static boolean glow(Entity self, boolean original) {
        if (original) return true;
        return isInRange(self);
    }
    static int glintColor(Entity self, int original, boolean halfWave) {
        if (!isInRange(self)) {
            return original;
        }
        long time = self.getWorld().getTime();
        float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        //float[] goldHSV = Color.RGBtoHSB(0xFF,0xAA,0,null);
        float delta = ((halfWave ? -1 : 1) * MathHelper.sin((time + tickDelta) / 4) + 1) / 2;
        return MathHelper.hsvToRgb(1 / 9f, delta, (float) 1);
    }
}
