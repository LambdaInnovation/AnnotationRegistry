package cn.annoreg.mc;

import java.lang.reflect.Method;

import cn.annoreg.ARModContainer;
import cn.annoreg.mc.proxy.ServerProxy;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientRegistryHelper {
	
	
	static {
		try {
			if (isClient()) {
				proxy = (ServerProxy) Class.forName("cn.annoreg.mc.proxy.ClientProxy").newInstance();
			} else {
				proxy = (ServerProxy) Class.forName("cn.annoreg.mc.proxy.ServerProxy").newInstance();
			}
		} catch (Exception e) {
			ARModContainer.log.fatal("Can not create proxy.");
			throw new RuntimeException(e);
		}
	}
	
	private static ServerProxy proxy;
	
	public static boolean isClient() {
		return FMLCommonHandler.instance().getSide() == Side.CLIENT;
	}
	
	public static void regEntityRender(Class<? extends Entity> clazz, Object obj) {
		proxy.regEntityRender(clazz, obj);
	}
	
	public static void regTileEntityRender(Class<? extends TileEntity> clazz, Object obj) {
		proxy.regTileEntityRender(clazz, obj);
	}
	
	public static void regItemRender(Item item, Object obj) {
		proxy.RegItemRender(item, obj);
	}
}
