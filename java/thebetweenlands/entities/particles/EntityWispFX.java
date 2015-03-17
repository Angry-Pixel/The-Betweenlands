package thebetweenlands.entities.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityWispFX extends EntityFX {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/wisp.png");

	private float flameScale;
	private int color;
	private int brightness;

	public EntityWispFX(World world, double x, double y, double z, double mx, double my, double mz, float size, int bright, int col) {
		super(world, x, y, z, mx, my, mz);
		this.motionX = motionX * 0.01D + mx;
		this.motionY = motionY * 0.01D + my;
		this.motionZ = motionZ * 0.01D + mz;
		x += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		y += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		z += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.flameScale = size;
		this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 1000;
		this.noClip = true;
		this.color = col;
		this.brightness = bright;
	}

	public void renderParticle(Tessellator par1Tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		float ipx = (float)((this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks) - this.interpPosX);
		float ipy = (float)((this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks) - this.interpPosY);
		float ipz = (float)((this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks) - this.interpPosZ);
		float scale = this.flameScale / 10; 

		par1Tessellator.setBrightness(this.brightness);

		float a = (float)(color >> 24 & 0xff) / 255F - (float) ((10.0f - Minecraft.getMinecraft().renderViewEntity.getDistance(posX, posY, posZ) + 10.0f) / 10.0f);
		float r = (float)(color >> 16 & 0xff) / 255F;
		float g = (float)(color >> 8 & 0xff) / 255F;
		float b = (float)(color & 0xff) / 255F;

		par1Tessellator.setColorRGBA_F(r, g, b, a);
		par1Tessellator.addVertexWithUV(ipx - rx * scale - ryz * scale, ipy - rxz * scale, ipz - rz * scale - rxy * scale, 0.0D, 1.0D);
		par1Tessellator.addVertexWithUV(ipx - rx * scale + ryz * scale, ipy + rxz * scale, ipz - rz * scale + rxy * scale, 1.0D, 1.0D);
		par1Tessellator.addVertexWithUV(ipx + rx * scale + ryz * scale, ipy + rxz * scale, ipz + rz * scale + rxy * scale, 1.0D, 0.0D);
		par1Tessellator.addVertexWithUV(ipx + rx * scale - ryz * scale, ipy - rxz * scale, ipz + rz * scale - rxy * scale, 0.0D, 0.0D);        
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.moveEntity(this.motionX, this.motionY, this.motionZ);

		this.motionX *= 0.95999997854232788D;
		this.motionZ *= 0.95999997854232788D;

		if (this.particleAge++ >= this.particleMaxAge || this.flameScale <= 0) {
			setDead();
		}
		if(this.particleAge != 0) {
			if(this.flameScale > 0) {
				this.flameScale -= 0.025;
			}
			this.motionY += 0.00008;
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}
}
