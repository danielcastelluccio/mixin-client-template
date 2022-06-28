package me.danterus.mixinclient.bootstrap.v1_8_9;

import org.spongepowered.asm.mixin.Mixins;

public class Bootstrap {

    public static void bootstrap() {
        Mixins.addConfiguration("mixins.client.1.8.9.json");
    }

}
