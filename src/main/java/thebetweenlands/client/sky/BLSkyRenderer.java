package thebetweenlands.client.sky;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import thebetweenlands.api.sky.IBetweenlandsSky;
import thebetweenlands.api.sky.IRiftRenderer;
import thebetweenlands.client.shader.GeometryBuffer;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.WorldShader;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.FramebufferStack;
import thebetweenlands.util.Mesh;
import thebetweenlands.util.Mesh.Triangle;
import thebetweenlands.util.Mesh.Triangle.Vertex;
import thebetweenlands.util.Mesh.Triangle.Vertex.Vector3D;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BLSkyRenderer implements IBetweenlandsSky {
	public static final ResourceLocation SKY_TEXTURE = TheBetweenlands.prefix("textures/sky/sky_texture.png");
	public static final ResourceLocation SKY_SPOOPY_TEXTURE = TheBetweenlands.prefix("textures/sky/spoopy.png");
	public static final ResourceLocation FOG_TEXTURE = TheBetweenlands.prefix("textures/sky/fog_texture.png");

	protected List<AuroraRenderer> auroras = new ArrayList<>();

	private static int skyDomeDispList = -1;

	@Nullable
	private static Mesh starMesh;

	@Nullable
	public static GeometryBuffer clipPlaneBuffer;

	protected int ticks;
	protected boolean spoopy;

	private IRiftRenderer riftRenderer;

	@Nullable
	private static RiftRenderer blRiftRenderer;

	public BLSkyRenderer() {
		if (clipPlaneBuffer == null) {
			clipPlaneBuffer = new GeometryBuffer(Minecraft.getInstance().getTextureManager(), WorldShader.CLIP_PLANE_DIFFUSE_TEXTURE, WorldShader.CLIP_PLANE_DEPTH_TEXTURE, true);
		}

		if (starMesh == null && ShaderHelper.INSTANCE.canUseShaders()) {
			starMesh = this.createStarMesh();
		}

		if (skyDomeDispList == -1) {
			skyDomeDispList = GL11.glGenLists(1);
			GL11.glNewList(skyDomeDispList, GL11.GL_COMPILE);
			this.renderSkyDome();
			GL11.glEndList();
		}

		if (blRiftRenderer == null) {
			blRiftRenderer = new RiftRenderer(skyDomeDispList);
		}

		this.setRiftRenderer(blRiftRenderer);
	}

	@Override
	public void render(ClientLevel level, float partialTicks, Matrix4f projectionMatrix, Camera camera, Matrix4f frustrumMatrix, boolean isFoggy, Runnable skyFogSetup) {
		PoseStack posestack = new PoseStack();
		posestack.mulPose(projectionMatrix);
		this.renderSky(level, partialTicks, posestack, camera);

		this.riftRenderer.render(level, partialTicks, projectionMatrix, camera, frustrumMatrix, isFoggy, skyFogSetup);

		this.renderFog(partialTicks, posestack, skyFogSetup);

		this.renderAuroras(partialTicks, posestack, Minecraft.getInstance());

		this.resetRenderingStates();
	}

	protected Mesh createStarMesh() {
		List<Triangle> triangles = new ArrayList<>();

		RandomSource random = RandomSource.create(10842L);

		for (int i = 0; i < 1500; ++i) {
			double rx = random.nextFloat() * 2.0F - 1.0F;
			double ry = random.nextFloat() * 0.5F - 1.0F;
			double rz = random.nextFloat() * 2.0F - 1.0F;
			double centerDistance = rx * rx + ry * ry + rz * rz;

			if (centerDistance < 1.0D && centerDistance > 0.01D) {
				centerDistance = 1.0D / Math.sqrt(centerDistance);
				rx *= centerDistance;
				ry *= centerDistance;
				rz *= centerDistance;

				double farX = rx * 100.0D;
				double farY = ry * 100.0D;
				double farZ = rz * 100.0D;
				double xzAngle = Math.atan2(rx, rz);
				double xzRotX = Math.sin(xzAngle);
				double xzRotY = Math.cos(xzAngle);
				double distYAngle = Math.atan2(Math.sqrt(rx * rx + rz * rz), ry);
				double distYRotX = Math.sin(distYAngle);
				double distYRotZ = Math.cos(distYAngle);
				double randAngle = random.nextDouble() * Math.PI * 2.0D;
				double randRotX = Math.sin(randAngle);
				double randRotY = Math.cos(randAngle);

				int color = 0xFFFFFFFF;
				if (random.nextInt(2) == 1) {
					color = 0xFF009900;
				}

				double randSize = 0.15F + random.nextFloat() * 0.1F;
				Vertex v1 = this.getQuadPoint(0, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v2 = this.getQuadPoint(1, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v3 = this.getQuadPoint(2, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Vertex v4 = this.getQuadPoint(3, randSize, randRotX, randRotY, distYRotX, distYRotZ, xzRotX, xzRotY, farX, farY, farZ, color);
				Triangle t1 = new Triangle(v1, v2, v3);
				Triangle t2 = new Triangle(v3, v4, v1);
				triangles.add(t1);
				triangles.add(t2);
			}
		}

		return new Mesh(triangles);
	}

	protected Vertex getQuadPoint(int vertex, double randSize, double randRotX, double randRotY,
								  double distYRotX, double distYRotZ, double xzRotX, double xzRotY, double farX, double farY, double farZ,
								  int color) {
		double randRotYMultiplier = 0.0D;
		double vertX = (double) ((vertex & 2) - 1) * randSize;
		double vertZ = (double) ((vertex + 1 & 2) - 1) * randSize;
		double rotVertX = vertX * randRotY - vertZ * randRotX;
		double rotVertZ = vertZ * randRotY + vertX * randRotX;
		double rotVertX2 = rotVertX * distYRotX + randRotYMultiplier * distYRotZ;
		double rotVertZ2 = randRotYMultiplier * distYRotX - rotVertX * distYRotZ;
		vertX = rotVertZ2 * xzRotX - rotVertZ * xzRotY;
		vertZ = rotVertZ * xzRotX + rotVertZ2 * xzRotY;
		return new Vertex(farX + vertX, farY + rotVertX2, farZ + vertZ, new Vector3D(0, -1, 0), color);
	}

	protected void renderSky(ClientLevel level, float partialTicks, PoseStack posestack, Camera camera) {
		Vec3 skyColor = level.getSkyColor(camera.getPosition(), partialTicks);
		float skyR = (float) skyColor.x;
		float skyG = (float) skyColor.y;
		float skyB = (float) skyColor.z;

		float invRainStrength = 1.0F - level.getRainLevel(partialTicks);

		posestack.pushPose();
		posestack.mulPose(Axis.XP.rotationDegrees(180.0F));

		RenderSystem.enableBlend();

		boolean useShaderSky = ShaderHelper.INSTANCE.isWorldShaderActive() && ShaderHelper.INSTANCE.getWorldShader() != null && ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture() >= 0;

		float starBrightness = (level.getStarBrightness(partialTicks) + 0.5F) * invRainStrength * invRainStrength * invRainStrength;
		float fade = 1.0F;
//		WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(level);
//		if (provider != null) {
//			fade = provider.getEnvironmentEventRegistry().denseFog.getFade(partialTicks) * 0.95F + 0.05F;
//		}
		starBrightness *= fade;
		if (starBrightness > 0.0F && !useShaderSky && starMesh != null) {
			GL14.glBlendColor(0, 0, 0, (starBrightness - 0.22F) * 3.5F);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
			posestack.pushPose();
			starMesh.render();
			posestack.popPose();
			GL14.glBlendColor(1, 1, 1, 1);
		}

		posestack.popPose();

		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		RenderSystem.disableBlend();

		if (true/*world.provider.isSkyColored()*/) {
			RenderSystem.setShaderColor(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F, starBrightness / (!useShaderSky ? 1.5F : 1.0F));
		} else {
			RenderSystem.setShaderColor(skyR, skyG, skyB, starBrightness / (!useShaderSky ? 1.5F : 1.0F));
		}

		if (useShaderSky) {
			//Render shader sky dome
			RenderSystem.bindTexture(ShaderHelper.INSTANCE.getWorldShader().getStarfieldTexture());
			RenderSystem.enableBlend();
			Lighting.setupForFlatItems();
			RenderSystem.depthMask(false);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
			GL11.glCallList(skyDomeDispList);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();

			//Render sky clip plane
			this.renderFlatSky(posestack, true, false);
		} else {
			if (Minecraft.useFancyGraphics()) {
				//Render fancy non-shader sky dome
				RenderSystem.setShaderTexture(0, SKY_TEXTURE);
				RenderSystem.enableBlend();
				Lighting.setupForFlatItems();
				RenderSystem.depthMask(false);
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
				GL11.glCallList(skyDomeDispList);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				RenderSystem.depthMask(true);
				RenderSystem.disableBlend();
			} else {
				//Render flat sky
				this.renderFlatSky(posestack, false, false);
			}
		}

		if (this.spoopy) {
			if (Minecraft.useFancyGraphics()) {
				RenderSystem.setShaderTexture(0, SKY_SPOOPY_TEXTURE);

				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

				GL11.glMatrixMode(GL11.GL_TEXTURE);
				posestack.pushPose();
				posestack.translate(-0.5D, -0.5D, 1);
				posestack.scale(2.0F, 2.0F, 0.0F);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);

				RenderSystem.enableBlend();
				Lighting.setupForFlatItems();
				RenderSystem.depthMask(false);
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
				GL11.glCallList(skyDomeDispList);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				RenderSystem.depthMask(true);
				RenderSystem.disableBlend();

				GL11.glMatrixMode(GL11.GL_TEXTURE);
				posestack.popPose();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
			} else {
				posestack.pushPose();

				RenderSystem.setShaderTexture(0, SKY_SPOOPY_TEXTURE);

				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
				RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

				GL11.glMatrixMode(GL11.GL_TEXTURE);
				posestack.pushPose();
				posestack.translate(-0.5D, -0.5D, 1);
				posestack.scale(2.0F, 2.0F, 0.0F);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);

				this.renderFlatSky(posestack, false, true);

				GL11.glMatrixMode(GL11.GL_TEXTURE);
				posestack.popPose();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);

				posestack.popPose();
			}
		}
	}

	protected void renderFlatSky(PoseStack stack, boolean renderClipPlane, boolean spoopy) {
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

			BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			buffer.addVertex(-90.0F, -50.0F, -90.0F).setUv(0.0F, 0.0F);
			buffer.addVertex(-90.0F, -50.0F, 90.0F).setUv(0.0F, 1.0F);
			buffer.addVertex(90.0F, -50.0F, 90.0F).setUv(1.0F, 1.0F);
			buffer.addVertex(90.0F, -50.0F, -90.0F).setUv(1.0F, 0.0F);
			BufferUploader.drawWithShader(buffer.buildOrThrow());
		} else {
			//Render clip plane (for god rays)
			RenderSystem.depthMask(true);
			FogRenderer.setupNoFog();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

			//try (FramebufferStack.State ignored = FramebufferStack.push()) {
				RenderTarget mcFbo = Minecraft.getInstance().getMainRenderTarget();
				clipPlaneBuffer.updateGeometryBuffer(mcFbo.viewWidth, mcFbo.viewHeight);
				clipPlaneBuffer.bind();
				clipPlaneBuffer.clear(0.0F, 0.0F, 0.0F, 0.0F);

				BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
				buffer.addVertex(-9000.0F, -90.0F, -9000.0F).setColor(255, 255, 255, 255);
				buffer.addVertex(-9000.0F, -90.0F, 9000.0F).setColor(255, 255, 255, 255);
				buffer.addVertex(9000.0F, -90.0F, 9000.0F).setColor(255, 255, 255, 255);
				buffer.addVertex(9000.0F, -90.0F, -9000.0F).setColor(255, 255, 255, 255);
				BufferUploader.drawWithShader(buffer.buildOrThrow());

				clipPlaneBuffer.updateDepthBuffer();
			//}

			RenderSystem.setShaderColor(1, 1, 1, 1);
		}

		stack.popPose();
	}

	protected void renderFog(float partialTicks, PoseStack stack, Runnable skyFogSetup) {
		//Render sky dome with fog texture for fog noise illusion
		if (Minecraft.useFancyGraphics()) {
			stack.pushPose();

			float renderRadius = 80.0F;

			skyFogSetup.run();
			RenderSystem.setShaderFogStart(renderRadius / 2F);
			RenderSystem.setShaderFogEnd(renderRadius * 2F);

			stack.scale(
				1.0F / 50.0F * renderRadius,
				1.0F / 50.0F * renderRadius,
				1.0F / 50.0F * renderRadius
			);

			stack.translate(0, 10, 0);

			RenderSystem.setShaderTexture(0, FOG_TEXTURE);

			RenderSystem.enableBlend();
			Lighting.setupForFlatItems();
			RenderSystem.depthMask(false);
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

			float renderTicks = this.ticks + partialTicks;

			float domeRotation = renderTicks * 0.1F;

			stack.scale(1F, 0.8F, 1F);

			RenderSystem.setShaderColor(0, 0, 0, 0.25F);
			GL11.glCallList(skyDomeDispList);

			RenderSystem.setShaderColor(0, 0, 0, 0.15F);
			stack.pushPose();
			stack.mulPose(Axis.YP.rotationDegrees(domeRotation));
			stack.translate(0, Math.cos(renderTicks / 150.0F) * 6.0F + 4.0F, 0.0F);
			GL11.glCallList(skyDomeDispList);
			stack.popPose();

			stack.pushPose();
			stack.mulPose(Axis.YP.rotationDegrees(-domeRotation / 1.8F));
			stack.translate(0, -Math.sin(renderTicks / 170.0F) * 6.0F + 4.0F, 0.0F);
			GL11.glCallList(skyDomeDispList);
			stack.popPose();

			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();

			stack.popPose();

			//RenderSystem.setShaderFogStart(FogHandler.getCurrentFogStart());
			//RenderSystem.setShaderFogEnd(FogHandler.getCurrentFogEnd());
		}
	}

	protected void renderSkyDome() {
		double tileSize = 5.0D;
		Vec3 yOffset = new Vec3(0, 2, 0);
		Vec3 cp = new Vec3(0, -20, 0);
		double radius = 55.0D;
		int tiles = 12;
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_TRIANGLES);
		//Renders tiles and then normalizes their vertices to create a texture mapped dome
		for (int tx = -tiles; tx < tiles; tx++) {
			for (int tz = -tiles; tz < tiles; tz++) {
				/*
				 * 1-----4
				 * |     |
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

				float v00 = (float) (1 - ((tp1.z) / (radius * 2.0D) + 0.5D));
				float v10 = (float) (1 - ((tp4.z) / (radius * 2.0D) + 0.5D));
				float v11 = (float) (1 - ((tp3.z) / (radius * 2.0D) + 0.5D));
				float v01 = (float) (1 - ((tp2.z) / (radius * 2.0D) + 0.5D));

				GL11.glTexCoord2f(u00, v00);
				GL11.glVertex3f((float) tp1.x, (float) tp1.y, (float) tp1.z);
				GL11.glTexCoord2f(u11, v11);
				GL11.glVertex3f((float) tp3.x, (float) tp3.y, (float) tp3.z);
				GL11.glTexCoord2f(u01, v01);
				GL11.glVertex3f((float) tp2.x, (float) tp2.y, (float) tp2.z);

				GL11.glTexCoord2f(u11, v11);
				GL11.glVertex3f((float) tp3.x, (float) tp3.y, (float) tp3.z);
				GL11.glTexCoord2f(u00, v00);
				GL11.glVertex3f((float) tp1.x, (float) tp1.y, (float) tp1.z);
				GL11.glTexCoord2f(u10, v10);
				GL11.glVertex3f((float) tp4.x, (float) tp4.y, (float) tp4.z);
			}
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	protected void resetRenderingStates() {
		//Value used while rendering the world, but is only set once before rendering the sky
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);

		//RenderSystem.setShaderFogStart(FogHandler.getCurrentFogStart());
		//RenderSystem.setShaderFogEnd(FogHandler.getCurrentFogEnd());

		RenderSystem.disableBlend();

		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
	}

	protected void renderAuroras(float partialTicks, PoseStack stack, Minecraft mc) {
		if (!this.auroras.isEmpty()) {
			FogRenderer.setupNoFog();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			stack.pushPose();
			stack.translate(-mc.gameRenderer.getMainCamera().getPosition().x(), -mc.gameRenderer.getMainCamera().getPosition().y(), -mc.gameRenderer.getMainCamera().getPosition().z());
			for (AuroraRenderer aurora : this.auroras) {
				aurora.render(partialTicks, 1, stack);
			}
			stack.popPose();
			RenderSystem.depthMask(true);
		}
	}

	public void update(ClientLevel level, Minecraft mc) {
		this.ticks++;

		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.get(level);
		if (storage != null) {
			this.spoopy = BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.SPOOPY);

			if (BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.AURORAS)) {
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

//	public static void onClientTick(ClientTickEvent event) {
//		if (event.phase == Phase.END) {
//			ClientLevel world = Minecraft.getInstance().level;
//			BLSkyRenderer skyRenderer = WorldProviderBetweenlands.getBLSkyRenderer();
//			if (world != null && skyRenderer != null) {
//				skyRenderer.update(world, Minecraft.getInstance());
//			}
//		}
//	}

	@Override
	public void setRiftRenderer(IRiftRenderer renderer) {
		this.riftRenderer = renderer;
	}

	@Override
	public IRiftRenderer getRiftRenderer() {
		return this.riftRenderer;
	}
}
