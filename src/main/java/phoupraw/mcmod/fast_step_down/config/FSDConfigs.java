package phoupraw.mcmod.fast_step_down.config;

import com.mojang.serialization.Codec;
import phoupraw.mcmod.fast_step_down.FastStepDown;
import phoupraw.mcmod.trilevel_config.api.ConfigKey;
import phoupraw.mcmod.trilevel_config.api.SimpleConfigKey;

import java.nio.file.Path;

public interface FSDConfigs {
    Path PATH = Path.of(FastStepDown.ID + ".json");
    ConfigKey<Boolean> ON = new SimpleConfigKey<>("on", Codec.BOOL, true);
}
