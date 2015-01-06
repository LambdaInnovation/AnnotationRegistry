package cn.annoreg.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.annoreg.ARModContainer;
import cn.annoreg.core.AnnotationData.Type;

public abstract class RegistryType {
	
	private static class RegDataCmp implements Comparator<AnnotationData> {
		
		public static final RegDataCmp INSTANCE = new RegDataCmp();
		
		@Override
		public int compare(AnnotationData arg0, AnnotationData arg1) {
			if (arg0.type != arg1.type) {
				return arg0.type.compareTo(arg1.type);
			} else if (arg0.type == Type.CLASS) {
				return arg0.getTheClass().getCanonicalName().compareTo(arg1.getTheClass().getCanonicalName());
			} else {
				return arg0.getTheField().toString().compareTo(arg1.getTheField().toString());
			}
		}
		
	}
	
	protected Map<RegModInformation, List<AnnotationData>> data = new HashMap();
	protected List<AnnotationData> unknownData = new LinkedList();
	
	private Class<? extends Annotation> annoClass;
	private String name;
	
	private Set<RegModInformation> loadedMods = new HashSet();
	
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
		loadedMods.add(mod);
		
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
		
		List<AnnotationData> regList = data.get(mod);
		Collections.sort(regList, RegDataCmp.INSTANCE);
		
		itor = regList.iterator();
		while (itor.hasNext()) {
			AnnotationData ad = itor.next();
			switch (ad.type) {
			case CLASS:
				try {
					if (registerClass(ad))
						itor.remove();
				} catch (Exception e) {
					ARModContainer.log.error("Error when registering {}.", ad.toString());
					itor.remove();
				}
				break;
			case FIELD:
				try {
					if (registerField(ad))
						itor.remove();
				} catch (Exception e) {
					ARModContainer.log.error("Error when registering {}.", ad.toString());
					itor.remove();
				}
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
	
	public void checkLoadState() {
		for (RegModInformation mod : RegistrationManager.INSTANCE.getMods()) {
			if (!loadedMods.contains(mod)) {
				if (data.containsKey(mod) && !data.get(mod).isEmpty()) {
					ARModContainer.log.error("{} in mod {} is not registered.", this.name, mod.getModID());
					throw new RuntimeException();
				}
			}
		}
	}
}
