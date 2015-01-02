package cn.annoreg.mc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface RegSubmoduleInit {
	
	public enum Side {
		CLIENT_ONLY,
		BOTH,
	}
	
	Side side() default Side.BOTH;
}
