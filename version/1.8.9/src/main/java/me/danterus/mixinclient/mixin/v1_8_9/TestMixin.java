package me.danterus.mixinclient.mixin.v1_8_9;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class TestMixin {

    @Inject(method = "startGame", at = @At("HEAD"))
    public void onStartGame(CallbackInfo callbackInfo) {
        System.out.println("Client Started 1.8.9!");
    }

}
