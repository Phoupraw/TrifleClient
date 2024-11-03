package phoupraw.mcmod.trifleclient.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.constant.TCIDs;

import java.util.Collection;
import java.util.function.Function;

/**
 杀戮光环
 */
@Environment(EnvType.CLIENT)
@UtilityClass
public class AutoAttacker {
    /**
     最终结果为{@code null}视为{@code false}
     */
    public static final Event<Function<? super ItemContext, @Nullable Boolean>> WEAPON = EventFactory.createArrayBacked(Function.class, callbacks -> context -> {
        for (var callback : callbacks) {
            var r = callback.apply(context);
            if (r != null) return r;
        }
        return null;
    });
    /**
     最终结果为{@code null}视为{@code false}
     */
    public static final Event<Function<? super TargetContext, @Nullable Boolean>> VICTIM = EventFactory.createArrayBacked(Function.class, callbacks -> context -> {
        for (var callback : callbacks) {
            var r = callback.apply(context);
            if (r != null) return r;
        }
        return null;
    });
    /**
     最终结果为{@code null}视为{@code false}
     */
    public static final Event<Function<? super TargetContext, @Nullable Boolean>> BULLET = EventFactory.createArrayBacked(Function.class, callbacks -> context -> {
        for (var callback : callbacks) {
            var r = callback.apply(context);
            if (r != null) return r;
        }
        return null;
    });
    static {
        ClientPreAttackCallback.EVENT.register(AutoAttacker::autoAttack);
        UseItemCallback.EVENT.addPhaseOrdering(TCIDs.BEFORE, Event.DEFAULT_PHASE);
        UseItemCallback.EVENT.register(TCIDs.BEFORE, AutoAttacker::autoAttackPreventUse);
        UseBlockCallback.EVENT.addPhaseOrdering(TCIDs.BEFORE, Event.DEFAULT_PHASE);
        UseBlockCallback.EVENT.register(AutoAttacker::autoAttackPreventUse);
        WEAPON.register(TCIDs.of("tag/weapon"), AutoAttacker::checkTagWeapon);
        WEAPON.register(TCIDs.of("tag/enchantable/weapon"), AutoAttacker::checkTagWeaponEnchantable);
        VICTIM.register(TCIDs.of("alive"), AutoAttacker::checkAlive);
        VICTIM.register(TCIDs.of("monster"), AutoAttacker::checkMonster);
        VICTIM.register(TCIDs.of("fish"), AutoAttacker::checkFish);
        VICTIM.register(TCIDs.of("squid"), AutoAttacker::checkSquid);
        BULLET.register(TCIDs.of("alive"), AutoAttacker::checkAlive);
        BULLET.register(TCIDs.of("shulker_bullet"), AutoAttacker::checkShulkerBullet);
        BULLET.register(TCIDs.of("fireball"), AutoAttacker::checkFireball);
    }
    public static boolean isAutoAttacking(ClientPlayerEntity player) {
        GameOptions options = MinecraftClient.getInstance().options;
        return TCConfigs.A.isAutoAttacker() && options.attackKey.isPressed() && options.useKey.isPressed() && Boolean.TRUE.equals(WEAPON.invoker().apply(new ItemContext(player.getWeaponStack(), player)));
    }
    /**
     @return 越小越优先
     */
    public static int compareVictim(PlayerEntity player, Entity current, Entity iterated) {
        {
            boolean hurt0 = current instanceof LivingEntity living && living.hurtTime <= 0;
            boolean hurt1 = iterated instanceof LivingEntity living && living.hurtTime <= 0;
            int r = -Boolean.compare(hurt0, hurt1);
            if (r != 0) return r;
        }
        {
            Box near = player.getBoundingBox().expand(3);
            boolean near0 = near.intersects(current.getBoundingBox()), near1 = near.intersects(iterated.getBoundingBox());
            int r = -Boolean.compare(near0, near1);
            if (r != 0) return r;
        }
        {
            boolean ranged0 = current instanceof RangedAttackMob, ranged1 = iterated instanceof RangedAttackMob;
            int r = -Boolean.compare(ranged0, ranged1);
            if (r != 0) return r;
        }
        {
            boolean harmless0 = isHarmlessMob(current), harmless1 = isHarmlessMob(iterated);
            int r = Boolean.compare(harmless0, harmless1);
            if (r != 0) return r;
        }
        double d0 = player.squaredDistanceTo(current), d1 = player.squaredDistanceTo(iterated);
        return Double.compare(d0, d1);
    }
    public static boolean isHarmlessMob(Entity entity) {
        return entity instanceof FishEntity || entity instanceof SquidEntity || entity.getType() == EntityType.SLIME && entity instanceof SlimeEntity slime && slime.getSize() <= 1;
    }
    public static boolean isValidVictim(ClientPlayerEntity player, Entity entity) {
        return Boolean.TRUE.equals(VICTIM.invoker().apply(new TargetContext(entity, player)));
    }
    public static boolean isValidBullet(ClientPlayerEntity player, Entity entity) {
        return Boolean.TRUE.equals(BULLET.invoker().apply(new TargetContext(entity, player)));
    }
    private static @Nullable Boolean checkTagWeapon(ItemContext context) {
        return context.stack().isIn(ConventionalItemTags.MELEE_WEAPON_TOOLS) ? true : null;
    }
    private static @Nullable Boolean checkTagWeaponEnchantable(ItemContext context) {
        return context.stack().isIn(ItemTags.WEAPON_ENCHANTABLE) ? true : null;
    }
    private static boolean autoAttack(MinecraftClient client, ClientPlayerEntity player, int clickCount) {
        var interactor = client.interactionManager;
        if (!isAutoAttacking(player) || interactor == null) {
            return false;
        }
        if (player.getOffHandStack().isIn(ConventionalItemTags.SHIELD_TOOLS)) {
            interactor.interactItem(player, Hand.OFF_HAND);
        }
        double range = player.getEntityInteractionRange() + 10;
        Entity target = null;
        Collection<Entity> bullets = new ObjectArrayList<>();
        for (Entity entity : player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(range))) {
            if (!player.canInteractWithEntity(entity, 1)) continue;
            if (isValidVictim(player, entity)) {
                if (target == null || compareVictim(player, target, entity) > 0) {
                    target = entity;
                }
            }
            if (isValidBullet(player, entity)) {
                bullets.add(entity);
            }
        }
        if (target != null && player.getAttackCooldownProgress(0) >= 1) {
            interactor.attackEntity(player, target);
        }
        for (Entity bullet : bullets) {
            interactor.attackEntity(player, bullet);
        }
        return true;
    }
    private static TypedActionResult<ItemStack> autoAttackPreventUse(PlayerEntity player, World world, Hand hand) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (player instanceof ClientPlayerEntity player1 && isAutoAttacking(player1) && !stackInHand.isIn(ConventionalItemTags.SHIELD_TOOLS)) {
            return TypedActionResult.fail(stackInHand);
        }
        return TypedActionResult.pass(stackInHand);
    }
    private static ActionResult autoAttackPreventUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (player instanceof ClientPlayerEntity player1 && isAutoAttacking(player1) && !stackInHand.isIn(ConventionalItemTags.SHIELD_TOOLS)) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
    private static @Nullable Boolean checkAlive(TargetContext context) {
        return context.target().isAlive() ? null : false;
    }
    private static @Nullable Boolean checkMonster(TargetContext context) {
        return context.target() instanceof Monster ? true : null;
    }
    private static @Nullable Boolean checkFish(TargetContext context) {
        return context.target() instanceof FishEntity ? true : null;
    }
    private static @Nullable Boolean checkSquid(TargetContext context) {
        return context.target() instanceof SquidEntity ? true : null;
    }
    private static @Nullable Boolean checkShulkerBullet(TargetContext context) {
        return context.target() instanceof ShulkerBulletEntity ? true : null;
    }
    private static @Nullable Boolean checkFireball(TargetContext context) {
        Entity target = context.target();
        return target instanceof FireballEntity && target.getVelocity().dotProduct(context.player().getPos().subtract(target.getPos())) > 0 ? true : null;
    }
    
    public record ItemContext(ItemStack stack, ClientPlayerEntity player) {}
    
    public record TargetContext(Entity target, ClientPlayerEntity player) {}
}
