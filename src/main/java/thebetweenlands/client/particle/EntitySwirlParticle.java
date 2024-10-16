package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;

public class EntitySwirlParticle extends SwirlParticle {

	protected final Entity entityTarget;
	private final Vec3 targetOffset;

	public EntitySwirlParticle(EntitySwirlParticleOptions options, ClientLevel level, double x, double y, double z, int maxAge, float scale, float progress, Entity target) {
		super(options, level, x, y, z, maxAge, scale, progress);
		this.entityTarget = target;
		this.targetOffset = options.targetOffset;

		this.updateTarget();
	}

	public void updateTarget() {
		this.targetMotion = new Vec3(this.entityTarget.getX() - this.entityTarget.xo, this.entityTarget.getY() - this.entityTarget.yo, this.entityTarget.getZ() - this.entityTarget.zo);
		this.target = new Vec3(this.entityTarget.getX() + this.targetOffset.x(), this.entityTarget.getY() + this.entityTarget.getEyeHeight() / 2.0D + this.targetOffset.y(), this.entityTarget.getZ() + this.targetOffset.z());
		this.setChanged();
	}

	@Override
	public void tick() {
		if (!this.entityTarget.isAlive()) {
			this.remove();
		}

		this.updateTarget();
		super.tick();
	}

	public static final class EmberSwirlFactory extends ParticleFactory<EmberSwirlFactory, EntitySwirlParticleOptions> {

		private final SpriteSet spriteSet;

		public EmberSwirlFactory(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public EntitySwirlParticle createParticle(EntitySwirlParticleOptions options, ImmutableParticleArgs args) {
			var particle = new EntitySwirlParticle(options, args.level, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2));
			particle.pickSprite(this.spriteSet);
			return particle;
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
}
