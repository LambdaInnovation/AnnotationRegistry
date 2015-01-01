package cn.annoreg.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.annoreg.ARModContainer;
import cn.annoreg.mc.BlockRegistration;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

public class RegistrationManager {
	
	public static final RegistrationManager INSTANCE = new RegistrationManager();
	
	private Set<String> unloadedClass = new HashSet();
	
	private Map<Class<? extends Annotation>, RegistryType> regByClass = new HashMap();
	private Map<String, RegistryType> regByName = new HashMap();
	
	private Map<Class<?>, RegistryMod> modMap = new HashMap();
	private Set<RegistryMod> mods = new HashSet();
	
	private RegistrationManager() {
		addRegType(new BlockRegistration());
	}
	
	public void annotationList(Set<ASMData> data) {
		for (ASMData asm : data) {
			unloadedClass.add(asm.getClassName());
		}
	}
	
	private void loadClasses() {
		for (String name : unloadedClass) {
			try {
				prepareClass(Class.forName(name));
			} catch (ClassNotFoundException e) {
				ARModContainer.log.error("Error on loading class {}.", name);
				e.printStackTrace();
			}
		}
		unloadedClass.clear();
	}
	
	private void prepareClass(Class<?> clazz) {
		ARModContainer.log.info("Loading registration information on {}.", clazz.getName());
		
		//Class annotations
		for (Annotation anno : clazz.getAnnotations()) {
			Class<? extends Annotation> annoclazz = anno.getClass();
			if (regByClass.containsKey(annoclazz)) {
				regByClass.get(annoclazz).visitClass(clazz);
			}
		}
		
		//Field annotations
		for (Field field : clazz.getDeclaredFields()) {
			for (Annotation anno : field.getAnnotations()) {
				Class<? extends Annotation> annoclazz = anno.annotationType();
				if (regByClass.containsKey(annoclazz)) {
					regByClass.get(annoclazz).visitField(field);
				}
			}
		}
	}
	
	RegistryMod findMod(AnnotationData data) {
		for (RegistryMod mod : mods) {
			Class<?> clazz = data.type == AnnotationData.Type.CLASS ?
					data.getTheClass() : data.getTheField().getDeclaringClass();
			if (clazz.getCanonicalName().startsWith(mod.getPackage())) return mod;
		}
		return null;
	}
	
	public void addRegType(RegistryType type) {
		regByClass.put(type.getAnnotation(), type);
		regByName.put(type.getName(), type);
	}
	
	private RegistryMod createModFromObj(Class<?> modClazz) {
		if (modMap.containsKey(modClazz)) {
			return modMap.get(modClazz);
		} else {
			RegistrationPackage pkg = modClazz.getAnnotation(RegistrationPackage.class);
			if (pkg == null) {
				ARModContainer.log.error("Unable to create RegistryMod {}", modClazz.getCanonicalName());
				return null;
			}
			RegistryMod rm = new RegistryMod(pkg.value());
			modMap.put(modClazz, rm);
			return rm;
		}
	}
	
	private void addMod(RegistryMod mod) {
		mods.add(mod);
	}

	public void addAnnotationMod(Set<ASMData> data) {
		for (ASMData asm : data) {
			try {
				addMod(createModFromObj(Class.forName(asm.getClassName())));
			} catch (Exception e) {
				ARModContainer.log.error("Error on loading registration package {}.", asm.getClassName());
				e.printStackTrace();
			}
		}
	}
	
	private void registerAll(RegistryMod mod, String type) {
		//First load all classes that have not been loaded.
		loadClasses();
		regByName.get(type).registerAll(mod);
	}
	
	public void registerAll(Object mod, String type) {
		registerAll(createModFromObj(mod.getClass()), type);
	}
}
