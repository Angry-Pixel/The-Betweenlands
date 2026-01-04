package thebetweenlands.common.entity.projectile;

import java.util.List;

import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.particle.options.DripParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.fishing.BubblerCrab;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BubblerCrabBubble extends ThrowableProjectile {

	private static final byte EVENT_IMPACT = 106;
	private static final byte EVENT_EXPLODE = 107;
	private static final byte EVENT_BIG_EXPLODE = 108;

	private boolean impacted = false;

	private static final EntityDataAccessor<Float> STUCK_OFFSET_X = SynchedEntityData.defineId(BubblerCrabBubble.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> STUCK_OFFSET_Y = SynchedEntityData.defineId(BubblerCrabBubble.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> STUCK_OFFSET_Z = SynchedEntityData.defineId(BubblerCrabBubble.class, EntityDataSerializers.FLOAT);
	private boolean updating = false;
	public int swell = 0;

	public BubblerCrabBubble(EntityType<? extends BubblerCrabBubble> type, Level level) {
		super(type, level);
	}

	public BubblerCrabBubble(Level level, LivingEntity entity) {
		super(EntityRegistry.BUBBLER_CRAB_BUBBLE.get(), entity, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(STUCK_OFFSET_X, 0.0f);
		builder.define(STUCK_OFFSET_Y, 0.0f);
		builder.define(STUCK_OFFSET_Z, 0.0f);
	}

	@Override
	public void move(MoverType type, Vec3 pos) {
		if(this.tickCount < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}
		super.move(type, pos);
	}

	@Override
	public void absMoveTo(double x, double y, double z, float yaw, float pitch) {
		//Position handled by stuck offset while riding
		if(!this.isPassenger())
			super.absMoveTo(x, y, z, yaw, pitch);
	}

	public boolean startRiding(Entity entityIn, boolean force) {
		if(super.startRiding(entityIn, force)) {
			if(entityIn instanceof Player player && this.level() instanceof ServerLevel)
				this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(player));

			if(!this.level().isClientSide()) {
				this.getEntityData().set(STUCK_OFFSET_X, (float)(this.getX() - entityIn.getX()));
				this.getEntityData().set(STUCK_OFFSET_Y, (float)(this.getY() - entityIn.getY()));
				this.getEntityData().set(STUCK_OFFSET_Z, (float)(this.getZ() - entityIn.getZ()));
			}
			return true;
		}
		return false;
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		if(entity instanceof Player && this.level() instanceof ServerLevel)
			this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(entity));
	}

	@Override
	 public void rideTick() {
		super.rideTick();
		Entity entity = this.getVehicle();
		if(this.isPassenger() && entity != null)
			this.setPos(entity.getX() + this.getEntityData().get(STUCK_OFFSET_X), entity.getY() + this.getEntityData().get(STUCK_OFFSET_Y), entity.getZ() + this.getEntityData().get(STUCK_OFFSET_Z));
	}

	@Override
	public void tick() {
		double prevPosX = this.getX();
		double prevPosY = this.getY();
		double prevPosZ = this.getZ();

		this.updating = true;
		super.tick();
		this.updating = false;

		if(this.isInWater())
			this.setDeltaMovement(getDeltaMovement().multiply(0.97F, 0.97F, 0.97F));

		double newX = this.getX();
		double newY = this.getY();
		double newZ = this.getZ();
		this.setPos(prevPosX, prevPosY, prevPosZ);
		this.move(MoverType.SELF, new Vec3 (newX - prevPosX, newY - prevPosY, newZ - prevPosZ));
		this.xOld = this.xo = prevPosX;
		this.yOld = this.yo = prevPosY;
		this.zOld = this.zo = prevPosZ;

		if(this.isInWater())
			this.setDeltaMovement(getDeltaMovement().add(0F, 0.033F, 0F));

		if(!level().isClientSide()) {
			if(tickCount >= 120) {
				explode(2.0D);
				this.level().broadcastEntityEvent(this, EVENT_EXPLODE);
				level().playSound(null, blockPosition(), SoundRegistry.BUBBLER_POP.get(), SoundSource.HOSTILE, 1F, 1F);
				discard();
			} else if(this.isPassenger() && this.tickCount >= 100) {
				Entity riding = this.getVehicle();
				this.stopRiding();
				if(riding != null) {
					this.setDeltaMovement(riding.getDeltaMovement());
					this.setDeltaMovement(0, 0.075f, 0);
					this.hasImpulse = true;
					this.hurtMarked = true;
				}
			}
		} else {
			if(this.impacted) {
				this.spawnSwellingParticles();
				swell++;
				this.setDeltaMovement(0D, 0.0D, 0D);
			} else {
				this.spawnTrailParticles();
			}
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if(!this.level().isClientSide()) {
			setOnGround(true);
			this.hasImpulse = true;
			impact();
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!this.level().isClientSide()) {
			Entity entity = result.getEntity();
			if (entity != null && entity != getOwner() && !(entity instanceof BubblerCrab)) {
				setOnGround(true);
				this.hasImpulse = true;
				if (!this.impacted && !this.isPassenger()) {
					Vec3 dir = this.getDeltaMovement().normalize().scale(1.5f);
					 HitResult ray = this.level().clip(new ClipContext(this.position().add(0, this.getBbHeight() * 0.5f, 0).subtract(dir), this.position().add(0, this.getBbHeight() * 0.5f, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
					if (ray == null || ray.getLocation() == null)
						this.level().clip(new ClipContext(this.position().add(0, this.getBbHeight() * 0.5f, 0), this.position().add(0, this.getBbHeight() * 0.5f, 0).add(dir), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

					if (ray != null && ray.getLocation() != null) {
						this.setPos(ray.getLocation());
						this.startRiding(entity, true);
					}
				}
			}
			impact();
		}
	}

	public void impact() {
		if (!this.impacted) {
			this.level().broadcastEntityEvent(this, EVENT_IMPACT);
			level().playSound(null, blockPosition(), getSplashSound(), SoundSource.HOSTILE, 0.5F, 1.0F);
			impacted = true;
		}
	}

	@Override
	public boolean isInWater() {
		//Prevent bubble particles and water slowdown
		return !this.updating && super.isInWater();
	}

	@Override
	protected void doWaterSplashEffect() {
		//dontWaterSplashEffect
	}

	private void explode(double radius) {
		AABB aoe = this.getBoundingBox().inflate(radius);

		if (!level().isClientSide() && level().getDifficulty() != Difficulty.PEACEFUL) {
			List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, aoe);

			for(LivingEntity entity : list) {
				if(!(entity instanceof BubblerCrab) && !entity.isInvulnerable() && entity.invulnerableTime <= 0 && entity.distanceTo(this) <= radius) {
					double attackDamage;

					if(this.getOwner() != null && ((LivingEntity) this.getOwner()).getAttributeValue(Attributes.ATTACK_DAMAGE) > 0D)
						attackDamage = ((LivingEntity) this.getOwner()).getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5D;
					else
						attackDamage = 2.0D;

					if(entity.hurt(this.damageSources().mobProjectile(this, this.getOwner() instanceof LivingEntity living ? living : null), (float) attackDamage)) {
						entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.25F, 1F, 0.25F));
						entity.setDeltaMovement(entity.getDeltaMovement().add(0F, 0.1F, 0F));
						entity.hurtMarked = true;
					}
				}
			}
		}
	}

	private float getBubbleRadius() {
		return 0.15f + Math.min(this.swell, 120) * 0.0065f;
	}

	private void spawnTrailParticles() {
		float radius = this.getBubbleRadius();
		TheBetweenlands.createParticle(ParticleRegistry.FANCY_BUBBLE.get(),level(), getX() + (this.level().getRandom().nextFloat() - 0.5f) * radius, getY() + (this.level().getRandom().nextFloat() - 0.5f) * radius, getZ() + (this.level().getRandom().nextFloat() - 0.5f) * radius, ParticleFactory.ParticleArgs.get().withColor(0.44f, 0.46f, 0.42f, 0.95f).withScale(0.5f + this.level().getRandom().nextFloat() * 0.5f));
	}

	private void spawnSwellingParticles() {
		float radius = this.getBubbleRadius();

		if(this.level().getRandom().nextInt(3) == 0) {
			double ox = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
			double oy = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
			double oz = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
			double velX = ox * getRandom().nextFloat() * 0.25f;
			double velY = -0.1f;
			double velZ = oz * getRandom().nextFloat() * 0.25f;
			TheBetweenlands.createParticle(new DripParticleOptions(false, false),level(), this.getX() + ox, this.getY() + radius * 0.25f + oy, this.getZ() + oz, ParticleFactory.ParticleArgs.get().withMotion(velX, velY, velZ).withScale(0.5f).withColor(0.44f, 0.46f, 0.42f, 0.8f));
		}

		if(this.level().getRandom().nextInt(10) == 0) {
			TheBetweenlands.createParticle(ParticleRegistry.FANCY_BUBBLE.get(), level(), getX() + (this.level().getRandom().nextFloat() - 0.5f) * radius, getY() + radius * 0.5f + (this.level().getRandom().nextFloat() - 0.5f) * radius * 0.5f, getZ() + (this.level().getRandom().nextFloat() - 0.5f) * radius, 
					ParticleFactory.ParticleArgs.get().withMotion((this.level().getRandom().nextFloat() - 0.5f) * 0.01f, 0.05f, (this.level().getRandom().nextFloat() - 0.5f) * 0.01f)
					.withScale(0.25f + Math.max(0.0f, this.getBubbleRadius() - 0.2f) * 1.5f + this.level().getRandom().nextFloat() * 0.5f)
					.withData(true)
					.withColor(0.44f, 0.46f, 0.42f, 0.8f));
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if(id == EVENT_IMPACT) {
			float radius = this.getBubbleRadius();
			for(int i = 0; i < 8; ++i)
				TheBetweenlands.createParticle(ParticleRegistry.FANCY_BUBBLE.get(), level(), getX() + (this.level().getRandom().nextFloat() - 0.5f) * radius, getY() + (this.level().getRandom().nextFloat() - 0.5f) * radius, getZ() + (this.level().getRandom().nextFloat() - 0.5f) * radius, ParticleFactory.ParticleArgs.get().withColor(0.44f, 0.46f, 0.42f, 0.8f));
			this.impacted = true;
		}

		if(id == EVENT_EXPLODE) {
			float radius = this.getBubbleRadius();
			for(int i = 0; i < 8; ++i)
				TheBetweenlands.createParticle(ParticleRegistry.FANCY_BUBBLE.get(), level(), getX() + (this.level().getRandom().nextFloat() - 0.5f) * radius, getY() + (this.level().getRandom().nextFloat() - 0.5f) * radius, getZ() + (this.level().getRandom().nextFloat() - 0.5f) * radius,
						ParticleArgs.get()
						.withColor(0.44f, 0.46f, 0.42f, 0.9f)
						.withScale(1.0f + this.level().getRandom().nextFloat()));

			for(int j = 0; j < 25; ++j) {
				double ox = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double oy = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double oz = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double velX = ox * getRandom().nextFloat();
				double velY = 0.1f + getRandom().nextFloat() * 0.5f;
				double velZ = oz * getRandom().nextFloat();
				TheBetweenlands.createParticle(new DripParticleOptions(false, false),level(), this.getX() + ox, this.getY() + radius * 0.25f + oy, this.getZ() + oz, ParticleFactory.ParticleArgs.get().withMotion(velX, velY, velZ).withScale(0.5f).withColor(0.44f, 0.46f, 0.42f, 1.0f));
			}
		}

		if(id == EVENT_BIG_EXPLODE) {
			float radius = this.getBubbleRadius();
			for(int i = 0; i < 25; ++i) {
				float ox = (this.level().getRandom().nextFloat() - 0.5f) * radius;
				float oy = (this.level().getRandom().nextFloat() - 0.5f) * radius;
				float oz = (this.level().getRandom().nextFloat() - 0.5f) * radius;
				TheBetweenlands.createParticle(ParticleRegistry.FANCY_BUBBLE.get(), level(), getX() + ox, getY() + oy, getZ() + oz,
						ParticleArgs.get()
						.withMotion(ox * 1.5f, oy * 1.5f + 0.1f, oz * 1.5f)
						.withColor(0.44f, 0.46f, 0.42f, 0.9f)
						.withScale(1.0f + this.level().getRandom().nextFloat()));
			}

			for(int j = 0; j < 40; ++j) {
				double ox = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double oy = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double oz = (getRandom().nextFloat() - 0.5f) * radius * 0.5f;
				double velX = ox * getRandom().nextFloat() * 3.5f;
				double velY = 0.1f + getRandom().nextFloat() * 0.45f;
				double velZ = oz * getRandom().nextFloat() * 3.5f;
				TheBetweenlands.createParticle(new DripParticleOptions(false, false), level(), this.getX() + ox, this.getY() + radius * 0.25f + oy, this.getZ() + oz, ParticleFactory.ParticleArgs.get().withMotion(velX, velY, velZ).withScale(0.75f).withColor(0.44f, 0.46f, 0.42f, 1.0f));
			}
		}
	}

	protected SoundEvent getSplashSound() {
		return SoundRegistry.BUBBLER_LAND.get();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(!this.level().isClientSide() && amount > 0.5f + this.level().getRandom().nextFloat() * 5.0f) {
			if(this.isPassenger()) {
				this.stopRiding();
			} else {
				explode(2.75D);
				this.level().broadcastEntityEvent(this, EVENT_BIG_EXPLODE);
				level().playSound(null, blockPosition(), SoundRegistry.BUBBLER_POP.get(), SoundSource.HOSTILE, 1F, 0.5F);
				discard();
			}
		}
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

}