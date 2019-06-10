package thebetweenlands.client.render.particle.entity;

import java.util.Random;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.ParticleFactory;

@SideOnly(Side.CLIENT)
public class ParticlePuzzleBeam2 extends ParticleBeam {
	public ParticlePuzzleBeam2(World worldIn, double x, double y, double z, double vx, double vy, double vz, float scale, int lifetime, Vec3d end) {
		super(worldIn, x, y, z, 0, 0, 0, end);
		this.particleMaxAge = lifetime;
		this.particleScale = scale;
		this.texUScale = scale * 4;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.particleAlpha = 0;
	}

	@Override
	public int getBrightnessForRender(float pTicks) {
		return 255;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.particleAge > this.particleMaxAge - 10) {
			this.particleAlpha = (this.particleMaxAge - this.particleAge) / 10.0F;
		} else if(this.particleAge < 10) {
			this.particleAlpha = this.particleAge / 10.0F;
		} else {
			this.particleAlpha = 1.0F;
		}
		
		if(this.particleRed > 1) {
			this.particleRed /= 255.0f;
		}

		if(this.particleGreen > 1) {
			this.particleGreen /= 255.0f;
		}

		if(this.particleBlue > 1) {
			this.particleBlue /= 255.0f;
		}

		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	public static Random random = new Random();

	@Override
	public void onUpdate() {
		super.onUpdate();
		
		this.texUOffset = -this.particleAge / (float)this.particleMaxAge * 4;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticlePuzzleBeam2> {
		public Factory() {
			super(ParticlePuzzleBeam2.class);
		}

		@Override
		public ParticlePuzzleBeam2 createParticle(ImmutableParticleArgs args) {
			return new ParticlePuzzleBeam2(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0), args.data.getObject(Vec3d.class, 1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(20, new Vec3d(0, 0, 1));
		}
	}
}
