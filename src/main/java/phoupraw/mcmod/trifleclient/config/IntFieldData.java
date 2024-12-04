package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.autogen.IntField;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public record IntFieldData(int min, int max, String format) implements IntField {
    @Override
    public Class<? extends Annotation> annotationType() {
        return IntField.class;
    }
}
