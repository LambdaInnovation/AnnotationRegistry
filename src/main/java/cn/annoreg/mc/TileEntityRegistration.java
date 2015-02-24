/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.lambdacraft.cn/
 *
 * AnnotationRegistry is open-source, and it is distributed under 
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * AnnotationRegistry是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.annoreg.mc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
import cn.annoreg.base.RegistrationClassSimple;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;
import cn.annoreg.mc.RegTileEntity.HasRender;

@RegistryTypeDecl
public class TileEntityRegistration extends RegistrationClassSimple<RegTileEntity, TileEntity> {

	public TileEntityRegistration() {
		super(RegTileEntity.class, "TileEntity");
		this.setLoadStage(LoadStage.INIT);
		
		this.addWork(HasRender.class, new PostWork<HasRender, Class<? extends TileEntity>>() {
			@Override
			public void invoke(HasRender anno, Class<? extends TileEntity> obj) throws Exception {
				ProxyHelper.regTileEntityRender(obj, helper.getFieldFromClass(obj, RegTileEntity.Render.class));
			}
		});
	}

	@Override
	protected void register(Class<? extends TileEntity> theClass, RegTileEntity anno) throws Exception {
		GameRegistry.registerTileEntity(theClass, getSuggestedName());
	}
}
