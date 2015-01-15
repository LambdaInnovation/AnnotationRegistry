package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventBus;
import cn.annoreg.base.RegistrationInstance;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class EventHandlerRegistration extends RegistrationInstance<RegEventHandler, Object> {

	public EventHandlerRegistration() {
		super(RegEventHandler.class, "EventHandler");
	}
	
	@Override
	protected void register(Object obj, RegEventHandler anno) throws Exception {
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
