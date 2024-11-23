package phoupraw.mcmod.trifleclient.mixins.minecraft;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.jetbrains.annotations.ApiStatus;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMPacketCodecDispatcher {
    static DecoderException logException(DecoderException original) {
        LOGGER.error("检测到解码异常：");
        return LOGGER.throwing(original);
    }
    static EncoderException logException(EncoderException original) {
        LOGGER.error("检测到编码异常：");
        return LOGGER.throwing(original);
    }
}
