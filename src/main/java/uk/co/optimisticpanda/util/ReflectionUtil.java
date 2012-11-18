package uk.co.optimisticpanda.util;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import com.google.common.base.CaseFormat;

public enum ReflectionUtil {
	;

	private static boolean isValidGetter(Method method) {
		boolean get = method.getName().startsWith("get");
		boolean is = method.getName().startsWith("is");
		boolean hasIgnoreAnnotation = method.isAnnotationPresent(IgnoreForSerializing.class);
		return (get || is) && !method.getName().equals("getClass") && method.getParameterTypes().length == 0 && !hasIgnoreAnnotation ;
	}

	private static String getAttributeName(Method method) {
		int indexOfAttribute = method.getName().startsWith("get") ? 3 : 2;
		String name = method.getName().substring(indexOfAttribute);
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
	}

	public static void invokeGetters(Class<?> clazz, Object instance, GetterCallBack callback) {
		for (Method method : clazz.getMethods()) {
			if (isValidGetter(method)) {
				String name = UPPER_CAMEL.to(LOWER_CAMEL, getAttributeName(method));
				Object value = invoke(method, instance);
				try {
					callback.visit(name, value);
				} catch (Exception e) {
					throw new ConfigurationException("Problem invoking method on:" + instance.getClass(), e);
				}
			}
		}
	}

	public static Object invoke(Method method, Object instance, Object... args) {
		try {
			return method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException("Problem accessing :" + instance.getClass(), e);
		} catch (InvocationTargetException e) {
			throw new ConfigurationException("Problem invoking method on:" + instance.getClass(), e);
		} catch (Exception e) {
			throw new ConfigurationException("Problem invoking method on:" + instance.getClass(), e);
		}
	}

	/**marker annotation to ignore when serializing the object*/
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IgnoreForSerializing{
		
	}
	
	public static interface GetterCallBack {

		void visit(String attributeName, Object value) throws Exception;

	}

}
