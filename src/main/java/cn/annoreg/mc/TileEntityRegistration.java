package cn.annoreg.mc;

import net.minecraft.tileentity.TileEntity;
import cn.annoreg.base.RegistrationClassSimple;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.mc.RegTileEntity.HasRender;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@RegistryTypeDecl
public class TileEntityRegistration extends RegistrationClassSimple<RegTileEntity, TileEntity> {

	public TileEntityRegistration() {
		super(RegTileEntity.class, "TileEntity");
		this.setLoadStage(LoadStage.INIT);
		
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
		this.addWork(HasRender.class, new PostWork<HasRender, Class<? extends TileEntity>>() {
			@Override
			public void invoke(HasRender anno, Class<? extends TileEntity> obj) throws Exception {
				ProxyHelper.regTileEntityRender(obj, helper.getFieldFromClass(obj, RegTileEntity.Render.class));
			}
		});
	}

	@Override
	protected void register(Class<? extends TileEntity> theClass, RegTileEntity anno) throws Exception {
		GameRegistry.registerTileEntity(theClass, getSuggestedName());
	}
}
