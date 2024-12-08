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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Contract;
import phoupraw.mcmod.trifleclient.compact.MekanismCompact;
import phoupraw.mcmod.trifleclient.compact.MekanismWeaponsCompact;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.config.TCYACL;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.events.*;
import phoupraw.mcmod.trifleclient.misc.*;
import phoupraw.mcmod.trifleclient.mixin.minecraft.AEntity;
import phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin;

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
        GlowingCallback.BEFORE.register(entity -> isNearHostile(entity) ? true : null);
        GlowingColorCallback.EVENT.register((entity, original, tickCounter, camera) -> {
            if (isNearHostile(entity)) {
                int period = 20;
                float delta = (entity.getWorld().getTime() % period + tickCounter.getTickDelta(false)) / period;
                delta = Math.abs(delta - 0.5f);
                //delta = delta*delta;
                float r = MathHelper.lerp(delta, 0.8f, 0f);
                //float gb = MathHelper.lerp(delta,0f,1f);
                return ~MathHelper.packRgb(r, 0, 0);
            }
            return original;
        });
        ClientTickEvents.END_WORLD_TICK.register(FishingRodTweaks::onEndTick);
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
        if (!TCMixinConfigPlugin.NEOFORGE) {
            AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                if (!world.isClient()) return ActionResult.PASS;
                Boolean r = ClientAttackEntityCallback.EVENT.invoker().shouldCancel(MinecraftClient.getInstance().interactionManager, player, entity);
                if (r == null) return ActionResult.PASS;
                return r ? ActionResult.FAIL : ActionResult.SUCCESS;
            });
        }
    }
    @Contract(pure = true)
    public static boolean isNearHostile(Entity entity) {
        double range0 = TCConfigs.A().getHostileGlowRange();
        if (range0 > 0) {
            var player = MinecraftClient.getInstance().player;
            if (player != null && entity instanceof HostileEntity) {
                double range = range0 + Math.max(entity.getWidth(), entity.getHeight());
                if (entity.squaredDistanceTo(player) < range * range) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.setFlightEnabled(false));
    }
}
