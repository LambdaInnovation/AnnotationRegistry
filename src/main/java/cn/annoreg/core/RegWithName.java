package cn.annoreg.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If not provided on registered items, default name will be generated as:
 * Mod prefix + Type name + "_" + { class simple name or field name }
 * @author acaly
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface RegWithName {
	String value();
}
