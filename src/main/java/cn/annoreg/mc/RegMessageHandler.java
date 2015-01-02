package cn.annoreg.mc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegMessageHandler {
	
	public enum Side {
		SERVER,
		CLIENT,
		BOTH,
	}
	
	Class<?> msg();
	Side side();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface WrapperInstance {}
}
