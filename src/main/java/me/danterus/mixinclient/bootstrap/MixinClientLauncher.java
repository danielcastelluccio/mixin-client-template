package me.danterus.mixinclient.bootstrap;

import org.spongepowered.asm.mixin.Mixins;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MixinClientLauncher {

    public static void main(String[] args) {
        Mixins.addConfiguration("mixins.client.json");
        try {
            Class<?> mainClass = Class.forName("net.minecraft.client.main.Main");

            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
