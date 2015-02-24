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
import java.lang.reflect.Method;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cn.annoreg.base.RegistrationClassOrField;
import cn.annoreg.core.LoadStage;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryTypeDecl;

@RegistryTypeDecl
public class SubmoduleInitRegistration extends RegistrationClassOrField<RegSubmoduleInit> {

	public SubmoduleInitRegistration() {
		super(RegSubmoduleInit.class, "SubmoduleInit");
		this.setLoadStage(LoadStage.INIT);
	}
	
	private boolean onSide(RegSubmoduleInit anno) {
		return FMLCommonHandler.instance().getSide().isClient() ||
				anno.side() != RegSubmoduleInit.Side.CLIENT_ONLY;
	}

	@Override
	protected void register(Class<?> value, RegSubmoduleInit anno) throws Exception {
		if (!onSide(anno))
			return;
		Method method = value.getDeclaredMethod("init");
		method.invoke(null);
	}

	@Override
	protected void register(Object value, RegSubmoduleInit anno, String field) throws Exception {
		if (!onSide(anno))
			return;
		Method method = value.getClass().getDeclaredMethod("init");
		method.invoke(value);
	}
}
