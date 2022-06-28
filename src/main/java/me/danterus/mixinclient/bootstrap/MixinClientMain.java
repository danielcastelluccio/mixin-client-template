package me.danterus.mixinclient.bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MixinClientMain {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ClassLoader classLoader = new MixinClientClassLoader();
        try {
            Class<?> wrapperClass = classLoader.loadClass("me.danterus.mixinclient.bootstrap.MixinClientLauncher");
            Method mainMethod = wrapperClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
