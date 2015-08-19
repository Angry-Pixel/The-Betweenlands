package thebetweenlands.entities.particles;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.stalactite.BlockStalactite;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityStalactiteWaterDripFX extends EntityFX {
	public EntityStalactiteWaterDripFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		motionX = motionY = motionZ = 0;
		particleRed = 0.2F;
		particleGreen = 0.3F;
		particleBlue = 1;
		setParticleTextureIndex(112);
		setSize(0.01F, 0.01F);
		particleGravity = 0.06F;
		particleMaxAge = (int) (64 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= particleGravity;
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.98;
		motionY *= 0.98;
		motionZ *= 0.98;
		if (particleMaxAge-- <= 0) {
			setDead();
		}
		if (onGround) {
			setDead();
			worldObj.spawnParticle("splash", posX, posY, posZ, 0, 0, 0);
			motionX *= 0.7;
			motionZ *= 0.7;
		}
		int blockX = MathHelper.floor_double(posX), blockY = MathHelper.floor_double(posY), blockZ = MathHelper.floor_double(posZ);
		Block block = worldObj.getBlock(blockX, blockY, blockZ);
		Material material = block.getMaterial();
		if (material.isLiquid() || material.isSolid() && !(block instanceof BlockStalactite)) {
			double y = blockY + 1 - BlockLiquid.getLiquidHeightPercent(worldObj.getBlockMetadata(blockX, blockY, blockZ));
			if (posY < y) {
				setDead();
			}
		}
	}
}
