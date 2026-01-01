package thebetweenlands.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.client.handler.ShaderHandler;

@OnlyIn(Dist.CLIENT)
@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

	/**
	 * Creates shader render event
	 */
	@Inject(method = "renderScreenEffect", at = @At("HEAD"), remap = false)
	private static void renderScreenEffectHook(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
		ShaderHandler.renderWorldShader(minecraft.getTimer().getRealtimeDeltaTicks());
	}
}
