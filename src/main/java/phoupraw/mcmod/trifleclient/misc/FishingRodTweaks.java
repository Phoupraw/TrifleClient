package phoupraw.mcmod.trifleclient.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

import java.util.Objects;

@UtilityClass
public class FishingRodTweaks {
    private static boolean just;
    @ApiStatus.Internal
    public static void onEndTick(ClientWorld world) {
        if (!TCConfigs.A().isSwitchOnHook()) return;
        var interactor = Objects.requireNonNull(MinecraftClient.getInstance().interactionManager);
        if (just) {
            var player = Objects.requireNonNull(MinecraftClient.getInstance().player);
            if (player.getMainHandStack().isIn(ConventionalItemTags.FISHING_ROD_TOOLS)) {
                interactor.interactItem(player, Hand.MAIN_HAND);
            }
            just = false;
            return;
        }
        for (Entity entity : world.getEntities()) {
            if (entity instanceof FishingBobberEntity bobber && bobber.getOwner() instanceof ClientPlayerEntity player && bobber.getHookedEntity() != null) {
                if (player.getMainHandStack().isIn(ConventionalItemTags.FISHING_ROD_TOOLS)) {
                    int selectedSlot = player.getInventory().selectedSlot;
                    player.getInventory().selectedSlot = (selectedSlot + 1) % 9;
                    interactor.stopUsingItem(player);
                    player.getInventory().selectedSlot = selectedSlot;
                    just = true;
                }
                break;
            }
        }
    }
}
