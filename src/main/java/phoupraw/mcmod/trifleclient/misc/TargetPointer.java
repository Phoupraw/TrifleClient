package phoupraw.mcmod.trifleclient.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Collection;

public class TargetPointer {
    public static final Collection<BlockPos> POSITIONS = new ObjectOpenHashSet<>();
    public static Matrix4f viewMatrix = new Matrix4f(), projMatrix = new Matrix4f();
    //public static BlockPos pos;
    static {
        POSITIONS.add(BlockPos.ORIGIN);
        WorldRenderEvents.LAST.register(TargetPointer::onLast);
        HudRenderCallback.EVENT.register(TargetPointer::onHudRender);
        WorldRenderEvents.END.register(TargetPointer::onEnd);
        WorldRenderEvents.BLOCK_OUTLINE.register(TargetPointer::onBlockOutline);
    }
    private static void onLast(WorldRenderContext context) {
        viewMatrix = new Matrix4f(RenderSystem.getModelViewStack());
        projMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
    }
    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!MinecraftClient.isHudEnabled() || client.isPaused()) return;
        Matrix4f viewMatrix = new Matrix4f(TargetPointer.viewMatrix);
        Matrix4f projMatrix = new Matrix4f(TargetPointer.projMatrix);
        for (BlockPos pos : POSITIONS) {
            pos = new BlockPos(1, 1, 1);
            Vector4f homoPos = new Vector4f(pos.getX(), pos.getY(), pos.getZ(), 1);
            Vector4f viewCoords = viewMatrix.transform(homoPos);
            Vector4f clipCoords = projMatrix.transform(viewCoords);
            float w = clipCoords.w();
            if (w != 0) {
                clipCoords.div(w, w, w, 1);
            }
            float screenX = (clipCoords.x + 1) / 2 * drawContext.getScaledWindowWidth();
            float screenY = (1 - clipCoords.y) / 2 * drawContext.getScaledWindowHeight();
            drawContext.drawVerticalLine((int) screenX, 0, (int) screenY, -1);
        }
        //if (!MinecraftClient.isHudEnabled() || MinecraftClient.getInstance().isPaused() ) return;
        //Matrix4f viewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
        //Matrix4f projMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
        //Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        //camera.getRotation();
        //Camera.Projection projection = camera.getProjection();
        //Vec3d C = camera.getPos();
        //Vec3d O = projection.getPosition(0, 0);
        //Vec3d X = projection.getPosition(1, 0);
        //Vec3d Y = projection.getPosition(0, 1);
        //Vec3d OX = X.subtract(O).normalize();
        //Vec3d OY = Y.subtract(O).normalize();
        //Vec3d n = OX.crossProduct(OY);
        ////float tickDelta = tickCounter.getTickDelta(false);
        ////float yaw0 = camera.getYaw();
        ////float pitch0 = camera.getPitch();
        //int x1 = drawContext.getScaledWindowWidth() / 2 - 1;
        //int x2 = x1 - 100;
        //int y = drawContext.getScaledWindowHeight() / 2;
        ////Vec3d OC = C.subtract(O);
        //drawContext.drawHorizontalLine(x1, x2, y, -1);
        //for (BlockPos pos : POSITIONS) {
        //    Vector4f homoPos = new Vector4f(pos.getX(),pos.getY(),pos.getZ(),0);
        //    Vector4f viewCoords = viewMatrix.transform(homoPos);
        //    Vector4f clipCoords = projMatrix.transform(viewCoords);
        //
        //    if (clipCoords.x()!=0) {
        //        clipCoords.div(clipCoords.w(),clipCoords.w(),clipCoords.w(),1);
        //    }
        //    float screenX = (clipCoords.x + 1)/2*drawContext.getScaledWindowWidth();
        //    float screenY = (1 - clipCoords.y)/2*drawContext.getScaledWindowHeight();
        //
        //    Vec3d P = pos.toCenterPos();
        //    Vec3d PC = C.subtract(P);
        //    Vec3d OP = P.subtract(O);
        //    //= -(n.x * (P.x - O.x) + n.y * (P.y - O.y) + n.z * (P.z - O.z)) / (n.x * (C.x - P.x) + n.y * (C.y - P.y) + n.z * (C.z - P.z));
        //    double t = -n.dotProduct(OP) / n.dotProduct(PC);
        //    Vec3d Q= P.add(PC.multiply(t));
        //    Vec3d OQ = Q.subtract(O);
        //    Matrix3x2dc A = new Matrix3x2d(OX.x,OY.x,OX.y,OY.y,OX.z,OY.z);
        //
        //    //Vec3d Q ;//= new Vec3d(P.x + t * (C.x - P.x), P.y + t * (C.y - P.y), P.z + t * (C.z - P.z));
        //    //a=(Q.x-b*OX.x)
        //    //Q.y=(Q.x-b*OX.x)*OX.y+b*OY.y
        //    //Q.y=Q.x*OX.y-b*OX.x*OX.y+b*OY.y
        //    //Q.y=Q.x*OX.y+b*(OY.y-OX.x*OX.y)
        //    double b = (OQ.y - OQ.x * OX.y) / (OY.y - OX.x * OX.y);
        //    double a = OQ.x - b * OX.x;
        //
        //    //Vec3d dis = O.subtract(P);
        //    //double rho = dis.length();
        //    //double pitch1 = Math.acos(dis.y / rho);
        //    //double yaw1 = Math.atan2(dis.y, dis.z);
        //    //double yaw2 = yaw0 - yaw1;
        //    //double pitch2 = pitch0 - pitch1;
        //    double angle = Math.atan2(screenY, screenX);
        //    if (Double.isNaN(angle)) {
        //        angle = 0;
        //    }
        //    //MatrixStack matrices = drawContext.getMatrices();
        //    //matrices.push();
        //    //matrices.multiply(new Quaternionf().rotateZ((float) angle), x1 + 0.5f, y + 0.5f, 0);
        //    //drawContext.drawHorizontalLine(x1, x2, y, -1);
        //    //matrices.pop();
        //}
    }
    private static void onEnd(WorldRenderContext context) {
    
    }
    private static boolean onBlockOutline(WorldRenderContext worldRenderContext, WorldRenderContext.BlockOutlineContext blockOutlineContext) {
        return true;
    }
}
