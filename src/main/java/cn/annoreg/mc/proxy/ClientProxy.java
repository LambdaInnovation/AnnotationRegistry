package cn.annoreg.mc.proxy;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
	@Override
	public void regEntityRender(Class<? extends Entity> clazz, Object obj) {
		RenderingRegistry.registerEntityRenderingHandler(clazz, (Render) obj);
	}

	@Override
	public void regTileEntityRender(Class<? extends TileEntity> clazz, Object obj) {
		ClientRegistry.bindTileEntitySpecialRenderer(clazz, (TileEntitySpecialRenderer) obj);
	}
	
	@Override
	public void RegItemRender(Item item, Object obj) {
		MinecraftForgeClient.registerItemRenderer(item, (IItemRenderer) obj);
	}
}