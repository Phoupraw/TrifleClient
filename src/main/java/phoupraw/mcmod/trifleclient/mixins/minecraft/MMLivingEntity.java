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
        if (original && self instanceof ClientPlayerEntity player && FreeElytraFlying.canFly(player)) {
            /*player.getAbilities().allowFlying =*/
            player.getAbilities().flying = true;//FIXME 加上这句无法在空中取消飞行；去掉这句在空中取消飞行容易摔死。
            //player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(player.getYaw(),90,false));
            return false;
        }
        return original;
    }
}
