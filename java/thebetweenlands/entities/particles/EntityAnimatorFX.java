package thebetweenlands.entities.particles;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class EntityAnimatorFX extends EntityPathParticle {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/wisp.png");

	private int ticks = 0;
	private float sizeDecrease = 0.0F;

	public EntityAnimatorFX(World world, double x, double y, double z,
			double motionX, double motionY, double motionZ,
			ArrayList<Vector3d> targetPoints, IIcon icon, float sizeDecrease) {
		super(world, x, y, z, motionX, motionY, motionZ, targetPoints);

		this.particleScale = world.rand.nextFloat() / 2.0F;
		this.particleMaxAge = 10000000;
		this.particleAge = 0;
		this.sizeDecrease = sizeDecrease;

		this.setParticleIcon(icon);

		this.particleAlpha = 0.35F + world.rand.nextFloat() / 2.0F;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.particleScale -= this.sizeDecrease;

		this.ticks++;
		if(this.ticks >= 200 || this.particleScale <= 0.0F) {
			this.setDead();
		}

		double t = 1.0D / 200.0D * this.ticks;

		Vector3d pos = this.getPosition(t);

		this.setPosition(pos.x, pos.y, pos.z);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	public void renderParticle(Tessellator tessellator, float x, float y, float z, float rx, float rxz, float rxy)
	{
		float umin = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
		float umax = umin + 0.015609375F;
		float vmin = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
		float vmax = vmin + 0.015609375F;
		float scale = 0.1F * this.particleScale;

		if (this.particleIcon != null)
		{
			umin = this.particleIcon.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
			umax = this.particleIcon.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
			vmin = this.particleIcon.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
			vmax = this.particleIcon.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
		}

		float px = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x - interpPosX);
		float py = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x - interpPosY);
		float pz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x - interpPosZ);
		tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
		tessellator.addVertexWithUV((double)(px - y * scale - rxz * scale), (double)(py - z * scale), (double)(pz - rx * scale - rxy * scale), (double)umin, (double)vmax);
		tessellator.addVertexWithUV((double)(px - y * scale + rxz * scale), (double)(py + z * scale), (double)(pz - rx * scale + rxy * scale), (double)umin, (double)vmin);
		tessellator.addVertexWithUV((double)(px + y * scale + rxz * scale), (double)(py + z * scale), (double)(pz + rx * scale + rxy * scale), (double)umax, (double)vmin);
		tessellator.addVertexWithUV((double)(px + y * scale - rxz * scale), (double)(py - z * scale), (double)(pz + rx * scale - rxy * scale), (double)umax, (double)vmax);
	}

	@Override
	public int getFXLayer() {
		return 2;
	}
}
