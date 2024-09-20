package thebetweenlands.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.client.handler.MainMenuHandler;
import thebetweenlands.common.config.BetweenlandsConfig;

//TODO migrate to ASM
@Mixin(Screen.class)
public abstract class ScreenMixin {

	@Inject(method = "onClose", at = @At("HEAD"), remap = false)
	public void closeBackground(CallbackInfo ci) {
		MainMenuHandler.background.onClose();
	}

	@Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true, remap = false)
	public void renderBLBackground(GuiGraphics graphics, float partialTicks, CallbackInfo ci) {
		if (BetweenlandsConfig.blMainMenu) {
			MainMenuHandler.background.render(graphics, partialTicks, 1.0F);
			ci.cancel();
		}
	}
}
