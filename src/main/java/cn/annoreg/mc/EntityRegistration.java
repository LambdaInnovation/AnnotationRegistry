package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;

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
		EntityRegistry.registerModEntity(clazz, name, getID(mod), mod, 
				anno.trackRange(), anno.freq(), anno.updateVel());
		return true;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		return false;
	}

}
