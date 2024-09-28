package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
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
		this.offset = new Vec3(0, -1.6D, 0);

		this.updateTarget();
		this.updatePosition();
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
	}

	public void updateTarget() {
		this.targetMotion = new Vec3(this.entityTarget.getX() - this.entityTarget.xo, this.entityTarget.getY() - this.entityTarget.yo, this.entityTarget.getZ() - this.entityTarget.zo);
		this.target = new Vec3(this.entityTarget.getX() + this.targetOffset.x(), this.entityTarget.getY() + this.entityTarget.getEyeHeight() / 2.0D + this.targetOffset.y(), this.entityTarget.getZ() + this.targetOffset.z());
	}

	@Override
	public void tick() {
		if (!this.entityTarget.isAlive()) {
			this.remove();
		}

		this.updateTarget();
		super.tick();
	}
}
