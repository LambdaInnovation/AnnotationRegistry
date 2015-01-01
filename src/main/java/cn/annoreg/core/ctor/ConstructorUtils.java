package cn.annoreg.core.ctor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cn.annoreg.ARModContainer;

public class ConstructorUtils {
	
	public static Object newInstance(Class<?> clazz, Ctor ctor) {
		for (Constructor c : clazz.getConstructors()) {
			if (c.isAnnotationPresent(Constructible.class)) {
				try {
					Object result = callConstructor(c, ctor);
					if (result == null)
						throw new RuntimeException("Unable to create object.");
					else {
						ARModContainer.log.info("Successfully create the object.");
						return result;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
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
