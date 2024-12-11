package phoupraw.mcmod.trifleclient.v0.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import phoupraw.mcmod.trifleclient.constant.TCIDs;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface AutoHarvestCallback {
    /**
     {@link BlockApiLookup#find}将会并发调用，<b>不要修改{@code world}！</b>
     */
    BlockApiLookup<AutoHarvestCallback, Void> LOOKUP = BlockApiLookup.get(TCIDs.of("auto_harvest"), AutoHarvestCallback.class, Void.class);
    /**
     用于方法引用
     */
    static void simpleUse(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        interactor.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos.toImmutable(), false));
    }
    /**
     用于方法引用
     */
    static void simpleAttack(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        interactor.attackBlock(pos.toImmutable(), Direction.UP);
    }
    /**
     用于方法引用
     */
    static void simpleInstaMine(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state) {
        if (state.calcBlockBreakingDelta(player, world, pos) >= 1) {
            interactor.attackBlock(pos.toImmutable(), Direction.UP);
        }
    }
    /**
     @param pos {@link BlockPos.Mutable}
     */
    void act(ClientPlayerEntity player, ClientPlayerInteractionManager interactor, ClientWorld world, BlockPos pos, BlockState state);
}
