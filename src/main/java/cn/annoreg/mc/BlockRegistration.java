package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
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
		Class<?> blockClass = field.getType();
		try {
			Block block;
			if (field.isAnnotationPresent(Ctor.class)) {
				block = (Block) ConstructorUtils.newInstance(blockClass, field.getAnnotation(Ctor.class));
			} else {
				block = (Block) blockClass.newInstance();
			}
			field.set(null, block);
			GameRegistry.registerBlock(block, anno.name());
			
			//oredict
			if (field.isAnnotationPresent(RegBlock.OreDict.class)) {
				RegBlock.OreDict od = field.getAnnotation(RegBlock.OreDict.class);
				OreDictionary.registerOre(od.value(), block);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
