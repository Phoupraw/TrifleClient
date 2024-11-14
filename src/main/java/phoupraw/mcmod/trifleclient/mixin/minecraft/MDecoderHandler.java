package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.handler.DecoderHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@Mixin(DecoderHandler.class)
abstract class MDecoderHandler {
    @Inject(method = "decode", at = @At(value = "INVOKE", target = "Ljava/io/IOException;<init>(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void printCustomPayloadId(CallbackInfo ci, @Local Packet<?> packet) {
        if (TCConfigs.A.isDetailPacketError() && packet instanceof CustomPayloadS2CPacket customPacket) {
            CustomPayload payload = customPacket.payload();
            TrifleClient.LOGGER.error("CustomPayload.Id: " + payload.getId());
            TrifleClient.LOGGER.error("CustomPayload: " + payload);
        }
    }
}
