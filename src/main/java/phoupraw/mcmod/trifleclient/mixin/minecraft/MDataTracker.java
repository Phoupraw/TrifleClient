package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMDataTracker;

@Mixin(DataTracker.class)
abstract class MDataTracker {
    @Shadow
    @Final
    private DataTracked trackedEntity;
    @Environment(EnvType.CLIENT)
    @WrapOperation(method = "writeUpdatedEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;copyToFrom(Lnet/minecraft/entity/data/DataTracker$Entry;Lnet/minecraft/entity/data/DataTracker$SerializedEntry;)V"))
    private void freeElytraFlying(DataTracker instance, DataTracker.Entry<?> to, DataTracker.SerializedEntry<?> from, Operation<Void> original) {
        MMDataTracker.freeElytraFlying(instance, to, from, original, trackedEntity);
    }
}
