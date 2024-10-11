package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
abstract class MEntity {
    
    @Shadow
    public abstract boolean isGlowing();
    @ModifyReturnValue(method = "isGlowing()Z", at = @At("RETURN"))
    protected boolean glow(boolean original) {
        return original;
    }
}
