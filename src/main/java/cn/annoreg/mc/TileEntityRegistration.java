package cn.annoreg.mc;

import java.lang.annotation.Annotation;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class TileEntityRegistration extends RegistryType {

	public TileEntityRegistration() {
		super(RegTileEntity.class, "TileEntity");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		Class<? extends TileEntity> clazz = (Class<? extends TileEntity>) data.getTheClass();
		String name = data.<RegTileEntity>getAnnotation().name();
		if (name.equals("")) {
			name = clazz.getSimpleName();
		}
		name = data.mod.getPrefix() + name;
		GameRegistry.registerTileEntity(clazz, name);
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		return false;
	}

}
