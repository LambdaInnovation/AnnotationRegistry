package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import cn.annoreg.base.RegistrationFieldSimple;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class ItemRegistration extends RegistrationFieldSimple<RegItem, Item> {

	public ItemRegistration() {
		super(RegItem.class, "Item");
		this.setLoadStage(LoadStage.INIT);
		
		this.addWork(RegItem.OreDict.class, new PostWork<RegItem.OreDict, Item>() {
			@Override
			public void invoke(RegItem.OreDict anno, Item obj) throws Exception {
				OreDictionary.registerOre(anno.value(), obj);
			}
		});
		
		this.addWork(RegItem.UTName.class, new PostWork<RegItem.UTName, Item>() {
			@Override
			public void invoke(RegItem.UTName anno, Item obj) throws Exception {
				obj.setUnlocalizedName(getCurrentMod().getPrefix() + anno.value());
				obj.setTextureName(getCurrentMod().getRes(anno.value()));
			}
		});
		
		this.addWork(RegItem.HasRender.class, new PostWork<RegItem.HasRender, Item>() {
			@Override
			public void invoke(RegItem.HasRender anno, Item obj) throws Exception {
				if (ProxyHelper.isClient()) {
					ProxyHelper.regItemRender(obj, helper.getFieldFromObject(obj, RegItem.Render.class));
				}
			}
		});
	}

	@Override
	protected void register(Item value, RegItem anno, String field) throws Exception {
		GameRegistry.registerItem(value, getSuggestedName());
	}
}
