package phoupraw.mcmod.trifleclient.mixins.minecraft;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.TrifleClient;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMPacketCodecDispatcher {
    static DecoderException logException(DecoderException original) {
        TrifleClient.LOGGER.error("检测到解码异常：");
        TrifleClient.LOGGER.throwing(original);
        return original;
    }
    static EncoderException logException(EncoderException original) {
        TrifleClient.LOGGER.error("检测到编码异常：");
        TrifleClient.LOGGER.throwing(original);
        return original;
    }
}
