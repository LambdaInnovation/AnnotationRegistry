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
	
	public String getRes() {
		return res + ":";
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
}
