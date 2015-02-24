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
package cn.annoreg.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cn.annoreg.core.RegistrationClass;
import cn.annoreg.core.RegistrationManager;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * This class currently does nothing.
 * @author acaly
 *
 */
public class RegistryTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] data) {
		if (arg0.startsWith("cn.annoreg.")) {
			return data;
		}
		ArrayList<String> innerClasses = new ArrayList();
		ClassReader cr = new ClassReader(data);
		ClassWriter cw = new ClassWriter(Opcodes.ASM4);
		InnerClassVisitor cv = new InnerClassVisitor(Opcodes.ASM4, cw);
		cr.accept(cv, 0);
		List<String> inner = cv.getInnerClassList();
		if (inner != null) {
		    RegistrationManager.INSTANCE.addInnerClassList(arg0, inner);
		}
		return cw.toByteArray();
	}

}
