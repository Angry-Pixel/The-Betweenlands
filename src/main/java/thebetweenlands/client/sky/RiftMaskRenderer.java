package thebetweenlands.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12;
import thebetweenlands.api.sky.IRiftMaskRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.RiftEvent;

// unused
public class RiftMaskRenderer implements IRiftMaskRenderer {
	public static final ResourceLocation SKY_RIFT_MASK_BACK_TEXTURE = TheBetweenlands.prefix("textures/sky/rifts/sky_rift_mask_back.png");

	protected VertexBuffer skyDomeMesh;

	public RiftMaskRenderer(VertexBuffer skyDomeMesh) {
		this.skyDomeMesh = new VertexBuffer(VertexBuffer.Usage.STATIC);
		this.skyDomeMesh.bind();
		this.skyDomeMesh.upload(this.createSkyDome(Tesselator.getInstance()));
		VertexBuffer.unbind();
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

	@Override
	public void renderMask(ClientLevel level, float partialTicks, PoseStack stack, Matrix4f frustrumMatrix, float skyBrightness) {
		RiftEvent rift = EnvironmentEventRegistry.RIFT.get();
		float[] riftAngles = rift.getRiftAngles(partialTicks);
		float scale = rift.getRiftScale(partialTicks);
		RiftVariant variant = rift.getVariant();

		//Render back mask
		RenderSystem.setShaderTexture(0, SKY_RIFT_MASK_BACK_TEXTURE);
		stack.pushPose();
		//stack.scale(-1.0F, -1.0F, -1.0F);
		//stack.translate(0.0F, -1.0F, 0.0F);
		//stack.mulPose(Axis.YP.rotationDegrees(riftAngles[0]));
		//stack.mulPose(Axis.ZP.rotationDegrees(riftAngles[1]));
		//stack.mulPose(Axis.XP.rotationDegrees(riftAngles[2]));

		//GL11.glCullFace(GL11C.GL_FRONT);
		RenderSystem.setShaderTexture(0, 0);
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(1f,1f,1f,1f);
		this.skyDomeMesh.bind();
		GameRenderer.getPositionShader().setDefaultUniforms(VertexFormat.Mode.TRIANGLES, stack.last().pose(), frustrumMatrix, Minecraft.getInstance().getWindow());
		GameRenderer.getPositionShader().apply();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		//RenderSystem.polygonMode(GL11.GL_FRONT, GL11.GL_FILL);
		this.skyDomeMesh.draw();
		stack.popPose();
		//GL11.glCallList(this.skyDomeDispList);
		//GL11.glCullFace(GL11C.GL_BACK);

		/*
		stack.popPose();

		//Render front mask
		RenderSystem.setShaderTexture(0, variant.maskTexture());

		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		stack.pushPose();
		int mirrorU = (rift.getRiftMirrorU() ? -1 : 1);
		int mirrorV = (rift.getRiftMirrorV() ? -1 : 1);
		stack.translate(mirrorU * -0.5f / scale, mirrorV * -0.5f / scale, 0);
		stack.scale(mirrorU / scale, mirrorV / scale, 1);
		stack.translate(mirrorU * 0.5f * scale, mirrorV * 0.5f * scale, 0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		stack.pushPose();
		stack.translate(0, -1, 0);
		stack.mulPose(Axis.YP.rotationDegrees(riftAngles[0]));
		stack.mulPose(Axis.ZP.rotationDegrees(riftAngles[1]));
		stack.mulPose(Axis.YP.rotationDegrees(riftAngles[2]));

		//skyDomeMesh.draw();
		//GL11.glCallList(this.skyDomeDispList);

		stack.popPose();

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		stack.popPose();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		*/
	}

	@Override
	public void renderOverlay(ClientLevel level, float partialTicks, PoseStack stack, float skyBrightness) {
		RiftEvent rift = EnvironmentEventRegistry.RIFT.get();
		float[] riftAngles = rift.getRiftAngles(partialTicks);
		float visibility = rift.getVisibility(partialTicks);
		float scale = rift.getRiftScale(partialTicks);
		RiftVariant variant = rift.getVariant();

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		stack.pushPose();
		int mirrorU = (rift.getRiftMirrorU() ? -1 : 1);
		int mirrorV = (rift.getRiftMirrorV() ? -1 : 1);
		stack.translate(mirrorU * -0.5f / scale, mirrorV * -0.5f / scale, 0);
		stack.scale(mirrorU / scale, mirrorV / scale, 1);
		stack.translate(mirrorU * 0.5f * scale, mirrorV * 0.5f * scale, 0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		stack.pushPose();
		stack.translate(0, -1, 0);
		stack.mulPose(Axis.YP.rotationDegrees(riftAngles[0]));
		stack.mulPose(Axis.ZP.rotationDegrees(riftAngles[1]));
		stack.mulPose(Axis.YP.rotationDegrees(riftAngles[2]));

		if(variant.altOverlayTexture() != null) {
			RenderSystem.setShaderColor(1, 1, 1, visibility * skyBrightness);
		} else {
			RenderSystem.setShaderColor(1, 1, 1, visibility);
		}

		RenderSystem.setShaderTexture(0, variant.overlayTexture());

		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		//skyDomeMesh.draw();
		//GL11.glCallList(this.skyDomeDispList);

		if(variant.altOverlayTexture() != null) {
			RenderSystem.setShaderColor(1, 1, 1, visibility * (1 - skyBrightness));

			RenderSystem.setShaderTexture(0, variant.altOverlayTexture());

			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

			//skyDomeMesh.draw();
			//GL11.glCallList(this.skyDomeDispList);
		}

		stack.popPose();

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		stack.popPose();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();

		RenderSystem.setShaderTexture(0, variant.overlayTexture());

		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		if(variant.altOverlayTexture() != null) {
			RenderSystem.setShaderTexture(0, variant.altOverlayTexture());

			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}
	}

	@Override
	public void renderRiftProjection(ClientLevel level, float partialTicks, Camera camera, float skyBrightness) {
		RiftEvent rift = EnvironmentEventRegistry.RIFT.get();
		float visibility = rift.getVisibility(partialTicks);
		float visibilitySq = visibility * visibility;

		RenderSystem.setShaderColor(visibilitySq, visibilitySq, visibilitySq, visibility);

		FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, Minecraft.getInstance().gameRenderer.getRenderDistance(), false, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
		//RenderSystem.setShaderFogStart(FogHandler.getCurrentFogStart() / 2);
		//RenderSystem.setShaderFogEnd(FogHandler.getCurrentFogEnd() / 2);

		GL11.glCullFace(GL11C.GL_FRONT);
		//this.projectionSphere.draw(55, 8, 8);
		GL11.glCullFace(GL11C.GL_BACK);
	}
}
