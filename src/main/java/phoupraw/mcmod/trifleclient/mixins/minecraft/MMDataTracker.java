package phoupraw.mcmod.trifleclient.mixins.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMDataTracker {
    @Environment(EnvType.CLIENT)
    static void freeElytraFlying(DataTracker instance, DataTracker.Entry<?> to, DataTracker.SerializedEntry<?> from, Operation<Void> original, DataTracked trackedEntity) {
        if (trackedEntity instanceof ClientPlayerEntity player && FreeElytraFlying.isFlying(player)) {
            original.call(instance, to, from);
            if (!player.isFallFlying() /*&& !player.checkFallFlying()*/) {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
                if (player.checkFallFlying()) {
                    player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                } else {
                    player.getAbilities().flying = false;
                }
            }
        } else {
            original.call(instance, to, from);
        }
    }
}
