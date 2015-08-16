package thebetweenlands.entities.particles;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityTarBeastDrip extends EntityFX {

	private int bobTimer;

	public EntityTarBeastDrip(World world, double x, double y, double z, double velX, double velY, double velZ) {
		super(world, x, y, z, velX, velY, velZ);
		motionX *= 0.10000000149011612D;
		motionY *= 0.10000000149011612D;
		motionZ *= 0.10000000149011612D;
		motionX += velX;
		motionY += velY;
		motionZ += velZ;

		setParticleTextureIndex(113);
		setSize(0.01F, 0.01F);
		particleGravity = 0.01F;
		bobTimer = 5;
		particleMaxAge = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
	}

	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		motionY -= (double) particleGravity;

		if (bobTimer-- > 0) {
			motionX *= 0.02D;
			motionY *= 0.02D;
			motionZ *= 0.02D;
			setParticleTextureIndex(113);
		} else
			setParticleTextureIndex(112);

		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (particleMaxAge-- <= 0)
			setDead();


		if (onGround) {
			setParticleTextureIndex(114);
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
		}

		Material material = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)).getMaterial();

		if (material.isLiquid() || material.isSolid()) {
			double d0 = (double) ((float) (MathHelper.floor_double(posY) + 1) - BlockLiquid.getLiquidHeightPercent(worldObj.getBlockMetadata(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))));

			if (posY < d0)
				setDead();
		}
	}
}
