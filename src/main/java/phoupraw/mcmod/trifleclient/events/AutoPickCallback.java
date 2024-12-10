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
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;

import java.util.function.Predicate;
@Deprecated
@Environment(EnvType.CLIENT)
public interface AutoPickCallback {
    //TODO
    @ApiStatus.Internal
    static @Nullable Hand config(ClientPlayerEntity player, BlockPos pos, BlockState state) {
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
                return Hand.MAIN_HAND;
            }
        }
        return null;
    }
    Multimap<String, Predicate<CachedBlockPosition>> CACHE = Multimaps.newMultimap(new Object2ObjectOpenHashMap<>(), ObjectArrayList::new);
}
