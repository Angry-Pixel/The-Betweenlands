package thebetweenlands.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static thebetweenlands.client.handler.ShaderHandler.*;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

	// Copy main render target depth to WorldShader depth buffer before depth is cleared
	@Inject(method = "renderDebug", at = @At("HEAD"), remap = false)
	public void renderDebugHook(PoseStack poseStack, MultiBufferSource buffer, Camera camera, CallbackInfo ci) {
		onPreRenderDebug(poseStack, buffer, camera);
	}

	@Inject(method = "renderSectionLayer", at = @At("HEAD"), remap = false)
	public void translucentPatcherStart(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		// Translucent RenderType only, Fast & Fancy only
		if (renderType != RenderType.translucent() || Minecraft.getInstance().levelRenderer.transparencyChain != null) return;
		onPreTranslucentBatch();
	}

	@Inject(method = "renderSectionLayer", at = @At("RETURN"), remap = false)
	public void translucentPatcherEnd(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
		if (renderType != RenderType.translucent() || Minecraft.getInstance().levelRenderer.transparencyChain != null) return;
		onPostTranslucentBatch();
	}
}
