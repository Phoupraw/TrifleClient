package phoupraw.mcmod.trifleclient.mixin.modchecker;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fun.rtos.modchecker.ModChecker;
import fun.rtos.modchecker.network.listener.client.RequestPacketListener;
import net.fabricmc.loader.api.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;
import java.util.List;

@Mixin(value = RequestPacketListener.class, remap = false)
abstract class MRequestPacketListener {
    @ModifyExpressionValue(method = "onRequest", at = @At(value = "INVOKE", target = "Lnet/fabricmc/loader/api/FabricLoader;getAllMods()Ljava/util/Collection;"))
    private static Collection<ModContainer> emptyMods(Collection<ModContainer> original) {
        return List.of();
    }
    @ModifyArg(method = "onRequest", at = @At(value = "INVOKE", target = "Lfun/rtos/modchecker/network/ModsPacket;<init>(Ljava/util/List;)V"))
    private static List<String> itself(List<String> mods) {
        return List.of(ModChecker.MOD_ID);
    }
}
