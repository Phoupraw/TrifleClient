package phoupraw.mcmod.trifleclient;

import lombok.SneakyThrows;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.misc.TargetPointer;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public final class TrifleClient implements ModInitializer, ClientModInitializer {
    public static final String ID = "fast_step_down";
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger(ID);
    @SneakyThrows
    private static void loadClasses() {
        for (var cls : Arrays.asList(TargetPointer.class)) {
            MethodHandles.lookup().ensureInitialized(cls);
        }
    }
    //private static Boolean checkConfig(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
    //    return ClientConfigs.g(FSDConfigs.PATH).get(FSDConfigs.ON) ? null : false;
    //}
    //private static Boolean checkMovementType(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
    //    return movementType != MovementType.SELF ? false : null;
    //}
    //private static Boolean checkFlying(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
    //    return self.getAbilities().flying ? false : null;
    //}
    //private static Boolean checkMovingDirection(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround) {
    //    return movement.getY() > 0 ? false : null;
    //}
    //private static Boolean checkStepHeight(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround, Box highestBox, double deltaY) {
    //    return -0.01 < deltaY ? false : null;
    //}
    //private static Boolean avoidMagmaBlock(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client, boolean prevOnGround, Box highestBox, double deltaY) {
    //    BlockState blockState = self.getWorld().getBlockState(BlockPos.ofFloored(self.getX(), self.getY() + deltaY, self.getZ()));
    //    if (!blockState.isOf(Blocks.MAGMA_BLOCK)) {
    //        return null;
    //    }
    //    if (self.bypassesSteppingEffects() || self.isFireImmune()) {
    //        return null;
    //    }
    //    for (var regEntry : self.getEquippedStack(EquipmentSlot.FEET).getEnchantments().getEnchantments()) {
    //        if (regEntry.matchesKey(Enchantments.FROST_WALKER)) {
    //            return null;
    //        }
    //    }
    //    return false;
    //}
    @Override
    public void onInitializeClient() {
        loadClasses();
        //Configs.register(FSDConfigs.PATH, FSDConfigs.ON);
        //BeforeStepDown.EVENT.register(TrifleClient::checkConfig);
        //BeforeStepDown.EVENT.register(TrifleClient::checkMovementType);
        //BeforeStepDown.EVENT.register(TrifleClient::checkFlying);
        //BeforeStepDown.EVENT.register(TrifleClient::checkMovingDirection);
        //OnStepDown.EVENT.register(TrifleClient::checkStepHeight);
        //OnStepDown.EVENT.register(TrifleClient::avoidMagmaBlock);
    }
    @Override
    public void onInitialize() {
    
    }
}
