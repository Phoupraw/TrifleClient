package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@Environment(EnvType.CLIENT)
@Mixin(DimensionType.class)
abstract class MDimensionType {
    @ModifyReturnValue(method = "ambientLight", at = @At("RETURN"))
    public float lighter(float original) {
        return Math.max(original, TCConfigs.A().getMinAmbientLight());
    }
}
