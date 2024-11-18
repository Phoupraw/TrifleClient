package phoupraw.mcmod.trifleclient.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.LootCondition;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TCConfigs {
    public static final TCConfigs A = new TCConfigs();
    boolean speedSpeed = true;
    int speedSteps = 20;
    double speedFactor = 0.05;
    boolean normalSpeed = true;
    boolean noUsingItemSlow = true;
    boolean autoAttacker = true;
    boolean blockFinder = true;
    boolean miningDelay = true;
    boolean autoCrit = true;
    boolean oftenOnGround = true;
    float minStepHeight = 1 + 6 / 16f;
    @Nullable LootCondition glowingItem = AnyOfLootCondition.builder().build();//MatchToolLootCondition.builder(ItemPredicate.Builder.create().component(ComponentPredicate.builder().add(DataComponentTypes.RARITY, Rarity.COMMON).build())).build();
    float minAmbientLight = 0.08f;
    double itemGlowingRange = 24;
    boolean rightClickOpenFolder = true;
    boolean detailPacketError = true;
    boolean elytraCancelSyncFlying = true;
    boolean freeElytraFlying;
    protected TCConfigs() {
    }
}
