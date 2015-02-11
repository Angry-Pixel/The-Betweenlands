package thebetweenlands.entities.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityDruidCastingFX extends EntityFX {
	private float portalParticleScale;
	private double portalPosX;
	private double portalPosY;
	private double portalPosZ;

	public EntityDruidCastingFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
		super(par1World, par2, par4, par6, par8, par10, par12);
		this.motionX = par8;
		this.motionY = par10;
		this.motionZ = par12;
		this.portalPosX = this.posX = par2;
		this.portalPosY = this.posY = par4;
		this.portalPosZ = this.posZ = par6;
		float f = this.rand.nextFloat() * 0.3F;
		this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * f;
		this.particleMaxAge = (int)(Math.random() * 10.0D) + 40;
		this.noClip = true;
		this.setParticleTextureIndex((int)(Math.random() * 8.0D));
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
		float f6 = ((float)this.particleAge + par2) / (float)this.particleMaxAge;
		f6 = 1.0F - f6;
		f6 *= f6;
		f6 = 1.0F - f6;
		this.particleScale = this.portalParticleScale * f6;
		super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
	}

	@Override
	public int getBrightnessForRender(float par1) {
		int i = super.getBrightnessForRender(par1);
		float f1 = (float)this.particleAge / (float)this.particleMaxAge;
		f1 *= f1;
		f1 *= f1;
		int j = i & 255;
		int k = i >> 16 & 255;
		k += (int)(f1 * 15.0F * 16.0F);

		if (k > 240)
		{
			k = 240;
		}

		return j | k << 16;
	}

	/**
	 * Gets how bright this entity is.
	 */
	 @Override
	 public float getBrightness(float par1) {
		float f1 = super.getBrightness(par1);
		float f2 = (float)this.particleAge / (float)this.particleMaxAge;
		f2 = f2 * f2 * f2 * f2;
		return f1 * (1.0F - f2) + f2;
	 }

	 /**
	  * Called to update the entity's position/logic.
	  */
	 @Override
	 public void onUpdate() {
		 this.prevPosX = this.posX;
		 this.prevPosY = this.posY;
		 this.prevPosZ = this.posZ;
		 float f = (float)this.particleAge / (float)this.particleMaxAge;
		 f = -f + f * f * 2.0F;
		 f = 1.0F - f;
		 this.posX = this.portalPosX + this.motionX * (double)f;
		 this.posY = this.portalPosY + this.motionY * (double)f;
		 this.posZ = this.portalPosZ + this.motionZ * (double)f;

		 if (this.particleAge++ >= this.particleMaxAge)
		 {
			 this.setDead();
		 }
	 }
}
