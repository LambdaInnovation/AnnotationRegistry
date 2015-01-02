package cn.annoreg.mc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegTileEntity {
	/**
	 * If blank, use mod.prefix + class.simpleName.
	 * @return
	 */
	String name() default "";
}
