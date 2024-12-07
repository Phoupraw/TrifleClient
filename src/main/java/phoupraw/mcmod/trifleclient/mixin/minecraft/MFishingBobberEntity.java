package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FishingBobberEntity.class)
abstract class MFishingBobberEntity extends ProjectileEntity {
    public MFishingBobberEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
}
