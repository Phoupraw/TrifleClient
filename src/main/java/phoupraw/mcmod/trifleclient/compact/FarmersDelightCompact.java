package phoupraw.mcmod.trifleclient.compact;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.events.AutoHarvestCallback;

@UtilityClass
public class FarmersDelightCompact {
    public static final String MOD_ID = "farmersdelight";
    public static final Identifier TOMATO = Identifier.of(MOD_ID, "tomatoes");
    public static final Identifier RICE = Identifier.of(MOD_ID, "rice_panicles");
    public static AutoHarvestCallback.@Nullable Action checkTomatoes(ClientWorld world, BlockPos pos, BlockState state) {
        var block = Registries.BLOCK.get(TOMATO);
        var property = Properties.AGE_3;
        if (state.isOf(block) && state.get(property) == Properties.AGE_3_MAX) {
            return AutoHarvestCallback::simpleUse;
        }
        return null;
    }
    public static AutoHarvestCallback.@Nullable Action checkRice(ClientWorld world, BlockPos pos, BlockState state) {
        var block = Registries.BLOCK.get(RICE);
        var property = Properties.AGE_3;
        if (state.isOf(block) && state.get(property) == Properties.AGE_3_MAX) {
            return AutoHarvestCallback::simpleAttack;
        }
        return null;
    }
}
