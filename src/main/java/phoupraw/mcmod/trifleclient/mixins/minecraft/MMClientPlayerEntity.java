package phoupraw.mcmod.trifleclient.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.misc.FreeElytraFlying;

import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMClientPlayerEntity {
    /**
     @deprecated 这个方法关联太多，无论怎么改都无法做到在不接触流体时保持原样，还是直接去{@link LivingEntity#travel}里面改吧。
     */
    @Deprecated
    static void updateVelocity(ClientPlayerEntity self, float speed, Vec3d movementInput, BiConsumer<Float, Vec3d> invokeSuper, float offGroundSpeed) {
        float slipperiness = 0.6f;
        invokeSuper.accept(Math.max(speed,/*self.isOnGround() ? */self.getMovementSpeed() * (0.216f / (slipperiness * slipperiness * slipperiness)) /*: offGroundSpeed*/), movementInput);
    }
    static boolean noUsingItemSlow(ClientPlayerEntity self, boolean original) {
        return original && !TCConfigs.A().isNoUsingItemSlow();
    }
    static float minStepHeight(float original) {
        return Math.max(original, TCConfigs.A().getMinStepHeight());
    }
    static boolean notSendStopFlyingPacket(ClientPlayerEntity instance) {
        return instance.getAbilities().allowFlying;
    }
    //static boolean cancelPoseSync(AbstractClientPlayerEntity instance, TrackedData<?> data, TrackedData<?> POSE) {
    //    return !instance.isFallFlying() || !POSE.equals(data);
    //}
    //static void startFlying(ClientPlayerEntity self) {
    //    if (FreeElytraFlying.isFlying(self)) {
    //        self.getAbilities().flying = true;
    //    }
    //}
    static boolean toggleFlying(ClientPlayerEntity self, boolean original) {
        return original || FreeElytraFlying.canFly(self);
    }
    static boolean stopFlying(ClientPlayerEntity self, PlayerAbilities instance, boolean value) {
        if (!value && FreeElytraFlying.isFlying(self)) {
            //self.stopFallFlying();
            var interactor = MinecraftClient.getInstance().interactionManager;
            var playerMenu = self.playerScreenHandler;
            if (interactor != null && playerMenu == self.currentScreenHandler) {
                //for (Slot slot : playerMenu.slots) {
                //    if (slot.getStack().isOf(Items.ELYTRA)) {
                //        System.out.println(slot.id);
                //    }
                //}
                //for (int i = 0; i < 1; i++) {
                //    interactor.clickSlot(playerMenu.syncId, 5 + 1, GLFW.GLFW_MOUSE_BUTTON_LEFT, SlotActionType.QUICK_MOVE, self);
                //}
            }
        }
        return true;
    }
    static float getVelocityMultiplier(ClientPlayerEntity self, float original) {
        return Math.max(original, TCConfigs.A().getMinSpeedFactor());
    }
}
