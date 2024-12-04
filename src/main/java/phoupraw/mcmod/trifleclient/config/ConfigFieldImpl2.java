package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGenField;
import dev.isxander.yacl3.config.v2.impl.ReflectionFieldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConfigFieldImpl2<T> implements ConfigField<T> {
    private ReflectionFieldAccess<T> field;
    private final ReadOnlyFieldAccess<T> defaultAccess;
    private final ConfigClassHandler<?> parent;
    private final SerialField serial;
    private final AutoGenField autoGen;
    
    public ConfigFieldImpl2(ReflectionFieldAccess<T> field, ReadOnlyFieldAccess<T> defaultAccess, ConfigClassHandler<?> parent, @Nullable SerialEntry serial, @Nullable AutoGen autoGen) {
        this.field = field;
        this.defaultAccess = defaultAccess;
        this.parent = parent;
        this.serial = serial == null ? null : new SerialFieldImpl(
          "".equals(serial.value()) ? field.name() : serial.value(),
          "".equals(serial.comment()) ? Optional.empty() : Optional.of(serial.comment()),
          serial.required(),
          serial.nullable()
        );
        this.autoGen = autoGen == null ? null : new AutoGenFieldImpl(
          autoGen.category(),
          "".equals(autoGen.group()) ? Optional.empty() : Optional.of(autoGen.group())
        );
    }
    
    @Override
    public ReflectionFieldAccess<T> access() {
        return field;
    }
    
    public void setFieldAccess(ReflectionFieldAccess<T> field) {
        this.field = field;
    }
    
    @Override
    public ReadOnlyFieldAccess<T> defaultAccess() {
        return defaultAccess;
    }
    
    @Override
    public ConfigClassHandler<?> parent() {
        return parent;
    }
    
    @Override
    public Optional<SerialField> serial() {
        return Optional.ofNullable(serial);
    }
    
    @Override
    public Optional<AutoGenField> autoGen() {
        return Optional.ofNullable(autoGen);
    }
    
    private record SerialFieldImpl(String serialName, Optional<String> comment, boolean required, boolean nullable) implements SerialField {
    }
    
    private record AutoGenFieldImpl(String category, Optional<String> group) implements AutoGenField {
    }
}
