package com.workdawn.simpledynamicplugin.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射调用工具
 * Created by Administrator
 */
public class ReflectUtils {

    /**
     * 设置字段的值
     * @param clazz class
     * @param fieldName 字段名
     * @param value 值
     */
    public static void setFieldValue(Class<?> clazz, Object target, String fieldName, Object value) throws Exception{
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * 获取字段值
     * @param clazz class
     * @param target 目标对象
     * @param fieldName 字段名
     * @throws Exception
     */
    public static Object getFieldValue(Class<?> clazz, Object target, String fieldName) throws Exception{
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    public static Object invokeMethod(Class<?> clazz, String methodName) throws Exception{
        return invokeMethod(clazz, null, methodName, null, (Class[])null);
    }

    public static Object invokeMethod(Class<?> clazz, Object target, String methodName) throws Exception{
        return invokeMethod(clazz, target, methodName, null, (Class[])null);
    }
    /**
     * 执行方法
     * @param clazz class
     * @param target 方法所属对象
     * @param methodName 方法名
     * @param parameterTypes 参数类型
     * @param paramsValue 参数值
     * @throws Exception
     */
    public static Object invokeMethod(Class<?> clazz, Object target, String methodName, Object[] paramsValue, Class<?>... parameterTypes) throws Exception{
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        return method.invoke(target, paramsValue);
    }
}
