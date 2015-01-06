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
	
	private Map<Class<?>, RegModInformation> modMap = new HashMap();
	private Set<RegModInformation> mods = new HashSet();
	
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
			Class<? extends Annotation> annoclazz = anno.annotationType();
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
		
		//Inner classes
		for (Class<?> inner : clazz.getClasses()) {
			prepareClass(inner);
		}
	}
	
	RegModInformation findMod(AnnotationData data) {
		for (RegModInformation mod : mods) {
			Class<?> clazz = data.type == AnnotationData.Type.CLASS ?
					data.getTheClass() : data.getTheField().getDeclaringClass();
			if (clazz.getCanonicalName().startsWith(mod.getPackage())) {
				data.mod = mod;
				return mod;
			}
		}
		return null;
	}
	
	public void addRegType(RegistryType type) {
		if (regByClass.containsKey(type.getAnnotation()) ||
				regByName.containsKey(type.getName())) {
			ARModContainer.log.error("Unable to add the registry type {}.", type.getName());
			return;
		}
		regByClass.put(type.getAnnotation(), type);
		regByName.put(type.getName(), type);
	}
	
	private RegModInformation createModFromObj(Class<?> modClazz) {
		if (modMap.containsKey(modClazz)) {
			return modMap.get(modClazz);
		} else {
			if (!modClazz.isAnnotationPresent(RegistrationMod.class)) {
				ARModContainer.log.error("Unable to create RegistryMod {}", modClazz.getCanonicalName());
				return null;
			}
			
			RegModInformation rm = new RegModInformation(modClazz);
			modMap.put(modClazz, rm);
			return rm;
		}
	}
	
	private void addMod(RegModInformation mod) {
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
	
	private void registerAll(RegModInformation mod, String type) {
		//First load all classes that have not been loaded.
		loadClasses();
		RegistryType rt = regByName.get(type);
		if (rt == null) {
			ARModContainer.log.fatal("RegistryType {} not found.", type);
			throw new RuntimeException();
		}
		rt.registerAll(mod);
	}
	
	public void registerAll(Object mod, String type) {
		registerAll(createModFromObj(mod.getClass()), type);
	}
	
	public void addRegistryTypes(Set<ASMData> data) {
		for (ASMData asm : data) {
			try {
				Class<?> clazz = Class.forName(asm.getClassName());
				RegistryType rt = (RegistryType) clazz.newInstance();
				addRegType(rt);
			} catch (Exception e) {
				ARModContainer.log.error("Error on adding registry type {}.", asm.getClassName());
				e.printStackTrace();
			}
		}
	}
	
	public void addSideOnlyRegAnnotation(Set<ASMData> data) {
		for (ASMData asm : data) {
			try {
				Class<?> clazz = Class.forName(asm.getClassName());
			} catch (Exception e) {
				ARModContainer.log.error("Error on adding registry annotation {}.", asm.getClassName());
			}
		}
	}
}
