package cn.annoreg.mc.s11n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class StorageOption {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Data {}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Instance {}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Update {}
	
	public enum Option {
		DATA,
		INSTANCE,
		UPDATE,
	}
}
