package thebetweenlands.client.sky;

import java.nio.FloatBuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import thebetweenlands.api.sky.IRiftMaskRenderer;
import thebetweenlands.api.sky.IRiftRenderer;
import thebetweenlands.api.sky.IRiftSkyRenderer;
import thebetweenlands.client.shader.ResizableFramebuffer;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.RiftEvent;
import thebetweenlands.util.FramebufferStack;

import javax.annotation.Nullable;

public class RiftRenderer implements IRiftRenderer {
	protected final int skyDomeDispList;

	@Nullable
	private static ResizableFramebuffer overworldSkyFbo;

	private final FloatBuffer textureMatrix = BufferUtils.createFloatBuffer(16);
	private final FloatBuffer modelviewMatrix = BufferUtils.createFloatBuffer(16);
	private final FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
	private final FloatBuffer buffer4f = BufferUtils.createFloatBuffer(16);

	private IRiftMaskRenderer riftMaskRenderer;
	private IRiftSkyRenderer riftSkyRenderer;

	@Nullable
	private static RiftMaskRenderer blRiftMaskRenderer;
	@Nullable
	private static OverworldRiftSkyRenderer blRiftSkyRenderer;

	public RiftRenderer(int skyDomeDispList) {
		this.skyDomeDispList = skyDomeDispList;

		if (overworldSkyFbo == null) {
			overworldSkyFbo = new ResizableFramebuffer(true);
		}

		if (blRiftMaskRenderer == null) {
			blRiftMaskRenderer = new RiftMaskRenderer(this.skyDomeDispList);
		}

		if (blRiftSkyRenderer == null) {
			blRiftSkyRenderer = new OverworldRiftSkyRenderer();
		}

		this.setRiftMaskRenderer(blRiftMaskRenderer);
		this.setRiftSkyRenderer(blRiftSkyRenderer);
	}

	private FloatBuffer getBuffer4f(float v1, float v2, float v3, float v4) {
		this.buffer4f.clear();
		this.buffer4f.put(v1).put(v2).put(v3).put(v4);
		this.buffer4f.flip();
		return this.buffer4f;
	}

	@Override
	public void render(ClientLevel level, float partialTicks, Matrix4f projectionMatrix, Camera camera, Matrix4f frustrumMatrix, boolean isFoggy, Runnable skyFogSetup) {

		RiftEvent rift = EnvironmentEventRegistry.RIFT.get();

		if (rift.getActivationTicks() > 0 && rift.getVisibility(partialTicks) > 0) {
			RenderTarget skyFbo;
			float skyBrightness;
			PoseStack posestack = new PoseStack();
			posestack.mulPose(projectionMatrix);

			try (FramebufferStack.State state = FramebufferStack.push()) {
				skyFbo = overworldSkyFbo.getFramebuffer(state.getTarget().viewWidth, state.getTarget().viewHeight);

				skyFbo.setClearColor(0, 0, 0, 0);
				skyFbo.clear(Minecraft.ON_OSX);
				skyFbo.bindWrite(false);

				GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
				RenderSystem.enableCull();
				RenderSystem.enableDepthTest();
				RenderSystem.depthMask(true);
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);

				//Render rift sky
				posestack.pushPose();
				this.riftSkyRenderer.setClearColor(camera, level, partialTicks);
				RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
				this.riftSkyRenderer.render(level, partialTicks, projectionMatrix, camera, frustrumMatrix, isFoggy, skyFogSetup);
				posestack.popPose();

				skyBrightness = this.riftSkyRenderer.getSkyBrightness(level, partialTicks);

				RenderSystem.enableBlend();
				Lighting.setupForFlatItems();
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
				FogRenderer.setupNoFog();
				RenderSystem.depthMask(false);
				RenderSystem.setShaderColor(1, 1, 1, 1);

				//Set all alpha to 1 for mask blending
				RenderSystem.colorMask(false, false, false, true);
				RenderSystem.clearColor(0, 0, 0, 1);
				RenderSystem.clearDepth(1.0D);
				RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
				RenderSystem.colorMask(true, true, true, true);

				//Render mask
				if (GL.getCapabilities().OpenGL14) {
					RenderSystem.blendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_ALPHA);
				} else {
					RenderSystem.blendFunc(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_ALPHA); //Still decent looking fallback
				}

				this.riftMaskRenderer.renderMask(level, partialTicks, posestack, skyBrightness);
			}

			//Reset fog to this world's fog
			skyFogSetup.run();

			RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			FogRenderer.setupNoFog();
			RenderSystem.bindTexture(skyFbo.getColorTextureId());

			//Project onto sphere
			GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, this.modelviewMatrix);
			GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, this.projectionMatrix);

			//Set up UV generator FIXME how do I do this now
//			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_LINEAR);
//			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_LINEAR);
//			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_LINEAR);
//			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_PLANE, this.getBuffer4f(1.0F, 0.0F, 0.0F, 0.0F));
//			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_PLANE, this.getBuffer4f(0.0F, 1.0F, 0.0F, 0.0F));
//			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_PLANE, this.getBuffer4f(0.0F, 0.0F, 1.0F, 0.0F));
//			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
//			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
//			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);

			GL11.glMatrixMode(GL11.GL_TEXTURE);
			posestack.pushPose();
			GL11.glLoadIdentity();
			posestack.translate(0.5F, 0.5F, 0.0F);
			posestack.scale(0.5F, 0.5F, 1.0F);
			GL11.glMultMatrixf(this.projectionMatrix);
			GL11.glMultMatrixf(this.modelviewMatrix);

			//Render projection
			this.riftMaskRenderer.renderRiftProjection(level, partialTicks, camera, skyBrightness);

			GL11.glMatrixMode(GL11.GL_TEXTURE); //Make sure texture matrix is popped
			posestack.popPose();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

//			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
//			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
//			GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);

			//Render overlay
			this.riftMaskRenderer.renderOverlay(level, partialTicks, posestack, skyBrightness);

			RenderSystem.setShaderColor(1, 1, 1, 1);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			RenderSystem.disableBlend();
			RenderSystem.enableDepthTest();
		}
	}

	@Override
	public void setRiftMaskRenderer(IRiftMaskRenderer maskRenderer) {
		this.riftMaskRenderer = maskRenderer;
	}

	@Override
	public IRiftMaskRenderer getRiftMaskRenderer() {
		return this.riftMaskRenderer;
	}

	@Override
	public void setRiftSkyRenderer(IRiftSkyRenderer skyRenderer) {
		this.riftSkyRenderer = skyRenderer;
	}

	@Override
	public IRiftSkyRenderer getRiftSkyRenderer() {
		return this.riftSkyRenderer;
	}
}
