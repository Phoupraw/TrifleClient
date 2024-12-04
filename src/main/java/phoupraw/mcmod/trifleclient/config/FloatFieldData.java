package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.autogen.FloatField;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public record FloatFieldData(float min, float max, String format) implements FloatField {
    @Override
    public Class<? extends Annotation> annotationType() {
        return FloatField.class;
    }
}
