package phoupraw.mcmod.trifleclient.mixin.minecraft;

import net.minecraft.loot.condition.AlternativeLootCondition;
import net.minecraft.loot.condition.LootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AlternativeLootCondition.class)
public interface AAlternativeLootCondition {
    @Accessor
    List<LootCondition> getTerms();
}
