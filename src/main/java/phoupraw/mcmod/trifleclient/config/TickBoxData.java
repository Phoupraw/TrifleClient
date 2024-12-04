package phoupraw.mcmod.trifleclient.config;

import dev.isxander.yacl3.config.v2.api.autogen.TickBox;

import java.lang.annotation.Annotation;

@SuppressWarnings("ClassExplicitlyAnnotation")
public enum TickBoxData implements TickBox {
    A;
    @Override
    public Class<? extends Annotation> annotationType() {
        return TickBox.class;
    }
}
