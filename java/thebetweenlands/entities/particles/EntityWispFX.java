package thebetweenlands.entities.particles;

import thebetweenlands.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityWispFX extends EntityFX {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/wisp.png");

	private float prevFlameScale;
	private float flameScale;
	private int color;
	private int brightness;

	public EntityWispFX(World world, double x, double y, double z, double mx, double my, double mz, float size, int bright, int col) {
		super(world, x, y, z, mx, my, mz);
		motionX = motionX * 0.01D + mx;
		motionY = motionY * 0.01D + my;
		motionZ = motionZ * 0.01D + mz;
		x += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		y += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		z += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		posX = prevPosX = x;
		posY = prevPosY = y;
		posZ = prevPosZ = z;
		flameScale = size;
		particleMaxAge = (int) (8 / (Math.random() * 0.8 + 0.2)) + 1000;
		noClip = true;
		color = col;
		brightness = bright;
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float partialRenderTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		float currentX = (float) (prevPosX + (posX - prevPosX) * partialRenderTicks);
		float currentY = (float) (prevPosY + (posY - prevPosY) * partialRenderTicks);
		float currentZ = (float) (prevPosZ + (posZ - prevPosZ) * partialRenderTicks);

		float ipx = (float) (currentX - interpPosX);
		float ipy = (float) (currentY - interpPosY);
		float ipz = (float) (currentZ - interpPosZ);
		float scale = (prevFlameScale + (flameScale - prevFlameScale) * partialRenderTicks) / 10;

		par1Tessellator.setBrightness(brightness);

		float distance = MathHelper.clamp_float(getDistanceToViewer(currentX, currentY, currentZ, partialRenderTicks), 10, 20);
		float a = (color >>> 24 & 0xFF) / 255F - MathHelper.sin(MathUtils.PI / 20 * distance);
		float r = (color >> 16 & 0xFF) / 255F;
		float g = (color >> 8 & 0xFF) / 255F;
		float b = (color & 0xFF) / 255F;

		par1Tessellator.setColorRGBA_F(r, g, b, a);
		par1Tessellator.addVertexWithUV(ipx - rx * scale - ryz * scale, ipy - rxz * scale, ipz - rz * scale - rxy * scale, 0.0D, 1.0D);
		par1Tessellator.addVertexWithUV(ipx - rx * scale + ryz * scale, ipy + rxz * scale, ipz - rz * scale + rxy * scale, 1.0D, 1.0D);
		par1Tessellator.addVertexWithUV(ipx + rx * scale + ryz * scale, ipy + rxz * scale, ipz + rz * scale + rxy * scale, 1.0D, 0.0D);
		par1Tessellator.addVertexWithUV(ipx + rx * scale - ryz * scale, ipy - rxz * scale, ipz + rz * scale - rxy * scale, 0.0D, 0.0D);
	}

	public static float getDistanceToViewer(double x, double y, double z, float partialRenderTicks) {
		EntityLivingBase entity = Minecraft.getMinecraft().renderViewEntity;
		double dx = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * partialRenderTicks) - x;
		double dy = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * partialRenderTicks) - y;
		double dz = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialRenderTicks) - z;
		return MathHelper.sqrt_float((float) (dx * dx + dy * dy + dz * dz));
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevFlameScale = flameScale;

		moveEntity(motionX, motionY, motionZ);

		motionX *= 0.96;
		motionZ *= 0.96;

		if (particleAge++ >= particleMaxAge || flameScale <= 0) {
			setDead();
		}
		if (particleAge != 0) {
			if (flameScale > 0) {
				flameScale -= 0.025;
			}
			motionY += 0.00008;
		}

		moveEntity(motionX, motionY, motionZ);
	}
}
