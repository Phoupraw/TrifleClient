package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGenField;
import dev.isxander.yacl3.config.v2.impl.ConfigFieldImpl;
import dev.isxander.yacl3.config.v2.impl.ReflectionFieldAccess;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionAccessImpl;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionFactoryRegistry;
import dev.isxander.yacl3.config.v2.impl.autogen.YACLAutoGenException;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.SneakyThrows;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public abstract class BaseConfigClassHandler<T> implements ConfigClassHandler<T> {
    private final Constructor<T> noArgsConstructor;
    @Contract(pure = true)
    public BaseConfigClassHandler(Class<T> configClass) {
        try {
            noArgsConstructor = configClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new YACLAutoGenException("Failed to find no-args constructor for config class %s.".formatted(configClass().getName()), e);
        }
    }
    @Contract(pure = true)
    @Override
    public boolean supportsAutoGen() {
        return false;
    }
    @Contract(pure = true)
    @Override
    public YetAnotherConfigLib generateGui() {
        var storage = new OptionAccessImpl();
        var categories = new Object2ObjectLinkedOpenHashMap<String, Pair<ConfigCategory.Builder, Map<String, OptionAddable>>>();
        for (var field : fields()) {
            var autoGen0 = field.autoGen();
            if (autoGen0.isEmpty()) continue;
            AutoGenField autoGen = autoGen0.get();
            String categoryKey = autoGen.category();
            var category = categories.get(categoryKey);
            if (category == null) {
                category = Pair.of(
                  ConfigCategory.createBuilder().name(Text.translatable("yacl3.config.%s.category.%s".formatted(id().toString(), categoryKey))),
                  new Object2ObjectLinkedOpenHashMap<>()
                );
                categories.put(categoryKey, category);
            }
            String groupKey = autoGen.group().orElse("");
            var group = category.second().get(groupKey);
            if (group == null) {
                if (groupKey.isEmpty()) {
                    group = category.first();
                } else {
                    group = OptionGroup.createBuilder().name(Text.translatable("yacl3.config.%s.category.%s.group.%s".formatted(id().toString(), autoGen.category(), groupKey)));
                }
                category.second().put(groupKey, group);
            }
            Option<?> option;
            try {
                var option0 = OptionFactoryRegistry.createOption(field.access().field(), field, storage);
                if (option0.isEmpty()) {
                    throw new YACLAutoGenException("Failed to create option for field '%s'".formatted(field.access().name()));
                }
                option = option0.get();
            } catch (YACLAutoGenException e) {
                throw e;
            } catch (Throwable e) {
                throw new YACLAutoGenException("Failed to create option for field '%s'".formatted(field.access().name()), e);
            }
            storage.putOption(field.access().name(), option);
            group.option(option);
        }
        storage.checkBadOperations();
        for (var category : categories.values()) {
            for (var entry : category.second().entrySet()) {
                if (entry.getValue() instanceof OptionGroup.Builder groupBuilder) {
                    category.first().group(groupBuilder.build());
                }
            }
        }
        var yaclBuilder = YetAnotherConfigLib.createBuilder()
          .save(this::save)
          .title(Text.translatable("yacl3.config.%s.title".formatted(id())));
        for (var category : categories.values()) {
            yaclBuilder.category(category.first().build());
        }
        return yaclBuilder.build();
    }
    //@SuppressWarnings("unchecked")
    //@Override
    //public abstract boolean load();
    @Contract(pure = true)
    @Override
    public abstract ConfigFieldImpl<?> @Unmodifiable [] fields();
    @Contract(pure = true)
    @SneakyThrows
    protected T newInstance() {
        return noArgsConstructor.newInstance();
    }
    @SneakyThrows
    @Contract(pure = true)
    public static <T> Collection<? extends ConfigFieldImpl<?>> toFields(ConfigClassHandler<T> handler, T instance, boolean ignoreSame) {
        var fields = new ObjectArrayList<ConfigFieldImpl<?>>();
        var serial = new SerialEntryData("", "", false, false);
        for (Field field : handler.configClass().getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
                continue;
            }
            if (!field.trySetAccessible()) {
                continue;
            }
            if (ignoreSame && Objects.equals(field.get(instance), field.get(handler.defaults()))) {
                continue;
            }
            var configField = new ConfigFieldImpl<>(
              new ReflectionFieldAccess<>(field, instance),
              new ReflectionFieldAccess<>(field, handler.defaults()),
              handler,
              Objects.requireNonNullElse(field.getAnnotation(SerialEntry.class), serial),
              field.getAnnotation(AutoGen.class)
            );
            fields.add(configField);
        }
        return fields;
    }
    
}