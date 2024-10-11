package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin(ItemEntity.class)
abstract class MItemEntity extends Entity {
    public MItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }
    @Intrinsic
    @Override
    public boolean isGlowing() {
        return super.isGlowing();
    }
    //@Mixin(MItemEntity.class)
    //abstract static class MIItemEntity extends Entity {
    //    public MIItemEntity(EntityType<?> type, World world) {
    //        super(type, world);
    //    }
    //}
}
