package phoupraw.mcmod.trifleclient.events;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.xpple.clientarguments.arguments.CBlockPredicateArgument;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.util.MCUtils;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface AutoPickCallback {
    @ApiStatus.Internal
    static BlockPos mixin(Entity self, BlockPos prevPos) {
        if (TCConfigs.A().isAutoPick() && !self.getBlockPos().equals(prevPos) && self instanceof ClientPlayerEntity player) {
            var interactor = MCUtils.getInteractor();
            int range = (int) Math.ceil(player.getBlockInteractionRange());
            for (BlockPos pos : BlockPos.iterateOutwards(BlockPos.ofFloored(self.getEyePos()), range, range, range)) {
                if (player.canInteractWithBlockAt(pos, 0)) {
                    BlockState state = player.getWorld().getBlockState(pos);
                    if (Boolean.TRUE.equals(EVENT.invoker().shouldPick(player, pos, state))) {
                        interactor.interactBlock(player, Hand.MAIN_HAND, new BlockHitResult(pos.toCenterPos(), Direction.UP, pos.toImmutable(), false));
                    }
                }
            }
        }
        return prevPos;
    }
    @ApiStatus.Internal
    static Boolean config(ClientPlayerEntity player, BlockPos pos, BlockState state) {
        String config = TCConfigs.A().getAutoPickBlocks();
        var predicates = CACHE.get(config);
        if (predicates.isEmpty()) {
            String[] split = config.replaceAll("\\s", "").split("\\|");
            for (String s : split) {
                Predicate<CachedBlockPosition> predicate;
                try {
                    predicate = CBlockPredicateArgument.parse(player.getRegistryManager().getWrapperOrThrow(RegistryKeys.BLOCK), new StringReader(s));
                } catch (CommandSyntaxException e) {
                    continue;
                }
                predicates.add(predicate);
            }
        }
        CachedBlockPosition cachedPos = new CachedBlockPosition(player.getWorld(), pos, false);
        for (var predicate : predicates) {
            if (predicate.test(cachedPos)) {
                return true;
            }
        }
        return null;
        //return (state.isOf(Blocks.CAVE_VINES_PLANT) || state.isOf(Blocks.CAVE_VINES)) && state.get(CaveVines.BERRIES) || state.isOf(Blocks.SWEET_BERRY_BUSH) && state.get(SweetBerryBushBlock.AGE) == SweetBerryBushBlock.MAX_AGE ? true : null;
    }
    @Nullable Boolean shouldPick(ClientPlayerEntity player, BlockPos pos, BlockState state);
    Event<AutoPickCallback> EVENT = EventFactory.createArrayBacked(AutoPickCallback.class, callbacks -> (player, pos, state) -> {
        for (AutoPickCallback callback : callbacks) {
            var r = callback.shouldPick(player, pos, state);
            if (r != null) return r;
        }
        return null;
    });
    Multimap<String, Predicate<CachedBlockPosition>> CACHE = Multimaps.newMultimap(new Object2ObjectOpenHashMap<>(), ObjectArrayList::new);
}
