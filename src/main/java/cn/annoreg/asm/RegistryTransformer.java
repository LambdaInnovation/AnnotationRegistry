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
