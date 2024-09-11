package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    //@Shadow
    //protected abstract void sendMovementPackets();
    //@Shadow
    //@Final
    //protected MinecraftClient client;
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    //@Inject(method = "move", at = @At(value = "HEAD"))
    //private void storePrevStates(MovementType movementType, Vec3d movement, CallbackInfo ci, @Share(namespace = TrifleClient.ID, value = "prevOnGround") LocalBooleanRef prevOnGround) {
    //    MMClientPlayerEntity.storePrevStates((ClientPlayerEntity) (Object) this, movementType, movement, client, prevOnGround);
    //}
    //@Inject(method = "move", at = @At(value = "RETURN"))
    //private void stepDown(MovementType movementType, Vec3d movement, CallbackInfo ci, @Share(namespace = TrifleClient.ID, value = "prevOnGround") LocalBooleanRef prevOnGround) {
    //    MMClientPlayerEntity.stepDown((ClientPlayerEntity) (Object) this, movementType, movement, client, prevOnGround, this::sendMovementPackets);
    //}
}
