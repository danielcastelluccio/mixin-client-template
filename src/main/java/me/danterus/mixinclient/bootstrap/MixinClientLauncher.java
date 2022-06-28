package me.danterus.mixinclient.bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MixinClientLauncher {

    public static void main(String[] args) {
        String minecraftVersion = args[Arrays.asList(args).indexOf("--minecraftVersion") + 1];
        try {
            Class<?> bootstrapClass = Class.forName("me.danterus.mixinclient.bootstrap.v" + minecraftVersion.replace(".", "_") + ".Bootstrap");
            bootstrapClass.getMethod("bootstrap").invoke(null);

            Class<?> mainClass = Class.forName("net.minecraft.client.main.Main");

            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
