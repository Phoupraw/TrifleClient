package phoupraw.mcmod.trifleclient;

import lombok.SneakyThrows;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;
import phoupraw.mcmod.trifleclient.events.OnClientPlayerMove;
import phoupraw.mcmod.trifleclient.misc.*;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
//TODO 使在流体中行走不减速
/**
 @apiNote {@link TargetPointer} */
@Environment(EnvType.CLIENT)
public final class TrifleClient implements ModInitializer, ClientModInitializer {
    public static final String ID = "trifleclient";
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger(ID);
    @SneakyThrows
    private static void loadClasses() {
        for (var cls : Arrays.asList(TargetPointer.class, BlockFinder.class, BlockHighlighter.class, TCKeyBindings.class, MiningDelay.class, AutoAttacker.class)) {
            MethodHandles.lookup().ensureInitialized(cls);
        }
    }
    @Override
    public void onInitializeClient() {
        loadClasses();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> server.setFlightEnabled(false));
        OnClientPlayerMove.EVENT.register(NormalSpeed::onClientPlayerMove);
        OnClientPlayerMove.EVENT.register(SpeedSpeed::onClientPlayerMove);
        AttackEntityCallback.EVENT.register(AutoCrit::interact);
    }
    @Override
    public void onInitialize() {
    
    }
}
