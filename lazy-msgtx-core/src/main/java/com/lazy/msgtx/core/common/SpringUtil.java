package com.lazy.msgtx.core.common;

import org.springframework.context.ApplicationContext;

/**
 * <p>
 * spring util
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
public class SpringUtil {


    private static ApplicationContext context;

    public static void inject(ApplicationContext ctx) {
        context = ctx;
    }


    public static <T> T getBean(Class<T> clz) {
        return context.getBean(clz);
    }

    public static Class<?> getClass(String className) throws Exception {
        ClassLoader classLoader = context.getClassLoader();
        if (classLoader != null) {
            return classLoader.loadClass(className);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String className) throws Exception {

        // 1. ClassLoader 存在，则直接使用 clz 加载
        ClassLoader classLoader = context.getClassLoader();
        if (classLoader != null) {
            return (T) context.getBean(classLoader.loadClass(className));
        }
        // 2. ClassLoader 不存在(系统类加载器不可见)，尝试用类名称小写加载
        String[] split = className.split("\\.");
        String beanName = split[split.length - 1];
        // 小写转大写
        char[] cs = beanName.toCharArray();
        cs[0] += 32;
        String beanName0 = String.valueOf(cs);
        return (T) context.getBean(beanName0);
    }

}
