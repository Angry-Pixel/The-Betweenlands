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
import thebetweenlands.client.render.particle.ParticleFactory;

@SideOnly(Side.CLIENT)
public class ParticleTarBeastDrip extends Particle {

	private int bobTimer;

	public ParticleTarBeastDrip(World world, double x, double y, double z, double velX, double velY, double velZ, float scale) {
		super(world, x, y, z, velX, velY, velZ);
		motionX *= 0.10000000149011612D;
		motionY *= 0.10000000149011612D;
		motionZ *= 0.10000000149011612D;
		motionX += velX;
		motionY += velY;
		motionZ += velZ;
		particleScale = scale;
		setSize(0.01F, 0.01F);
		setParticleTextureIndex(113);
		particleGravity = 0.01F;
		bobTimer = 5;
		particleMaxAge = 40 + (int)(Math.random() * 40);
	}

	@Override
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

		move(motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (particleMaxAge-- <= 0) {
			this.setExpired();
		}


		if (this.isExpired) {
			setParticleTextureIndex(114);
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
		}

		IBlockState blockState = world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ));
		Material material = blockState.getMaterial();

		if (material.isLiquid() || material.isSolid()) {
			double d0 = (double) ((float) (MathHelper.floor(posY) + 1) - BlockLiquid.getLiquidHeightPercent(blockState.getBlock().getMetaFromState(blockState)));

			if (posY < d0) {
				this.setExpired();
			}
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleTarBeastDrip> {
		public Factory() {
			super(ParticleTarBeastDrip.class);
		}

		@Override
		public ParticleTarBeastDrip createParticle(ImmutableParticleArgs args) {
			return new ParticleTarBeastDrip(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
}
