/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * This project is open-source, and it is distributed under
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * 本项目是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.annoreg.mc.s11n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class StorageOption {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Null {}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Data {}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Instance {}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Update {}
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Enum {}

	/**
	 * Used only in server-to-client network call.
	 * Used on argument with the type of EntityPlayer.
	 * If Target is given, the message is only sent to this player.
	 * With this annotation, the StorageOption of INSTANCE is used.
	 * @author acaly
	 *
	 */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Target {}
	
	public enum Option {
	    NULL,
		DATA,
		INSTANCE,
		UPDATE,
		ENUM,
		
		/**
		 * Used only in deserialization.
		 * Will use the option contained in the data.
		 */
		AUTO,
	}
}
