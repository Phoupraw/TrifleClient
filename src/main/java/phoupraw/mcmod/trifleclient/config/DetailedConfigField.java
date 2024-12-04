package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.impl.ConfigFieldImpl;
import dev.isxander.yacl3.config.v2.impl.ReflectionFieldAccess;
import org.jetbrains.annotations.Nullable;

public class DetailedConfigField<T> extends ConfigFieldImpl<T> {
    public final @Nullable AutoGenDetails autoGenDetails;
    public DetailedConfigField(ReflectionFieldAccess<T> field, ReflectionFieldAccess<T> defaultField, ConfigClassHandler<?> parent, @Nullable SerialEntry config, @Nullable AutoGen autoGen, @Nullable AutoGenDetails autoGenDetails) {
        super(field, defaultField, parent, config, autoGen);
        this.autoGenDetails = autoGenDetails;
    }
}
