package cn.annoreg.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import net.minecraft.launchwrapper.IClassTransformer;

public class RegistryTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] data) {
		if (arg0.startsWith("cn.annoreg.")) {
			return data;
		}
		ClassReader cr = new ClassReader(data);
		ClassWriter cw = new ClassWriter(Opcodes.ASM4);
		ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
			@Override
			public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
				AnnotationVisitor av = super.visitAnnotation(desc, visible);
				return new ClientClassEraser(Opcodes.ASM4, av);
			}
		};
		cr.accept(cv, 0);
		return cw.toByteArray();
	}

}
