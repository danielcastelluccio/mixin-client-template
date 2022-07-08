package me.danterus.mixinclient.mixin.v1_8_9;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class InGameGuiMixin {

    @Inject(method = "renderGameOverlay", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
       //System.out.println("test");
    }

    @Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderScoreboard(Lnet/minecraft/scoreboard/ScoreObjective;Lnet/minecraft/client/gui/ScaledResolution;)V"))
    public void onRenderScoreboard(GuiIngame instance, ScoreObjective scoreObjective, ScaledResolution scaledResolution) {
        System.out.println("test2");
    }

}