package phoupraw.mcmod.trifleclient.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.constant.TCIDs;

import java.util.function.Supplier;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TCConfigs {
    public static TCConfigs A() {
        return EVENT.invoker().get();
    }
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
    //@Nullable LootCondition glowingItem = AnyOfLootCondition.builder().build();//MatchToolLootCondition.builder(ItemPredicate.Builder.create().component(ComponentPredicate.builder().add(DataComponentTypes.RARITY, Rarity.COMMON).build())).build();
    float minAmbientLight = 0.08f;
    double itemGlowingRange = 24;
    boolean rightClickOpenFolder = true;
    boolean detailPacketError = true;
    boolean elytraCancelSyncFlying = false;
    boolean freeElytraFlying = false;
    public static final Event<Supplier<@Nullable TCConfigs>> EVENT = EventFactory.createArrayBacked(Supplier.class, callbacks -> () -> {
        for (Supplier<TCConfigs> callback : callbacks) {
            var r = callback.get();
            if (r != null) return r;
        }
        return null;
    });
    static {
        TCConfigs a = new TCConfigs();
        EVENT.register(TCIDs.of("a"), () -> a);
    }
}
