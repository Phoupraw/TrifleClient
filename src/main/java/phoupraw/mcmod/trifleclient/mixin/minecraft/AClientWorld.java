package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public interface AClientWorld {
    @Accessor
    MinecraftClient getClient();
    @Invoker
    PendingUpdateManager invokeGetPendingUpdateManager();
}
