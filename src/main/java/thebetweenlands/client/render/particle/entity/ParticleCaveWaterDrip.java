package thebetweenlands.client.render.particle.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.block.terrain.BlockStalactite;

public class ParticleCaveWaterDrip extends Particle {
	protected ParticleCaveWaterDrip(World world, double x, double y, double z) {
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
		move(motionX, motionY, motionZ);
		motionX *= 0.98;
		motionY *= 0.98;
		motionZ *= 0.98;
		if (particleMaxAge-- <= 0) {
			this.setExpired();
		}
		if (this.onGround) {
			this.setExpired();
			this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
			motionX *= 0.7;
			motionZ *= 0.7;
		}
		BlockPos pos = new BlockPos(posX, posY, posZ);
		IBlockState block = world.getBlockState(pos);
		Material material = block.getMaterial();
		if (material.isLiquid() || material.isSolid() && !(block instanceof BlockStalactite)) {
			double y = pos.getY() + 1 - BlockLiquid.getLiquidHeightPercent(world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)));
			if (posY < y) {
				this.setExpired();
			}
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleCaveWaterDrip> {
		public Factory() {
			super(ParticleCaveWaterDrip.class);
		}

		@Override
		public ParticleCaveWaterDrip createParticle(ImmutableParticleArgs args) {
			return new ParticleCaveWaterDrip(args.world, args.x, args.y, args.z);
		}
	}
}
