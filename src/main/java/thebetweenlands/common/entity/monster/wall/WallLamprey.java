package thebetweenlands.common.entity.monster.wall;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.projectile.SludgeWallJet;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class WallLamprey extends AbstractMovingWallCreature implements Enemy {

	public static final byte EVENT_START_THE_SUCC = 80;

	private static final EntityDataAccessor<Boolean> HIDDEN = SynchedEntityData.defineId(WallLamprey.class, EntityDataSerializers.BOOLEAN);

	private static final EntityDataAccessor<Float> LOOK_X = SynchedEntityData.defineId(WallLamprey.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> LOOK_Y = SynchedEntityData.defineId(WallLamprey.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> LOOK_Z = SynchedEntityData.defineId(WallLamprey.class, EntityDataSerializers.FLOAT);

	private float prevHiddenPercent = 1.0F;
	private float hiddenPercent = 1.0F;

	private Vec3 prevHeadLook = Vec3.ZERO;
	private Vec3 headLook = Vec3.ZERO;

	private boolean clientHeadLookChanged = false;

	private int suckTimer = 0;

	public final WallSpriteInfo info = new WallSpriteInfo();

	public WallLamprey(EntityType<? extends AbstractMovingWallCreature> type, Level level) {
		super(type, level);
		this.lookMoveSpeedMultiplier = 15.0F;
		this.xpReward = 7;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);

		builder.define(HIDDEN, true);
		builder.define(LOOK_X, 0.0F);
		builder.define(LOOK_Y, 0.0F);
		builder.define(LOOK_Z, 0.0F);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH.get();
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new TrackTargetGoal<>(this, true, 28.0D) {
			@Override
			protected boolean canMove() {
				return !WallLamprey.this.isSucking();
			}
		});
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1, true));
		this.goalSelector.addGoal(2, new LampreySuckGoal(this));
		this.goalSelector.addGoal(3, new LampreySpitGoal(this, 3.0F));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, false, null).setUnseenMemoryTicks(120));

	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.08D)
			.add(Attributes.ATTACK_DAMAGE, 4.0D);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);

		if (LOOK_X.equals(key) || LOOK_Y.equals(key) || LOOK_Z.equals(key)) {
			this.clientHeadLookChanged = true;
		}
	}

	@Override
	protected boolean isTravelBlocked() {
		return super.isTravelBlocked() || this.isSucking();
	}

	@Override
	public void tick() {
		this.prevHeadLook = this.headLook;

		super.tick();

		if (this.clientHeadLookChanged) {
			this.headLook = new Vec3(this.getEntityData().get(LOOK_X), this.getEntityData().get(LOOK_Y), this.getEntityData().get(LOOK_Z));
			this.clientHeadLookChanged = false;
		}

		if (!this.level().isClientSide()) {
			LivingEntity attackTarget = this.getTarget();

			this.getEntityData().set(HIDDEN, attackTarget == null);

			if (attackTarget != null) {
				this.setHeadLook(attackTarget.getEyePosition().subtract(this.getEyePosition()));
			} else {
				this.setHeadLook(Vec3.atCenterOf(this.getFacing().getNormal()));
			}
		} else {
			this.prevHiddenPercent = this.hiddenPercent;

			if (this.getEntityData().get(HIDDEN)) {
				if (this.hiddenPercent < 1.0F) {
					this.hiddenPercent += 0.01F;
					if (this.hiddenPercent > 1.0F) {
						this.hiddenPercent = 1.0F;
					}
				}
			} else {
				if (this.hiddenPercent > 0.0F) {
					this.hiddenPercent -= 0.04F;
					if (this.hiddenPercent < 0.0F) {
						this.hiddenPercent = 0.0F;
					}
				}
			}

			this.info.updateWallSprite(this);
		}

		if (this.isSucking()) {
			this.suckTimer--;

			if (!this.level().isClientSide()) {
				List<Entity> affectedEntities = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(6.0F));

				for (Entity e : affectedEntities) {
					float dst = e.distanceTo(this);

					if (e == this || dst > 6.0F || !this.hasLineOfSight(e) || e instanceof BLEntity) {
						continue;
					}

					Vec3 vec = new Vec3(this.getX() - e.getX(), this.getY() - e.getY(), this.getZ() - e.getZ());
					vec = vec.normalize();

					float mod = (float) Math.pow(1.0F - dst / 6.0F, 1.3D);

					if (e instanceof Player player) {
						if (player.isBlocking()) mod *= 0.18F;
					}

					e.setDeltaMovement(e.getDeltaMovement().add(vec.x * 0.1F * mod, vec.y * 0.215F * mod, vec.z * 0.1F * mod));
					e.hurtMarked = true;
				}
			} else {
				Vec3 fwd = this.getHeadLook(1);
				Vec3 up = Vec3.atCenterOf(this.getFacingUp().getNormal());
				Vec3 right = fwd.cross(up);

				Vec3 front = this.getFrontCenter().add(fwd.scale(0.3D)).add(up.scale(-0.3D));

				for (int i = 0; i < 3; i++) {
					RandomSource rnd = this.getRandom();

					Vec3 vec = fwd.scale(rnd.nextFloat() * 5).add(up.scale((rnd.nextFloat() - 0.5F) * 1.2F)).add(right.scale((rnd.nextFloat() - 0.5F) * 1.2F));

					float rx = (float) vec.x;
					float ry = (float) vec.y;
					float rz = (float) vec.z;

					vec = vec.normalize();

					this.level().addParticle(ParticleTypes.WHITE_SMOKE, front.x + rx, front.y + ry, front.z + rz, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		boolean hasAttacked = false;

		if (entity instanceof Player player && DecayData.isDecayEnabled(player)) {
			float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

			if (entity.hurt(this.damageSources().mobAttack(this), attackDamage / 3.0F)) {
				hasAttacked = true;
				player.setData(AttachmentRegistry.DECAY, player.getData(AttachmentRegistry.DECAY).addDecayAcceleration(attackDamage * 2.0F));
			}
		} else {
			hasAttacked = super.doHurtTarget(entity);
		}

		if (hasAttacked) {
			this.playSound(SoundRegistry.WALL_LAMPREY_ATTACK.get());
		}

		return hasAttacked;
	}

	@Override
	public SoundSource getSoundSource() {
		return SoundSource.HOSTILE;
	}

	@Override
	public boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp) {
		return this.isValidBlockForMovement(pos, this.level().getBlockState(pos)) && this.isValidBlockForMovement(pos.relative(facingUp.getOpposite()), this.level().getBlockState(pos.relative(facingUp.getOpposite())));
	}

	@Override
	public int checkAnchorAt(BlockPos anchor, Direction facing, Direction facingUp, int checks) {
		int violations = super.checkAnchorAt(anchor, facing, facingUp, checks);

		//Check "below" (relative to facingUp) for entities
		if ((checks & AnchorChecks.ENTITIES) != 0) {
			if (!this.level().getEntitiesOfClass(AbstractWallCreature.class, this.getBoundingBox().move(anchor.subtract(this.getAnchor()).relative(facingUp.getOpposite())).inflate(facing.getStepX() * this.getPeek(), facing.getStepY() * this.getPeek(), facing.getStepZ() * this.getPeek()), e -> e != this).isEmpty()) {
				violations |= AnchorChecks.ENTITIES;
			}
		}

		return violations;
	}

	@Override
	protected boolean isValidBlockForMovement(BlockPos pos, BlockState state) {
		return state.isRedstoneConductor(this.level(), pos) && state.isSolid() && state.getDestroySpeed(this.level(), pos) > 0;
	}

	@Override
	public Vec3 getOffset(float movementProgress) {
		return super.getOffset(1.0F);
	}

	public float getHoleDepthPercent(float partialTicks) {
		return this.getHalfMovementProgress(partialTicks);
	}

	public float getLampreyHiddenPercent(float partialTicks) {
		return 1 - (1 - this.easeInOut(this.prevHiddenPercent + (this.hiddenPercent - this.prevHiddenPercent) * partialTicks)) * this.getHoleDepthPercent(partialTicks);
	}

	private float easeInOut(float percent) {
		float sq = percent * percent;
		return sq / (2.0f * (sq - percent) + 1.0f);
	}

	public void setHeadLook(Vec3 look) {
		look = look.normalize();
		Vec3 curr = this.headLook;
		if (Math.abs(curr.x - look.x) >= 0.01F || Math.abs(curr.y - look.y) >= 0.01F || Math.abs(curr.z - look.z) >= 0.01F) {
			if (!this.level().isClientSide()) {
				this.getEntityData().set(LOOK_X, (float) look.x);
				this.getEntityData().set(LOOK_Y, (float) look.y);
				this.getEntityData().set(LOOK_Z, (float) look.z);
			}
			this.headLook = look;
		}
	}

	public Vec3 getHeadLook(float partialTicks) {
		return new Vec3(
			this.prevHeadLook.x + (this.headLook.x - this.prevHeadLook.x) * partialTicks,
			this.prevHeadLook.y + (this.headLook.y - this.prevHeadLook.y) * partialTicks,
			this.prevHeadLook.z + (this.headLook.z - this.prevHeadLook.z) * partialTicks
		);
	}

	public float[] getRelativeHeadLookAngles(float partialTicks) {
		Vec3 headLook = this.getHeadLook(partialTicks);

		Vec3 fwdAxis = Vec3.atCenterOf(this.getFacing().getNormal());
		Vec3 upAxis = Vec3.atCenterOf(this.getFacingUp().getNormal());
		Vec3 rightAxis = fwdAxis.cross(upAxis);

		double fwd = fwdAxis.dot(headLook);
		double up = upAxis.dot(headLook);
		double right = rightAxis.dot(headLook);

		return new float[]{(float) Math.toDegrees(Math.atan2(right, fwd)), (float) Math.toDegrees(Math.atan2(fwd, up)) * (float) Math.signum(fwd) - 90.0F};
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_START_THE_SUCC) {
			this.startSucking();
		}
	}

	public void startSucking() {
		if (!this.level().isClientSide()) {
			this.level().broadcastEntityEvent(this, EVENT_START_THE_SUCC);
			this.playSound(SoundRegistry.WALL_LAMPREY_SUCK.get(), 0.8F, this.getRandom().nextFloat() * 0.3F + 0.8F);
		}
		this.suckTimer = 30 + this.getRandom().nextInt(20);
	}

	public boolean isSucking() {
		return this.suckTimer > 0;
	}

	public void startSpit(float spitDamage) {
		Entity target = this.getTarget();
		if (target != null) {
			Direction facing = this.getFacing();

			SludgeWallJet jet = new SludgeWallJet(this, this.level());
			jet.setPos(this.getX() + facing.getStepX() * (this.getBbWidth() / 2 + 0.1F), this.getY() + this.getBbHeight() / 2.0F + facing.getStepY() * (this.getBbHeight() / 2 + 0.1F), this.getZ() + facing.getStepZ() * (this.getBbWidth() / 2 + 0.1F));
			jet.setDamage(spitDamage);

			double dx = target.getX() - jet.getX();
			double dy = target.getBoundingBox().minY + (double) (target.getBbHeight() / 3.0F) - jet.getY();
			double dz = target.getZ() - jet.getZ();
			double dist = Mth.sqrt((float) (dx * dx + dz * dz));
			jet.shoot(dx, dy + dist * 0.2D, dz, 1, 1);

			this.level().addFreshEntity(jet);
		}
	}

	protected static class LampreySuckGoal extends Goal {
		protected final WallLamprey entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		public LampreySuckGoal(WallLamprey entity) {
			this(entity, 50, 140);
		}

		public LampreySuckGoal(WallLamprey entity, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
		}

		@Override
		public boolean canUse() {
			return this.entity.getFacing() != Direction.DOWN && !this.entity.isSucking() && !this.entity.isMoving() && this.entity.getTarget() != null && this.entity.getTarget().isAlive() &&
				this.entity.getSensing().hasLineOfSight(this.entity.getTarget()) && this.entity.distanceTo(this.entity.getTarget()) < 6.0F;
		}

		@Override
		public void start() {
			this.cooldown = 20 + this.entity.getRandom().nextInt(40);
		}

		@Override
		public void tick() {
			if (!this.entity.isSucking()) {
				if (this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.getRandom().nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSucking();
				}
				this.cooldown--;
			}
		}
	}

	protected static class LampreySpitGoal extends Goal {
		protected final WallLamprey entity;
		protected int minCooldown;
		protected int maxCooldown;

		protected int cooldown = 0;

		protected float spitDamage;

		public LampreySpitGoal(WallLamprey entity, float spitDamage) {
			this(entity, spitDamage, 50, 170);
		}

		public LampreySpitGoal(WallLamprey entity, float spitDamage, int minCooldown, int maxCooldown) {
			this.entity = entity;
			this.minCooldown = minCooldown;
			this.maxCooldown = maxCooldown;
			this.spitDamage = spitDamage;
		}

		protected boolean isInRange(LivingEntity target) {
			final Vec3 down = new Vec3(0, -1, 0);
			Vec3 dir = target.position().subtract(this.entity.position()).normalize();
			return Math.acos(down.dot(dir)) > 0.733D /*~42�*/;
		}

		@Override
		public boolean canUse() {
			return this.entity.getFacing() == Direction.DOWN && !this.entity.isSucking() && !this.entity.isMoving() && this.entity.getTarget() != null && this.entity.getTarget().isAlive() &&
				this.entity.getSensing().hasLineOfSight(this.entity.getTarget()) && this.isInRange(this.entity.getTarget());
		}

		@Override
		public void start() {
			this.cooldown = 20 + this.entity.getRandom().nextInt(40);
		}

		@Override
		public void tick() {
			if (!this.entity.isSucking()) {
				if (this.cooldown <= 0) {
					this.cooldown = this.minCooldown + this.entity.getRandom().nextInt(this.maxCooldown - this.minCooldown + 1);
					this.entity.startSpit(this.getSpitDamage());
				}
				this.cooldown--;
			}
		}

		protected float getSpitDamage() {
			return this.spitDamage;
		}
	}
}
