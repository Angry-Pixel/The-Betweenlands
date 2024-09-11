package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class BugParticle extends TextureSheetParticle {
	private final float jitter;
	private final float speed;
	private final boolean underwater;
	private double tx, ty, tz;
	protected final SpriteSet spriteSet;
	protected final int frametime;

	protected BugParticle(ClientLevel level, double x, double y, double z, double mx, double my, double mz, int maxAge, float speed, float jitter, float scale, boolean underwater, SpriteSet set, int frametime) {
		super(level, x, y, z, 0, 0, 0);
		this.x = this.xo = this.tx = x;
		this.y = this.yo = this.ty = y;
		this.z = this.zo = this.tz = z;
		this.xd = mx;
		this.yd = my;
		this.zd = mz;
		this.lifetime = maxAge;
		//this.noClip = true;
		this.scale(scale);
		this.jitter = jitter;
		this.speed = speed;
		this.underwater = underwater;
		this.spriteSet = set;
		this.frametime = frametime;
	}

	@Override
	public void tick() {
		super.tick();
		this.animateBug();
		this.move(this.level.getRandom().nextFloat() * this.jitter * 2 - this.jitter, this.level.getRandom().nextFloat() * this.jitter * 2 - this.jitter, this.level.getRandom().nextFloat() * this.jitter * 2 - this.jitter);
		double distToTarget = Math.sqrt((this.tx - this.x) * (this.tx - this.x) + (this.ty - this.y) * (this.ty - this.y) + (this.tz - this.z) * (this.tz - this.z));
		BlockState currBlock = this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z));
		if (this.underwater != currBlock.is(BlockRegistry.SWAMP_WATER)) {
			this.yd += 0.08D;
			if (this.removed)
				this.yd += 0.25D;
			this.tx = this.x;
			this.ty = this.y;
			this.tz = this.z;
		} else {
			if (distToTarget <= this.speed + this.jitter) {
				this.tx = this.x + this.level.getRandom().nextFloat() * 2.0F - 1.0F;
				this.ty = this.y + this.level.getRandom().nextFloat() * 2.0F - 1.0F;
				this.tz = this.z + this.level.getRandom().nextFloat() * 2.0F - 1.0F;
				BlockState targetBlock = this.level.getBlockState(BlockPos.containing(this.tx, this.ty + 0.5D, this.tz));
				if (this.underwater != targetBlock.is(BlockRegistry.SWAMP_WATER)) {
					this.tx = this.x;
					this.ty = this.y;
					this.tz = this.z;
				}
			} else {
				this.move(-(this.x - this.tx) / distToTarget * this.speed, -(this.y - this.ty) / distToTarget * this.speed, -(this.z - this.tz) / distToTarget * this.speed);
			}
		}
	}

	public void animateBug() {
		int sprite = this.age % (this.frametime * 2) >= this.frametime ? 1 : 0;
		this.setSprite(this.spriteSet.get(sprite, 1));
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class FlyFactory extends ParticleFactory<FlyFactory, SimpleParticleType> {

		private final SpriteSet spriteSet;

		public FlyFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BugParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BugParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, args.data.getBool(3), this.spriteSet, 1);
			particle.setSprite(this.spriteSet.get(0, 1));
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.25F).withData(400, 0.05F, 0.025F, false);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(0.25F * level.getRandom().nextFloat() + 0.1F);
		}
	}

	public static final class MosquitoFactory extends ParticleFactory<MosquitoFactory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public MosquitoFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BugParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BugParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, args.data.getBool(3), this.spriteSet, 1);
			particle.setSprite(this.spriteSet.get(0, 1));
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.4F).withData(400, 0.05F, 0.025F, false);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(0.4F * level.getRandom().nextFloat() + 0.1F);
		}
	}

	public static final class SilkMothFactory extends ParticleFactory<SilkMothFactory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public SilkMothFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BugParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BugParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, false, this.spriteSet, 3);
			particle.setSprite(this.spriteSet.get(0, 1));
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(1.0F).withData(400, 0.02F, 0.005F, 0);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(level.getRandom().nextFloat());
		}
	}

	public static final class WaterBugFactory extends ParticleFactory<WaterBugFactory, SimpleParticleType> {
		private final SpriteSet spriteSet;

		public WaterBugFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public BugParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var particle = new BugParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(1), args.data.getFloat(2), args.scale, args.data.getBool(3), this.spriteSet, 3);
			particle.setSprite(this.spriteSet.get(0, 1));
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.6F).withData(400, 0.03F, 0.002F, true);
		}

		@Override
		protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
			args.withScale(0.6F * level.getRandom().nextFloat() + 0.1F);
		}
	}
}
