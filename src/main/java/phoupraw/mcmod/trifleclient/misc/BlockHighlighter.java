package phoupraw.mcmod.trifleclient.misc;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;

import java.util.OptionalDouble;
import java.util.Set;

@ApiStatus.NonExtendable
public interface BlockHighlighter {
    Set<BlockBox> BLOCK_BOXES = new ObjectOpenHashSet<>();
    /**
     <a href=https://github.com/Earthcomputer/clientcommands/blob/fabric/src/main/java/net/earthcomputer/clientcommands/render/RenderQueue.java>Client Commands</a>
     */
    RenderLayer NO_DEPTH_LAYER = RenderLayer.of("clientcommands_no_depth", VertexFormats.LINES, VertexFormat.DrawMode.LINES, 256, true, true, RenderLayer.MultiPhaseParameters.builder()
      .program(RenderPhase.LINES_PROGRAM)
      .writeMaskState(RenderPhase.COLOR_MASK)
      .cull(RenderPhase.DISABLE_CULLING)
      .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
      .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
      .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(2)))
      .build(true));
    @ApiStatus.Internal
    static void afterEntities(WorldRenderContext context) {
        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;
        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;
        VertexConsumer buffer = consumers.getBuffer(NO_DEPTH_LAYER);
        if (buffer == null) return;
        Vec3d offset = context.camera().getPos().multiply(-1);
        for (BlockBox blockBox : BLOCK_BOXES) {
            Box box = Box.from(blockBox);
            WorldRenderer.drawBox(matrices, buffer, box.offset(offset), 1, 1, 1, 1);
        }
    }
}
