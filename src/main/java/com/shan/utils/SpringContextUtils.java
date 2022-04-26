package com.shan.utils;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class SpringContextUtils {
    private static ApplicationContext context;

    public SpringContextUtils() {
    }

    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }

    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return context.getAliases(name);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static Class getType(String name) throws NoSuchBeanDefinitionException {
        return context.getType(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return context.isSingleton(name);
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        if (context == null) {
            context = ctx;
        }

    }
}
