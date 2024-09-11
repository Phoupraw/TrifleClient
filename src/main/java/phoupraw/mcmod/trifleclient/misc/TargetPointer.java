package phoupraw.mcmod.trifleclient.misc;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.Collection;

public class TargetPointer {
    public static final Collection<BlockPos> POSITIONS = new ObjectOpenHashSet<>();
    //public static BlockPos pos;
    static {
        POSITIONS.add(BlockPos.ORIGIN);
        HudRenderCallback.EVENT.register(TargetPointer::onHudRender);
    }
    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!MinecraftClient.isHudEnabled()) return;
        //var camera = MinecraftClient.getInstance().cameraEntity;
        //if (camera == null) {
        //    camera = MinecraftClient.getInstance().player;
        //    if (camera == null) return;
        //}
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        float tickDelta = tickCounter.getTickDelta(false);
        float yaw0 = camera.getYaw();
        float pitch0 = camera.getPitch();
        for (BlockPos pos : POSITIONS) {
            Vec3d dis = camera.getPos().subtract(pos.toCenterPos());
            double rho = dis.length();
            double pitch1 = Math.acos(dis.getY() / rho);
            double yaw1 = Math.atan2(dis.getY(), dis.getZ());
            double yaw2 = yaw0 - yaw1;
            double pitch2 = pitch0 - pitch1;
            double angle = Math.atan2(pitch2, yaw2);
            if (Double.isNaN(angle)) {
                angle = 0;
            }
            MatrixStack matrices = drawContext.getMatrices();
            int x1 = drawContext.getScaledWindowWidth() / 2;
            int x2 = x1 - 100;
            int y = drawContext.getScaledWindowHeight() / 2;
            //drawContext.drawHorizontalLine(x1, x2, y, -1);
            matrices.push();
            matrices.multiply(new Quaternionf().rotateZ((float) angle), x1 + 0.5f, y + 0.5f, 0);
            drawContext.drawHorizontalLine(x1, x2, y, -1);
            matrices.pop();
        }
    }
}
