package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.handler.PacketCodecDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMPacketCodecDispatcher;

@Mixin(PacketCodecDispatcher.class)
abstract class MPacketCodecDispatcher {
    @ModifyExpressionValue(method = "decode(Lio/netty/buffer/ByteBuf;)Ljava/lang/Object;", at = {
      @At(value = "NEW", target = "(Ljava/lang/String;)Lio/netty/handler/codec/DecoderException;"),
      @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/Throwable;)Lio/netty/handler/codec/DecoderException;")
    })
    private DecoderException logException(DecoderException original) {
        return MMPacketCodecDispatcher.logException(original);
    }
    @ModifyExpressionValue(method = "encode(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V", at = {
      @At(value = "NEW", target = "(Ljava/lang/String;)Lio/netty/handler/codec/EncoderException;"),
      @At(value = "NEW", target = "(Ljava/lang/String;Ljava/lang/Throwable;)Lio/netty/handler/codec/EncoderException;")
    })
    private EncoderException logException(EncoderException original) {
        return MMPacketCodecDispatcher.logException(original);
    }
}
