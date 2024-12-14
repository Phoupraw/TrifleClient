package phoupraw.mcmod.trifleclient;

import lombok.SneakyThrows;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import phoupraw.mcmod.trifleclient.compact.*;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.config.TCYACL;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.events.*;
import phoupraw.mcmod.trifleclient.misc.*;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AClientWorld;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AEntity;
import phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin;
import phoupraw.mcmod.trifleclient.v0.impl.AutoHarvestImpls;

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
        loadClass(Attacking.class);
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
        AutoHarvestImpls.init();
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!TCConfigs.A().isAlwaysShield()) return;
            var player = MinecraftClient.getInstance().player;
            if (player == null ||player.isUsingItem()&& player.getActiveHand() == Hand.MAIN_HAND) {
                return;
            }
            ItemStack offHandStack = player.getOffHandStack();
            if (!offHandStack.isIn(ConventionalItemTags.SHIELD_TOOLS) || player.getItemCooldownManager().isCoolingDown(offHandStack.getItem())) {
                return;
            }
            //ItemStack mainHandStack = player.getMainHandStack();
            //if (!mainHandStack.isIn(ConventionalItemTags.MELEE_WEAPON_TOOLS) && !mainHandStack.isIn(ConventionalItemTags.MINING_TOOL_TOOLS)) {
            //    return;
            //}
            //MCUtils.getInteractor().interactItem(player, Hand.OFF_HAND);
            player.clearActiveItem();
            try (var sequence = ((AClientWorld) world).invokeGetPendingUpdateManager().incrementSequence()) {
                player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(Hand.OFF_HAND, sequence.getSequence(), player.getYaw(), player.getPitch()));
            }
            player.clearActiveItem();
        });
        //ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
        //    String autoPickBlocks = TCConfigs.A().getAutoPickBlocks();
        //    NbtCompound parsed;
        //    try {
        //        parsed = StringNbtReader.parse(autoPickBlocks);
        //    } catch (CommandSyntaxException e) {
        //        LOGGER.catching(e);
        //        LOGGER.error(autoPickBlocks);
        //        return;
        //    }
        //    var result = LootCondition.CODEC.decode(RegistryOps.of(NbtOps.INSTANCE, handler.getRegistryManager()), parsed);
        //    if (result.isError()) {
        //        LOGGER.error(result.error().orElseThrow().message());
        //    }
        //    if (!result.hasResultOrPartial()) {
        //        return;
        //    }
        //    LootCondition condition = result.getPartialOrThrow().getFirst();
        //    if (condition instanceof AnyOfLootCondition) {
        //        var anyOf = (AnyOfLootCondition & AAlternativeLootCondition) condition;
        //        for (var term : anyOf.getTerms()) {
        //            if (term instanceof LocationCheckLootCondition offsetted && offsetted.predicate().isPresent() && offsetted.predicate().get().block().isPresent()) {
        //
        //            }
        //        }
        //    }
        //});
        loadClass(AutoSwitchTools.class);
        loadClass(BlockFinder2.class);
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
            loadClass(FarmersDelightCompact.class);
        }
        if (FabricLoader.getInstance().isModLoaded(TwilightForestCompact.MOD_ID)) {
            loadClass(TwilightForestCompact.class);
        }
        if (FabricLoader.getInstance().isModLoaded(StorageDrawersCompact.MOD_ID)) {
            loadClass(StorageDrawersCompact.class);
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
