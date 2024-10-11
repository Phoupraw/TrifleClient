package phoupraw.mcmod.trifleclient.misc;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

@ApiStatus.NonExtendable
public interface AutoCrit {
    @ApiStatus.Internal
    static ActionResult interact(PlayerEntity player0, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (TCConfigs.A.isAutoCrit() && player0 instanceof ClientPlayerEntity player) {
            ClientPlayNetworkHandler network = player.networkHandler;
            if (network != null) {
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY() + 0.625, player.getZ(), false));
                network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY(), player.getZ(), false));
            }
        }
        return ActionResult.PASS;
    }
}