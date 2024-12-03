package phoupraw.mcmod.trifleclient.mixin.minecraft.elytra;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z", opcode = Opcodes.GETFIELD))
    private boolean toggleFlying(boolean original) {
        return MMClientPlayerEntity.toggleFlying((ClientPlayerEntity) (Object) this, original);
    }
    @WrapWithCondition(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendAbilitiesUpdate()V"))
    private boolean notSendStopFlyingPacket(ClientPlayerEntity instance) {
        return MMClientPlayerEntity.notSendStopFlyingPacket(instance);
    }
}
