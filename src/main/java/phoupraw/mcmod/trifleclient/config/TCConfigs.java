package phoupraw.mcmod.trifleclient.config;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.constant.TCIDs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Supplier;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TCConfigs {
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
    public static TCConfigs A() {
        return EVENT.invoker().get();
    }
    public static Collection<Field> toFields(Class<?> configClass) {
        var fields = new ObjectArrayList<Field>();
        for (Field field : configClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
                continue;
            }
            if (!field.trySetAccessible()) {
                continue;
            }
            fields.add(field);
        }
        return fields;
    }
    boolean speedSpeed = true;
    @NumberRange(min = 0)
    int speedSteps = 20;
    @NumberRange(min = 0)
    double speedFactor = 0.05;
    boolean normalSpeed = true;
    boolean noUsingItemSlow = true;
    boolean autoAttacker = true;
    boolean blockFinder = true;
    boolean miningDelay = true;
    @YACLDataGen(name = "自动暴击", desc = "每次攻击之前向服务端发送上移和下移的信包。")
    boolean autoCrit = true;
    @YACLDataGen(name = "减免摔落伤害", desc = "频繁向服务端发送落地信包。")
    boolean oftenOnGround = true;
    @NumberRange(min = 0)
    float minStepHeight = 1 + 6 / 16f;
    //@Nullable LootCondition glowingItem = AnyOfLootCondition.builder().build();//MatchToolLootCondition.builder(ItemPredicate.Builder.create().component(ComponentPredicate.builder().add(DataComponentTypes.RARITY, Rarity.COMMON).build())).build();
    @YACLDataGen(name = "环境亮度", desc = "每个维度的最低环境亮度不会低于此值。注意：只需很小的值就可以让整个维度非常亮。")
    @NumberRange(min = 0, max = 1)
    float minAmbientLight = 0.08f;
    @NumberRange(min = 0)
    double itemGlowingRange = 24;
    boolean rightClickOpenFolder = true;
    boolean detailPacketError = true;
    @YACLDataGen(name = "鞘翅取消飞行同步", desc = "穿着鞘翅时，忽略从服务端来的能力同步信包中的飞行能力同步。")
    boolean elytraCancelSyncFlying = false;
    @YACLDataGen(name = "鞘翅自由飞行", desc = "穿着鞘翅时可以如同在创造模式一样自由飞行。可能会在服务端错误移动，请不要移动得过于刁钻。")
    boolean freeElytraFlying = false;
    double hostileGlowRange = 16;
    boolean switchOnHook = true;
    float minMSPT = 0;
    float maxMSPT = 50;
    
    boolean debugAttackEntity;
}
