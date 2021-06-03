package thebetweenlands.client.render.particle.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

@SideOnly(Side.CLIENT)
public class ParticleFancyDrip extends Particle {
	protected ParticleFancyDrip(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double mx, double my, double mz, float scale) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, mx, my, mz);
		this.particleScale = scale;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.setParticleTextureIndex(112);
		this.setSize(0.01F, 0.01F);
		this.particleGravity = 0.06F;
		this.particleMaxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.motionY -= (double)this.particleGravity;

		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if(this.particleMaxAge-- <= 0) {
			this.setExpired();
		}

		if(this.onGround) {
			this.setExpired();

			BLParticles.RAIN.spawn(world, this.posX, this.posY, this.posZ, ParticleArgs.get()
					.withColor(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha));

			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR,
					BLParticles.WATER_RIPPLE.create(world, this.posX, this.posY + 0.1f, this.posZ,
							ParticleArgs.get()
							.withScale(this.particleScale * 1.5f)
							.withColor(this.particleRed * 1.25f, this.particleGreen * 1.25f, this.particleBlue * 1.5f, Math.min(1, 2 * this.particleAlpha))
							));

			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}

		BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
		IBlockState state = this.world.getBlockState(pos);
		Material material = state.getMaterial();

		if(material.isLiquid() || material.isSolid()) {
			double fluidHeight = 0.0D;

			if(state.getBlock() instanceof BlockLiquid) {
				fluidHeight = (double)BlockLiquid.getLiquidHeightPercent(((Integer)state.getValue(BlockLiquid.LEVEL)).intValue());
			}

			double fluidCeiling = (double)(MathHelper.floor(this.posY) + 1) - fluidHeight;

			if(this.posY < fluidCeiling) {
				this.setExpired();
			}
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleFancyDrip> {
		public Factory() {
			super(ParticleFancyDrip.class);
		}

		@Override
		public ParticleFancyDrip createParticle(ImmutableParticleArgs args) {
			return new ParticleFancyDrip(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
}