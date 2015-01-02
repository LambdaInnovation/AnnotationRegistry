package cn.annoreg.mc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegItem {
	/**
	 * The name used in registry.
	 * If not given (""), use the field name.
	 * When registered, the real name used is mod.prefix + name.
	 * @return
	 */
	String name() default "";
	
	/**
	 * Add this annotation to set unlocalized name and texture name.
	 * tname is set to mod.res (+ ":") + value.
	 * uname is set to mod.prefix + value.
	 * If value is empty, the RegItem.name will be used.
	 * @author acaly
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface UTName {
		String value() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface OreDict {
		String value();
	}
}
