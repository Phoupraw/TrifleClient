package phoupraw.mcmod.trifleclient.compact;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import phoupraw.mcmod.trifleclient.v0.api.AutoSwitchToolCallback;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
public class StorageDrawersCompact {
    public static final String MOD_ID = "storagedrawers";
    private static final TagKey<Block> DRAWERS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "drawers"));
    private static final RegistryKey<Block> COMPACTING_DRAWERS_2 = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "compacting_drawers_2"));
    private static final RegistryKey<Block> COMPACTING_DRAWERS_3 = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "compacting_drawers_3"));
    static {
        LOGGER.info("检测到《储物抽屉》("+MOD_ID+")，将加载相关兼容。");
        AutoSwitchToolCallback.EVENT.register(StorageDrawersCompact::check);
    }
    private static Boolean check(World world, BlockPos pos, BlockState state, Direction side, ClientPlayerEntity player, Hand hand) {
        if ((state.isIn(DRAWERS) || state.getRegistryEntry().matchesKey(COMPACTING_DRAWERS_2) || state.getRegistryEntry().matchesKey(COMPACTING_DRAWERS_3)) && state.contains(HORIZONTAL_FACING)) {
            return state.get(HORIZONTAL_FACING) != side;
        }
        return null;
    }
}
