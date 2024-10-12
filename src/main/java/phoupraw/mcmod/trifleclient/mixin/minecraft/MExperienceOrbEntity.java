package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMExperienceOrbEntity;

@Environment(EnvType.CLIENT)
@Mixin(ExperienceOrbEntity.class)
abstract class MExperienceOrbEntity extends Entity {
    public MExperienceOrbEntity(EntityType<?> type, World world) {
        super(type, world);
    }
    @Intrinsic
    @Override
    public boolean isGlowing() {
        return super.isGlowing();
    }
    @SuppressWarnings("target")
    @Dynamic(mixin = MItemEntity.class)
    @ModifyReturnValue(method = "isGlowing()Z", at = @At("RETURN"))
    protected boolean glow(boolean original) {
        return MMExperienceOrbEntity.glow((ExperienceOrbEntity) (Object) this, original);
    }
    @Intrinsic
    @Override
    public int getTeamColorValue() {
        return super.getTeamColorValue();
    }
    @SuppressWarnings("target")
    @Dynamic(mixin = MItemEntity.class)
    @ModifyReturnValue(method = "getTeamColorValue()I", at = @At("RETURN"))
    private int glintColor(int original) {
        return MMExperienceOrbEntity.glintColor((ExperienceOrbEntity) (Object) this, original);
    }
}
