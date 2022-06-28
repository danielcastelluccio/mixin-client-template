package me.danterus.mixinclient.bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {

    private static final ClassLoader classLoader;

    static {
        classLoader = Util.class.getClassLoader();
    }

    public static byte[] getClassBytes(String className) throws ClassNotFoundException {
        try {
            return (byte[]) invokeClassloaderMethod("getModifiedBytes", className);
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Object invokeClassloaderMethod(String name, Object... args) throws Throwable {
        try {
            Class<?>[] argsClasses = new Class[args.length];
            for(int i = 0 ; i < args.length; i++) {
                argsClasses[i] = args[i].getClass();
            }

            Method method = classLoader.getClass().getMethod(name, argsClasses);
            return method.invoke(classLoader, args);

        } catch(NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

}
