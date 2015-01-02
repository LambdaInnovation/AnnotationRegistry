package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.ctor.ConstructorUtils;
import cn.annoreg.core.ctor.Ctor;

@RegistryTypeDecl
public class SubmoduleInitRegistration extends RegistryType {

	public SubmoduleInitRegistration() {
		super(RegSubmoduleInit.class, "SubmoduleInit");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		if (!onSide(data.<RegSubmoduleInit>getAnnotation()))
			return true;
		Class<?> clazz = data.getTheClass();
		try {
			Method method = clazz.getDeclaredMethod("init");
			method.invoke(null);
		} catch (Exception e) {
			ARModContainer.log.error("Can not init submodule {}.", clazz.getName());
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		if (!onSide(data.<RegSubmoduleInit>getAnnotation()))
			return true;
		Field field = data.getTheField();
		Class<?> clazz = field.getType();
		Object obj = ConstructorUtils.newInstance(field);
		try {
			Method method = clazz.getDeclaredMethod("init");
			method.invoke(obj);
		} catch (Exception e) {
			ARModContainer.log.error("Can not init submodule {}.", clazz.getName());
			e.printStackTrace();
		}
		return true;
	}

	private boolean onSide(RegSubmoduleInit anno) {
		switch (FMLCommonHandler.instance().getSide()) {
		case CLIENT:
			return true;
		case SERVER:
			return anno.side() != RegSubmoduleInit.Side.CLIENT_ONLY;
		default:
			ARModContainer.log.warn("Unexpected side.");
			throw new RuntimeException();
		}
	}
}
