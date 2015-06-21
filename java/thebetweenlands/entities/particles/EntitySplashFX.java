package thebetweenlands.entities.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import thebetweenlands.lib.ModInfo;

public class EntitySplashFX extends EntityFX {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/particle/splash.png");

	private static final int STAGE_COUNT = 3;

	private static final float STAGE_COUNT_RECIPROCAL = 1F / (STAGE_COUNT + 1);

	public EntitySplashFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, int color) {
		super(world, x, y, z, motionX, motionY, motionZ);
		particleRed = (color >> 16 & 0xFF) / 255F;
		particleGreen = (color >> 8 & 0xFF) / 255F;
		particleBlue = (color & 0xFF) / 255F;
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		particleScale = 0.2F;
		particleGravity = 1.1F;
		particleMaxAge *= 2;
	}

	@Override
	public int getFXLayer() {
		return 3;
	}

	@Override
	public int getBrightnessForRender(float partialRenderTicks) {
		int x = MathHelper.floor_double(posX);
		int z = MathHelper.floor_double(posZ);

		if (worldObj.blockExists(x, 0, z)) {
			double offsetY = (boundingBox.maxY - boundingBox.minY) * 0.66;
			int y = MathHelper.floor_double(posY - yOffset + offsetY);
			return worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, x, y, z);
		} else {
			return 0;
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		Minecraft.getMinecraft().entityRenderer.disableLightmap(0);
        RenderHelper.disableStandardItemLighting();
		GL11.glColor3f(1, 1, 1);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		float ipx = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float ipy = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float ipz = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
		int textureBinding2D = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		int textureIndex = particleAge * STAGE_COUNT / particleMaxAge;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 1 - particleAge / (float) particleMaxAge);
		tessellator.addVertexWithUV(ipx - rx * particleScale - ryz * particleScale, ipy - rxz * particleScale, ipz - rz * particleScale - rxy * particleScale, (textureIndex + 1) * STAGE_COUNT_RECIPROCAL, 1);
		tessellator.addVertexWithUV(ipx - rx * particleScale + ryz * particleScale, ipy + rxz * particleScale, ipz - rz * particleScale + rxy * particleScale, textureIndex * STAGE_COUNT_RECIPROCAL, 1);
		tessellator.addVertexWithUV(ipx + rx * particleScale + ryz * particleScale, ipy + rxz * particleScale, ipz + rz * particleScale + rxy * particleScale, textureIndex * STAGE_COUNT_RECIPROCAL, 0);
		tessellator.addVertexWithUV(ipx + rx * particleScale - ryz * particleScale, ipy - rxz * particleScale, ipz + rz * particleScale - rxy * particleScale, (textureIndex + 1) * STAGE_COUNT_RECIPROCAL, 0);
		tessellator.draw();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBinding2D);
		Minecraft.getMinecraft().entityRenderer.disableLightmap(1);
	}
}
