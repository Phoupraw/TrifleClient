package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.misc.HostileGlowing;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderer.class)
abstract class MEntityRenderer {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;hasLabel(Lnet/minecraft/entity/Entity;)Z"))
    private boolean forceLabel(boolean original, @Local(argsOnly = true) Entity entity) {
        return original || HostileGlowing.isNearHostile(entity);
    }
    //@WrapWithCondition(method = "renderLabelIfPresent",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
    //private boolean labelGlow(TextRenderer instance, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light,@Local(argsOnly = true) Entity entity) {
    //    if (TrifleClient.isNearHostile(entity)) {
    //        instance.drawWithOutline(text.asOrderedText(),x,y,color,-1,matrix,vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE);
    //        return false;
    //    }
    //    return true;
    //}
}
