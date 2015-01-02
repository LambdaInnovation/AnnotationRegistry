package cn.annoreg.core;

public class RegModInformation {
	private String pkg, prefix, res;
	
	private RegModInformation(String pkg, String prefix, String res) {
		this.pkg = pkg;
		this.prefix = prefix;
		this.res = res;
	}
	
	public RegModInformation(RegistrationMod mod) {
		this(mod.pkg(), mod.prefix(), mod.res());
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
}
