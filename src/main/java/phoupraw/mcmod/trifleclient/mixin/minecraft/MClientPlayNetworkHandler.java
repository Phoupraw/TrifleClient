package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMClientPlayNetworkHandler;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
abstract class MClientPlayNetworkHandler extends ClientCommonNetworkHandler {
    protected MClientPlayNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }
    @WrapWithCondition(method = "onPlayerAbilities", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z"))
    private boolean elytraCancelSyncFlying(PlayerAbilities instance, boolean value, @Local(argsOnly = true) PlayerAbilitiesS2CPacket packet) {
        return MMClientPlayNetworkHandler.elytraCancelSyncFlying(instance, value, packet, client);
    }
    @WrapWithCondition(method = "onPlayerAbilities", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z"))
    private boolean elytraCancelSyncAllowFlying(PlayerAbilities instance, boolean value, @Local(argsOnly = true) PlayerAbilitiesS2CPacket packet) {
        return MMClientPlayNetworkHandler.elytraCancelSyncAllowFlying(instance, value, packet, client);
    }
    @ModifyExpressionValue(method = "onPlayerPositionLook", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/network/packet/s2c/play/PositionFlag;X_ROT:Lnet/minecraft/network/packet/s2c/play/PositionFlag;")), at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean no90sync(boolean original, @Local(argsOnly = true) PlayerPositionLookS2CPacket packet) {
        return original && !(TCConfigs.A.isFreeElytraFlying() && packet.getPitch() <= -90);
    }
}
