package cn.annoreg.base;

import java.lang.annotation.Annotation;

import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistryType;

public abstract class RegistrationClassRepeat<ANNO extends Annotation, BASE> extends RegistryType {

	public RegistrationClassRepeat(Class<ANNO> annoClass, String name) {
		super(annoClass, name);
	}

	protected abstract void register(Class<? extends BASE> theClass, ANNO anno) throws Exception;

	@Override
	public boolean registerClass(AnnotationData data) throws Exception {
		register((Class<? extends BASE>) data.getTheClass(), (ANNO) data.getAnnotation());
		return false;
	}

	@Override
	public boolean registerField(AnnotationData data) throws Exception {
		throw new RuntimeException("Invalid use of annotation " + this.annoClass.getSimpleName() + ": Can not use on field.");
	}

	@Override
	public void checkLoadState() {
	}
}
