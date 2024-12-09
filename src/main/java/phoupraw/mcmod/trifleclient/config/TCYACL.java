package phoupraw.mcmod.trifleclient.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.config.v2.impl.serializer.GsonConfigSerializer;
import dev.isxander.yacl3.gui.YACLScreen;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.constant.TCIDs;
import phoupraw.mcmod.trifleclient.constant.TCKeyBindings;

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.WeakHashMap;

import static phoupraw.mcmod.trifleclient.TrifleClient.ID;
import static phoupraw.mcmod.trifleclient.config.DynamicBinding.of;
import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

@ApiStatus.NonExtendable
public interface TCYACL {
    String MOD_ID = "yet_another_config_lib_v3";
    String FILE_NAME = ID + ".config.json5";
    Identifier CONFIG_ID = TCIDs.of("c");
    ConfigClassHandler<TCConfigs> HANDLER = new ParentedConfigClassHandler<>(new RootConfigClassHandler<>(TCConfigs.class, CONFIG_ID), FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME), "顶层");
    Map<Object, ConfigClassHandler<TCConfigs>> CACHE = new WeakHashMap<>();
    @ApiStatus.Internal
    static Screen createScreen(Screen parent) {
        ConfigClassHandler<TCConfigs> config = getConfig();
        config.load();
        if (true) {
            return toYACL(config).generateScreen(parent);
        } else {
            return config.generateGui().generateScreen(parent);
        }
    }
    private static YetAnotherConfigLib toYACL(ConfigClassHandler<TCConfigs> config) {
        TCConfigs defaults = config.defaults();
        TCConfigs instance = config.instance();
        return YetAnotherConfigLib.createBuilder()
          .title(TrifleClient.name())
          .category(ConfigCategory.createBuilder()
            .name(TrifleClient.name())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("自动暴击"))
              .description(OptionDescription.of(Text.of("每次攻击之前向服务端发送上移和下移的信包。")))
              .binding(of(instance::isAutoCrit, defaults::isAutoCrit, instance::setAutoCrit))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("减免摔落伤害"))
              .description(OptionDescription.of(Text.of("频繁向服务端发送落地信包。")))
              .binding(of(defaults::isOftenOnGround, instance::isOftenOnGround, instance::setOftenOnGround))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("鞘翅取消飞行同步"))
              .description(OptionDescription.of(Text.of("穿着鞘翅时，忽略从服务端来的能力同步信包中的飞行能力同步。")))
              .binding(of(defaults::isElytraCancelSyncFlying, instance::isElytraCancelSyncFlying, instance::setElytraCancelSyncFlying))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("鞘翅自由飞行"))
              .description(OptionDescription.of(Text.of("穿着鞘翅时可以如同在创造模式一样自由飞行。可能会在服务端错误移动，注意不要移动得过于刁钻。")))
              .binding(of(defaults::isFreeElytraFlying, instance::isFreeElytraFlying, instance::setFreeElytraFlying))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("钩到实体自动切竿"))
              .description(OptionDescription.of(Text.of("当鱼钩钩到实体时，立刻把鱼竿从主手拿开，然后再放回并重新抛竿。")))
              .binding(of(defaults::isSwitchOnHook, instance::isSwitchOnHook, instance::setSwitchOnHook))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("服务器启用飞行"))
              .description(OptionDescription.of(Text.of("原版默认为true。")))
              .binding(of(defaults::isAllowFlight, instance::isAllowFlight, instance::setAllowFlight))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("自动采摘"))
              .description(OptionDescription.of(Text.of("自动采摘周围的浆果等作物。")))
              .binding(of(defaults::isAutoPick, instance::isAutoPick, instance::setAutoPick))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .option(Option.<String>createBuilder()
              .name(Text.of("自动采摘方块列表"))
              .description(OptionDescription.of(Text.of("语法：方块ID[方块状态=值,方块状态=值]|方块ID[方块状态=值]")))
              .binding(of(defaults::getAutoPickBlocks, instance::getAutoPickBlocks, instance::setAutoPickBlocks))
              .controller(StringControllerBuilder::create)
              .build())
            .option(Option.<Integer>createBuilder()
              .name(Text.of("快速移速"))
              .description(OptionDescription.of(Text.empty()
                .append("按住")
                .append(Text.empty().append(TCKeyBindings.SPEED.getBoundKeyLocalizedText()).formatted(Formatting.UNDERLINE))
                .append("同时按住移动键可以高速水平移动。此设置项为每次移动时额外移动的次数。")
              ))
              .binding(of(defaults::getSpeedSteps, instance::getSpeedSteps, instance::setSpeedSteps))
              .controller(option -> IntegerSliderControllerBuilder.create(option)
                .range(0, 50)
                .step(1))
              .build())
            .option(Option.<Float>createBuilder()
              .name(Text.of("环境亮度"))
              .description(OptionDescription.of(Text.of("每个维度的最低环境亮度不会低于此值。注意：只需很小的值就可以让整个维度非常亮。")))
              .binding(of(defaults::getMinAmbientLight, instance::getMinAmbientLight, instance::setMinAmbientLight))
              .controller(option -> FloatFieldControllerBuilder.create(option)
                .range(0f, 1f)
                .formatValue(value -> Text.literal(DecimalFormat.getInstance().format(value))))
              .build())
            .option(Option.<Float>createBuilder()
              .name(Text.of("最大MSPT"))
              .description(OptionDescription.of(Text.of("客户端的最大MSPT。原版无上限。如果设定为50，则即便TPS小于20，也会视为20。")))
              .binding(of(defaults::getMaxMSPT, instance::getMaxMSPT, instance::setMaxMSPT))
              .controller(option -> FloatFieldControllerBuilder.create(option)
                .min(50f)
                .formatValue(value -> Text.literal(DecimalFormat.getInstance().format(value))))
              .build())
            .option(Option.<Float>createBuilder()
              .name(Text.of("最小MSPT"))
              .description(OptionDescription.of(Text.of("客户端的最小MSPT。原版为50，所以即使用tick指令将TPS调至20以上，客户端仍然保持TPS为20。")))
              .binding(of(defaults::getMinMSPT, instance::getMinMSPT, instance::setMinMSPT))
              .controller(option -> FloatFieldControllerBuilder.create(option)
                .range(0f, 50f)
                .formatValue(value -> Text.literal(DecimalFormat.getInstance().format(value))))
              .build())
            .option(Option.<Double>createBuilder()
              .name(Text.of("高亮怪物范围"))
              .description(OptionDescription.of(Text.of("使范围内的敌对生物带有深红色边框，且一直在头顶显示名称。")))
              .binding(of(defaults::getHostileGlowRange, instance::getHostileGlowRange, instance::setHostileGlowRange))
              .controller(option -> DoubleSliderControllerBuilder.create(option)
                .range(0.0, 128.0)
                .step(1.0)
                .formatValue(value -> Text.literal(DecimalFormat.getInstance().format(value))))
              .build())
            .build())
          .category(ConfigCategory.createBuilder()
            .name(Text.of("调试"))
            .option(Option.<Boolean>createBuilder()
              .name(Text.of("攻击实体"))
              .description(OptionDescription.of(Text.empty()
                .append("对准实体按下")
                .append(Text.empty().append(MinecraftClient.getInstance().options.attackKey.getBoundKeyLocalizedText()).formatted(Formatting.UNDERLINE))
                .append("以攻击实体时，打印相关信息到控制台")
              ))
              .binding(of(defaults::isDebugAttackEntity, instance::isDebugAttackEntity, instance::setDebugAttackEntity))
              .controller(TickBoxControllerBuilder::create)
              .build())
            .build())
          .category(ConfigCategory.createBuilder()
            .name(Text.of(config instanceof ParentedConfigClassHandler<TCConfigs> parented ? parented.name() : "默认"))
            .optionIf(config instanceof ParentedConfigClassHandler<TCConfigs>, ButtonOption.createBuilder()
              .name(Text.of("前往上级设置界面"))
              .action((screen, option) -> {
                  if (config instanceof ParentedConfigClassHandler<TCConfigs> parented) {
                      MinecraftClient.getInstance().setScreen(toYACL(parented.parent()).generateScreen(screen));
                      //FIXME 修改上级后，下级的instance默认值需要退出界面重进（即触发一次load）才会随上级生效
                  }
              })
              .build())
            .build())
          .save(config::save)
          .build();
    }
    @ApiStatus.Internal
    static void assignConfig() {
        TCConfigs.EVENT.register(CONFIG_ID, () -> getConfig().instance());
        TCConfigs.EVENT.addPhaseOrdering(CONFIG_ID, TCIDs.of("a"));
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.remove(screen).register(screen1 -> {
                if (screen1 instanceof YACLScreen yaclScreen) {
                
                }
            });
        });
    }
    static ConfigClassHandler<TCConfigs> getConfig(MinecraftServer server) {
        String key = server.getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();
        ConfigClassHandler<TCConfigs> handler = CACHE.get(key);
        if (handler == null) {
            Path path = server.getSavePath(WorldSavePath.ROOT).resolve("config").resolve(FILE_NAME);
            handler = new ParentedConfigClassHandler<>(HANDLER, path, key);
            CACHE.put(key, handler);
        }
        return handler;
    }
    static ConfigClassHandler<TCConfigs> getConfig(ServerInfo serverInfo) {
        String key = serverInfo.address;
        ConfigClassHandler<TCConfigs> handler = CACHE.get(key);
        if (handler == null) {
            Path path = FabricLoader.getInstance().getConfigDir().resolve("servers").resolve(key).resolve(FILE_NAME);
            handler = new ParentedConfigClassHandler<>(HANDLER, path, key);
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
    private static ConfigSerializer<TCConfigs> toSerializer(ConfigClassHandler<TCConfigs> handler) {
        return GsonConfigSerializerBuilder.create(handler)
          .setPath(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME))
          .setJson5(true)
          .build();
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
}
