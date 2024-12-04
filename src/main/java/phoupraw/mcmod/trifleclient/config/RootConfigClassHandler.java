package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

public class RootConfigClassHandler<T> extends BaseConfigClassHandler<T> {
    private final T defaults;
    private final Class<T> configClass;
    private final Identifier id;
    private final ConfigFieldImpl2<?> @Unmodifiable [] fields;
    @Contract(pure = true)
    public RootConfigClassHandler(Class<T> configClass, Identifier id) {
        super(configClass);
        this.configClass = configClass;
        this.id = id;
        defaults = newInstance();
        fields = toConfigFields(this, instance(), false).toArray(new ConfigFieldImpl2<?>[0]);
    }
    @Contract(pure = true)
    @Override
    public T defaults() {
        return defaults;
    }
    @Contract(pure = true)
    @Override
    public Class<T> configClass() {
        return configClass;
    }
    @Contract(pure = true)
    @Override
    public ConfigFieldImpl2<?> @Unmodifiable [] fields() {
        return fields;
    }
    @Contract(pure = true)
    @Override
    public Identifier id() {
        return id;
    }
    @Contract(pure = true)
    @Override
    public T instance() {
        return defaults();
    }
    @Contract(pure = true)
    @Override
    public boolean load() {
        return false;
    }
    @Contract(pure = true)
    @Override
    public void save() {
    
    }
    @Contract("->fail")
    @Override
    public ConfigSerializer<T> serializer() {
        throw new UnsupportedOperationException();
    }
}
