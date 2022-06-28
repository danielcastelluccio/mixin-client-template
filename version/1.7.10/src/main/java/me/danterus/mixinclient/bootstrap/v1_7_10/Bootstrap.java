package me.danterus.mixinclient.bootstrap.v1_7_10;

import org.spongepowered.asm.mixin.Mixins;

public class Bootstrap {

    public static void bootstrap() {
        Mixins.addConfiguration("mixins.client.1.7.10.json");
    }

}
