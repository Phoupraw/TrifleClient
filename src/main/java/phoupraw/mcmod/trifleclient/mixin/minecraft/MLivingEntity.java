package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMLivingEntity;

@Mixin(LivingEntity.class)
abstract class MLivingEntity extends Entity {
    public MLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }
    @Environment(EnvType.CLIENT)
    @ModifyReturnValue(method = "getStepHeight()F", at = @At("RETURN"))
    private float minStepHeight(float original) {
        return MMLivingEntity.minStepHeight((LivingEntity) (Object) this, original);
    }
}
