package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.minecraft.entity.ItemEntity;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.ItemGlowing;

@ApiStatus.NonExtendable
@ApiStatus.Internal
public interface MMItemEntity {
    static boolean glow(ItemEntity self, boolean original) {
        return ItemGlowing.glow(self, original);
    }
    static int glintColor(ItemEntity self, int original) {
        return ItemGlowing.glintColor(self, original, false);
    }
}
