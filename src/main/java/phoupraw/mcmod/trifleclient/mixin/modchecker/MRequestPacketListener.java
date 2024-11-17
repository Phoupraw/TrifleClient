package phoupraw.mcmod.trifleclient.mixin.modchecker;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fun.rtos.modchecker.network.client.RequestPacketListener;
import net.fabricmc.loader.api.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.List;

@Mixin(value = RequestPacketListener.class, remap = false)
abstract class MRequestPacketListener {
    @ModifyExpressionValue(method = "onRequest", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/api/FabricLoader;getAllMods()Ljava/util/Collection;"))
    private static Collection<ModContainer> emptyMods(Collection<ModContainer> original) {
        return List.of();
    }
}
