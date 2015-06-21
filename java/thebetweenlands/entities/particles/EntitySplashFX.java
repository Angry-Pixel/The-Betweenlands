package thebetweenlands.entities.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import thebetweenlands.lib.ModInfo;

public class EntitySplashFX extends EntityFX {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/particle/splash.png");

	private static final int STAGE_COUNT = 4;

	private static final float STAGE_COUNT_RECIPROCAL = 1F / 4;

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
	public void renderParticle(Tessellator tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		float ipx = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float ipy = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float ipz = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);
		int textureBinding2D = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		int textureIndex = particleAge * STAGE_COUNT / particleMaxAge;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, 0.5F);
		tessellator.addVertexWithUV(ipx - rx * particleScale - ryz * particleScale, ipy - rxz * particleScale, ipz - rz * particleScale - rxy * particleScale, (textureIndex + 1) * STAGE_COUNT_RECIPROCAL, 1);
		tessellator.addVertexWithUV(ipx - rx * particleScale + ryz * particleScale, ipy + rxz * particleScale, ipz - rz * particleScale + rxy * particleScale, textureIndex * STAGE_COUNT_RECIPROCAL, 1);
		tessellator.addVertexWithUV(ipx + rx * particleScale + ryz * particleScale, ipy + rxz * particleScale, ipz + rz * particleScale + rxy * particleScale, textureIndex * STAGE_COUNT_RECIPROCAL, 0);
		tessellator.addVertexWithUV(ipx + rx * particleScale - ryz * particleScale, ipy - rxz * particleScale, ipz + rz * particleScale - rxy * particleScale, (textureIndex + 1) * STAGE_COUNT_RECIPROCAL, 0);
		tessellator.draw();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBinding2D);
	}
}
