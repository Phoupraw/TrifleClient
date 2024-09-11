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
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Camera.Projection projection = camera.getProjection();
        Vec3d C = camera.getPos();
        Vec3d O = projection.getPosition(0, 0);
        Vec3d X = projection.getPosition(1, 0);
        Vec3d Y = projection.getPosition(0, 1);
        Vec3d OX = X.subtract(O).normalize();
        Vec3d OY = Y.subtract(O).normalize();
        Vec3d n = OX.crossProduct(OY);
        //float tickDelta = tickCounter.getTickDelta(false);
        //float yaw0 = camera.getYaw();
        //float pitch0 = camera.getPitch();
        int x1 = drawContext.getScaledWindowWidth() / 2 - 1;
        int x2 = x1 - 100;
        int y = drawContext.getScaledWindowHeight() / 2;
        //Vec3d OC = C.subtract(O);
        drawContext.drawHorizontalLine(x1, x2, y, -1);
        for (BlockPos pos : POSITIONS) {
            Vec3d P = pos.toCenterPos();
            Vec3d PC = C.subtract(P);
            Vec3d OP = P.subtract(O);
            //= -(n.x * (P.x - O.x) + n.y * (P.y - O.y) + n.z * (P.z - O.z)) / (n.x * (C.x - P.x) + n.y * (C.y - P.y) + n.z * (C.z - P.z));
            double t = -n.dotProduct(OP) / n.dotProduct(PC);
            Vec3d OQ = P.add(PC.multiply(t));
            //Vec3d Q ;//= new Vec3d(P.x + t * (C.x - P.x), P.y + t * (C.y - P.y), P.z + t * (C.z - P.z));
            //a=(Q.x-b*OX.x)
            //Q.y=(Q.x-b*OX.x)*OX.y+b*OY.y
            //Q.y=Q.x*OX.y-b*OX.x*OX.y+b*OY.y
            //Q.y=Q.x*OX.y+b*(OY.y-OX.x*OX.y)
            double b = (OQ.y - OQ.x * OX.y) / (OY.y - OX.x * OX.y);
            double a = OQ.x - b * OX.x;
            
            //Vec3d dis = O.subtract(P);
            //double rho = dis.length();
            //double pitch1 = Math.acos(dis.y / rho);
            //double yaw1 = Math.atan2(dis.y, dis.z);
            //double yaw2 = yaw0 - yaw1;
            //double pitch2 = pitch0 - pitch1;
            double angle = Math.atan2(b, -a);
            if (Double.isNaN(angle)) {
                angle = 0;
            }
            MatrixStack matrices = drawContext.getMatrices();
            matrices.push();
            matrices.multiply(new Quaternionf().rotateZ((float) angle), x1 + 0.5f, y + 0.5f, 0);
            drawContext.drawHorizontalLine(x1, x2, y, -1);
            matrices.pop();
        }
    }
}
