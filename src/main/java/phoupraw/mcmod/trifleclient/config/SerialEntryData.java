package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public record SerialEntryData(String value, String comment, boolean required, boolean nullable) implements SerialEntry {
    @Override
    public Class<? extends Annotation> annotationType() {
        return SerialEntry.class;
    }
}
