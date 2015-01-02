package cn.annoreg.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.annoreg.ARModContainer;

public abstract class RegistryType {
	protected Map<RegModInformation, List<AnnotationData>> data = new HashMap();
	protected List<AnnotationData> unknownData = new LinkedList();
	
	private Class<? extends Annotation> annoClass;
	private String name;
	
	public RegistryType(Class<? extends Annotation> annoClass, String name) {
		this.annoClass = annoClass;
		this.name = name;
	}
	
	private void newData(AnnotationData anno) {
		RegModInformation mod = RegistrationManager.INSTANCE.findMod(anno);
		if (mod != null) {
			if (!data.containsKey(mod)) data.put(mod, new LinkedList());
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
	
	public void registerAll(RegModInformation mod) {
		//First find if there's unknownData.
		Iterator<AnnotationData> itor = unknownData.iterator();
		while (itor.hasNext()) {
			AnnotationData ad = itor.next();
			RegModInformation rm = RegistrationManager.INSTANCE.findMod(ad);
			if (rm != null) {
				if (!data.containsKey(rm)) data.put(rm, new LinkedList());
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
				if (registerClass(ad))
					itor.remove();
				break;
			case FIELD:
				if (registerField(ad))
					itor.remove();
				break;
			default:
				ARModContainer.log.error("Unknown registry data type.");
				break;
			}
		}
	}
	
	/**
	 * Return true to remove the data from list. 
	 * (For Command, reg is done each time the server is started, so can not always remove.)
	 * @param data
	 * @return
	 */
	public abstract boolean registerClass(AnnotationData data);
	public abstract boolean registerField(AnnotationData data);
	
	public String getName() {
		return name;
	}
	
	public Class<? extends Annotation> getAnnotation() {
		return annoClass;
	}
}
