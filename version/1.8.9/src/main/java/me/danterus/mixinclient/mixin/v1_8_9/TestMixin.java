package me.danterus.mixinclient.mixin.v1_8_9;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.GuiRenameWorld;

@Mixin(GuiRenameWorld.class)
public class TestMixin {

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/I18n;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    public String getTitle(String key, Object[] args) {
        return "Your Mom3s";
    }

}