package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.api.Binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DynamicBinding<T> implements Binding<T> {
    public static <T> DynamicBinding<T> of(Supplier<T> defaultGetter, Supplier<T> getter, Consumer<T> setter) {
        return new DynamicBinding<T>(defaultGetter, getter, setter);
    }
    private final Supplier<T> defaultGetter;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    public DynamicBinding(Supplier<T> defaultGetter, Supplier<T> getter, Consumer<T> setter) {
        this.setter = setter;
        this.getter = getter;
        this.defaultGetter = defaultGetter;
    }
    @Override
    public void setValue(T value) {
        setter.accept(value);
    }
    @Override
    public T getValue() {
        return getter.get();
    }
    @Override
    public T defaultValue() {
        return defaultGetter.get();
    }
}
