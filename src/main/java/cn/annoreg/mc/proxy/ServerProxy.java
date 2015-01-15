package cn.annoreg.mc.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cn.annoreg.ARModContainer;

public class ServerProxy {
	public void regEntityRender(Class<? extends Entity> clazz, Object obj) {
		ARModContainer.log.fatal("Try to load renderer on server.");
	}
	
	public void regTileEntityRender(Class<? extends TileEntity> clazz, Object obj) {
		ARModContainer.log.fatal("Try to load renderer on server.");
	}
	
	public void regItemRender(Item item, Object obj) {
		ARModContainer.log.fatal("Try to load renderer on server.");
	}
	
	public World getWorld(int dimension) {
		return DimensionManager.getWorld(dimension);
	}

	public Container getPlayerContainer(EntityPlayer player, int windowId) {
		Container ret = player.openContainer;
		if (ret.windowId == windowId) {
			return ret;
		}
		return null;
	}
	
	public EntityPlayer getThePlayer() {
		return null;
	}
}