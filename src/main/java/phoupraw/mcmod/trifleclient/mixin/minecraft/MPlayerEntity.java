package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMPlayerEntity;

@Mixin(PlayerEntity.class)
abstract class MPlayerEntity extends LivingEntity {
    protected MPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    @WrapWithCondition(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setFlag(IZ)V"))
    private boolean elytraFreeFlying(PlayerEntity instance, int index, boolean value) {
        return MMPlayerEntity.elytraFreeFlying(instance, index, value);
    }
    @ModifyExpressionValue(method = {"tickMovement", "getMovementSpeed"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/registry/entry/RegistryEntry;)D"))
    private double limitSpeed(double original) {
        return MMPlayerEntity.limitSpeed((PlayerEntity) (Object) this, original);
    }
}
