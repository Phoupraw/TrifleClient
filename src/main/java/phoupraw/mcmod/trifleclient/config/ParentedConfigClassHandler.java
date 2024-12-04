package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.config.v2.impl.ConfigFieldImpl;
import dev.isxander.yacl3.config.v2.impl.ReflectionFieldAccess;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.SneakyThrows;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static phoupraw.mcmod.trifleclient.mixins.TCMixinConfigPlugin.LOGGER;

public class ParentedConfigClassHandler<T> extends BaseConfigClassHandler<T> {
    private final ConfigClassHandler<T> parent;
    private final Path path;
    public Path path() {
        return path;
    }
    private final ConfigSerializer<T> serializer;
    private ConfigFieldImpl<?> @Unmodifiable [] fields;
    private final T instance;
    private final String name;
    @Contract(pure = true)
    public ParentedConfigClassHandler(ConfigClassHandler<T> parent, Path path, String name) {
        super(parent.configClass());
        this.parent = parent;
        this.path = path;
        this.name = name;
        this.instance = newInstance();
        setFields(false);
        serializer = GsonConfigSerializerBuilder.create(this)
          .setJson5(true)
          .setPath(path)
          .build();
    }
    @Contract(pure = true)
    @Override
    public ConfigFieldImpl<?> @Unmodifiable [] fields() {
        return fields;
    }
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    protected T newInstance() {
        T instance = super.newInstance();
        for (var field : toConfigFields(this, instance, false)) {
            ((ConfigField<Object>) field).access().set(field.defaultAccess().get());
        }
        return instance;
    }
    @Contract(pure = true)
    @Override
    public T instance() {
        return instance;
    }
    @Contract(pure = true)
    @Override
    public T defaults() {
        return parent().instance();
    }
    @Contract(pure = true)
    @Override
    public Class<T> configClass() {
        return parent().configClass();
    }
    @Contract(pure = true)
    @Override
    public Identifier id() {
        return parent().id();
    }
    @SuppressWarnings("unchecked")
    @Override
    public boolean load() {
        if (Files.notExists(path())) {
            LOGGER.info("{}不存在，将{}设为默认值。", path(), id());
            reset();
            return false;
        }
        LOGGER.info("正在从{}读取{}中……", path(), id());
        T newInstance = newInstance();
        Map<ConfigFieldImpl<?>, ReflectionFieldAccess<?>> accessBuffer = new Object2ObjectLinkedOpenHashMap<>();
        for (var field : fields()) {
            accessBuffer.put(field, new ReflectionFieldAccess<>(field.access().field(), newInstance));
        }
        ConfigSerializer.LoadResult result;
        Throwable error = null;
        try {
            result = serializer().loadSafely((Map<ConfigField<?>, FieldAccess<?>>) (Map<? extends ConfigField<?>, ? extends FieldAccess<?>>) accessBuffer);
        } catch (Throwable e) {
            result = ConfigSerializer.LoadResult.FAILURE;
            error = e;
        }
        switch (result) {
            case DIRTY:
            case SUCCESS:
                //for (var field : toConfigFields(this, newInstance, false)) {
                //    ((ConfigFieldImpl<Object>) field).setFieldAccess((ReflectionFieldAccess<Object>) accessBuffer.get(field));
                //}
                for (var field : fields()) {
                    ReflectionFieldAccess<?> newField = accessBuffer.get(field);
                    if (newField != null) {
                        ((ConfigField<Object>) field).access().set(newField.get());
                    }
                }
                //instance(newInstance);
                if (result == ConfigSerializer.LoadResult.DIRTY) {
                    save();
                }
            case NO_CHANGE:
                LOGGER.info("成功从{}读取了{}。", path(), id());
                return true;
            case FAILURE:
                LOGGER.error("Unsuccessful load of config class '{}'. The load will be abandoned and config remains unchanged.", configClass().getSimpleName(), error);
        }
        return false;
    }
    @SneakyThrows
    @Override
    public void save() {
        setFields(true);
        if (fields().length == 0) {
            LOGGER.info("检测到{}与默认值相等，正在删除{}……", id(), path());
            try {
                if (Files.deleteIfExists(path())) {
                    Path dir = path();
                    while (true) {
                        dir = dir.getParent();
                        try (var files = Files.list(dir)) {
                            if (files.iterator().hasNext()) {
                                break;
                            }
                            Files.delete(dir);
                        }
                    }
                }
                LOGGER.info("为{}删除了{}。", id(), path());
            } catch (IOException e) {
                LOGGER.error("为{}删除{}时错误！", id(), path());
                LOGGER.catching(e);
            }
        } else {
            LOGGER.info("正在将{}保存到{}……", id(), path());
            serializer().save();
            LOGGER.info("成功将{}保存到{}。", id(), path());
        }
        setFields(false);
    }
    @Contract(pure = true)
    @Override
    public ConfigSerializer<T> serializer() {
        return serializer;
    }
    public ConfigClassHandler<T> parent() {
        return parent;
    }
    public String name() {
        return name;
    }
    @SneakyThrows
    @Contract(mutates = "this")
    protected void setFields(boolean ignoreSame) {
        fields = toConfigFields(this, instance(), ignoreSame).toArray(new ConfigFieldImpl<?>[0]);
    }
}
