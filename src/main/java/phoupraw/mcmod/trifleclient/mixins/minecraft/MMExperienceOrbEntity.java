package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.ExperienceOrbEntity;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.ItemGlowing;

@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MMExperienceOrbEntity {
    static boolean glow(ExperienceOrbEntity self, boolean original) {
        return ItemGlowing.glow(self, original);
    }
    static int glintColor(ExperienceOrbEntity self, int original) {
        return ItemGlowing.glintColor(self, original, true);
    }
}
