package cn.annoreg.mc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on static field. The type of the field must be the class you want to creat the instance with.<BR/>
 * For example:
 *   <BR/>@RegBlock(name = "xxx")
 *   <BR/>public static BlockXXX blockxxx;
 * <BR/><BR/>
 * The id of the TileEntity is "tile_" + name.
 * @author acaly
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegBlock {
	
	String name();

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface OreDict {
		String value();
	}
}
