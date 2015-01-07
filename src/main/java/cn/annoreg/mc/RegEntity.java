package cn.annoreg.mc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegEntity {
	String name() default "";
	
	int trackRange() default 32;
	int freq() default 3;
	boolean updateVel() default true;
	boolean clientOnly() default false;
	
	/**
	 * Field name of the renderer instance.
	 * A field of the Entity class.
	 * @return
	 */
	String renderName() default "";
}
