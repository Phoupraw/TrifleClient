package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.impl.autogen.YACLAutoGenException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public record DecoratedFieldAccess<T>(Field field, Object instance, @Unmodifiable Map<Class<? extends Annotation>, Annotation> annotations) implements FieldAccess<T> {
    public DecoratedFieldAccess(Field field, Object instance, Iterable<? extends Annotation> annotations) {
        this(field, instance, toMap(annotations));
    }
    public static @NotNull Map<Class<? extends Annotation>, Annotation> toMap(Iterable<? extends Annotation> annotations) {
        Map<Class<? extends Annotation>, Annotation> annotationMap = new Object2ObjectOpenHashMap<>();
        for (Annotation annotation : annotations) {
            annotationMap.put(annotation.annotationType(), annotation);
        }
        return annotationMap;
    }
    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new YACLAutoGenException("Failed to access field '%s'".formatted(name()), e);
        }
    }
    
    @Override
    public void set(T value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new YACLAutoGenException("Failed to set field '%s'".formatted(name()), e);
        }
    }
    
    @Override
    public String name() {
        return field.getName();
    }
    
    @Override
    public Type type() {
        return field.getGenericType();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<T> typeClass() {
        return (Class<T>) field.getType();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
        A declared = field.getAnnotation(annotationClass);
        return declared != null ? Optional.of(declared) : Optional.ofNullable((A) annotations().get(annotationClass));
    }
}
