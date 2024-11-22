package phoupraw.mcmod.trifleclient.config;

import com.google.common.util.concurrent.Runnables;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.constant.TCIDs;

@ApiStatus.NonExtendable
public interface TCYACL {
    ConfigClassHandler<TCConfigs> HANDLER = ConfigClassHandler
      .createBuilder(TCConfigs.class)
      .id(TCIDs.of("cfg"))
      .serializer(handler -> null)
      //.serializer(handler-> GsonConfigSerializerBuilder.create(handler)
      //  .setPath(Path.of(handler.id().toTranslationKey()+".json5"))
      //  .setJson5(true)
      //  .build())
      .build();
    String MOD_ID = "yet_another_config_lib_v3";
    @ApiStatus.Internal
    static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, TCYACL::build).generateScreen(parent);
    }
    private static YetAnotherConfigLib.Builder build(TCConfigs defaults, TCConfigs config, YetAnotherConfigLib.Builder builder) {
        return builder
          .title(TrifleClient.name())
          .category(ConfigCategory.createBuilder()
            .name(Text.of("设置目前还在开发中，尚不全面，且不会保存到硬盘"))
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
          .save(Runnables.doNothing());
    }
    @ApiStatus.Internal
    static void assignConfig() {
        TCConfigs.A = HANDLER.instance();
    }
}
