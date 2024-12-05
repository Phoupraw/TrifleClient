package phoupraw.mcmod.trifleclient.mixin.minecraft.connector;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.trifleclient.events.ClientAttackEntityCallback;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
abstract class MClientPlayerInteractionManager {
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void clientAttackEntity(PlayerEntity player, Entity entity, CallbackInfo info) {
        if (Boolean.TRUE.equals(ClientAttackEntityCallback.EVENT.invoker().shouldCancel((ClientPlayerInteractionManager) (Object) this, player, entity))) {
            info.cancel();
        }
    }
}
