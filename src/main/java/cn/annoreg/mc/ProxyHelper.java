package cn.annoreg.mc;

import java.lang.reflect.Method;

import cn.annoreg.ARModContainer;
import cn.annoreg.mc.proxy.ServerProxy;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProxyHelper {
	
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
		proxy.regItemRender(item, obj);
	}
	
	public static World getWorld(int dimension) {
		return proxy.getWorld(dimension);
	}
	
	public static Container getPlayerContainer(EntityPlayer player, int windowId) {
		return proxy.getPlayerContainer(player, windowId);
	}
	
	public static EntityPlayer getThePlayer() {
		return proxy.getThePlayer();
	}
}
