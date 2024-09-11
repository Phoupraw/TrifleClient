package phoupraw.mcmod.trifleclient.config;

import com.mojang.serialization.Codec;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trilevel_config.api.ConfigKey;
import phoupraw.mcmod.trilevel_config.api.SimpleConfigKey;

import java.nio.file.Path;

public interface FSDConfigs {
    Path PATH = Path.of(TrifleClient.ID + ".json");
    ConfigKey<Boolean> ON = new SimpleConfigKey<>("on", Codec.BOOL, true);
}
