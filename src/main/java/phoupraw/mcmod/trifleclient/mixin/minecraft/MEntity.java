package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMEntity;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
abstract class MEntity {
    @WrapWithCondition(method = "onTrackedDataSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;calculateDimensions()V"))
    private boolean cancelPoseSync(Entity instance) {
        return MMEntity.cancelPoseSync(instance);
    }
}
