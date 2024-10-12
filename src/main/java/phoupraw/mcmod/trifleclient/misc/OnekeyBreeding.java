package phoupraw.mcmod.trifleclient.misc;

import com.google.common.base.Predicates;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.SequencedCollection;

@UtilityClass
public class OnekeyBreeding {
    public static final Collection<Pair<TagKey<Item>, Class<? extends AnimalEntity>>> REGISTRY = new ObjectArrayList<>(Arrays.asList(
      Pair.of(ItemTags.COW_FOOD, CowEntity.class),
      Pair.of(ItemTags.CHICKEN_FOOD, ChickenEntity.class),
      Pair.of(ItemTags.RABBIT_FOOD, RabbitEntity.class),
      Pair.of(ItemTags.FOX_FOOD, FoxEntity.class),
      Pair.of(ItemTags.FROG_FOOD, FrogEntity.class),
      Pair.of(ItemTags.ARMADILLO_FOOD, ArmadilloEntity.class),
      Pair.of(ItemTags.AXOLOTL_FOOD, AxolotlEntity.class),
      Pair.of(ItemTags.BEE_FOOD, BeeEntity.class),
      Pair.of(ItemTags.CAMEL_FOOD, CamelEntity.class),
      Pair.of(ItemTags.CAT_FOOD, CatEntity.class),
      Pair.of(ItemTags.GOAT_FOOD, GoatEntity.class),
      Pair.of(ItemTags.HOGLIN_FOOD, HoglinEntity.class),
      Pair.of(ItemTags.HORSE_FOOD, HorseEntity.class),
      Pair.of(ItemTags.LLAMA_FOOD, LlamaEntity.class),
      Pair.of(ItemTags.OCELOT_FOOD, OcelotEntity.class),
      Pair.of(ItemTags.PANDA_FOOD, PandaEntity.class),
      Pair.of(ItemTags.PARROT_FOOD, ParrotEntity.class),
      Pair.of(ItemTags.PIG_FOOD, PigEntity.class),
      Pair.of(ItemTags.SNIFFER_FOOD, SnifferEntity.class),
      Pair.of(ItemTags.STRIDER_FOOD, StriderEntity.class),
      Pair.of(ItemTags.TURTLE_FOOD, TurtleEntity.class),
      Pair.of(ItemTags.WOLF_FOOD, WolfEntity.class),
      Pair.of(ItemTags.SHEEP_FOOD, SheepEntity.class)
    ));
    private long lastUse;
    @ApiStatus.Internal
    public static TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        if (Screen.hasShiftDown()) {
            ItemStack stack = player.getStackInHand(hand);
            if (!stack.isEmpty()) {
                if (lastUse == Long.MIN_VALUE) {
                    lastUse = 0;
                    boolean success = false;
                    for (Pair<TagKey<Item>, Class<? extends AnimalEntity>> pair : REGISTRY) {
                        if (stack.isIn(pair.first()) && findAndBreed(player, world, hand, stack, pair.second())) {
                            success = true;
                        }
                    }
                    if (success) {
                        return TypedActionResult.success(stack);
                    }
                }
            }
        }
        return TypedActionResult.pass(ItemStack.EMPTY);
    }
    private static boolean findAndBreed(PlayerEntity player, World world, Hand hand, ItemStack stack, Class<? extends AnimalEntity> entityClass) {
        Vec3d eyePos = player.getEyePos();
        double range0 = player.getEntityInteractionRange();
        float width = player.getWidth();
        Box box = new Box(eyePos, eyePos).expand(range0 + width, range0 + player.getHeight(), range0 + width);
        SequencedCollection<Entity> animals = new ObjectArrayList<>();
        for (var animal : world.getEntitiesByClass(entityClass, box, Predicates.alwaysTrue())) {
            if (!animal.isBaby() && player.canInteractWithEntity(animal, 0) && animal.isBreedingItem(stack)) {
                animals.add(animal);
            }
        }
        if (animals.size() % 2 == 1) {
            animals.removeLast();
        }
        ClientPlayerInteractionManager interactor = MinecraftClient.getInstance().interactionManager;
        boolean success = false;
        for (Entity animal : animals) {
            if (interactor.interactEntity(player, animal, hand).isAccepted()) {
                success = true;
            }
        }
        return success;
    }
    @ApiStatus.Internal
    public static ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return interact(player, world, hand).getResult();
    }
    @ApiStatus.Internal
    public static ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        return interact(player, world, hand).getResult();
    }
    @ApiStatus.Internal
    public static void onUseKeyPress(MinecraftClient client, KeyBinding useKey) {
        if (Screen.hasShiftDown()) {
            if (lastUse == 0) {
                lastUse = client.world.getTime();
            } else if (client.world.getTime() - lastUse < 5) {
                lastUse = Long.MIN_VALUE;
            } else {
                lastUse = 0;
            }
        }
    }
}
