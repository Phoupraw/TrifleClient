package phoupraw.mcmod.trifleclient.misc;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.OptionalDouble;
import java.util.Set;

@UtilityClass
public class BlockHighlighter {
    /**
     <a href=https://github.com/Earthcomputer/clientcommands/blob/fabric/src/main/java/net/earthcomputer/clientcommands/render/RenderQueue.java>Client Commands</a>
     */
    public static RenderLayer NO_DEPTH_LAYER = RenderLayer.of("clientcommands_no_depth", VertexFormats.LINES, VertexFormat.DrawMode.LINES, 256, true, true, RenderLayer.MultiPhaseParameters.builder()
      .program(RenderPhase.LINES_PROGRAM)
      .writeMaskState(RenderPhase.COLOR_MASK)
      .cull(RenderPhase.DISABLE_CULLING)
      .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
      .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
      .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2)))
      .build(true));
    public static final Set<BlockBox> BLOCK_BOXES = new ObjectOpenHashSet<>();
    static {
        WorldRenderEvents.AFTER_ENTITIES.register(BlockHighlighter::afterEntities);
    }
    private static void afterEntities(WorldRenderContext context) {
        MatrixStack matrices = context.matrixStack();
        VertexConsumer buffer = context.consumers().getBuffer(NO_DEPTH_LAYER);
        Vec3d offset = context.camera().getPos().multiply(-1);
        //RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        for (BlockBox blockBox : BLOCK_BOXES) {
            Box box = Box.from(blockBox);
            WorldRenderer.drawBox(matrices, buffer, box.offset(offset), 1, 1, 1, 1);
            //WorldRenderer.drawBox(matrices, buffer, box.contract(1/128.0).offset(offset), 0, 0, 0, 1);
        }
    }
}
