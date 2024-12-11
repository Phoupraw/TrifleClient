package phoupraw.mcmod.trifleclient.compact;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.misc.AutoAttacker;
import phoupraw.mcmod.trifleclient.v0.api.AutoHarvestCallback;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
public class TwilightForestCompact {
    public static final String MOD_ID = "twilightforest";
    private static final RegistryKey<EntityType<?>> LICH_BOLT = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "lich_bolt"));
    private static final RegistryKey<Block> TORCH_BERRY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "torchberry_plant"));
    public static void init() {
        LOGGER.info("检测到《暮色森林》，将加载相关兼容。");
        AutoHarvestCallback.LOOKUP.registerForBlocks(TwilightForestCompact::findTorchBerry, Registries.BLOCK.get(TwilightForestCompact.TORCH_BERRY));
        AutoAttacker.BULLET.register(TwilightForestCompact::autoAttack);
    }
    private static AutoHarvestCallback findTorchBerry(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        for (var entry : state.getEntries().entrySet()) {
            if (entry.getKey().getName().equals("has_torchberries")&&(Boolean) entry.getValue()) {
                return AutoHarvestCallback::simpleUse;
            }
        }
        return null;
    }
    @SuppressWarnings("deprecation")
    private static Boolean autoAttack(AutoAttacker.TargetContext targetContext) {
        return targetContext.target().getType().getRegistryEntry().getKey().orElseThrow().equals(LICH_BOLT) ? true : null;
    }
}
