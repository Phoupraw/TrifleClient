package phoupraw.mcmod.trifleclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

@FunctionalInterface
public interface AfterUseBlockCallback {
    Event<AfterUseBlockCallback> EVENT= EventFactory.createArrayBacked(AfterUseBlockCallback.class,callbacks-> (player, hand, hitResult) -> {
        for (AfterUseBlockCallback callback : callbacks) {
            var r = callback.afterVanilla(player, hand, hitResult);
            if (r.isAccepted()) return r;
        }
        return ActionResult.PASS;
    });
    ActionResult afterVanilla(PlayerEntity player, Hand hand, BlockHitResult hitResult);
}
