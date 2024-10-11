package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMLivingEntity;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
abstract class MCLivingEntity {
    @ModifyReturnValue(method = "getStepHeight()F", at = @At("RETURN"))
    private float minStepHeight(float original) {
        return MMLivingEntity.minStepHeight((LivingEntity) (Object) this, original);
    }
}
