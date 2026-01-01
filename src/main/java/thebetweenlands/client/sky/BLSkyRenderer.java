package thebetweenlands.client.sky;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import thebetweenlands.api.sky.BetweenlandsSky;
import thebetweenlands.api.sky.IRiftRenderer;
import thebetweenlands.client.BetweenlandsSpecialEffects;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.WorldShader;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BLSkyRenderer implements BetweenlandsSky {
	public static final ResourceLocation SKY_TEXTURE = TheBetweenlands.prefix("textures/sky/sky_texture.png");
	public static final ResourceLocation SKY_SPOOPY_TEXTURE = TheBetweenlands.prefix("textures/sky/spoopy.png");
	public static final ResourceLocation FOG_TEXTURE = TheBetweenlands.prefix("textures/sky/fog_texture.png");

	protected final List<AuroraRenderer> auroras = new ArrayList<>();

	private static int skyDomeDispList = -1;

	@Nullable
	private VertexBuffer starMesh;
	private VertexBuffer skyDomeMesh;
	private VertexBuffer spoopyDomeMesh;

	@Nullable
	public static RenderTarget clipPlaneBuffer;

	protected int ticks;
	protected boolean spoopy;

	//	Beware! Here be very hacky code!
	//	This flag is toggled and used by a lot of methods, see usages
	public static boolean drawOverworldSky = false;		// used by BetweenlandsSpecialEffects.renderSky to draw overworld sky

	private IRiftRenderer riftRenderer;

	@Nullable
	private static RiftRenderer blRiftRenderer;

	public BLSkyRenderer() {
		Window window = Minecraft.getInstance().getWindow();

		if (clipPlaneBuffer == null) {
			clipPlaneBuffer = new TextureTarget(window.getWidth(), window.getHeight(), true, true);
			clipPlaneBuffer.setClearColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		if (this.starMesh == null && ShaderHelper.INSTANCE.canUseShaders()) {
			if (this.starMesh != null) {
				this.starMesh.close();
			}

			this.starMesh = new VertexBuffer(VertexBuffer.Usage.STATIC);
			this.starMesh.bind();
			this.starMesh.upload(Minecraft.getInstance().levelRenderer.drawStars(Tesselator.getInstance()));
			VertexBuffer.unbind();
		}

		if (skyDomeMesh == null) {
			if (this.skyDomeMesh != null) {
				this.skyDomeMesh.close();
			}

			this.skyDomeMesh = new VertexBuffer(VertexBuffer.Usage.STATIC);
			this.skyDomeMesh.bind();
			this.skyDomeMesh.upload(this.createSkyDome(Tesselator.getInstance()));
			VertexBuffer.unbind();
		}

		if (spoopyDomeMesh == null) {
			if (this.spoopyDomeMesh != null) {
				this.spoopyDomeMesh.close();
			}

			this.spoopyDomeMesh = new VertexBuffer(VertexBuffer.Usage.STATIC);
			this.spoopyDomeMesh.bind();
			this.spoopyDomeMesh.upload(this.createScaledSkyDome(Tesselator.getInstance(), 2.0f, 2.0f));
			VertexBuffer.unbind();
		}

		if (blRiftRenderer == null) {
			blRiftRenderer = new RiftRenderer(this.skyDomeMesh);
		}

		this.setRiftRenderer(blRiftRenderer);
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	@Override
	public void render(ClientLevel level, float partialTicks, Matrix4f viewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable skyFogSetup) {
		PoseStack posestack = new PoseStack();
		posestack.mulPose(viewMatrix);

		this.renderSky(level, partialTicks, viewMatrix, projectionMatrix, posestack, camera);

		this.riftRenderer.render(level, partialTicks, viewMatrix, camera, projectionMatrix, isFoggy, skyFogSetup);

		this.renderFog(partialTicks, viewMatrix, projectionMatrix, posestack, skyFogSetup);

		this.renderAuroras(partialTicks, posestack, projectionMatrix, Minecraft.getInstance());
	}

	protected void renderSky(ClientLevel level, float partialTicks, Matrix4f viewMatrix, Matrix4f projectionMatrix, PoseStack posestack, Camera camera) {
		Vec3 skyColor = new Vec3(0.1F, 0.8F, 0.55F);
		float skyR = (float) skyColor.x;
		float skyG = (float) skyColor.y;
		float skyB = (float) skyColor.z;

		float invRainStrength = 1.0F - level.getRainLevel(partialTicks);

		posestack.pushPose();
		posestack.mulPose(Axis.XP.rotationDegrees(180.0F));

		RenderSystem.enableBlend();

		boolean useShaderSky = ShaderHelper.INSTANCE.isWorldShaderActive() && ShaderHelper.INSTANCE.getWorldShader() != null && ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture() >= 0;

		float starBrightness = (level.getStarBrightness(partialTicks) + 0.5F) * invRainStrength * invRainStrength * invRainStrength;
		float fade = EnvironmentEventRegistry.DENSE_FOG.get().getFade(partialTicks) * 0.95F + 0.05F;

		starBrightness *= fade;
		RenderSystem.disableBlend();

		if (false) { // world.provider.isSkyColored()
			RenderSystem.setShaderColor(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F, starBrightness / (!useShaderSky ? 1.5F : 1.0F));
		} else {
			RenderSystem.setShaderColor(skyR, skyG, skyB, starBrightness / (!useShaderSky ? 1.5F : 1.0F));
		}

		if (useShaderSky) {
			RenderSystem.setShaderTexture(0, ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture());
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.enableBlend();
			this.skyDomeMesh.bind();
			GameRenderer.getPositionTexShader().setDefaultUniforms(VertexFormat.Mode.TRIANGLES, viewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
			GameRenderer.getPositionTexShader().apply();
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			this.skyDomeMesh.draw();
			GameRenderer.getPositionTexShader().clear();

			//Render sky clip plane
			this.renderFlatSky(posestack, projectionMatrix,true, false);
		} else {
			if(Minecraft.getInstance().options.graphicsMode().get() != GraphicsStatus.FAST) {
				//Render fancy non-shader sky dome
				RenderSystem.setShaderTexture(0, SKY_TEXTURE);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.enableBlend();
				this.skyDomeMesh.bind();
				GameRenderer.getPositionTexShader().setDefaultUniforms(VertexFormat.Mode.TRIANGLES, viewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
				GameRenderer.getPositionTexShader().apply();
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				this.skyDomeMesh.draw();
				GameRenderer.getPositionTexShader().clear();
			}
			else {
				//Render flat sky
				this.renderFlatSky(posestack, projectionMatrix, false, false);
			}
		}
		RenderSystem.setShaderColor(1f,1f,1f,1f);
		if (this.spoopy) {
			if (Minecraft.getInstance().options.graphicsMode().get() != GraphicsStatus.FAST) {
				RenderSystem.setShaderTexture(0, SKY_SPOOPY_TEXTURE);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.enableBlend();
				this.spoopyDomeMesh.bind();
				GameRenderer.getPositionTexShader().setDefaultUniforms(VertexFormat.Mode.TRIANGLES, viewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
				GameRenderer.getPositionTexShader().apply();
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
				RenderSystem.depthMask(false);
				this.spoopyDomeMesh.draw();
			}
			else {
				this.renderFlatSky(posestack, projectionMatrix, false, true);
			}
		}
		posestack.popPose();
	}

	protected void renderFlatSky(PoseStack stack, Matrix4f projectionMatrix, boolean renderClipPlane, boolean spoopy) {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		RenderSystem.depthMask(false);

		Tesselator tesselator = Tesselator.getInstance();

		stack.pushPose();
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		if (!renderClipPlane) {
			if (spoopy) {
				RenderSystem.setShaderTexture(0, SKY_SPOOPY_TEXTURE);
			} else {
				boolean shaderTexture = false;
				if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
					WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
					if (shader != null && shader.getStarfieldTexture() >= 0) {
						RenderSystem.setShaderTexture(0, shader.getStarfieldTexture());
						shaderTexture = true;
					}
				}

				if (!shaderTexture) {
					RenderSystem.setShaderTexture(0, SKY_TEXTURE);
				}
			}

			float uscale = 1.0f;
			float vscale = 1.0f;
			if (spoopy) {
				uscale = 2.0f;
				vscale = 2.0f;
			}
			float uoffset = -0.5f * uscale + 0.5f;
			float voffset = -0.5f * vscale + 0.5f;

			BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			buffer.addVertex(-90.0F, -50.0F, -90.0F).setUv(uoffset, voffset);
			buffer.addVertex(-90.0F, -50.0F, 90.0F).setUv(uoffset, voffset + vscale);
			buffer.addVertex(90.0F, -50.0F, 90.0F).setUv(uoffset + uscale, voffset + vscale);
			buffer.addVertex(90.0F, -50.0F, -90.0F).setUv(uoffset + uscale, voffset);
			BufferUploader.drawWithShader(buffer.buildOrThrow());
		} else {
			//Render clip plane (for god rays)
			RenderSystem.depthMask(true);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

			RenderTarget mcFbo = Minecraft.getInstance().getMainRenderTarget();
			clipPlaneBuffer.resize(mcFbo.viewWidth, mcFbo.viewHeight, false);
			clipPlaneBuffer.bindWrite(false);
			clipPlaneBuffer.clear(false);

			BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			buffer.addVertex(-9000.0F, -90.0F, -9000.0F).setColor(255, 255, 255, 255);
			buffer.addVertex(-9000.0F, -90.0F, 9000.0F).setColor(255, 255, 255, 255);
			buffer.addVertex(9000.0F, -90.0F, 9000.0F).setColor(255, 255, 255, 255);
			buffer.addVertex(9000.0F, -90.0F, -9000.0F).setColor(255, 255, 255, 255);
			BufferUploader.drawWithShader(buffer.buildOrThrow());
			mcFbo.bindWrite(false);
		}

		RenderSystem.depthMask(true);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		stack.popPose();
	}

	protected void renderFog(float partialTicks, Matrix4f projectionMatrix, Matrix4f frustrumMatrix, PoseStack stack, Runnable skyFogSetup) {
		//Render sky dome with fog texture for fog noise illusion
		float renderTicks = this.ticks + partialTicks;

		float domeRotation = renderTicks * 0.1F;

		float renderRadius = 80.0F;

		stack.pushPose();
		stack.scale(
				1.0F / 50.0F * renderRadius,
				1.0F / 50.0F * renderRadius,
				1.0F / 50.0F * renderRadius
			);

		stack.translate(0, 10, 0);

		RenderSystem.setShaderColor(0, 0, 0, 0.25F);
		RenderSystem.setShaderTexture(0, FOG_TEXTURE);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		RenderSystem.setShader(ShaderHelper.INSTANCE::getSkyFogShader);
		RenderSystem.enableBlend();
		RenderSystem.depthMask(false);

		this.skyDomeMesh.bind();
		ShaderHelper.INSTANCE.getSkyFogShader().setDefaultUniforms(VertexFormat.Mode.TRIANGLES, stack.last().pose(), frustrumMatrix, Minecraft.getInstance().getWindow());
		ShaderHelper.INSTANCE.getSkyFogShader().FOG_COLOR.set(FogRenderer.fogRed, FogRenderer.fogGreen, FogRenderer.fogBlue);
		ShaderHelper.INSTANCE.getSkyFogShader().apply();
		this.skyDomeMesh.draw();

		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(domeRotation));
		stack.translate(0, Math.cos(renderTicks / 150.0F) * 6.0F + 4.0F, 0.0F);
		ShaderHelper.INSTANCE.getSkyFogShader().MODEL_VIEW_MATRIX.set(stack.last().pose());
		ShaderHelper.INSTANCE.getSkyFogShader().apply();
		this.skyDomeMesh.draw();
		stack.popPose();

		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(-domeRotation / 1.8F));
		stack.translate(0, -Math.sin(renderTicks / 170.0F) * 6.0F + 4.0F, 0.0F);
		ShaderHelper.INSTANCE.getSkyFogShader().MODEL_VIEW_MATRIX.set(stack.last().pose());
		ShaderHelper.INSTANCE.getSkyFogShader().apply();
		this.skyDomeMesh.draw();
		ShaderHelper.INSTANCE.getSkyFogShader().clear();
		stack.popPose();

		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		stack.popPose();
		RenderSystem.setShaderColor(1, 1, 1, 1);

		RenderSystem.setShaderFogStart(FogHandler.getCurrentFogStart());
		RenderSystem.setShaderFogEnd(FogHandler.getCurrentFogEnd());
		RenderSystem.depthMask(true);
	}

	protected MeshData createSkyDome(Tesselator tesselator) {
		double tileSize = 5.0D;
		Vec3 yOffset = new Vec3(0, 2, 0);
		Vec3 cp = new Vec3(0, -20, 0);
		double radius = 55.0D;
		int tiles = 12;
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);

		//Renders tiles and then normalizes their vertices to create a texture mapped dome
		for (int tx = -tiles; tx < tiles; tx++) {
			for (int tz = -tiles; tz < tiles; tz++) {
				/*
				 * 1-----4
				 * |  \  |
				 * 2-----3
				 */
				Vec3 tp1 = new Vec3(tx * tileSize, 0, tz * tileSize);
				tp1 = cp.add(tp1.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3 tp2 = new Vec3((tx) * tileSize, 0, (tz + 1) * tileSize);
				tp2 = cp.add(tp2.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3 tp3 = new Vec3((tx + 1) * tileSize, 0, (tz + 1) * tileSize);
				tp3 = cp.add(tp3.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3 tp4 = new Vec3((tx + 1) * tileSize, 0, (tz) * tileSize);
				tp4 = cp.add(tp4.subtract(cp).normalize().scale(radius)).add(yOffset);

				float u00 = (float) ((tp1.x) / (radius * 2.0D) + 0.5D);
				float u10 = (float) ((tp4.x) / (radius * 2.0D) + 0.5D);
				float u11 = (float) ((tp3.x) / (radius * 2.0D) + 0.5D);
				float u01 = (float) ((tp2.x) / (radius * 2.0D) + 0.5D);

				float v00 = (float) (((tp1.z) / (radius * 2.0D)) + 0.5D);
				float v10 = (float) (((tp4.z) / (radius * 2.0D)) + 0.5D);
				float v11 = (float) (((tp3.z) / (radius * 2.0D)) + 0.5D);
				float v01 = (float) (((tp2.z) / (radius * 2.0D)) + 0.5D);

				bufferbuilder.addVertex((float) tp1.x, (float) tp1.y, (float) tp1.z).setUv(u00, v00);
				bufferbuilder.addVertex((float) tp3.x, (float) tp3.y, (float) tp3.z).setUv(u11, v11);
				bufferbuilder.addVertex((float) tp2.x, (float) tp2.y, (float) tp2.z).setUv(u01, v01);

				bufferbuilder.addVertex((float) tp3.x, (float) tp3.y, (float) tp3.z).setUv(u11, v11);
				bufferbuilder.addVertex((float) tp1.x, (float) tp1.y, (float) tp1.z).setUv(u00, v00);
				bufferbuilder.addVertex((float) tp4.x, (float) tp4.y, (float) tp4.z).setUv(u10, v10);
			}
		}
		return bufferbuilder.buildOrThrow();
	}

	/**
	 * Mimics FFPL scaling texture matrix, by baking coordinate scale into mesh.
	 * @param tesselator
	 * @param x
	 * @param y
	 * @return
	 */
	protected MeshData createScaledSkyDome(Tesselator tesselator, float x, float y) {
		double tileSize = 5.0D;
		Vec3 yOffset = new Vec3(0, 2, 0);
		Vec3 cp = new Vec3(0, -20, 0);
		double radius = 55.0D;
		int tiles = 12;
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);

		//Renders tiles and then normalizes their vertices to create a texture mapped dome
		for (int tx = -tiles; tx < tiles; tx++) {
			for (int tz = -tiles; tz < tiles; tz++) {
				/*
				 * 1-----4
				 * |  \  |
				 * 2-----3
				 */
				Vec3 tp1 = new Vec3(tx * tileSize, 0, tz * tileSize);
				tp1 = cp.add(tp1.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3 tp2 = new Vec3((tx) * tileSize, 0, (tz + 1) * tileSize);
				tp2 = cp.add(tp2.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3 tp3 = new Vec3((tx + 1) * tileSize, 0, (tz + 1) * tileSize);
				tp3 = cp.add(tp3.subtract(cp).normalize().scale(radius)).add(yOffset);

				Vec3 tp4 = new Vec3((tx + 1) * tileSize, 0, (tz) * tileSize);
				tp4 = cp.add(tp4.subtract(cp).normalize().scale(radius)).add(yOffset);

				float u00 = (float) (((tp1.x) / (radius * 2.0D) + 0.5D) * x) - 0.5F;
				float u10 = (float) (((tp4.x) / (radius * 2.0D) + 0.5D) * x) - 0.5F;
				float u11 = (float) (((tp3.x) / (radius * 2.0D) + 0.5D) * x) - 0.5F;
				float u01 = (float) (((tp2.x) / (radius * 2.0D) + 0.5D) * x) - 0.5F;

				float v00 = (float) ((((tp1.z) / (radius * 2.0D)) + 0.5D) * y) - 0.5F;
				float v10 = (float) ((((tp4.z) / (radius * 2.0D)) + 0.5D) * y) - 0.5F;
				float v11 = (float) ((((tp3.z) / (radius * 2.0D)) + 0.5D) * y) - 0.5F;
				float v01 = (float) ((((tp2.z) / (radius * 2.0D)) + 0.5D) * y) - 0.5F;

				bufferbuilder.addVertex((float) tp1.x, (float) tp1.y, (float) tp1.z).setUv(u00, v00);
				bufferbuilder.addVertex((float) tp3.x, (float) tp3.y, (float) tp3.z).setUv(u11, v11);
				bufferbuilder.addVertex((float) tp2.x, (float) tp2.y, (float) tp2.z).setUv(u01, v01);

				bufferbuilder.addVertex((float) tp3.x, (float) tp3.y, (float) tp3.z).setUv(u11, v11);
				bufferbuilder.addVertex((float) tp1.x, (float) tp1.y, (float) tp1.z).setUv(u00, v00);
				bufferbuilder.addVertex((float) tp4.x, (float) tp4.y, (float) tp4.z).setUv(u10, v10);
			}
		}
		return bufferbuilder.buildOrThrow();
	}

	protected void renderAuroras(float partialTicks, PoseStack stack, Matrix4f projectionMatrix, Minecraft mc) {
		if (!this.auroras.isEmpty()) {
			FogRenderer.setupNoFog();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			stack.pushPose();
			stack.translate(-mc.gameRenderer.getMainCamera().getPosition().x(), -mc.gameRenderer.getMainCamera().getPosition().y(), -mc.gameRenderer.getMainCamera().getPosition().z());
			for (AuroraRenderer aurora : this.auroras) {
				aurora.render(partialTicks, 1, stack, projectionMatrix);
			}
			stack.popPose();
			RenderSystem.depthMask(true);
		}
	}

	public void update(ClientLevel level, Minecraft mc) {
		this.ticks++;

		BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(level);
		if (storage != null) {
			this.spoopy = BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.SPOOPY);

			if (BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.AURORAS)) { // BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.AURORAS)
				//TheBetweenlands.LOGGER.debug("open");
				RandomSource rand = level.getRandom();
				double newAuroraPosX = mc.player.getX() + rand.nextInt(160) - 80;
				double newAuroraPosZ = mc.player.getZ() + rand.nextInt(160) - 80;
				double newAuroraPosY = 260;
				double minDist = 0.0D;

				for (AuroraRenderer aurora : this.auroras) {
					if (aurora.getDistance(mc.player.getX(), aurora.getY(), mc.player.getZ()) > 180) {
						aurora.setActive(false);
					}
					double dist = aurora.getDistance(newAuroraPosX, newAuroraPosY, newAuroraPosZ);
					if (dist < minDist || minDist == 0.0D) {
						minDist = dist;
					}
				}

				if (minDist > 150 || this.auroras.isEmpty()) {
					List<Vector4f> gradients = new ArrayList<>();
					switch (EnvironmentEventRegistry.AURORAS.get().getAuroraType()) {
						default:
						case 0:
							gradients.add(new Vector4f(0, 1, 0, 0.01F));
							gradients.add(new Vector4f(0, 1, 0, 0.15F));
							gradients.add(new Vector4f(0, 1, 0.8F, 0.8F));
							gradients.add(new Vector4f(0, 0.7F, 1, 0.15F));
							gradients.add(new Vector4f(0, 0.4F, 1, 0.01F));
							break;
						case 1:
							gradients.add(new Vector4f(1, 0, 0, 0.05F));
							gradients.add(new Vector4f(1, 0, 0, 0.2F));
							gradients.add(new Vector4f(1, 0, 0.5F, 0.5F));
							gradients.add(new Vector4f(1, 0.2F, 0.5F, 0.8F));
							gradients.add(new Vector4f(1, 0, 0.5F, 0.5F));
							gradients.add(new Vector4f(0.8F, 0, 0.5F, 0.25F));
							break;
						case 2:
							gradients.add(new Vector4f(0, 1, 0, 0.05F));
							gradients.add(new Vector4f(0.5F, 1, 0, 0.15F));
							gradients.add(new Vector4f(1, 0.8F, 0, 0.7F));
							gradients.add(new Vector4f(0.5F, 0.4F, 0, 0.15F));
							gradients.add(new Vector4f(1, 0.2F, 0, 0.05F));
							break;
					}

					this.auroras.add(new AuroraRenderer(newAuroraPosX, newAuroraPosY + rand.nextInt(100), newAuroraPosZ, new Vector2d(rand.nextFloat() * 2.0F - 1.0F, rand.nextFloat() * 2.0F - 1.0F), rand.nextInt(40) + 15, gradients));
				}
			} else {
				for (AuroraRenderer aurora : this.auroras) {
					aurora.setActive(false);
				}
			}

			Iterator<AuroraRenderer> auroraIT = this.auroras.iterator();
			while (auroraIT.hasNext()) {
				AuroraRenderer aurora = auroraIT.next();
				if (aurora.isRemoved()) {
					auroraIT.remove();
				} else {
					aurora.update();
				}
			}
		}
	}

	public static void onClientTick(ClientTickEvent.Post event) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;

		DimensionSpecialEffects effect = DimensionSpecialEffects.forType(level.dimensionType());
		if (effect instanceof BetweenlandsSpecialEffects) {
			BLSkyRenderer skyRenderer = ((BetweenlandsSpecialEffects)effect).getSkyRenderer();
			skyRenderer.update(level, Minecraft.getInstance());
		}
	}

	@Override
	public void setRiftRenderer(IRiftRenderer renderer) {
		this.riftRenderer = renderer;
	}

	@Override
	public IRiftRenderer getRiftRenderer() {
		return this.riftRenderer;
	}
}
