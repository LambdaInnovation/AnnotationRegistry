package cn.annoreg;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class AnnoRegCorePlugin implements IFMLLoadingPlugin {
	
	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return "cn.annoreg.ARModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		int i = 0;
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
