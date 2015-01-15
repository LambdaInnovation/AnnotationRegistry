package cn.annoreg.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegistryType;

public class RegistrationEmpty extends RegistryType {

	public RegistrationEmpty(String name) {
		super(name);
	}

	@Override
	public boolean registerClass(AnnotationData data) throws Exception {
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) throws Exception {
		return false;
	}

	@Override
	public void visitClass(Class<?> clazz) {}
	
	@Override
	public void visitField(Field field) {}
}
