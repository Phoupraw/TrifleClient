package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.autogen.DoubleField;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public record DoubleFieldData(double min, double max, String format) implements DoubleField {
    @Override
    public Class<? extends Annotation> annotationType() {
        return DoubleField.class;
    }
}
