package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
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
    private ConfigFieldImpl2<?> @Unmodifiable [] fields;
    private T instance;
    @Contract(pure = true)
    public ParentedConfigClassHandler(ConfigClassHandler<T> parent, Path path) {
        super(parent.configClass());
        this.parent = parent;
        this.path = path;
        instance(newInstance());
        setFields(false);
        serializer = GsonConfigSerializerBuilder.create(this)
          .setJson5(true)
          .setPath(path)
          .build();
    }
    private final ConfigSerializer<T> serializer;
    @Contract(pure = true)
    @Override
    public ConfigSerializer<T> serializer() {
        return serializer;
    }
    @Contract(pure = true)
    @Override
    public T defaults() {
        return parent.instance();
    }
    @Contract(pure = true)
    @Override
    public Class<T> configClass() {
        return parent.configClass();
    }
    @Contract(pure = true)
    @Override
    public Identifier id() {
        return parent.id();
    }
    @SneakyThrows
    @Override
    public void save() {
        setFields(true);
        if (fields().length == 0) {
            LOGGER.info("检测到{}与默认值相等，正在删除{}……", id(), path);
            try {
                if (Files.deleteIfExists(path)) {
                    Path dir = path;
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
                LOGGER.info("为{}删除了{}。", id(), path);
            } catch (IOException e) {
                LOGGER.error("为{}删除{}时错误！", id(), path);
                LOGGER.catching(e);
            }
        } else {
            LOGGER.info("正在将{}保存到{}……", id(), path);
            serializer().save();
            LOGGER.info("成功将{}保存到{}。", id(), path);
        }
        setFields(false);
    }
    @Contract(pure = true)
    @Override
    public ConfigFieldImpl2<?> @Unmodifiable [] fields() {
        return fields;
    }
    @Contract(pure = true)
    @Override
    public T instance() {
        return instance;
    }
    @SuppressWarnings("unchecked")
    @Override
    public boolean load() {
        if (Files.notExists(path)) {
            LOGGER.info("{}不存在，将{}设为默认值。", path, id());
            instance(newInstance());
            return false;
        }
        LOGGER.info("正在从{}读取{}中……", path, id());
        T newInstance = newInstance();
        Map<ConfigFieldImpl2<?>, DecoratedFieldAccess<?>> accessBuffer = new Object2ObjectLinkedOpenHashMap<>();
        for (var field : fields()) {
            accessBuffer.put(field, new DecoratedFieldAccess<>(field.access().field(), newInstance, field.access().annotations()));
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
                for (var field : toConfigFields(this, newInstance, false)) {
                    ((ConfigFieldImpl2<Object>) field).setFieldAccess((DecoratedFieldAccess<Object>) accessBuffer.get(field));
                }
                instance(newInstance);
                if (result == ConfigSerializer.LoadResult.DIRTY) {
                    save();
                }
            case NO_CHANGE:
                LOGGER.info("成功从{}读取了{}。", path, id());
                return true;
            case FAILURE:
                LOGGER.error("Unsuccessful load of config class '{}'. The load will be abandoned and config remains unchanged.", configClass().getSimpleName(), error);
        }
        return false;
    }
    @SneakyThrows
    @Contract(mutates = "this")
    protected void setFields(boolean ignoreSame) {
        fields = toConfigFields(this, instance(), ignoreSame).toArray(new ConfigFieldImpl2<?>[0]);
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
    protected void instance(T instance) {
        this.instance = instance;
    }
}
