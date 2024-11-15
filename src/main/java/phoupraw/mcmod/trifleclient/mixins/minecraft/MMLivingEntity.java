package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMLivingEntity {
    static float moveLikeOnLand(LivingEntity self, float speed, float speed06) {
        //if (self instanceof ClientPlayerEntity) {
        //    return speed06;
        //}
        return speed;
    }
    /**
     为了避免与 eclipse's tweakeroo冲突，在这里修改
     */
    @Environment(EnvType.CLIENT)
    static float minStepHeight(LivingEntity self, float original) {
        return self instanceof ClientPlayerEntity ? MMClientPlayerEntity.minStepHeight(original) : original;
    }
    @Environment(EnvType.CLIENT)
    static boolean freeElytraFly(LivingEntity self, boolean original) {
        if (original && self instanceof ClientPlayerEntity player && FreeElytraFlying.isFlying(player)) {
            /*player.getAbilities().allowFlying =*/
            player.getAbilities().flying = true;
            //player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(player.getYaw(),90,false));
            return false;
        }
        return original;
    }
}
