package thebetweenlands.client.render.sky;

import org.lwjgl.opengl.GL11;
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
	public static final ResourceLocation SKY_RIFT_OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/sky_rift_overlay.png");
	public static final ResourceLocation SKY_RIFT_MASK_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/sky_rift_mask.png");
	public static final ResourceLocation SKY_RIFT_MASK_BACK_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/sky_rift_mask_back.png");
	
	protected final int skyDomeDispList;

	private Sphere projectionSphere = new Sphere();
	
	public RiftMaskRenderer(int skyDomeDispList) {
		this.skyDomeDispList = skyDomeDispList;

		this.projectionSphere.setTextureFlag(false);
	}

	@Override
	public void renderMask(float partialTicks, WorldClient world, Minecraft mc) {
		TextureManager textureManager = mc.getTextureManager();

		float[] riftAngles = this.getRiftAngles(partialTicks, world, mc);

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
		textureManager.bindTexture(SKY_RIFT_MASK_TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -1, 0);
		GlStateManager.rotate(riftAngles[0], 0, 1, 0);
		GlStateManager.rotate(riftAngles[1], 0, 0, 1);
		GlStateManager.rotate(riftAngles[2], 0, 1, 0);

		GlStateManager.callList(this.skyDomeDispList);

		GlStateManager.popMatrix();
	}

	@Override
	public void renderOverlay(float partialTicks, WorldClient world, Minecraft mc) {
		TextureManager textureManager = mc.getTextureManager();

		float[] riftAngles = this.getRiftAngles(partialTicks, world, mc);

		float visibility = this.getRiftVisibility(partialTicks, world, mc);

		GlStateManager.color(1, 1, 1, visibility);

		textureManager.bindTexture(SKY_RIFT_OVERLAY_TEXTURE);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -1, 0);
		GlStateManager.rotate(riftAngles[0], 0, 1, 0);
		GlStateManager.rotate(riftAngles[1], 0, 0, 1);
		GlStateManager.rotate(riftAngles[2], 0, 1, 0);

		GlStateManager.callList(this.skyDomeDispList);

		GlStateManager.popMatrix();

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
	}

	@Override
	public void renderRiftProjection(float partialTicks, WorldClient world, Minecraft mc) {
		float visibility = this.getRiftVisibility(partialTicks, world, mc);

		GlStateManager.color(visibility*visibility*visibility, visibility*visibility*visibility, visibility*visibility*visibility, visibility);

		GlStateManager.enableFog();
		GlStateManager.setFogStart(FogHandler.getCurrentFogStart() / 2);
		GlStateManager.setFogEnd(FogHandler.getCurrentFogEnd() / 2);

		GlStateManager.cullFace(CullFace.FRONT);
		this.projectionSphere.draw(55, 8, 8);
		GlStateManager.cullFace(CullFace.BACK);
	}

	protected float getRiftVisibility(float partialTicks, WorldClient world, Minecraft mc) {
		EventRift rift = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().rift;
		return rift.getVisibility(partialTicks);
	}

	protected float[] getRiftAngles(float partialTicks, WorldClient world, Minecraft mc) {
		EventRift rift = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().rift;
		return rift.getRiftAngles(partialTicks);
	}
}
