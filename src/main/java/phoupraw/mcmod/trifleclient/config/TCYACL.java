package phoupraw.mcmod.trifleclient.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.config.v2.impl.serializer.GsonConfigSerializer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.constant.TCIDs;

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.WeakHashMap;

import static phoupraw.mcmod.trifleclient.TrifleClient.ID;
import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@ApiStatus.NonExtendable
public interface TCYACL {
    String MOD_ID = "yet_another_config_lib_v3";
    String FILE_NAME = ID + ".json5";
    ConfigClassHandler<TCConfigs> HANDLER = new ParentedConfigClassHandler<>(new RootConfigClassHandler<>(TCConfigs.class, TCIDs.of("c")), FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
    @ApiStatus.Internal
    static Screen createScreen(Screen parent) {
        ConfigClassHandler<TCConfigs> config = getConfig();
        config.load();
        return YetAnotherConfigLib.create(config, TCYACL::build).generateScreen(parent);
    }
    private static YetAnotherConfigLib.Builder build(TCConfigs defaults, TCConfigs config, YetAnotherConfigLib.Builder builder) {
        return builder
          .title(TrifleClient.name())
          .category(ConfigCategory.createBuilder()
            .name(Text.of("设置目前还在开发中，尚不全面，且不会保存到硬盘"))
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("自动暴击"))
              .binding(defaults.isAutoCrit(), config::isAutoCrit, config::setAutoCrit)
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("鞘翅取消同步飞行能力"))
              .binding(defaults.isElytraCancelSyncFlying(), config::isElytraCancelSyncFlying, config::setElytraCancelSyncFlying)
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("鞘翅创造飞行"))
              .binding(defaults.isFreeElytraFlying(), config::isFreeElytraFlying, config::setFreeElytraFlying)
              .controller(TickBoxControllerBuilder::create)
              .build())
            .build())
          /*.save(Runnables.doNothing())*/;
    }
    @ApiStatus.Internal
    static void assignConfig() {
        TCConfigs.A = HANDLER.instance();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            TCConfigs.A = getConfig(server).instance();
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            TCConfigs.A = HANDLER.instance();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            TCConfigs.A = getConfig(handler).instance();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            TCConfigs.A = HANDLER.instance();
        });
    }
    private static ConfigSerializer<TCConfigs> toSerializer(ConfigClassHandler<TCConfigs> handler) {
        return GsonConfigSerializerBuilder.create(handler)
          .setPath(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME))
          .setJson5(true)
          .build();
    }
    private static void onPlayInit(ClientPlayNetworkHandler handler, MinecraftClient client) {
        TCConfigs.A = getConfig().instance();
    }
    @Deprecated
    class Serializer<T> extends ConfigSerializer<T> {
        public static final Gson GSON = new GsonBuilder()
          .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
          .serializeNulls()
          .registerTypeHierarchyAdapter(Text.class, new Text.Serializer(DynamicRegistryManager.EMPTY))
          .registerTypeHierarchyAdapter(Style.class, new GsonConfigSerializer.StyleTypeAdapter())
          .registerTypeHierarchyAdapter(Color.class, new GsonConfigSerializer.ColorTypeAdapter())
          .registerTypeHierarchyAdapter(Item.class, new GsonConfigSerializer.ItemTypeAdapter())
          .setPrettyPrinting()
          .setLenient()
          .create();
        public Serializer(ConfigClassHandler<T> config) {
            super(config);
        }
        @Override
        public void save() {
            LOGGER.info("Serializing {} to '{}'", config().configClass(), path());
            Map<String, JsonElement> configChanges = new Object2ObjectLinkedOpenHashMap<>();
            
            try (StringWriter stringWriter = new StringWriter()) {
                JsonWriter jsonWriter = new JsonWriter(stringWriter);
                
                jsonWriter.beginObject();
                
                for (ConfigField<?> field : config().fields()) {
                    SerialField serial = field.serial().orElse(null);
                    if (serial == null) continue;
                    
                    //jsonWriter.comment(serial.comment().orElse(null));
                    
                    jsonWriter.name(serial.serialName());
                    
                    JsonElement element;
                    try {
                        element = gson().toJsonTree(field.access().get(), field.access().type());
                    } catch (Exception e) {
                        LOGGER.error("Failed to serialize config field '{}'. Serializing as null.", serial.serialName(), e);
                        jsonWriter.nullValue();
                        continue;
                    }
                    
                    try {
                        gson().toJson(element, jsonWriter);
                    } catch (Exception e) {
                        LOGGER.error("Failed to serialize config field '{}'. Due to the error state this JSON writer cannot continue safely and the save will be abandoned.", serial.serialName(), e);
                        return;
                    }
                }
                
                jsonWriter.endObject();
                jsonWriter.flush();
                
                Files.createDirectories(path().getParent());
                Files.writeString(path(), stringWriter.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            } catch (IOException e) {
                LOGGER.error("Failed to serialize config class '{}'.", config().configClass().getSimpleName(), e);
            }
        }
        @Override
        public LoadResult loadSafely(Map<ConfigField<?>, FieldAccess<?>> bufferAccessMap) {
            return super.loadSafely(bufferAccessMap);
        }
        public ConfigClassHandler<T> config() {
            return config;
        }
        public Gson gson() {
            return GSON;
        }
        public Path path() {
            return Path.of("");
        }
    }
    
    Map<Object, ConfigClassHandler<TCConfigs>> CACHE = new WeakHashMap<>();
    static ConfigClassHandler<TCConfigs> getConfig(MinecraftServer server) {
        String key = server.getName();
        ConfigClassHandler<TCConfigs> handler = CACHE.get(key);
        if (handler == null) {
            Path path = server.getSavePath(WorldSavePath.ROOT).resolve("config").resolve(FILE_NAME);
            handler = new ParentedConfigClassHandler<>(HANDLER, path);
            CACHE.put(key, handler);
        }
        return handler;
    }
    static ConfigClassHandler<TCConfigs> getConfig(ServerInfo serverInfo) {
        String key = serverInfo.address;
        ConfigClassHandler<TCConfigs> handler = CACHE.get(key);
        if (handler == null) {
            Path path = FabricLoader.getInstance().getConfigDir().resolve("servers").resolve(key).resolve(FILE_NAME);
            handler = new ParentedConfigClassHandler<>(HANDLER, path);
            CACHE.put(key, handler);
        }
        return handler;
    }
    static ConfigClassHandler<TCConfigs> getConfig(ClientPlayNetworkHandler networkHandler) {
        ServerInfo serverInfo = networkHandler.getServerInfo();
        if (serverInfo != null) {
            return getConfig(serverInfo);
        }
        return getConfig();
    }
    static ConfigClassHandler<TCConfigs> getConfig() {
        IntegratedServer server = MinecraftClient.getInstance().getServer();
        if (server != null) {
            return getConfig(server);
        }
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        if (serverInfo != null) {
            return getConfig(serverInfo);
        }
        return HANDLER;
    }
}
