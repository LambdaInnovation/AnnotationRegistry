package cn.annoreg.core.ctor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {
	int Int() default 0;
	String Str() default "";
}
