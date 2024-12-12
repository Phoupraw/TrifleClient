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
import phoupraw.mcmod.trifleclient.v0.api.RegistryFreezeCallback;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@UtilityClass
public class TwilightForestCompact {
    public static final String MOD_ID = "twilightforest";
    private static final RegistryKey<EntityType<?>> LICH_BOLT = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "lich_bolt"));
    private static final RegistryKey<EntityType<?>> HYDRA_MORTAR = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "hydra_mortar"));
    private static final RegistryKey<Block> TORCH_BERRY = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "torchberry_plant"));
    static {
        LOGGER.info("检测到《暮色森林》，将加载相关兼容。");
        AutoAttacker.BULLET.register(TwilightForestCompact::autoAttack);
        RegistryFreezeCallback.EVENT.register(() -> {
            AutoHarvestCallback.LOOKUP.registerForBlocks(TwilightForestCompact::findTorchBerry, Registries.BLOCK.get(TwilightForestCompact.TORCH_BERRY));
        });
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
        var registryKey = targetContext.target().getType().getRegistryEntry().getKey().orElseThrow();
        return registryKey.equals(LICH_BOLT)||registryKey.equals(HYDRA_MORTAR) ? true : null;
    }
}
