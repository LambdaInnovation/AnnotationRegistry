package cn.annoreg.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData;
import cn.annoreg.core.RegModInformation;
import cn.annoreg.core.RegistrationWithPostWork;

public abstract class RegistrationInstance<ANNO extends Annotation, BASE> extends RegistrationWithPostWork<BASE> {

	public RegistrationInstance(Class<ANNO> annoClass, String name) {
		super(annoClass, name);
	}

	protected abstract void register(BASE obj, ANNO anno) throws Exception;
	
	@Override
	public boolean registerClass(AnnotationData data) throws Exception {
		BASE obj = (BASE) data.getTheClass().newInstance();
		register(obj, (ANNO) data.getAnnotation());
		return true;
	}

	@Override
	public boolean registerField(AnnotationData data) throws Exception {
		Field field = data.getTheField();
		if (!Modifier.isStatic(field.getModifiers())) {
			ARModContainer.log.error("Invalid use of annotation {}: Field must be static.",
					this.annoClass.getSimpleName());
		}
		BASE value = null;
		try {
			value = (BASE) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException("Can not get the value of the field " + field.toString() + ".", e);
		}
		try {
			if (value == null) {
				// Create new instance. Use default constructor.
				value = (BASE) field.getType().newInstance();
			}
		} catch (Exception e) {
			throw new RuntimeException("Can not create new instance for the field.", e);
		}
		register(value, (ANNO) data.getAnnotation());
		return true;
	}

}
