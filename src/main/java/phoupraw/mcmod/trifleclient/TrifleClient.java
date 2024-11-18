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
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.compact.MekanismCompact;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.events.AfterClientPlayerMove;
import phoupraw.mcmod.trifleclient.events.OnClientPlayerMove;
import phoupraw.mcmod.trifleclient.events.OnUseKeyPress;
import phoupraw.mcmod.trifleclient.misc.*;

import java.lang.invoke.MethodHandles;
//TODO 使在流体中行走不减速
//TODO 稀有度不为常见或有自定义名称的物品始终发光，其它物品和经验球则有不可穿墙的发光轮廓

/**
 @see TargetPointer */
@Environment(EnvType.CLIENT)
public final class TrifleClient implements ModInitializer, ClientModInitializer {
    public static final String ID = "trifleclient";
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger();
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
        ClientCommandRegistrationCallback.EVENT.register(AutoCrit::register);
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            var player = MinecraftClient.getInstance().player;
            if (TCConfigs.A.isOftenOnGround() && player != null && !player.isClimbing() && !player.isSwimming() && /*!FreeElytraFlying.isFlying(player) &&*/ (player.getVelocity().getY() < 0/* || player.getAbilities().flying */) && !player.isFallFlying()) {
                player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
                //player.stopFallFlying();
            }
        });
        ClientTickEvents.END_WORLD_TICK.register(FreeElytraFlying::lambda_onEndTick);
        //ClientPlayConnectionEvents.JOIN.register(new ClientPlayConnectionEvents.Join() {
        //    @Override
        //    public void onPlayReady(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        //        ClientPlayerEntity player = client.player;
        //        if (FreeElytraFlying.isFlying(player)) {
        //            player.get
        //        }
        //    }
        //});
        if (FabricLoader.getInstance().isModLoaded(MekanismCompact.MOD_ID)) {
            TrifleClient.LOGGER.info("检测到《通用机械》，将加载相关兼容。");
            AutoAttacker.WEAPON.register(MekanismCompact::isWeapon);
        }
    }
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.setFlightEnabled(false));
    }
}
