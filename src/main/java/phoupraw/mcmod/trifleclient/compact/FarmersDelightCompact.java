package phoupraw.mcmod.trifleclient.compact;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import phoupraw.mcmod.trifleclient.util.MCUtils;

@UtilityClass
public class FarmersDelightCompact {
    public static final String MOD_ID = "farmersdelight";
    public static final Identifier TOMATO = Identifier.of(MOD_ID, "tomatoes");
    public static final Identifier RICE = Identifier.of(MOD_ID, "rice_panicles");
    public static Hand shouldPick(ClientPlayerEntity player, BlockPos pos, BlockState state) {
        var tomato = Registries.BLOCK.get(TOMATO);
        var rice = Registries.BLOCK.get(RICE);
        var property = Properties.AGE_3;
        var world = player.getWorld();
        var interactor = MCUtils.getInteractor();
        if (state.isOf(rice)) {
            if (state.get(property) == Properties.AGE_3_MAX) {
                interactor.attackBlock(pos.toImmutable(), Direction.UP);
            }
        } else if (state.isOf(tomato)) {
            if (state.get(property) == Properties.AGE_3_MAX) {
                return Hand.MAIN_HAND;
            }
        }
        return null;
    }
}
