package cn.annoreg.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

public class ClientClassEraser extends AnnotationVisitor {

	public ClientClassEraser(int api, AnnotationVisitor av) {
		super(api, av);
	}

	@Override
	public void visit(String name, Object value) {
		if (value instanceof Type) {
			Type theType = (Type) value;
			String clazz = theType.getClassName();
			super.visit(name, value);
		} else {
			super.visit(name, value);
		}
	}
}
