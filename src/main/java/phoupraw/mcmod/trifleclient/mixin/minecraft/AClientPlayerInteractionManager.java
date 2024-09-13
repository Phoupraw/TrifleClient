package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public interface AClientPlayerInteractionManager {
    @Accessor
    int getBlockBreakingCooldown();
    @Accessor
    void setBlockBreakingCooldown(int value);
}
