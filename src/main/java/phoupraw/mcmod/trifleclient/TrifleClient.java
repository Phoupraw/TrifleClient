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
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import phoupraw.mcmod.trifleclient.compact.MekanismCompact;
import phoupraw.mcmod.trifleclient.compact.MekanismWeaponsCompact;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.config.TCYACL;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.events.AfterClientPlayerMove;
import phoupraw.mcmod.trifleclient.events.OnClientPlayerMove;
import phoupraw.mcmod.trifleclient.events.OnUseKeyPress;
import phoupraw.mcmod.trifleclient.misc.*;

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
        ServerLifecycleEvents.SERVER_STARTING.register(server -> server.setFlightEnabled(false));
        AfterClientPlayerMove.EVENT.register(NormalSpeed::afterClientPlayerMove);
        OnClientPlayerMove.EVENT.register(SpeedSpeed::onClientPlayerMove);
        AttackEntityCallback.EVENT.register(AutoCrit::interact);
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
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            var player = MinecraftClient.getInstance().player;
            if (TCConfigs.A().isOftenOnGround() && player != null && !player.isClimbing() && !player.isSwimming() && /*!FreeElytraFlying.isFlying(player) &&*/ (player.getVelocity().getY() < 0/* || player.getAbilities().flying */) && !player.isFallFlying()) {
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
    }
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.setFlightEnabled(false));
    }
}
