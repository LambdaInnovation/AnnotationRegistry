package cn.annoreg.base;

import java.lang.annotation.Annotation;

import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegModInformation;

public abstract class RegistrationClassOrField<ANNO extends Annotation> extends RegistrationFieldSimple<ANNO, Object> {

	public RegistrationClassOrField(Class<ANNO> annoClass, String name) {
		super(annoClass, name);
	}
	
	protected abstract void register(Class<?> value, ANNO anno) throws Exception;

	@Override
	public boolean registerClass(AnnotationData data) throws Exception {
		register(data.getTheClass(), (ANNO) data.anno);
		return true;
	}
}
