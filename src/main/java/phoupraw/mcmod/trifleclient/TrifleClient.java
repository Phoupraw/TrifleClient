package phoupraw.mcmod.trifleclient;

import lombok.SneakyThrows;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import phoupraw.mcmod.trifleclient.compact.FarmersDelightCompact;
import phoupraw.mcmod.trifleclient.compact.MekanismCompact;
import phoupraw.mcmod.trifleclient.compact.MekanismWeaponsCompact;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.config.TCYACL;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.events.*;
import phoupraw.mcmod.trifleclient.misc.*;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AEntity;
import phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin;
import phoupraw.mcmod.trifleclient.util.MCUtils;

import java.lang.invoke.MethodHandles;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;
//TODO 使在流体中行走不减速
//TODO 稀有度不为常见或有自定义名称的物品始终发光，其它物品和经验球则有不可穿墙的发光轮廓

/**
 @see TargetPointer */
@Environment(EnvType.CLIENT)
public final class TrifleClient implements ModInitializer, ClientModInitializer {
    public static final String ID = "trifleclient";
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    public static MutableText name() {
        return Text.translatableWithFallback(NAME_KEY, ID);
    }
    @SneakyThrows
    static void loadClass(Class<?> cls) {
        MethodHandles.lookup().ensureInitialized(cls);
    }
    @Override
    public void onInitializeClient() {
        loadClass(TargetPointer.class);
        loadClass(BlockFinder.class);
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(BlockHighlighter::afterEntities);
        loadClass(TCKeyBindings.class);
        loadClass(MiningDelay.class);
        loadClass(AutoAttacker.class);
        loadClass(MiningSame.class);
        ServerTickEvents.START_SERVER_TICK.register(server -> server.setFlightEnabled(TCConfigs.A().isAllowFlight()));
        AfterClientPlayerMove.EVENT.register(NormalSpeed::afterClientPlayerMove);
        OnClientPlayerMove.EVENT.register(SpeedSpeed::onClientPlayerMove);
        //AttackEntityCallback.EVENT.register(AutoCrit::interact);
        ClientAttackEntityCallback.EVENT.register((interactor, player, target) -> {
            if (TCConfigs.A().isAutoCrit()) {
                boolean vanilla = player.getAttackCooldownProgress(0.5f) > 0.9f
                  && player.fallDistance > 0
                  && !player.isOnGround()
                  && !player.isClimbing()
                  && !player.isTouchingWater()
                  && !player.hasStatusEffect(StatusEffects.BLINDNESS)
                  && !player.hasVehicle()
                  && target instanceof LivingEntity
                  && !player.isSprinting();
                if (!vanilla && player instanceof ClientPlayerEntity) {
                    var clientPlayer = (ClientPlayerEntity & AEntity) player;
                    ClientPlayNetworkHandler network = clientPlayer.networkHandler;
                    double y = player.getY() + clientPlayer.invokeAdjustMovementForCollisions(new Vec3d(0, 0.1, 0)).getY();
                    network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), y, player.getZ(), false));
                    network.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX(), player.getY(), player.getZ(), false));
                }
            }
            return null;
        });
        //OnClientPlayerMove.EVENT.register(OftenOnGround::onClientPlayerMove);
        UseItemCallback.EVENT.register(OnekeyBreeding::lambda_interact);
        UseBlockCallback.EVENT.register(OnekeyBreeding::lambda_interact);
        UseEntityCallback.EVENT.register(OnekeyBreeding::lambda_interact);
        OnUseKeyPress.EVENT.register(OnekeyBreeding::lambda_onUseKeyPress);
        ClientCommandRegistrationCallback.EVENT.register(SprucePlanter::lambda_register);
        ClientTickEvents.START_WORLD_TICK.register(SprucePlanter::onStartAndEndTick);
        ClientTickEvents.END_WORLD_TICK.register(SprucePlanter::onStartAndEndTick);
        ClientCommandRegistrationCallback.EVENT.register(LogStripper::lambda_register);
        ClientTickEvents.START_WORLD_TICK.register(LogStripper::onStartAndEndTick);
        ClientTickEvents.END_WORLD_TICK.register(LogStripper::onStartAndEndTick);
        ClientTickEvents.START_WORLD_TICK.register(world -> {
            var player = MinecraftClient.getInstance().player;
            if (TCConfigs.A().isOftenOnGround() && player != null && !player.isClimbing() && !player.isSwimming() && /*!FreeElytraFlying.isFlying(player) &&*/ (player.fallDistance > 2/* || player.getAbilities().flying */) && !player.isFallFlying()) {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
                //player.stopFallFlying();
            }
        });
        ClientTickEvents.END_WORLD_TICK.register(FreeElytraFlying::lambda_onEndTick);
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (TCConfigs.A().isDebugAttackEntity()) {
                String saveName;
                IntegratedServer server = MinecraftClient.getInstance().getServer();
                if (server != null) {
                    saveName = server.getSaveProperties().getLevelName();
                } else {
                    ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
                    if (serverInfo != null) {
                        saveName = serverInfo.name;
                    } else {
                        saveName = "?";
                    }
                }
                LOGGER.info("debugAttackEntity ({}) {}({}) {}", saveName, entity.getName().getString(), Registries.ENTITY_TYPE.getId(entity.getType()), entity.getPos());
            }
            return ActionResult.PASS;
        });
        GlowingCallback.BEFORE.register(HostileGlowing::shouldGlow);
        GlowingColorCallback.EVENT.register(HostileGlowing::getColor);
        ClientTickEvents.END_WORLD_TICK.register(FishingRodTweaks::onEndTick);
        AutoPickCallback.EVENT.register(AutoPickCallback::config);
        AutoPickCallback.EVENT.register((player, pos, state) -> {
            var world = player.getWorld();
            var interactor = MCUtils.getInteractor();
            if (state.isOf(Blocks.SUGAR_CANE)) {
                for (int i = -1; i <= 1; i += 2) {
                    if (!world.getBlockState(pos.up(i)).isOf(Blocks.SUGAR_CANE)) {
                        return null;
                    }
                }
                interactor.attackBlock(pos.toImmutable(), Direction.UP);
            } else if (state.getBlock() instanceof CropBlock block) {
                if (state.calcBlockBreakingDelta(player, world, pos) >= 1 && block.isMature(state)) {
                    ItemStack offHandStack = player.getOffHandStack();
                    if (offHandStack.getItem() instanceof BlockItem item && item.getBlock() == block) {
                        RegistryEntry<Enchantment> enchEntry = player.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.FORTUNE).orElseThrow();
                        if (EnchantmentHelper.getLevel(enchEntry, player.getMainHandStack()) >= enchEntry.value().getMaxLevel()) {
                            interactor.attackBlock(pos.toImmutable(), Direction.UP);
                            offHandStack.decrement(1);
                            return Hand.OFF_HAND;
                        }
                    }
                }
            } else if (state.isOf(Blocks.BAMBOO)) {
                if (state.calcBlockBreakingDelta(player,world,pos)>=1) {
                    BlockPos.Mutable pos1 = new BlockPos.Mutable().set(pos);
                    if (world.getBlockState(pos1.move(0, -1, 0)).isOf(Blocks.BAMBOO)) {
                        BlockState state1 = world.getBlockState(pos1.move(0, -1, 0));
                        if (!state1.isOf(Blocks.BAMBOO) && !state1.isAir()) {
                            pos1.set(pos);
                            while (world.getBlockState(pos1.move(0, 1, 0)).isOf(Blocks.BAMBOO)) ;
                            BlockState state2 = world.getBlockState(pos1.move(0, -1, 0));
                            if (state2.isOf(Blocks.BAMBOO) && state2.get(BambooBlock.STAGE) == 1) {
                                interactor.attackBlock(pos.toImmutable(), Direction.UP);
                            }
                        }
                    }
                }
            } else if (state.isOf(Blocks.NETHER_WART)) {
                if (state.get(NetherWartBlock.AGE) == NetherWartBlock.MAX_AGE) {
                    ItemStack offHandStack = player.getOffHandStack();
                    if (offHandStack.isOf(Items.NETHER_WART)) {
                        interactor.attackBlock(pos.toImmutable(), Direction.UP);
                        offHandStack.decrement(1);
                        return Hand.OFF_HAND;
                    }
                }
            }
            return null;
        });
        if (FabricLoader.getInstance().isModLoaded(MekanismCompact.MOD_ID)) {
            LOGGER.info("检测到《通用机械》，将加载相关兼容。");
            AutoAttacker.WEAPON.register(MekanismCompact::isWeapon);
        }
        if (FabricLoader.getInstance().isModLoaded(MekanismWeaponsCompact.MOD_ID)) {
            LOGGER.info("检测到《通用机械武器》，将加载相关兼容。");
            AutoAttacker.WEAPON.register(MekanismWeaponsCompact::isWeapon);
        }
        if (FabricLoader.getInstance().isModLoaded(TCYACL.MOD_ID)) {
            LOGGER.info("检测到《Yet Another Config Lib》，将加载相关兼容。");
            TCYACL.assignConfig();
        }
        if (FabricLoader.getInstance().isModLoaded(FarmersDelightCompact.MOD_ID)) {
            LOGGER.info("检测到《农夫乐事》，将加载相关兼容。");
            AutoPickCallback.EVENT.register(FarmersDelightCompact.TOMATO, FarmersDelightCompact::shouldPick);
            AutoPickCallback.EVENT.addPhaseOrdering(FarmersDelightCompact.TOMATO, Event.DEFAULT_PHASE);
        }
        if (!TCMixinConfigPlugin.NEOFORGE) {
            LOGGER.info("检测到《Neoforge》，将加载相关兼容。");
            AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                if (!world.isClient()) return ActionResult.PASS;
                Boolean r = ClientAttackEntityCallback.EVENT.invoker().shouldCancel(MinecraftClient.getInstance().interactionManager, player, entity);
                if (r == null) return ActionResult.PASS;
                return r ? ActionResult.FAIL : ActionResult.SUCCESS;
            });
        }
    }
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.setFlightEnabled(false));
    }
}
