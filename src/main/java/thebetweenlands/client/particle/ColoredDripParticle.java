package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ParticleRegistry;

public class ColoredDripParticle extends DripParticle {
	protected ColoredDripParticle(ClientLevel level, double x, double y, double z, Fluid type) {
		super(level, x, y, z, type);
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.preMoveUpdate();
		if (!this.removed) {
			this.yd = this.yd - (double)this.gravity;
			this.move(this.xd, this.yd, this.zd);
			this.postMoveUpdate();
			if (!this.removed) {
				this.xd *= 0.98F;
				this.yd *= 0.98F;
				this.zd *= 0.98F;
				BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
				FluidState fluidstate = this.level.getFluidState(blockpos);
				if (this.y < (double)((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos))) {
					this.remove();
				}
			}
		}
	}

	public static TextureSheetParticle createFluidHangParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		return new FluidDripHangParticle(level, x, y, z, ParticleRegistry.FALLING_FLUID.get());
	}

	public static TextureSheetParticle createFluidLandParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		DripParticle particle = new ColoredDripLandParticle(level, x, y, z);
		particle.setLifetime(40 + (int)(Math.random() * 40));
		return particle;
	}

	public static class FluidDripHangParticle extends DripParticle.DripHangParticle {

		private final ParticleOptions fallingParticle;

		protected FluidDripHangParticle(ClientLevel level, double x, double y, double z, ParticleOptions particle) {
			super(level, x, y, z, Fluids.EMPTY, particle);
			this.gravity = 0.01F;
			this.fallingParticle = particle;
		}

		@Override
		protected void preMoveUpdate() {
			if (this.lifetime-- <= 0) {
				this.remove();
				TheBetweenlands.createParticle(this.fallingParticle, this.level, this.x, this.y, this.z, ParticleFactory.ParticleArgs.get().withMotion(this.xd, this.yd, this.zd).withColor(this.rCol, this.gCol, this.bCol, this.alpha));
			}
		}
	}

	public static class FluidFallAndLandParticle extends DripParticle.FallAndLandParticle {

		private final boolean createLandParticle;

		protected FluidFallAndLandParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, ParticleOptions particle, boolean createLandParticle) {
			super(level, x, y, z, Fluids.EMPTY, particle);
			this.gravity = 0.01F;
			this.xd = xd;
			this.yd = yd;
			this.zd = zd;
			this.createLandParticle = createLandParticle;
		}

		@Override
		protected void postMoveUpdate() {
			if (this.onGround) {
				this.remove();
				if (this.createLandParticle) {
					TheBetweenlands.createParticle(this.landParticle, this.level, this.x, this.y, this.z, ParticleFactory.ParticleArgs.get().withColor(this.rCol, this.gCol, this.bCol, this.alpha));
				}
			}
		}
	}

	public static class ColoredDripLandParticle extends ColoredDripParticle {

		protected ColoredDripLandParticle(ClientLevel level, double x, double y, double z) {
			super(level, x, y, z, Fluids.EMPTY);
		}
	}

	public static class FallFactory extends ParticleFactory<FallFactory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public FallFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		protected @Nullable Particle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new FluidFallAndLandParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, ParticleRegistry.LANDING_FLUID.get(), args.data.getBool(0));
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(true);
		}
	}
}
