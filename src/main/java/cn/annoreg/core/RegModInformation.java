/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * AnnotationRegistry is open-source, and it is distributed under 
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * AnnotationRegistry是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.annoreg.core;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;

public class RegModInformation {
	
	private String modid;
	private String pkg, prefix, res;
	/**
	 * Cached mod instance.
	 */
	private Object mod;
	
	public RegModInformation(Class<?> clazz) {
		RegistrationMod mod = clazz.getAnnotation(RegistrationMod.class);
		this.pkg = mod.pkg();
		this.prefix = mod.prefix();
		this.res = mod.res();
		modid = clazz.getAnnotation(Mod.class).modid();
	}

	public String getPackage() {
		return pkg;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getRes(String id) {
		return res + ":" + id;
	}
	
	public Object getModInstance() {
		if (mod != null) return mod;
		ModContainer mc = Loader.instance().getIndexedModList().get(modid);
		if (mc != null) {
			mod = mc.getMod();
			return mod;
		} else {
			return null;
		}
	}
	
	public String getModID() {
		return modid;
	}
}
