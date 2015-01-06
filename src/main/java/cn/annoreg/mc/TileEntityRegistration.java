package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.ctor.ConstructorUtils;

@RegistryTypeDecl
public class TileEntityRegistration extends RegistryType {

	public TileEntityRegistration() {
		super(RegTileEntity.class, "TileEntity");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		Class<? extends TileEntity> clazz = (Class<? extends TileEntity>) data.getTheClass();
		RegTileEntity anno = data.<RegTileEntity>getAnnotation();
		String name = anno.name();
		if (name.equals("")) {
			name = clazz.getSimpleName();
		}
		name = data.mod.getPrefix() + name;
		GameRegistry.registerTileEntity(clazz, name);
		
		if (ClientRegistryHelper.isClient() && !anno.renderName().equals("")) {
			ClientRegistryHelper.regTileEntityRender(clazz, getRenderer(clazz, anno.renderName()));
		}
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		return false;
	}

	private Object getRenderer(Class<?> clazz, String name) {
		try {
			Field field = clazz.getField(name);
			return ConstructorUtils.newInstance(field);
		} catch (Exception e) {
			ARModContainer.log.error("Can not get renderer field {} in {}.", name, clazz.getCanonicalName());
			throw new RuntimeException(e);
		}
	}
}
