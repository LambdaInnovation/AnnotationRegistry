package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
import cn.annoreg.base.RegistrationClassSimple;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.mc.RegTileEntity.HasRender;

@RegistryTypeDecl
public class TileEntityRegistration extends RegistrationClassSimple<RegTileEntity, TileEntity> {

	public TileEntityRegistration() {
		super(RegTileEntity.class, "TileEntity");
		
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
