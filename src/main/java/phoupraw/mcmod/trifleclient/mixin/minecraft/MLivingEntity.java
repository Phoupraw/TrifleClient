package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
abstract class MLivingEntity {
    @Shadow
    protected abstract float getMovementSpeed(float slipperiness);
    //@ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"))
    //private float moveLikeOnLand(float speed) {
    //    return MMLivingEntity.moveLikeOnLand((LivingEntity) (Object) this, speed, getMovementSpeed(0.6f));
    //}
}
