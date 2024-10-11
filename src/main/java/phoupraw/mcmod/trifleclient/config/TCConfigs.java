package phoupraw.mcmod.trifleclient.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.LootCondition;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
public class TCConfigs {
    public static final TCConfigs A = new TCConfigs();
    private boolean speedSpeed = true;
    private int speedSteps = 20;
    private double speedFactor = 0.05;
    private boolean normalSpeed = true;
    private boolean noUsingItemSlow = true;
    private boolean autoAttacker = true;
    private boolean blockFinder = true;
    private boolean miningDelay = true;
    private boolean autoCrit = true;
    private boolean oftenOnGround = true;
    private float minStepHeight = 1 + 6 / 16f;
    private @Nullable LootCondition glowingItem = AnyOfLootCondition.builder().build();//MatchToolLootCondition.builder(ItemPredicate.Builder.create().component(ComponentPredicate.builder().add(DataComponentTypes.RARITY, Rarity.COMMON).build())).build();
    private float minAmbientLight = 0.08f;
    protected TCConfigs() {
    }
    //public float getMinStepHeight() {
    //    return 0;
    //}
}
