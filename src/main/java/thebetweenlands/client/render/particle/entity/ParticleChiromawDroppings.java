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
public class ParticleChiromawDroppings extends Particle {

	private int bobTimer;

	public ParticleChiromawDroppings(World world, double x, double y, double z, double velX, double velY, double velZ, float scale) {
		super(world, x, y, z, velX, velY, velZ);
		motionX += velX;
		motionY += velY;
		motionZ += velZ;
		particleScale = scale;
		setSize(0.01F, 0.01F);
		setParticleTextureIndex(112);
		particleGravity = 0.03F;
		particleMaxAge = 40 + (int)(Math.random() * 40);
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		motionY -= (double) particleGravity;
		motionX *= 0.1800000190734863D;
		motionZ *= 0.1800000190734863D;

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
		move(motionX, motionY, motionZ);
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleChiromawDroppings> {
		public Factory() {
			super(ParticleChiromawDroppings.class);
		}

		@Override
		public ParticleChiromawDroppings createParticle(ImmutableParticleArgs args) {
			return new ParticleChiromawDroppings(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale);
		}
	}
}
