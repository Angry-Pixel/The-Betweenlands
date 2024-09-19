package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;

public class EntitySwirlParticle extends SwirlParticle {

	protected Entity entityTarget;
	private final Vec3 targetOffset;
	private final SpriteSet spriteSet;
	private final int frames;
	private final int frameTime;
	private int currentFrame;

	public EntitySwirlParticle(EntitySwirlParticleOptions options, ClientLevel level, double x, double y, double z, int maxAge, float scale, float progress, Entity target, SpriteSet spriteSet, int frames, int frameTime) {
		super(options, level, x, y, z, maxAge, scale, progress);
		this.entityTarget = target;
		this.targetOffset = options.targetOffset;
		this.offset = new Vec3(0, -1.6D, 0);
		this.spriteSet = spriteSet;
		this.currentFrame = level.getRandom().nextInt(frames);
		this.frames = frames;
		this.frameTime = frameTime;

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

		if (this.age % (this.frameTime * 2) >= this.frameTime) {
			this.currentFrame++;
		}
		if (this.currentFrame > frames) {
			this.currentFrame = 0;
		}
		this.setSprite(this.spriteSet.get(this.currentFrame, this.frames));

		super.tick();
	}
}
