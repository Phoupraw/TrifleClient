package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.entity.EntityLike;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMEntity;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
abstract class MEntity implements EntityLike {
    @WrapWithCondition(method = "onTrackedDataSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;calculateDimensions()V"))
    private boolean cancelPoseSync(Entity instance) {
        return MMEntity.cancelPoseSync(instance);
    }
    @WrapOperation(method = "setPos", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;blockPos:Lnet/minecraft/util/math/BlockPos;", opcode = Opcodes.PUTFIELD))
    private void onBlockPosSet(Entity instance, BlockPos value, Operation<Void> original) {
        BlockPos prevPos = instance.getBlockPos();
        original.call(instance, value);
        MMEntity.onBlockPosSet(instance, prevPos);
    }
}
