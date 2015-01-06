package cn.annoreg.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationData {
	public enum Type {
		CLASS, FIELD,
	}
	
	public final Type type;
	public final Annotation anno;
	public final Object reflect;
	public RegModInformation mod;
	
	public AnnotationData(Annotation anno, Class<?> clazz) {
		this.anno = anno;
		this.reflect = clazz;
		this.type = Type.CLASS;
	}
	
	public AnnotationData(Annotation anno, Field field) {
		this.anno = anno;
		this.reflect = field;
		this.type = Type.FIELD;
	}

	public <T extends Annotation> T getAnnotation() {
		return (T) anno;
	}
	
	public boolean isClass() {
		return type == Type.CLASS;
	}
	
	public boolean isField() {
		return type == Type.FIELD;
	}
	
	public Class<?> getTheClass() {
		return isClass() ? (Class<?>) reflect : null;
	}
	
	public Field getTheField() {
		return isField() ? (Field) reflect : null;
	}
	
	@Override
	public String toString() {
		return "AnnotationData (" + anno.annotationType().getSimpleName() + ", " +
				(isClass() ? getTheClass().getCanonicalName() : getTheField().toString()) + ")";
	}
}
