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
import cn.annoreg.base.RegistrationFieldSimple;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class GuiHandlerRegistration extends RegistrationFieldSimple<RegGuiHandler, GuiHandlerBase> {

	public GuiHandlerRegistration() {
		super(RegGuiHandler.class, "GuiHandler");
		this.setLoadStage(LoadStage.INIT);
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

	@Override
	protected void register(GuiHandlerBase value, RegGuiHandler anno, String field) throws Exception {
		regHandler(this.getCurrentMod(), value);
	}

}
