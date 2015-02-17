package cn.annoreg.mc;

import cn.annoreg.base.RegistrationFieldSimple;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.core.RegistrationWithPostWork.PostWork;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;

@RegistryTypeDecl
public class BlockRegistration extends RegistrationFieldSimple<RegBlock, Block> {

	public BlockRegistration() {
		super(RegBlock.class, "Block");
		this.setLoadStage(LoadStage.INIT);
		
		this.addWork(RegBlock.OreDict.class, new PostWork<RegBlock.OreDict, Block>() {
			@Override
			public void invoke(RegBlock.OreDict anno, Block obj) throws Exception {
				OreDictionary.registerOre(anno.value(), obj);
			}
		});
		
		this.addWork(RegBlock.BTName.class, new PostWork<RegBlock.BTName, Block>() {
			@Override
			public void invoke(RegBlock.BTName anno, Block obj) throws Exception {
				obj.setBlockTextureName(getCurrentMod().getRes(anno.value()));
				obj.setBlockName(anno.value());
			}
		});
		
	}

	@Override
	protected void register(Block value, RegBlock anno, String field) throws Exception {
		GameRegistry.registerBlock(value, getSuggestedName());
	}

}
