package cn.annoreg.core.ctor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import cn.annoreg.ARModContainer;

public class ConstructorUtils {
	
	public static Object newInstance(Class<?> clazz) {
		Object obj = null;
		try {
			obj = ConstructorUtils.newInstance(clazz, clazz.getAnnotation(Ctor.class));
		} catch (Exception e) {
			ARModContainer.log.error("Can not create instance for class {}.", clazz.getCanonicalName());
			throw new RuntimeException(e);
		}
		return obj;
	}
	
	public static Object newInstance(Field field) {
		Object ret = null;
		try {
			ret = field.get(null);
			if (ret != null) {
				//No need to create new instance.
				return ret;
			}
			ret = newInstance(field.getType(), field.getAnnotation(Ctor.class));
			field.set(null, ret);
		} catch (Exception e) {
			ARModContainer.log.error("Can not create instance for field {}.", field.toString());
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	public static Object newInstance(Class<?> clazz, Ctor ctor) {
		if (ctor == null) {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Unable to create object.", e);
			}
		}
		for (Constructor c : clazz.getConstructors()) {
			if (c.isAnnotationPresent(Constructible.class)) {
				try {
					Object result = callConstructor(c, ctor);
					if (result == null) {
						throw new RuntimeException("Unable to create object.");
					} else {
						return result;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		throw new RuntimeException("Unable to create object. No Constructible constructor.");
	}
	
	private static Object callConstructor(Constructor ctor, Ctor anno) throws Exception {
		if (ctor.isVarArgs()) return null;//not supported
		Class<?> params[] = ctor.getParameterTypes();
		Arg args[] = anno.value();
		if (params.length != args.length) return null;
		Object obj[] = new Object[args.length];
		for (int i = 0; i < args.length; ++i) {
			if (params[i] == Integer.class || params[i] == Integer.TYPE) {
				obj[i] = args[i].Int();
			} else if (params[i] == String.class) {
				obj[i] = args[i].Str();
			} else {
				return null;
			}
		}
		return ctor.newInstance(obj);
	}
	
}
