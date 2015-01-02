package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.ctor.ConstructorUtils;
import cn.annoreg.core.ctor.Ctor;

@RegistryTypeDecl
public class BlockRegistration extends RegistryType {

	public BlockRegistration() {
		super(RegBlock.class, "Block");
	}

	@Override
	public boolean registerClass(AnnotationData data) {
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) {
		RegBlock anno = data.<RegBlock>getAnnotation();
		Field field = data.getTheField();
		String name = anno.name();
		if (name.equals("")) {
			//Use the field name if anno.name is blank (default).
			name = field.getName();
		}
		name = data.mod.getPrefix() + name;
		Class<?> blockClass = field.getType();
		Block block = (Block) ConstructorUtils.newInstance(field);
		GameRegistry.registerBlock(block, name);
		
		//oredict
		if (field.isAnnotationPresent(RegBlock.OreDict.class)) {
			RegBlock.OreDict od = field.getAnnotation(RegBlock.OreDict.class);
			OreDictionary.registerOre(od.value(), block);
		}
		return true;
	}

}
