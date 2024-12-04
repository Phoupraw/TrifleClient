package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public record AutoGenData(String category, String group, String name, String desc) implements AutoGen, AutoGenDetails {
    public AutoGenData(@Nullable AutoGen autoGen, @Nullable AutoGenDetails details) {
        this(autoGen != null ? autoGen.category() : "", autoGen != null ? autoGen.group() : "", details != null ? details.name() : "", details != null ? details.desc() : "");
    }
    @Override
    public Class<? extends Annotation> annotationType() {
        return AutoGen.class;
    }
}
