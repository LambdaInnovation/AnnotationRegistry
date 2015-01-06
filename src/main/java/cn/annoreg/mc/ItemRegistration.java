package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.ctor.ConstructorUtils;
import cn.annoreg.core.ctor.Ctor;

@RegistryTypeDecl
public class ItemRegistration extends RegistryType {

	public ItemRegistration() {
		super(RegItem.class, "Item");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		RegItem anno = data.<RegItem>getAnnotation();
		Field field = data.getTheField();
		String name = anno.name();
		if (name.equals("")) {
			//Use the field name if anno.name is blank (default).
			name = field.getName();
		}
		Class<?> itemClass = field.getType();
		
		Item item = (Item) ConstructorUtils.newInstance(field);
		if (field.isAnnotationPresent(RegItem.UTName.class)) {
			String utname = field.getAnnotation(RegItem.UTName.class).value();
			if (utname.equals("")) {
				utname = name;
			}
			item.setUnlocalizedName(data.mod.getPrefix() + name);
			item.setTextureName(data.mod.getRes() + name);
		}
		
		GameRegistry.registerItem(item, data.mod.getPrefix() + name);
		
		if (field.isAnnotationPresent(RegItem.OreDict.class)) {
			RegItem.OreDict od = field.getAnnotation(RegItem.OreDict.class);
			OreDictionary.registerOre(od.value(), item);
		}
		
		if (ClientRegistryHelper.isClient() && !anno.renderName().equals("")) {
			ClientRegistryHelper.regItemRender(item, getRenderer(field.getDeclaringClass(), itemClass, anno.renderName()));
		}
		return true;
	}

	private Object getRenderer(Class<?> clazz1, Class<?> clazz2, String name) {
		for (Field field : clazz1.getFields()) {
			if (field.getName().equals(name)) {
				return ConstructorUtils.newInstance(field);
			}
		}
		for (Field field : clazz2.getFields()) {
			if (field.getName().equals(name)) {
				return ConstructorUtils.newInstance(field);
			}
		}
		throw new RuntimeException("Render field not found.");
	}
}
