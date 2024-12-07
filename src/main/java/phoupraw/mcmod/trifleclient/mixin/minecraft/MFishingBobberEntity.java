package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

import java.util.Objects;

@Mixin(FishingBobberEntity.class)
abstract class MFishingBobberEntity extends ProjectileEntity {
    public MFishingBobberEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "tick", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity$State;HOOKED_IN_ENTITY:Lnet/minecraft/entity/projectile/FishingBobberEntity$State;")), at = @At(value = "FIELD", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;state:Lnet/minecraft/entity/projectile/FishingBobberEntity$State;", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
    private void switchOnHook(CallbackInfo ci) {
        if (TCConfigs.A().isSwitchOnHook() && getOwner() instanceof ClientPlayerEntity player) {
            var interactor = Objects.requireNonNull(MinecraftClient.getInstance().interactionManager);
            if (player.getMainHandStack().isIn(ConventionalItemTags.FISHING_ROD_TOOLS)) {
                int selectedSlot = player.getInventory().selectedSlot;
                player.getInventory().selectedSlot = (selectedSlot + 1) % 9;
                interactor.stopUsingItem(player);
                player.getInventory().selectedSlot = selectedSlot;
            }
        }
    }
}
