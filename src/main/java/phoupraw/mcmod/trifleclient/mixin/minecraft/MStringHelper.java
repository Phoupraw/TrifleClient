package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.util.StringHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@Mixin(StringHelper.class)
abstract class MStringHelper {
    @ModifyArg(method = "truncateChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringHelper;truncate(Ljava/lang/String;IZ)Ljava/lang/String;"))
    private static int maxLen(int maxLength) {
        return Math.max(maxLength, TCConfigs.A().getChatMaxLen());
    }
}
