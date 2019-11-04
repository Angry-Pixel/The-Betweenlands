package thebetweenlands.client.render.particle.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;

public class ParticleCaveWaterDrip extends Particle {
	private int bobTimer;
	protected ParticleCaveWaterDrip(World world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		motionX = motionY = motionZ = 0;
		particleRed = 0.2F;
		particleGreen = 0.3F;
		particleBlue = 1;
		setParticleTextureIndex(112);
		setSize(0.01F, 0.01F);
		particleGravity = 0.06F;
		this.bobTimer = 10;
		particleMaxAge = (int) (64 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= particleGravity;
        if (this.bobTimer-- > 0)
        {
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
            this.setParticleTextureIndex(113);
        }
        else
        {
            this.setParticleTextureIndex(112);
        }
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
		IBlockState state = world.getBlockState(pos);
		Material material = state.getMaterial();
		if (material.isLiquid() || material.isSolid()) {
			double d0 = 0.0D;
			if (state.getBlock() instanceof BlockLiquid)
				d0 = (double) BlockLiquid.getLiquidHeightPercent((state.getValue(BlockLiquid.LEVEL)).intValue());
			double y = (double) (MathHelper.floor(this.posY) + 1) - d0;
			if (this.posY < y)
				this.setExpired();
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
