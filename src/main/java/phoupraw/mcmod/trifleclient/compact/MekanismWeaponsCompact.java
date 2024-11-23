package phoupraw.mcmod.trifleclient.compact;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.misc.AutoAttacker;

@ApiStatus.NonExtendable
public interface MekanismWeaponsCompact {
    String MOD_ID = "mekaweapons";
    Item MEKA_TANA = Registries.ITEM.get(id("meka_tana"));
    static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    @ApiStatus.Internal
    static @Nullable Boolean isWeapon(AutoAttacker.ItemContext itemContext) {
        ItemStack stack = itemContext.stack();
        return stack.isOf(MEKA_TANA) ? true : null;
    }
}
