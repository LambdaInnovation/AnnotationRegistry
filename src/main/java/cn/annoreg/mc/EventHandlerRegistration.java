package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.ctor.ConstructorUtils;
import cn.annoreg.core.ctor.Ctor;

@RegistryTypeDecl
public class EventHandlerRegistration extends RegistryType {

	public EventHandlerRegistration() {
		super(RegEventHandler.class, "EventHandler");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		Class<?> clazz = data.getTheClass();
		Object obj = ConstructorUtils.newInstance(clazz);
		register(data.<RegEventHandler>getAnnotation(), obj);
		return true;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		Field field = data.getTheField();
		Object obj = ConstructorUtils.newInstance(field);
		register(data.<RegEventHandler>getAnnotation(), obj);
		return true;
	}

	private void register(RegEventHandler anno, Object obj) {
		for (RegEventHandler.Bus bus : anno.value()) {
			switch (bus) {
			case FML:
				FMLCommonHandler.instance().bus().register(obj);
				break;
			case Forge:
				MinecraftForge.EVENT_BUS.register(obj);
				break;
			default:
			}
		}
	}
}
