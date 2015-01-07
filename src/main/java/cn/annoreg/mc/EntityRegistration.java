package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.ctor.ConstructorUtils;

@RegistryTypeDecl
public class EntityRegistration extends RegistryType {
	
	private Map<Object, Integer> indexMap = new HashMap();

	public EntityRegistration() {
		super(RegEntity.class, "Entity");
	}
	
	private int getID(Object mod) {
		if (!indexMap.containsKey(mod)) {
			indexMap.put(mod, 1);
			return 0;
		} else {
			int ret = indexMap.get(mod);
			indexMap.put(mod, ret + 1);
			return ret;
		}
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		Object mod = data.mod.getModInstance();
		if (mod == null) {
			ARModContainer.log.error("Can not get the mod instance for package \"{}\".", 
					data.mod.getPackage());
		}
		Class<? extends Entity> clazz = (Class<? extends Entity>) data.getTheClass();
		RegEntity anno = data.<RegEntity>getAnnotation();
		String name = anno.name();
		if (name.equals("")) {
			name = clazz.getSimpleName();
		}
		name = data.mod.getPrefix() + name;
		
		if (!anno.clientOnly()) {
			EntityRegistry.registerModEntity(clazz, name, getID(mod), mod, 
					anno.trackRange(), anno.freq(), anno.updateVel());
		}
		
		if (ClientRegistryHelper.isClient() && !anno.renderName().equals("")) {
			ClientRegistryHelper.regEntityRender(clazz, getRenderer(clazz, anno.renderName()));
		}
		return true;
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
