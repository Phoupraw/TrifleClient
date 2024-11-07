package phoupraw.mcmod.trifleclient.compact;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.misc.AutoAttacker;

@ApiStatus.NonExtendable
public interface MekanismCompact {
    String MOD_ID = "mekanism";
    Item ATOMIC_DISASSEMBLER = Registries.ITEM.get(id("atomic_disassembler"));
    Item MEKA_TOOL = Registries.ITEM.get(id("meka_tool"));
    static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    @ApiStatus.Internal
    static @Nullable Boolean isWeapon(AutoAttacker.ItemContext itemContext) {
        ItemStack stack = itemContext.stack();
        return stack.isOf(ATOMIC_DISASSEMBLER) || stack.isOf(MEKA_TOOL) ? true : null;
    }
}
