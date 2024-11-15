package phoupraw.mcmod.trifleclient.mixin.minecraft;

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
    //@ModifyExpressionValue(method = "travel",at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z", opcode = Opcodes.GETFIELD))
    //private boolean elytraFreeFlying(boolean original){
    //    return original || isFallFlying();
    //}
    @WrapWithCondition(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setFlag(IZ)V"))
    private boolean elytraFreeFlying(PlayerEntity instance, int index, boolean value) {
        return MMPlayerEntity.elytraFreeFlying(instance, index, value);
    }
}
