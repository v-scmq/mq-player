package com.scmq.player.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflect {
	/**
	 * 获取任何类的Class对象中name对应的Field(属性)对象
	 *
	 * @param clazz
	 *            类的Class对象
	 * @param name
	 *            属性名称
	 * @return 属性对应的Field对象
	 */
	public static Field getField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取任何类的Class对象中name对应的Method(方法)对象
	 *
	 * @param clazz
	 *            类的Class对象
	 * @param name
	 *            方法名称
	 * @param params
	 *            方法参数(可变参数,根据方法参数个数依次传入)
	 * @return 方法对应的Method对象
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
		try {
			Method method = clazz.getDeclaredMethod(name, params);
			method.setAccessible(true);
			return method;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 设置Field对应的属性的值
	 *
	 * @param obj
	 *            Field对象表示的属性所在的类的对象
	 * @param value
	 *            属性值
	 * @param field
	 *            属性对应的Field对象
	 */
	public static void setValue(Object obj, Object value, Field field) {
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取Field对象所表示的这个字段对应的值
	 * 
	 * @param field
	 *            字段(属性)对象
	 * @param object
	 *            字段所属类的对象,若是静态字段(属性)则需要传入null.
	 * @param <T>
	 *            泛型参数,返回值类型
	 * @return field对象所对应字段(属性)的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Field field, Object object) {
		try {
			return field == null ? null : (T) field.get(object);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取属性的值
	 * 
	 * @param clazz
	 *            所属类的Class对象
	 * @param field
	 *            字段(属性)对象
	 * @param object
	 *            所属对象,若字段是静态的,那么这个对象需要传入null.
	 * @param <T>
	 *            泛型参数,返回值类型
	 * @return 字段(属性)所对应的值
	 */
	public static <T> T getValue(Class<?> clazz, String field, Object object) {
		return getValue(getField(clazz, field), object);
	}

	/**
	 * 反射调用方法
	 *
	 * @param obj
	 *            方法所属的对象
	 * @param method
	 *            方法对象
	 * @param params
	 *            方法参数
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object obj, Method method, Object... params) {
		try {
			return method == null ? null : (T) method.invoke(obj, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
}
