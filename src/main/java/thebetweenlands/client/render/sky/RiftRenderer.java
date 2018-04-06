package thebetweenlands.client.render.sky;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import thebetweenlands.api.sky.IRiftMaskRenderer;
import thebetweenlands.api.sky.IRiftRenderer;
import thebetweenlands.api.sky.IRiftSkyRenderer;
import thebetweenlands.client.render.shader.ResizableFramebuffer;
import thebetweenlands.common.world.event.EventRift;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.RenderUtils;

public class RiftRenderer implements IRiftRenderer {
	protected final int skyDomeDispList;

	private static ResizableFramebuffer overworldSkyFbo;

	private final FloatBuffer biasMatrix = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer textureMatrix = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer buffer4f = GLAllocation.createDirectFloatBuffer(16);

	private IRiftMaskRenderer riftMaskRenderer;
	private IRiftSkyRenderer riftSkyRenderer;

	private static RiftMaskRenderer blRiftMaskRenderer;
	private static OverworldRiftSkyRenderer blRiftSkyRenderer;

	public RiftRenderer(int skyDomeDispList) {
		this.skyDomeDispList = skyDomeDispList;

		this.biasMatrix.clear();
		this.biasMatrix
		.put(0.5F).put(0.0F).put(0.0F).put(0.0F)
		.put(0.0F).put(0.5F).put(0.0F).put(0.0F)
		.put(0.0F).put(0.0F).put(0.5F).put(0.0F)
		.put(0.5F).put(0.5F).put(0.5F).put(1.0F)
		.flip();

		if(overworldSkyFbo == null) {
			overworldSkyFbo = new ResizableFramebuffer(true);
		}

		if(blRiftMaskRenderer == null) {
			blRiftMaskRenderer = new RiftMaskRenderer(this.skyDomeDispList);
		}

		if(blRiftSkyRenderer == null) {
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
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		if(OpenGlHelper.isFramebufferEnabled()) {
			EventRift rift = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().rift;

			if(rift.getActiveTicks() > 0 && rift.getVisibility(partialTicks) > 0) {
				int parentFboId = RenderUtils.getBoundFramebuffer();
				
				Framebuffer mcFbo = mc.getFramebuffer();
				Framebuffer skyFbo = overworldSkyFbo.getFramebuffer(mcFbo.framebufferWidth, mcFbo.framebufferHeight);

				skyFbo.setFramebufferColor(0, 0, 0, 0);
				skyFbo.framebufferClear();
				skyFbo.bindFramebuffer(false);

				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);
				GlStateManager.enableCull();
				GlStateManager.enableDepth();
				GlStateManager.depthMask(true);
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
				GlStateManager.disableTexture2D();

				//Render rift sky
				GlStateManager.pushMatrix();
				this.riftSkyRenderer.setClearColor(partialTicks, world, mc);
				GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				this.riftSkyRenderer.render(partialTicks, world, mc);
				GlStateManager.popMatrix();

				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.enableTexture2D();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
				GlStateManager.disableFog();
				GlStateManager.depthMask(false);
				GlStateManager.color(1, 1, 1, 1);

				//Set all alpha to 1 for mask blending
				GlStateManager.colorMask(false, false, false, true);
				GlStateManager.clearColor(0, 0, 0, 1);
				GlStateManager.clearDepth(1.0D);
				GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				GlStateManager.colorMask(true, true, true, true);

				//Render mask
				if(OpenGlHelper.openGL14) {
					GlStateManager.tryBlendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_ALPHA);
				} else {
					GlStateManager.blendFunc(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_ALPHA); //Still decent looking fallback
				}

				this.riftMaskRenderer.renderMask(partialTicks, world, mc);

				if(parentFboId != -1) {
					OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, parentFboId);
				} else {
					mcFbo.bindFramebuffer(true);
				}

				//Reset fog to this world's fog
				mc.entityRenderer.setupFogColor(false);

				GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.disableFog();
				GlStateManager.bindTexture(skyFbo.framebufferTexture);

				//Project onto sphere
				GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, this.modelviewMatrix);
				GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, this.projectionMatrix);

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();

				GlStateManager.multMatrix(this.biasMatrix);
				GlStateManager.multMatrix(this.projectionMatrix);
				GlStateManager.multMatrix(this.modelviewMatrix);

				GlStateManager.getFloat(GL11.GL_TEXTURE_MATRIX, this.textureMatrix);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);

				//Set up UV generator
				GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_LINEAR);
				GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_LINEAR);
				GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_LINEAR);
				GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);
				GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_PLANE, this.getBuffer4f(this.textureMatrix.get(0), this.textureMatrix.get(4), this.textureMatrix.get(8), this.textureMatrix.get(12)));
				GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_PLANE, this.getBuffer4f(this.textureMatrix.get(1), this.textureMatrix.get(5), this.textureMatrix.get(9), this.textureMatrix.get(13)));
				GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_PLANE, this.getBuffer4f(this.textureMatrix.get(2), this.textureMatrix.get(6), this.textureMatrix.get(10), this.textureMatrix.get(14)));
				GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, this.getBuffer4f(this.textureMatrix.get(3), this.textureMatrix.get(7), this.textureMatrix.get(11), this.textureMatrix.get(15)));
				GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
				GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
				GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
				GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);

				//Render projection
				this.riftMaskRenderer.renderRiftProjection(partialTicks, world, mc);

				GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
				GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
				GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
				GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);

				//Render overlay
				this.riftMaskRenderer.renderOverlay(partialTicks, world, mc);

				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.disableBlend();
				GlStateManager.enableDepth();
			}
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
