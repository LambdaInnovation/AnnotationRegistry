package cn.annoreg.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.annoreg.ARModContainer;

public abstract class RegistryType {
	protected Map<RegistryMod, Set<AnnotationData>> data = new HashMap();
	protected Set<AnnotationData> unknownData = new HashSet();
	
	private Class<? extends Annotation> annoClass;
	private String name;
	
	public RegistryType(Class<? extends Annotation> annoClass, String name) {
		this.annoClass = annoClass;
		this.name = name;
	}
	
	private void newData(AnnotationData anno) {
		RegistryMod mod = RegistrationManager.INSTANCE.findMod(anno);
		if (mod != null) {
			if (!data.containsKey(mod)) data.put(mod, new HashSet());
			data.get(mod).add(anno);
		} else {
			unknownData.add(anno);
		}
	}
	
	public void visitClass(Class<?> clazz) {
		Annotation anno = clazz.getAnnotation(annoClass);
		if (anno != null) {
			newData(new AnnotationData(anno, clazz));
		}
	}
	
	public void visitField(Field field) {
		Annotation anno = field.getAnnotation(annoClass);
		if (anno != null) {
			newData(new AnnotationData(anno, field));
		}
	}
	
	public void registerAll(RegistryMod mod) {
		//First find if there's unknownData.
		Iterator<AnnotationData> itor = unknownData.iterator();
		while (itor.hasNext()) {
			AnnotationData ad = itor.next();
			RegistryMod rm = RegistrationManager.INSTANCE.findMod(ad);
			if (rm != null) {
				if (!data.containsKey(rm)) data.put(rm, new HashSet());
				data.get(rm).add(ad);
				itor.remove();
			}
		}
		
		//Do registration
		if (!data.containsKey(mod))	return;
		
		itor = data.get(mod).iterator();
		while (itor.hasNext()) {
			AnnotationData ad = itor.next();
			switch (ad.type) {
			case CLASS:
				registerClass(ad);
				itor.remove();
				break;
			case FIELD:
				registerField(ad);
				itor.remove();
				break;
			default:
				ARModContainer.log.error("Unknown registry data type.");
				break;
			}
		}
	}
	
	public abstract void registerClass(AnnotationData data);
	public abstract void registerField(AnnotationData data);
	
	public String getName() {
		return name;
	}
	
	public Class<? extends Annotation> getAnnotation() {
		return annoClass;
	}
}
