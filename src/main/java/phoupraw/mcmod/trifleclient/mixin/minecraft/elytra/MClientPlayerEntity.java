package phoupraw.mcmod.trifleclient.mixin.minecraft.elytra;

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
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    //@ModifyExpressionValue(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z", opcode = Opcodes.GETFIELD))
    //private boolean toggleFlying(boolean original) {
    //    return MMClientPlayerEntity.toggleFlying((ClientPlayerEntity) (Object) this, original);
    //}
    //@WrapWithCondition(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendAbilitiesUpdate()V"))
    //private boolean notSendStopFlyingPacket(ClientPlayerEntity instance) {
    //    return MMClientPlayerEntity.notSendStopFlyingPacket(instance);
    //}
}
