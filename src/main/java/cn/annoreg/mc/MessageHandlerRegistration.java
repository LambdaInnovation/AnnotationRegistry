package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class MessageHandlerRegistration extends RegistryType {
	
	private static class WrapperInfo {
		public SimpleNetworkWrapper wrapper;
		public int nextID = 100; //Use 100 to also allow the mod to reg itself
	}
	
	private Map<Object, WrapperInfo> wrapperMap = new HashMap();
	
	private WrapperInfo getWrapper(Object mod) {
		if (wrapperMap.containsKey(mod)) {
			return wrapperMap.get(mod);
		}
		WrapperInfo ret = new WrapperInfo();
		for (Field field : mod.getClass().getFields()) {
			if (field.isAnnotationPresent(RegMessageHandler.WrapperInstance.class) &&
					field.getType().equals(SimpleNetworkWrapper.class)) {
				try {
					if (Modifier.isStatic(field.getModifiers())) {
						ret.wrapper = (SimpleNetworkWrapper) field.get(null);
					} else {
						ret.wrapper = (SimpleNetworkWrapper) field.get(mod);
					}
					if (ret.wrapper != null) {
						wrapperMap.put(mod, ret);
						return ret;
					}
				} catch (Exception e) {
				}
			}
		}
		ARModContainer.log.error("Can not get NetworkWrapper for mod {}.", mod.getClass().getCanonicalName());
		throw new RuntimeException();
	}
	
	public MessageHandlerRegistration() {
		super(RegMessageHandler.class, "MessageHandler");
	}

	private <REQ extends IMessage> 
			void register(SimpleNetworkWrapper wrapper, Class<?> handler, Class<REQ> requestMessageType, int id, Side side) {
		Class<? extends IMessageHandler<REQ, IMessage>> messageHandler = (Class<? extends IMessageHandler<REQ, IMessage>>) handler;
		wrapper.registerMessage(messageHandler, requestMessageType, id, side);
	}
	
	private <T extends IMessage> void register(WrapperInfo wrapper, Class<?> handler, Class<T> msg, Side side) {
		this.register(wrapper.wrapper, handler, msg, wrapper.nextID++, side);
	}
	
	private void register_(WrapperInfo wrapper, Class<?> handler, Class<? extends IMessage> msg, Side side) {
		this.register(wrapper, handler, msg, side);
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		RegMessageHandler anno = data.<RegMessageHandler>getAnnotation();
		Class<?> handler = data.getTheClass();
		Class<? extends IMessage> msg = (Class<? extends IMessage>) anno.msg();
		WrapperInfo wrapper = getWrapper(data.mod.getModInstance());
		switch (anno.side()) {
		case BOTH:
			register(wrapper, handler, msg, Side.CLIENT);
			register(wrapper, handler, msg, Side.SERVER);
			break;
		case CLIENT:
			register(wrapper, handler, msg, Side.CLIENT);
			break;
		case SERVER:
			register(wrapper, handler, msg, Side.SERVER);
			break;
		default:
			ARModContainer.log.error("Unexpected side.");
			throw new RuntimeException();
		}
		return true;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		return false;
	}

}
