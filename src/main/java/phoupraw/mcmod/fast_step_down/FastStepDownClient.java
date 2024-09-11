package phoupraw.mcmod.fast_step_down;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import phoupraw.mcmod.fast_step_down.config.FSDConfigs;
import phoupraw.mcmod.fast_step_down.events.BeforeStepDown;
import phoupraw.mcmod.fast_step_down.events.OnStepDown;
import phoupraw.mcmod.trilevel_config.api.ClientConfigs;
import phoupraw.mcmod.trilevel_config.api.Configs;

@Environment(EnvType.CLIENT)
public final class FastStepDownClient implements ClientModInitializer {
    //@SneakyThrows
    private static void loadClasses() {
        //for (var cls : Arrays.asList(FSDConfigs.class)) {
        //    MethodHandles.lookup().ensureInitialized(cls);
        //}
    }
    private static Boolean checkConfig(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
        return ClientConfigs.g(FSDConfigs.PATH).get(FSDConfigs.ON) ? null : false;
    }
    private static Boolean checkMovementType(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
        return movementType != MovementType.SELF ? false : null;
    }
    private static Boolean checkFlying(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
        return self.getAbilities().flying ? false : null;
    }
    private static Boolean checkMovingDirection(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
        return movement.getY() > 0 ? false : null;
    }
    private static Boolean checkStepHeight(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround, Box highestBox, double deltaY) {
        return -0.01 < deltaY ? false : null;
    }
    private static Boolean avoidMagmaBlock(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround, Box highestBox, double deltaY) {
        BlockState blockState = self.getWorld().getBlockState(BlockPos.ofFloored(self.getX(), self.getY() + deltaY, self.getZ()));
        if (!blockState.isOf(Blocks.MAGMA_BLOCK)) {
            return null;
        }
        if (self.bypassesSteppingEffects() || self.isFireImmune()) {
            return null;
        }
        for (var regEntry : self.getEquippedStack(EquipmentSlot.FEET).getEnchantments().getEnchantments()) {
            if (regEntry.matchesKey(Enchantments.FROST_WALKER)) {
                return null;
            }
        }
        return false;
    }
    @Override
    public void onInitializeClient() {
        Configs.register(FSDConfigs.PATH, FSDConfigs.ON);
        BeforeStepDown.EVENT.register(FastStepDownClient::checkConfig);
        BeforeStepDown.EVENT.register(FastStepDownClient::checkMovementType);
        BeforeStepDown.EVENT.register(FastStepDownClient::checkFlying);
        BeforeStepDown.EVENT.register(FastStepDownClient::checkMovingDirection);
        OnStepDown.EVENT.register(FastStepDownClient::checkStepHeight);
        OnStepDown.EVENT.register(FastStepDownClient::avoidMagmaBlock);
    }
}
