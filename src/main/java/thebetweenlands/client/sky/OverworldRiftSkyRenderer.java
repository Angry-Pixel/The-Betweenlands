package thebetweenlands.client.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import thebetweenlands.api.sky.IRiftSkyRenderer;
import thebetweenlands.common.config.BetweenlandsConfig;

public class OverworldRiftSkyRenderer implements IRiftSkyRenderer {
	private static final ResourceLocation MOON_PHASES_TEXTURES = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
	private static final ResourceLocation SUN_TEXTURES = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

	private VertexBuffer starBuffer;
	private VertexBuffer skyBuffer;
	private VertexBuffer darkBuffer;

	public OverworldRiftSkyRenderer() {

		this.createStars();
		this.createLightSky();
		this.createDarkSky();
	}

	@Override
	public void render(ClientLevel level, float partialTicks, Matrix4f projectionMatrix, Camera camera, Matrix4f frustrumMatrix, boolean isFoggy, Runnable skyFogSetup) {
		Minecraft minecraft = Minecraft.getInstance();
		skyFogSetup.run();
		PoseStack posestack = new PoseStack();
		posestack.mulPose(projectionMatrix);
		if (!isFoggy) {
			FogType fogtype = camera.getFluidInCamera();
			if (fogtype != FogType.POWDER_SNOW && fogtype != FogType.LAVA/* && !this.doesMobEffectBlockSky(pCamera)*/) {

				Vec3 vec3 = level.getSkyColor(minecraft.gameRenderer.getMainCamera().getPosition(), partialTicks);
				float f = (float)vec3.x;
				float f1 = (float)vec3.y;
				float f2 = (float)vec3.z;
				FogRenderer.levelFogColor();
				Tesselator tesselator = Tesselator.getInstance();
				RenderSystem.depthMask(false);
				RenderSystem.setShaderColor(f, f1, f2, 1.0F);
				ShaderInstance shaderinstance = RenderSystem.getShader();
				this.skyBuffer.bind();
				this.skyBuffer.drawWithShader(posestack.last().pose(), frustrumMatrix, shaderinstance);
				VertexBuffer.unbind();
				RenderSystem.enableBlend();

				float[] afloat = level.effects().getSunriseColor(level.getTimeOfDay(partialTicks), partialTicks);
				if (afloat != null) {
					RenderSystem.setShader(GameRenderer::getPositionColorShader);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					posestack.pushPose();
					posestack.mulPose(Axis.XP.rotationDegrees(90.0F));
					float f3 = Mth.sin(level.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F;
					posestack.mulPose(Axis.ZP.rotationDegrees(f3));
					posestack.mulPose(Axis.ZP.rotationDegrees(90.0F));
					float f4 = afloat[0];
					float f5 = afloat[1];
					float f6 = afloat[2];
					Matrix4f matrix4f = posestack.last().pose();
					BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
					bufferbuilder.addVertex(matrix4f, 0.0F, 100.0F, 0.0F).setColor(f4, f5, f6, afloat[3]);
					for (int j = 0; j <= 16; j++) {
						float f7 = (float) j * Mth.TWO_PI / 16.0F;
						float f8 = Mth.sin(f7);
						float f9 = Mth.cos(f7);
						bufferbuilder.addVertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * afloat[3])
							.setColor(afloat[0], afloat[1], afloat[2], 0.0F);
					}

					BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
					posestack.popPose();
				}

				RenderSystem.blendFuncSeparate(
					GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
				);
				posestack.pushPose();
				float f11 = 1.0F;
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f11);
				posestack.mulPose(Axis.YP.rotationDegrees(-90.0F));
				posestack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));
				Matrix4f matrix4f1 = posestack.last().pose();
				float f12 = 30.0F;
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, SUN_TEXTURES);
				BufferBuilder bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, -f12).setUv(0.0F, 0.0F);
				bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, -f12).setUv(1.0F, 0.0F);
				bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, f12).setUv(1.0F, 1.0F);
				bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, f12).setUv(0.0F, 1.0F);
				BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
				f12 = 20.0F;
				RenderSystem.setShaderTexture(0, MOON_PHASES_TEXTURES);
				int k = level.getMoonPhase();
				int l = k % 4;
				int i1 = k / 4 % 2;
				float f13 = (float)(l) / 4.0F;
				float f14 = (float)(i1) / 2.0F;
				float f15 = (float)(l + 1) / 4.0F;
				float f16 = (float)(i1 + 1) / 2.0F;
				bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
				bufferbuilder1.addVertex(matrix4f1, -f12, -100.0F, f12).setUv(f15, f16);
				bufferbuilder1.addVertex(matrix4f1, f12, -100.0F, f12).setUv(f13, f16);
				bufferbuilder1.addVertex(matrix4f1, f12, -100.0F, -f12).setUv(f13, f14);
				bufferbuilder1.addVertex(matrix4f1, -f12, -100.0F, -f12).setUv(f15, f14);
				BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
				float f10 = level.getStarBrightness(partialTicks) * f11;
				if (f10 > 0.0F) {
					RenderSystem.setShaderColor(f10, f10, f10, f10);
					FogRenderer.setupNoFog();
					this.starBuffer.bind();
					this.starBuffer.drawWithShader(posestack.last().pose(), frustrumMatrix, GameRenderer.getPositionShader());
					VertexBuffer.unbind();
					skyFogSetup.run();
				}

				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.disableBlend();
				RenderSystem.defaultBlendFunc();
				posestack.popPose();
				RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
				double d0 = minecraft.player.getEyePosition(partialTicks).y - level.getLevelData().getHorizonHeight(level);
				if (d0 < 0.0) {
					posestack.pushPose();
					posestack.translate(0.0F, 12.0F, 0.0F);
					this.darkBuffer.bind();
					this.darkBuffer.drawWithShader(posestack.last().pose(), frustrumMatrix, shaderinstance);
					VertexBuffer.unbind();
					posestack.popPose();
				}

				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.depthMask(true);
			}
		}

		if (BetweenlandsConfig.skyRiftClouds) {
			skyFogSetup.run();
			posestack.pushPose();
			minecraft.levelRenderer.renderClouds(posestack, projectionMatrix, frustrumMatrix, partialTicks, 0, 50, 0);
			posestack.popPose();
		}
	}

	private void createDarkSky() {
		if (this.darkBuffer != null) {
			this.darkBuffer.close();
		}

		this.darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		this.darkBuffer.bind();
		this.darkBuffer.upload(buildSkyDisc(Tesselator.getInstance(), -16.0F));
		VertexBuffer.unbind();
	}

	private void createLightSky() {
		if (this.skyBuffer != null) {
			this.skyBuffer.close();
		}

		this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		this.skyBuffer.bind();
		this.skyBuffer.upload(buildSkyDisc(Tesselator.getInstance(), 16.0F));
		VertexBuffer.unbind();
	}

	private static MeshData buildSkyDisc(Tesselator tesselator, float yPos) {
		float f = Math.signum(yPos) * 512.0F;
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
		bufferbuilder.addVertex(0.0F, yPos, 0.0F);

		for (int i = -180; i <= 180; i += 45) {
			bufferbuilder.addVertex(f * Mth.cos(i * Mth.DEG_TO_RAD), yPos, 512.0F * Mth.sin(i * Mth.DEG_TO_RAD));
		}

		return bufferbuilder.buildOrThrow();
	}

	private void createStars() {
		if (this.starBuffer != null) {
			this.starBuffer.close();
		}

		this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		this.starBuffer.bind();
		this.starBuffer.upload(this.drawStars(Tesselator.getInstance()));
		VertexBuffer.unbind();
	}

	private MeshData drawStars(Tesselator tesselator) {
		RandomSource randomsource = RandomSource.create(10842L);
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

		for (int j = 0; j < 1500; j++) {
			float f1 = randomsource.nextFloat() * 2.0F - 1.0F;
			float f2 = randomsource.nextFloat() * 2.0F - 1.0F;
			float f3 = randomsource.nextFloat() * 2.0F - 1.0F;
			float f4 = 0.15F + randomsource.nextFloat() * 0.1F;
			float f5 = Mth.lengthSquared(f1, f2, f3);
			if (!(f5 <= 0.010000001F) && !(f5 >= 1.0F)) {
				Vector3f vector3f = new Vector3f(f1, f2, f3).normalize(100.0F);
				float f6 = (float)(randomsource.nextDouble() * (float) Math.PI * 2.0);
				Quaternionf quaternionf = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), vector3f).rotateZ(f6);
				bufferbuilder.addVertex(vector3f.add(new Vector3f(f4, -f4, 0.0F).rotate(quaternionf)));
				bufferbuilder.addVertex(vector3f.add(new Vector3f(f4, f4, 0.0F).rotate(quaternionf)));
				bufferbuilder.addVertex(vector3f.add(new Vector3f(-f4, f4, 0.0F).rotate(quaternionf)));
				bufferbuilder.addVertex(vector3f.add(new Vector3f(-f4, -f4, 0.0F).rotate(quaternionf)));
			}
		}

		return bufferbuilder.buildOrThrow();
	}

	@Override
	public void setClearColor(Camera camera, ClientLevel level, float partialTicks) {
		FogRenderer.setupColor(camera, partialTicks, level, Minecraft.getInstance().options.getEffectiveRenderDistance(), 0.0F);
	}

	@Override
	public float getSkyBrightness(ClientLevel level, float partialTicks) {
		float f = level.getTimeOfDay(partialTicks);
		float f1 = Mth.cos(f * Mth.TWO_PI) * 2.0F + 0.5F;
		f1 = Mth.clamp(f1, 0.0F, 1.0F);
		return f1;
	}
}
