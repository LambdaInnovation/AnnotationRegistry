package cn.annoreg.mc.gui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class GuiHandlerRegistration extends RegistryType {

	public GuiHandlerRegistration() {
		super(RegGuiHandler.class, "GuiHandler");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		Field field = data.getTheField();
		try {
			if (!Modifier.isStatic(field.getModifiers()) || 
					!GuiHandlerBase.class.isAssignableFrom(field.getType())) {
				throw new RuntimeException("Invalid GuiHandler field.");
			}
			GuiHandlerBase handler = (GuiHandlerBase) field.get(null);
			if (handler == null) {
				throw new RuntimeException("Invalid GuiHandler field.");
			}
			regHandler(data.mod, handler);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}
	
	private static class ModGuiHandler implements IGuiHandler {

		private List<IGuiHandler> subHandlers = new ArrayList();
		
		public int addHandler(IGuiHandler handler) {
			subHandlers.add(handler);
			return subHandlers.size() - 1;
		}
		
		@Override
		public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			if (ID >= subHandlers.size()) {
				ARModContainer.log.error("Invalid GUI id on server.");
				return null;
			}
			return subHandlers.get(ID).getServerGuiElement(0, player, world, x, y, z);
		}

		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			if (ID >= subHandlers.size()) {
				ARModContainer.log.error("Invalid GUI id on client.");
				return null;
			}
			return subHandlers.get(ID).getClientGuiElement(0, player, world, x, y, z);
		}
		
	}
	
	private Map<RegModInformation, ModGuiHandler> modHandlers = new HashMap();
	
	private void regHandler(RegModInformation mod, GuiHandlerBase handler) {
		ModGuiHandler modHandler = modHandlers.get(mod);
		if (modHandler == null) {
			modHandler = new ModGuiHandler();
			modHandlers.put(mod, modHandler);
			NetworkRegistry.INSTANCE.registerGuiHandler(mod.getModInstance(), modHandler);
		}
		handler.register(mod.getModInstance(), modHandler.addHandler(handler.getHandler()));
	}

}
