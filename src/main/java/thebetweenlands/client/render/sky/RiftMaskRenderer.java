package thebetweenlands.client.render.sky;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.Sphere;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.sky.IRiftMaskRenderer;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.common.world.event.EventRift;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class RiftMaskRenderer implements IRiftMaskRenderer {
	public static final ResourceLocation SKY_RIFT_MASK_BACK_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/rifts/sky_rift_mask_back.png");

	protected final int skyDomeDispList;

	private Sphere projectionSphere = new Sphere();

	public RiftMaskRenderer(int skyDomeDispList) {
		this.skyDomeDispList = skyDomeDispList;

		this.projectionSphere.setTextureFlag(false);
	}

	@Override
	public void renderMask(float partialTicks, WorldClient world, Minecraft mc, float skyBrightness) {
		TextureManager textureManager = mc.getTextureManager();

		EventRift rift = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().rift;
		float[] riftAngles = rift.getRiftAngles(partialTicks);
		float scale = rift.getRiftScale(partialTicks);
		RiftVariant variant = rift.getVariant();

		//Render back mask
		textureManager.bindTexture(SKY_RIFT_MASK_BACK_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.scale(-1, -1, -1);
		GlStateManager.translate(0, -1, 0);
		GlStateManager.rotate(riftAngles[0], 0, 1, 0);
		GlStateManager.rotate(riftAngles[1], 0, 0, 1);
		GlStateManager.rotate(riftAngles[2], 0, 1, 0);

		GlStateManager.cullFace(CullFace.FRONT);
		GlStateManager.callList(this.skyDomeDispList);
		GlStateManager.cullFace(CullFace.BACK);

		GlStateManager.popMatrix();

		//Render front mask
		textureManager.bindTexture(variant.getMaskTexture());

		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.pushMatrix();
		int mirrorU = (rift.getRiftMirrorU() ? -1 : 1);
		int mirrorV = (rift.getRiftMirrorV() ? -1 : 1);
		GlStateManager.translate(mirrorU * -0.5f / scale, mirrorV * -0.5f / scale, 0);
		GlStateManager.scale(mirrorU / scale, mirrorV / scale, 1);
		GlStateManager.translate(mirrorU * 0.5f * scale, mirrorV * 0.5f * scale, 0);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -1, 0);
		GlStateManager.rotate(riftAngles[0], 0, 1, 0);
		GlStateManager.rotate(riftAngles[1], 0, 0, 1);
		GlStateManager.rotate(riftAngles[2], 0, 1, 0);

		GlStateManager.callList(this.skyDomeDispList);

		GlStateManager.popMatrix();

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	}

	@Override
	public void renderOverlay(float partialTicks, WorldClient world, Minecraft mc, float skyBrightness) {
		TextureManager textureManager = mc.getTextureManager();

		EventRift rift = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().rift;
		float[] riftAngles = rift.getRiftAngles(partialTicks);
		float visibility = rift.getVisibility(partialTicks);
		float scale = rift.getRiftScale(partialTicks);
		RiftVariant variant = rift.getVariant();

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.pushMatrix();
		int mirrorU = (rift.getRiftMirrorU() ? -1 : 1);
		int mirrorV = (rift.getRiftMirrorV() ? -1 : 1);
		GlStateManager.translate(mirrorU * -0.5f / scale, mirrorV * -0.5f / scale, 0);
		GlStateManager.scale(mirrorU / scale, mirrorV / scale, 1);
		GlStateManager.translate(mirrorU * 0.5f * scale, mirrorV * 0.5f * scale, 0);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -1, 0);
		GlStateManager.rotate(riftAngles[0], 0, 1, 0);
		GlStateManager.rotate(riftAngles[1], 0, 0, 1);
		GlStateManager.rotate(riftAngles[2], 0, 1, 0);

		if(variant.getAltOverlayTexture() != null) {
			GlStateManager.color(1, 1, 1, visibility * skyBrightness);
		} else {
			GlStateManager.color(1, 1, 1, visibility);
		}

		textureManager.bindTexture(variant.getOverlayTexture());

		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		GlStateManager.callList(this.skyDomeDispList);

		if(variant.getAltOverlayTexture() != null) {
			GlStateManager.color(1, 1, 1, visibility * (1 - skyBrightness));
	
			textureManager.bindTexture(variant.getAltOverlayTexture());
	
			GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			
			GlStateManager.callList(this.skyDomeDispList);
		}
		
		GlStateManager.popMatrix();

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();

		textureManager.bindTexture(variant.getOverlayTexture());
		
		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		
		if(variant.getAltOverlayTexture() != null) {
			textureManager.bindTexture(variant.getAltOverlayTexture());
			
			GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GlStateManager.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}
	}

	@Override
	public void renderRiftProjection(float partialTicks, WorldClient world, Minecraft mc, float skyBrightness) {
		EventRift rift = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().rift;
		float visibility = rift.getVisibility(partialTicks);
		float visibilitySq = visibility * visibility;
		
		GlStateManager.color(visibilitySq, visibilitySq, visibilitySq, visibility);

		GlStateManager.enableFog();
		GlStateManager.setFogStart(FogHandler.getCurrentFogStart() / 2);
		GlStateManager.setFogEnd(FogHandler.getCurrentFogEnd() / 2);

		GlStateManager.cullFace(CullFace.FRONT);
		this.projectionSphere.draw(55, 8, 8);
		GlStateManager.cullFace(CullFace.BACK);
	}
}
