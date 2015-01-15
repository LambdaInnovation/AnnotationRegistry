package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cn.annoreg.base.RegistrationClassOrField;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class SubmoduleInitRegistration extends RegistrationClassOrField<RegSubmoduleInit> {

	public SubmoduleInitRegistration() {
		super(RegSubmoduleInit.class, "SubmoduleInit");
		this.setLoadStage(LoadStage.INIT);
	}
	
	private boolean onSide(RegSubmoduleInit anno) {
		return FMLCommonHandler.instance().getSide().isClient() ||
				anno.side() != RegSubmoduleInit.Side.CLIENT_ONLY;
	}

	@Override
	protected void register(Class<?> value, RegSubmoduleInit anno) throws Exception {
		if (!onSide(anno))
			return;
		Method method = value.getDeclaredMethod("init");
		method.invoke(null);
	}

	@Override
	protected void register(Object value, RegSubmoduleInit anno, String field) throws Exception {
		if (!onSide(anno))
			return;
		Method method = value.getClass().getDeclaredMethod("init");
		method.invoke(value);
	}
}
