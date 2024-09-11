package phoupraw.mcmod.trifleclient.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.Set;

/**
 @apiNote {@link #POSITIONS} */
public class TargetPointer {
    public static final Set<Vec3d> POSITIONS = new ObjectOpenHashSet<>();
    private static Matrix4f viewMatrix = new Matrix4f(), projMatrix = new Matrix4f();
    static {
        //POSITIONS.add(BlockPos.ORIGIN);
        WorldRenderEvents.LAST.register(TargetPointer::onLast);
        HudRenderCallback.EVENT.register(TargetPointer::onHudRender);
        //WorldRenderEvents.END.register(TargetPointer::onEnd);
        //WorldRenderEvents.BLOCK_OUTLINE.register(TargetPointer::onBlockOutline);
    }
    private static void onLast(WorldRenderContext context) {
        viewMatrix = new Matrix4f(RenderSystem.getModelViewStack());
        projMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
    }
    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!MinecraftClient.isHudEnabled()/* || client.isPaused()*/) return;
        Matrix4f viewMatrix = new Matrix4f(TargetPointer.viewMatrix);
        Matrix4f projMatrix = new Matrix4f(TargetPointer.projMatrix);
        MatrixStack matrices = drawContext.getMatrices();
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        float pitch = camera.getPitch();
        float yaw = camera.getYaw();
        Vec3d rotVec = Vec3d.fromPolar(pitch, yaw);
        int windowWidth = drawContext.getScaledWindowWidth();
        int windowHeight = drawContext.getScaledWindowHeight();
        float centerX = windowWidth / 2f;
        float centerY = windowHeight / 2f;
        int dx2 = 30;
        for (var blockPos : POSITIONS) {
            //pos = new BlockPos(1, 1, 1);
            Vec3d pos = blockPos.subtract(cameraPos);
            Vector4f homoPos = new Vector4f((float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), 1);
            Vector4f viewCoords = viewMatrix.transform(homoPos);
            Vector4f clipCoords = projMatrix.transform(viewCoords);
            float w = clipCoords.w();
            if (w != 0) {
                clipCoords.div(w, w, w, 1);
            }
            float screenX = (clipCoords.x + 1) / 2 * windowWidth;
            float screenY = (1 - clipCoords.y) / 2 * windowHeight;
            //drawContext.drawVerticalLine((int) screenX, 0, (int) screenY, -1);
            float y = screenY - centerY;
            float x = screenX - centerX;
            double angle = Math.atan2(y, x);
            double dotted = rotVec.dotProduct(pos);
            if (dotted < 0) {
                angle += Math.PI;
            } else if (x * x + y * y < dx2 * dx2) {
                continue;
            }
            matrices.push();
            matrices.multiply(new Quaternionf().rotateZ((float) angle), centerX, centerY, 0);
            drawContext.drawHorizontalLine((int) centerX + 10, (int) (centerX + dx2), (int) centerY - 1, -1);
            matrices.pop();
        }
    }
    //private static void onEnd(WorldRenderContext context) {
    //
    //}
    //private static boolean onBlockOutline(WorldRenderContext worldRenderContext, WorldRenderContext.BlockOutlineContext blockOutlineContext) {
    //    return true;
    //}
}
