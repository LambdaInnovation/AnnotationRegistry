package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cn.annoreg.base.RegistrationClassSimple;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.mc.RegEntity.HasRender;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.entity.Entity;

@RegistryTypeDecl
public class EntityRegistration extends RegistrationClassSimple<RegEntity, Entity> {

	public EntityRegistration() {
		super(RegEntity.class, "Entity");
		this.addWork(RegEntity.HasRender.class, new PostWork<RegEntity.HasRender, Class<? extends Entity>>() {
			@Override
			public void invoke(HasRender anno, Class<? extends Entity> obj) throws Exception {
				if (ProxyHelper.isClient()) {
					ProxyHelper.regEntityRender(obj, helper.getFieldFromClass(obj, RegEntity.Render.class));
				}
			}
		});
	}
	
	@Override
	protected void register(Class<? extends Entity> theClass, RegEntity anno) throws Exception {
		if (!anno.clientOnly()) {
			EntityRegistry.registerModEntity(theClass, getSuggestedName(), 
					helper.getNextIDForMod(), getCurrentMod().getModInstance(), 
					anno.trackRange(), anno.freq(), anno.updateVel());
		}
	}
}
