package cn.annoreg.mc;

import cn.annoreg.base.RegistrationFieldSimple;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;
import net.minecraft.block.Block;
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
	}

	@Override
	protected void register(Block value, RegBlock anno, String field) throws Exception {
		GameRegistry.registerBlock(value, getSuggestedName());
	}

}
