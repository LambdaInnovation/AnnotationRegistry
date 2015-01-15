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
import cn.annoreg.base.RegistrationClassSimple;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class MessageHandlerRegistration extends RegistrationClassSimple<RegMessageHandler, IMessageHandler> {
	
	public MessageHandlerRegistration() {
		super(RegMessageHandler.class, "MessageHandler");
		this.setLoadStage(LoadStage.INIT);
		
		//Set this annotation to prepare for calling getModField.
		helper.setModFieldAnnotation(RegMessageHandler.WrapperInstance.class);
	}
	
	private <REQ extends IMessage> void register(Class<?> handler, Class<REQ> msg, Side side) {
		Class<? extends IMessageHandler<REQ, IMessage>> messageHandler = (Class<? extends IMessageHandler<REQ, IMessage>>) handler;
		SimpleNetworkWrapper wrapper = (SimpleNetworkWrapper) helper.getModField();
		wrapper.registerMessage(messageHandler, msg, helper.getNextIDForMod(), side);
	}
	
	@Override
	protected void register(Class theClass, RegMessageHandler anno) throws Exception {
		Class<? extends IMessage> msg = (Class<? extends IMessage>) anno.msg();
		switch (anno.side()) {
		case CLIENT:
			register(theClass, msg, Side.CLIENT);
			break;
		case SERVER:
			register(theClass, msg, Side.SERVER);
			break;
		}
	}

}
