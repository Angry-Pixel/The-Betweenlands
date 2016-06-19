package thebetweenlands.entities.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityGasCloudFX extends EntityFX {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/staticGas.png");

	private int color;
	private float scale = 1.0F;
	private int rotation = 0;

	public EntityGasCloudFX(World world, double x, double y, double z, double mx, double my, double mz, int col) {
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
		particleMaxAge = 60;
		noClip = true;
		color = col;
		scale = this.rand.nextFloat() * 0.75F + 0.6F;
		rotation = this.rand.nextInt(4);
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		float ipx = (float)((this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks) - this.interpPosX);
		float ipy = (float)((this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks) - this.interpPosY);
		float ipz = (float)((this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks) - this.interpPosZ);

		int fadeInDuration = 15;
		int fadeOutStart = 45;
		int fadeOutDuration = this.particleMaxAge - fadeOutStart;
		float alphaFade;
		if(this.particleAge < fadeOutStart) {
			alphaFade = Math.min((float)this.particleAge / (float)fadeInDuration, 1.0F);
		} else {
			alphaFade = 1.0F - Math.min((float)(this.particleAge - fadeOutStart) / (float)fadeOutDuration, 1.0F);
		}
		float a = (float)(this.color >> 24 & 0xff) / 255F * alphaFade;
		float r = (float)(this.color >> 16 & 0xff) / 255F;
		float g = (float)(this.color >> 8 & 0xff) / 255F;
		float b = (float)(this.color & 0xff) / 255F;

		//par1Tessellator.startDrawingQuads();
		par1Tessellator.setColorRGBA_F(r, g, b, a);
		//par1Tessellator.setColorRGBA_F(1, 1, 1, 1);
		switch(this.rotation) {
		case 0:
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz - rz * this.scale - rxy * this.scale, 1, 1);
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz - rz * this.scale + rxy * this.scale, 1, 0);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz + rz * this.scale + rxy * this.scale, 0, 0);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz + rz * this.scale - rxy * this.scale, 0, 1);
			break;
		case 1:
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz - rz * this.scale - rxy * this.scale, 1, 0);
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz - rz * this.scale + rxy * this.scale, 0, 0);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz + rz * this.scale + rxy * this.scale, 0, 1);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz + rz * this.scale - rxy * this.scale, 1, 1);
			break;
		case 2:
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz - rz * this.scale - rxy * this.scale, 0, 0);
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz - rz * this.scale + rxy * this.scale, 0, 1);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz + rz * this.scale + rxy * this.scale, 1, 1);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz + rz * this.scale - rxy * this.scale, 1, 0);
			break;
		case 3:
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz - rz * this.scale - rxy * this.scale, 0, 1);
			par1Tessellator.addVertexWithUV(ipx - rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz - rz * this.scale + rxy * this.scale, 1, 1);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz + rz * this.scale + rxy * this.scale, 1, 0);
			par1Tessellator.addVertexWithUV(ipx + rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz + rz * this.scale - rxy * this.scale, 0, 0);
			break;
		}
		//par1Tessellator.draw();
	}

	@Override
	public int getFXLayer() {
		return 3;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}
}
