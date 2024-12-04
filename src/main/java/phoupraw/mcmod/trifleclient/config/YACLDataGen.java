package phoupraw.mcmod.trifleclient.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface YACLDataGen {
    //String lang() default "zh_cn";
    String name() default "";
    String desc() default "";
}
