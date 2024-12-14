package phoupraw.mcmod.trifleclient.v0.api;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.loot.condition.*;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AAlternativeLootCondition;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class BlockArgumentType implements ArgumentType<LootCondition> {
    public static final Collection<String> EXAMPLES = List.of("[{condition:location_check,predicate:{block:{blocks:'#c:ores'}}},{condition:any_of,terms:[{condition:location_check,offsetX:-1,predicate:{block:{blocks:'#air'}}},{condition:location_check,offsetX:1,predicate:{block:{blocks:'#air'}}},{condition:location_check,offsetY:-1,predicate:{block:{blocks:'#air'}}},{condition:location_check,offsetY:1,predicate:{block:{blocks:'#air'}}},{condition:location_check,offsetZ:-1,predicate:{block:{blocks:'#air'}}},{condition:location_check,offsetZ:1,predicate:{block:{blocks:'#air'}}}]}]");
    private static final DynamicCommandExceptionType EXCEPTION = new DynamicCommandExceptionType(Text.class::cast);
    /**
     只接受{@link LocationCheckLootCondition}及其复合
     */
    public static boolean test(LootCondition condition, World world, BlockPos pos) {
        if (condition instanceof LocationCheckLootCondition c) {
            return ClientLootConditions.test(c, world, pos.toCenterPos());
        }
        if (condition instanceof AnyOfLootCondition) {
            for (LootCondition term : ((AnyOfLootCondition & AAlternativeLootCondition) condition).getTerms()) {
                if (test(term, world, pos)) {
                    return true;
                }
            }
            return false;
        }
        if (condition instanceof AllOfLootCondition) {
            for (LootCondition term : ((AllOfLootCondition & AAlternativeLootCondition) condition).getTerms()) {
                if (!test(term, world, pos)) {
                    return false;
                }
            }
            return true;
        }
        if (condition instanceof InvertedLootCondition(LootCondition term)) {
            return !test(term, world, pos);
        }
        return false;
    }
    private final RegistryWrapper.WrapperLookup registries;
    @Override
    public LootCondition parse(StringReader reader) throws CommandSyntaxException {
        var result = LootCondition.CODEC.decode(RegistryOps.of(NbtOps.INSTANCE, registries), new StringNbtReader(reader).parseElement());
        if (result.isError()) {
            throw EXCEPTION.create(Text.of(result.error().orElseThrow().message()));
        }
        return result.getPartialOrThrow().getFirst();
    }
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(getExamples(), builder);
    }
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
