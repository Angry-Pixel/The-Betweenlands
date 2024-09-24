package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;

public class FishVortexParticle extends EntitySwirlParticle {
	public FishVortexParticle(EntitySwirlParticleOptions options, ClientLevel level, double x, double y, double z, int maxAge, float scale, float progress, Entity target, SpriteSet spriteSet) {
		super(options, level, x, y, z, maxAge, scale, progress, target, spriteSet, 4, 2);
	}

	@Override
	public void tick() {
		super.tick();

		BlockState state = this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z));
		if (!state.liquid()) {
			this.remove();
		}
	}

	public static final class Factory extends ParticleFactory<Factory, EntitySwirlParticleOptions> {

		private final SpriteSet spriteSet;

		public Factory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public FishVortexParticle createParticle(EntitySwirlParticleOptions options, ImmutableParticleArgs args) {
			return new FishVortexParticle(options, args.level, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2), this.spriteSet);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
}
