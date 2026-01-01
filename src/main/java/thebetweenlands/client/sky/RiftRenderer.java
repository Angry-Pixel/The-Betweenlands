package thebetweenlands.client.sky;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import thebetweenlands.api.sky.IRiftMaskRenderer;
import thebetweenlands.api.sky.IRiftRenderer;
import thebetweenlands.api.sky.IRiftSkyRenderer;
import thebetweenlands.client.shader.ResizableFramebuffer;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.RiftEvent;

import javax.annotation.Nullable;

import static com.mojang.blaze3d.platform.GlConst.*;
import static org.lwjgl.opengl.GL11.GL_REPEAT;

public class RiftRenderer implements IRiftRenderer {
	protected VertexBuffer skyDomeMesh;

	public static final ResourceLocation RIFT_BACK_TEXTURE = TheBetweenlands.prefix("textures/sky/rifts/sky_rift_mask_back.png");
	public static final ResourceLocation RIFT_MASK_TEXTURE = TheBetweenlands.prefix("textures/sky/rifts/sky_rift_mask_1.png");
	public static final ResourceLocation RIFT_OVERLAY_TEXTURE = TheBetweenlands.prefix("textures/sky/rifts/sky_rift_overlay_1.png");
	public static final ResourceLocation RIFT_ALT_OVERLAY_TEXTURE = TheBetweenlands.prefix("textures/sky/rifts/sky_rift_alt_overlay_1.png");

	@Nullable
	private static ResizableFramebuffer overworldSkyFbo;

	private IRiftMaskRenderer riftMaskRenderer;
	private IRiftSkyRenderer riftSkyRenderer;

	@Nullable
	private static RiftMaskRenderer blRiftMaskRenderer;
	@Nullable
	private static OverworldRiftSkyRenderer blRiftSkyRenderer;
	@Nullable
	public static RenderTarget skyFbo;

	// Sky render target buffer
	public RiftRenderer(VertexBuffer skyDomeMesh) {
		this.skyDomeMesh = skyDomeMesh;

		if (overworldSkyFbo == null) {
			overworldSkyFbo = new ResizableFramebuffer(true);
		}

		if (blRiftMaskRenderer == null) {
			blRiftMaskRenderer = new RiftMaskRenderer(this.skyDomeMesh);
		}

		if (blRiftSkyRenderer == null) {
			blRiftSkyRenderer = new OverworldRiftSkyRenderer();
		}

		if (skyFbo == null) {
			skyFbo = new TextureTarget(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), true, Minecraft.ON_OSX);
			skyFbo.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		}

		this.setRiftMaskRenderer(blRiftMaskRenderer);
		this.setRiftSkyRenderer(blRiftSkyRenderer);
	}

	@Override
	public void render(ClientLevel level, float partialTicks, Matrix4f viewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable skyFogSetup) {

		RiftEvent event = EnvironmentEventRegistry.RIFT.get();
		RiftVariant variant = event.getVariant();

		if (event.getActivationTicks() > 0 && event.getVisibility(partialTicks) > 0) {

			// Set sky draw state
			BLSkyRenderer.drawOverworldSky = true;

			// Set to overworld fog color
			FogRenderer.setupColor(camera, partialTicks, level, Minecraft.getInstance().options.getEffectiveRenderDistance(), 0.0F);
			FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, Minecraft.getInstance().gameRenderer.getRenderDistance(), false, partialTicks);
			float skyBrightness = Mth.clamp(Mth.cos(level.getTimeOfDay(partialTicks) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
			Vec3 fogColor = level.effects().getBrightnessDependentFogColor(Vec3.ZERO, skyBrightness);
			skyFbo.setClearColor((float)fogColor.x, (float)fogColor.y, (float)fogColor.z, 0.0F);
			FogRenderer.fogRed = (float)fogColor.x;
			FogRenderer.fogGreen = (float)fogColor.y;
			FogRenderer.fogBlue = (float)fogColor.z;
			FogRenderer.levelFogColor();

			// Render overworld sky
			skyFbo.clear(Minecraft.ON_OSX);
			skyFbo.bindWrite(false);
			this.riftSkyRenderer.render(level, partialTicks, viewMatrix, camera, projectionMatrix, isFoggy, skyFogSetup);

			// Reset sky draw state
			BLSkyRenderer.drawOverworldSky = false;
			Minecraft.getInstance().getMainRenderTarget().bindWrite(false);

			// DEBUG: show rift location
			PoseStack riftView = new PoseStack();
			riftView.mulPose(viewMatrix);

			PoseStack textureMatrix = new PoseStack();
			int mirrorU = event.getRiftMirrorU() ? -1 : 1;
			int mirrorV = event.getRiftMirrorV() ? -1 : 1;

			float scale = event.getRiftScale(partialTicks);

			textureMatrix.pushPose();
			textureMatrix.translate(mirrorU * -0.5f / scale, mirrorV * -0.5f / scale, 0);
			textureMatrix.scale(mirrorU / scale, mirrorV / scale, 1);
			textureMatrix.translate(mirrorU * 0.5f * scale, mirrorV * 0.5f * scale, 0);

			float[] riftAngles = event.getRiftAngles(partialTicks);

			riftView.pushPose();
			riftView.translate(0, -1, 0);
			riftView.mulPose(Axis.YP.rotationDegrees(riftAngles[0]));
			riftView.mulPose(Axis.ZP.rotationDegrees(riftAngles[1]));
			riftView.mulPose(Axis.YP.rotationDegrees(riftAngles[2]));

			RenderSystem.enableBlend();
			FogRenderer.setupNoFog();

			float visibility = event.getVisibility(partialTicks);
			float visibilitySq = visibility * visibility;

			this.skyDomeMesh.bind();
			RenderSystem.depthFunc(GL11.GL_ALWAYS);
			RenderSystem.setShader(ShaderHelper.INSTANCE::getRiftShader);
			RenderSystem.setShaderColor(visibilitySq,visibilitySq,visibilitySq, visibility);
			RenderSystem.setShaderTexture(0, skyFbo.getColorTextureId());
			RenderSystem.setShaderTexture(1, variant.maskTexture());
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			RenderSystem.setShaderTexture(2, variant.overlayTexture());
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			RenderSystem.setShaderTexture(3, variant.altOverlayTexture());
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			ShaderHelper.INSTANCE.getRiftShader().setDefaultUniforms(VertexFormat.Mode.TRIANGLES, riftView.last().pose(), projectionMatrix, Minecraft.getInstance().getWindow());
			ShaderHelper.INSTANCE.getRiftShader().setTexMatrix(textureMatrix.last().pose());
			ShaderHelper.INSTANCE.getRiftShader().setOverlay(1.0f - this.riftSkyRenderer.getSkyBrightness(level, partialTicks));
			ShaderHelper.INSTANCE.getRiftShader().apply();
			this.skyDomeMesh.draw();
			ShaderHelper.INSTANCE.getRiftShader().clear();
			textureMatrix.popPose();
			riftView.popPose();

			// cleanup
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);

			// Set to betweenlands fog color
			FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_TERRAIN, Minecraft.getInstance().gameRenderer.getRenderDistance(), false, partialTicks);
			FogRenderer.setupColor(camera, partialTicks, level, Minecraft.getInstance().options.getEffectiveRenderDistance(), 0.0F);
			FogRenderer.levelFogColor();
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
