package cn.annoreg.mc.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import cn.annoreg.ARModContainer;

public class ServerProxy {
	public void regEntityRender(Class<? extends Entity> clazz, Object obj) {
		ARModContainer.log.fatal("Try to load renderer on server.");
	}
	
	public void regTileEntityRender(Class<? extends TileEntity> clazz, Object obj) {
		ARModContainer.log.fatal("Try to load renderer on server.");
	}
	
	public void RegItemRender(Item item, Object obj) {
		ARModContainer.log.fatal("Try to load renderer on server.");
	}
}