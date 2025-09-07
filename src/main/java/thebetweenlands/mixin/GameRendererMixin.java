package thebetweenlands.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.client.handler.ShaderHandler;
import thebetweenlands.client.shader.ShaderHelper;

/* Notes:
	for renderItemInHand: renderHandsWithItems modifies input posestack invalidating @Local collection of posestack
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@Final
	@Shadow(remap = false)
	private RenderBuffers renderBuffers;

	@Final
	@Shadow(remap = false)
	public ItemInHandRenderer itemInHandRenderer;
	@Final
	@Shadow(remap = false)
	Minecraft minecraft;

	@Shadow(remap = false)
	protected abstract void bobHurt(PoseStack poseStack, float partialTicks);

	@Shadow(remap = false)
	protected abstract void bobView(PoseStack poseStack, float partialTicks);

	/**
	 * Calls itemHandRender again drawing depth to world depth buffer
	 */
	@Inject(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;turnOffLightLayer()V"), remap = false)
	private void injectWorldShaderHandRender(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) { // @Local PoseStack posestack
		if (!ShaderHelper.INSTANCE.canUseShaders()) return;

		// New hand projection matrix
		PoseStack posestack = new PoseStack();
		posestack.pushPose();
		posestack.mulPose(projectionMatrix.invert(new Matrix4f()));
		this.bobHurt(posestack, partialTick);
		if (this.minecraft.options.bobView().get()) {
			this.bobView(posestack, partialTick);
		}

		// Set depth draw target
		ShaderHelper.INSTANCE.getWorldShader().getDepthBuffer().bindWrite(false);

		// Render to world depth target
		itemInHandRenderer.renderHandsWithItems(partialTick, posestack, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, partialTick));

		// Cleanup
		minecraft.getMainRenderTarget().bindWrite(true);
	}

	// Inject into GameRender.resize to resize buffers

	/**
	 * Creates shader resize event
	 */
	@Inject(method = "resize", at = @At("TAIL"), remap = false)
	private void resizeBuffers(int width, int height, CallbackInfo ci) {
		ShaderHandler.resize(width, height);
	}
}
