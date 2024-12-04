package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public record AutoGenData(String category, String group) implements AutoGen {
    @Override
    public Class<? extends Annotation> annotationType() {
        return AutoGen.class;
    }
}
