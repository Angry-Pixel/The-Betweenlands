package thebetweenlands.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import thebetweenlands.api.sky.IRiftMaskRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.RiftEvent;

public class RiftMaskRenderer implements IRiftMaskRenderer {
	public static final ResourceLocation SKY_RIFT_MASK_BACK_TEXTURE = TheBetweenlands.prefix("textures/sky/rifts/sky_rift_mask_back.png");

	protected final int skyDomeDispList;

	//FIXME GLU is :crab:
	//private Sphere projectionSphere = new Sphere();

	public RiftMaskRenderer(int skyDomeDispList) {
		this.skyDomeDispList = skyDomeDispList;

		//this.projectionSphere.setTextureFlag(false);
	}

	@Override
	public void renderMask(ClientLevel level, float partialTicks, PoseStack stack, float skyBrightness) {
		RiftEvent rift = EnvironmentEventRegistry.RIFT.get();
		float[] riftAngles = rift.getRiftAngles(partialTicks);
		float scale = rift.getRiftScale(partialTicks);
		RiftVariant variant = rift.getVariant();

		//Render back mask
		RenderSystem.setShaderTexture(0, SKY_RIFT_MASK_BACK_TEXTURE);
		stack.pushPose();
		stack.scale(-1.0F, -1.0F, -1.0F);
		stack.translate(0.0F, -1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(riftAngles[0]));
		stack.mulPose(Axis.ZP.rotationDegrees(riftAngles[1]));
		stack.mulPose(Axis.XP.rotationDegrees(riftAngles[2]));

		GL11.glCullFace(GL11C.GL_FRONT);
		GL11.glCallList(this.skyDomeDispList);
		GL11.glCullFace(GL11C.GL_BACK);

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

		GL11.glCallList(this.skyDomeDispList);

		stack.popPose();

		GL11.glMatrixMode(GL11.GL_TEXTURE);
		stack.popPose();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
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

		GL11.glCallList(this.skyDomeDispList);

		if(variant.altOverlayTexture() != null) {
			RenderSystem.setShaderColor(1, 1, 1, visibility * (1 - skyBrightness));

			RenderSystem.setShaderTexture(0, variant.altOverlayTexture());

			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

			GL11.glCallList(this.skyDomeDispList);
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
