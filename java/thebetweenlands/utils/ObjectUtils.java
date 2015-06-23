package thebetweenlands.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ObjectUtils {
	public static String toString(Object o) {
		try {
			Class<?> clazz = o.getClass();
			List<Field> allFields = new ArrayList<Field>();
			Class<?> currentClass = clazz;
			do {
				for (Field field : currentClass.getDeclaredFields()) {
					if ((field.getModifiers() & Modifier.STATIC) == 0) {
						field.setAccessible(true);
						allFields.add(field);
					}
				}
			} while ((currentClass = currentClass.getSuperclass()) != null);
			if (allFields.size() == 0) {
				return o.toString();
			}
			StringBuilder stringBuilder = new StringBuilder(clazz.getSimpleName());
			stringBuilder.append('[');
			Collections.sort(allFields, new Comparator<Field>() {
				@Override
				public int compare(Field a, Field b) {
					return a.getName().compareTo(b.getName());
				}
			});
			int fieldCount = allFields.size();
			for (int i = 0; i < fieldCount; i++) {
				Field field = allFields.get(i);
				Object value = field.get(o);
				String valueString;
				if (value == null) {
					valueString = "null";
				} else {
					Class<?> valueClass = value.getClass();
					Method[] declaredMethods = valueClass.getDeclaredMethods();
					boolean allGood = true;
					for (Method method : declaredMethods) {
						if (method.getName().equals("toString") && (method.getModifiers() & Modifier.ABSTRACT) != 0) {
							allGood = false;
							break;
						}
					}
					if (allGood) {
						valueString = valueClass.isArray() ? arrayToString(value) : value.toString();
					} else {
						valueString = "<abstract>";
					}
				}
				stringBuilder.append(field.getName());
				stringBuilder.append('=');
				stringBuilder.append(valueString);
				if (i < fieldCount - 1) {
					stringBuilder.append(", ");
				}
			}
			return stringBuilder.append(']').toString();
		} catch (Exception e) {
			e.printStackTrace();
			return o.toString();
		}
	}

	public static String arrayToString(Object object) {
		Class<?> componentType = object.getClass().getComponentType();
		if (componentType.isPrimitive()) {
			if (boolean.class.isAssignableFrom(componentType)) {
				return Arrays.toString((boolean[]) object);
			} else if (byte.class.isAssignableFrom(componentType)) {
				return Arrays.toString((byte[]) object);
			} else if (char.class.isAssignableFrom(componentType)) {
				return Arrays.toString((char[]) object);
			} else if (double.class.isAssignableFrom(componentType)) {
				return Arrays.toString((double[]) object);
			} else if (float.class.isAssignableFrom(componentType)) {
				return Arrays.toString((float[]) object);
			} else if (int.class.isAssignableFrom(componentType)) {
				return Arrays.toString((int[]) object);
			} else if (long.class.isAssignableFrom(componentType)) {
				return Arrays.toString((long[]) object);
			} else if (short.class.isAssignableFrom(componentType)) {
				return Arrays.toString((short[]) object);
			}
		}
		return object.toString();
	}
}
