package phoupraw.mcmod.trifleclient.compact;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
    private static final RegistryKey<Block> BROWN_THORNS = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "brown_thorns"));
    private static final RegistryKey<Block> BURNT_THORNS = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "burnt_thorns"));
    private static final RegistryKey<Item> LAMP_OF_CINDERS = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "lamp_of_cinders"));
    static {
        LOGGER.info("检测到《暮色森林》，将加载相关兼容。");
        AutoAttacker.BULLET.register(TwilightForestCompact::autoAttack);
        RegistryFreezeCallback.EVENT.register(() -> {
            AutoHarvestCallback.LOOKUP.registerForBlocks(TwilightForestCompact::findTorchBerry, Registries.BLOCK.get(TwilightForestCompact.TORCH_BERRY));
            AutoHarvestCallback.LOOKUP.registerForBlocks(TwilightForestCompact::findBrownThorns, Registries.BLOCK.get(TwilightForestCompact.BROWN_THORNS));
            AutoHarvestCallback.LOOKUP.registerForBlocks(TwilightForestCompact::findBurntThorns, Registries.BLOCK.get(TwilightForestCompact.BURNT_THORNS));
        });
    }
    private static AutoHarvestCallback findTorchBerry(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        for (var entry : state.getEntries().entrySet()) {
            if (entry.getKey().getName().equals("has_torchberries") && (Boolean) entry.getValue()) {
                return AutoHarvestCallback::simpleUse;
            }
        }
        return null;
    }
    @SuppressWarnings("deprecation")
    private static Boolean autoAttack(AutoAttacker.TargetContext targetContext) {
        var registryKey = targetContext.target().getType().getRegistryEntry().getKey().orElseThrow();
        return registryKey.equals(LICH_BOLT) || registryKey.equals(HYDRA_MORTAR) ? true : null;
    }
    private static AutoHarvestCallback findBrownThorns(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        return TwilightForestCompact::burn;
    }
    @SuppressWarnings("deprecation")
    private static void burn(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        ItemStack mainHand = player.getMainHandStack();
        if (mainHand.getItem().getRegistryEntry().registryKey().equals(LAMP_OF_CINDERS)) {
            AutoHarvestCallback.simpleUse(player, interactor, world, pos, state);
        } else if (player.getOffHandStack().getItem().getRegistryEntry().registryKey().equals(LAMP_OF_CINDERS) && mainHand.isIn(ItemTags.SWORDS)) {
            interactor.interactBlock(player, Hand.OFF_HAND, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos.toImmutable(), false));
        }
    }
    private static AutoHarvestCallback findBurntThorns(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void context) {
        return AutoHarvestCallback::simpleAttack;
    }
}
