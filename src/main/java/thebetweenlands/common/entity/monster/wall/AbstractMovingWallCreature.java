package thebetweenlands.common.entity.monster.wall;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class AbstractMovingWallCreature extends AbstractWallCreature {

	public AbstractMovingWallCreature(EntityType<? extends AbstractWallCreature> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.isMoving() && this.level().isClientSide()) {
			if (this.tickCount % 3 == 0) {
				Direction facing = this.getFacing();
				double px = this.getX() + facing.getStepX() * this.getBbWidth() / 2;
				double py = this.getY() + this.getBbHeight() / 2 + facing.getStepY() * this.getBbHeight() / 2;
				double pz = this.getZ() + facing.getStepZ() * this.getBbWidth() / 2;
				for (int i = 0; i < 24; i++) {
					double rx = (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth();
					double ry = (this.getRandom().nextDouble() - 0.5D) * this.getBbHeight();
					double rz = (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth();
					BlockPos pos = BlockPos.containing(px + rx, py + ry, pz + rz);
					BlockState state = this.level().getBlockState(pos);
					if (!state.isAir()) {
						double mx = facing.getStepX() * 0.15F + (this.getRandom().nextDouble() - 0.5D) * 0.25F;
						double my = facing.getStepY() * 0.15F + (this.getRandom().nextDouble() - 0.5D) * 0.25F;
						double mz = facing.getStepZ() * 0.15F + (this.getRandom().nextDouble() - 0.5D) * 0.25F;
						this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), px + rx, py + ry, pz + rz, mx, my, mz);
					}
				}
			}
		}
	}

	@Override
	public boolean canMoveFaceInto(BlockPos pos, Direction facing, Direction facingUp) {
		BlockState state = this.level().getBlockState(pos);
		if (state.liquid() || state.isAir()) {
			return true;
		}
		if (state.is(BlockTags.LEAVES)) {
			return false;
		}
		return this.level().noBlockCollision(this, new AABB(pos));
	}

	@Override
	protected void fixUnsuitablePosition(int violatedChecks) {
		if (this.tickCount % 3 == 0) {
			int searchRange = 8;

			for (int i = 0; i < 50; i++) {
				float rx = this.getRandom().nextFloat() * 2 - 1;
				float ry = this.getRandom().nextFloat() * 2 - 1;
				float rz = this.getRandom().nextFloat() * 2 - 1;
				BlockPos rndPos = BlockPos.containing(this.getX() + this.getRandom().nextInt(searchRange * 2) - searchRange, this.getY() + this.getBbHeight() / 2 + this.getRandom().nextInt(searchRange * 2) - searchRange, this.getZ() + this.getRandom().nextInt(searchRange * 2) - searchRange);
				if (this.level().isLoaded(rndPos)) {
					Vec3 pos = new Vec3(rndPos.getX() + 0.5D, rndPos.getY() + 0.5D, rndPos.getZ() + 0.5D);
					if (this.checkAnchorAt(pos, new Vec3(rx, ry, rz), AnchorChecks.ALL) == 0) {
						this.getLookControl().setLookDirection(rx, ry, rz);
						this.moveControl.setWantedPosition(pos.x, pos.y, pos.z, 1);
						break;
					}
				}
			}
		}
	}

	protected abstract boolean isValidBlockForMovement(BlockPos pos, BlockState state);

	public List<BlockPos> findNearbyBlocksForMovement() {
		final int radius = 8;
		BlockPos center = this.blockPosition();
		List<BlockPos> blocks = new ArrayList<>();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					BlockState state = this.level().getBlockState(pos);
					if (this.isValidBlockForMovement(pos, state)) {
						blocks.add(pos.immutable());
					}
				}
			}
		}
		return blocks;
	}

	@Override
	public boolean isWithinMeleeAttackRange(LivingEntity entity) {
		float r1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
		float r2 = Math.max(this.getBbWidth(), this.getBbHeight());
		double reach = 0.25D;
		return (this.isInReach(entity, reach) ? (r1 * r1 + r2 * r2 + reach * reach) * 2 : 0) >= this.distanceToSqr(entity.getX(), entity.getBoundingBox().minY, entity.getZ());
	}

	protected boolean isInReach(LivingEntity enemy, double reach) {
		double dist = Math.max(0, calculateAABBDistance(this.getBoundingBox(), enemy.getBoundingBox()));
		return dist <= reach;
	}

	public static double calculateAABBDistance(AABB aabb1, AABB aabb2) {
		double dist;

		if (aabb1.intersects(aabb2)) {
			double dx = Math.max(aabb1.minX - aabb2.maxX, aabb2.minX - aabb1.maxX);
			double dy = Math.max(aabb1.minY - aabb2.maxY, aabb2.minY - aabb1.maxY);
			double dz = Math.max(aabb1.minZ - aabb2.maxZ, aabb2.minZ - aabb1.maxZ);
			dist = Math.max(dx, Math.max(dy, dz));
		} else {
			double dx = Math.max(0, Math.max(aabb1.minX - aabb2.maxX, aabb2.minX - aabb1.maxX));
			double dy = Math.max(0, Math.max(aabb1.minY - aabb2.maxY, aabb2.minY - aabb1.maxY));
			double dz = Math.max(0, Math.max(aabb1.minZ - aabb2.maxZ, aabb2.minZ - aabb1.maxZ));
			dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
		}

		return dist;
	}

	protected static abstract class TrackTargetGoal<T extends AbstractMovingWallCreature> extends Goal {
		protected final T entity;

		protected int findBlocksCooldown = 0;
		protected List<BlockPos> trackingBlocks;

		protected int checkCooldown = 0;

		protected boolean stayInRange;
		protected double maxRangeSq;

		public TrackTargetGoal(T entity) {
			this(entity, false, 0);
		}

		public TrackTargetGoal(T entity, boolean stayInRange, double maxRange) {
			this.entity = entity;
			this.stayInRange = stayInRange;
			this.maxRangeSq = maxRange * maxRange;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		protected abstract boolean canMove();

		protected boolean isTargetVisibleAndInRange() {
			return this.entity.getSensing().hasLineOfSight(this.entity.getTarget()) && (!this.stayInRange || this.entity.getTarget().distanceToSqr(this.entity) <= this.maxRangeSq);
		}

		@Override
		public boolean canUse() {
			return this.entity.isAlive() && this.canMove() && !this.entity.isMoving() && this.entity.getTarget() != null && this.entity.getTarget().isAlive() && !this.isTargetVisibleAndInRange();
		}

		@Override
		public void start() {
			this.checkCooldown = 0;
			this.findBlocksCooldown = 20 + this.entity.getRandom().nextInt(30);
			this.trackingBlocks = null;
		}

		@Override
		public void tick() {
			LivingEntity target = this.entity.getTarget();

			if (target != null && this.canMove()) {
				if (this.findBlocksCooldown <= 0 && (this.trackingBlocks == null || this.trackingBlocks.isEmpty())) {
					this.findBlocksCooldown = 20 + this.entity.getRandom().nextInt(40);
					this.trackingBlocks = this.entity.findNearbyBlocksForMovement();
				}

				if (this.trackingBlocks != null && !this.trackingBlocks.isEmpty() && this.checkCooldown <= 0) {
					this.checkCooldown = 5 + this.entity.getRandom().nextInt(15);

					for (int i = 0; i < 16; i++) {
						if (this.trackingBlocks.isEmpty()) {
							break;
						}

						BlockPos pos = this.trackingBlocks.remove(this.entity.getRandom().nextInt(this.trackingBlocks.size()));

						if (!this.stayInRange || target.distanceToSqr(Vec3.atCenterOf(pos)) <= this.maxRangeSq) {
							Vec3 center = new Vec3(pos.getX() + this.entity.getBlockWidth() / 2.0D, pos.getY() + this.entity.getBlockHeight() / 2.0D, pos.getZ() + this.entity.getBlockWidth() / 2.0D);
							Vec3 lookDir = target.position().add(0, target.getEyeHeight(), 0).subtract(center);

							Direction facing = Direction.getNearest((float) lookDir.x, (float) lookDir.y, (float) lookDir.z);

							if (this.canSeeFrom(pos, facing, target) && this.entity.checkAnchorAt(center, lookDir, AnchorChecks.ALL) == 0) {
								this.entity.getMoveControl().setWantedPosition(center.x, center.y, center.z, 1);
								this.entity.getLookControl().setLookDirection(facing.getStepX(), facing.getStepY(), facing.getStepZ());
								break;
							} else {
								for (Direction otherFacing : Direction.Plane.HORIZONTAL) {
									if (otherFacing != facing) {
										lookDir = new Vec3(otherFacing.getStepX(), 0, otherFacing.getStepZ());

										if (this.canSeeFrom(pos, otherFacing, target) && this.entity.checkAnchorAt(center, lookDir, AnchorChecks.ALL) == 0) {
											this.entity.getMoveControl().setWantedPosition(center.x, center.y, center.z, 1);
											this.entity.getLookControl().setLookDirection(otherFacing.getStepX(), otherFacing.getStepY(), otherFacing.getStepZ());
											break;
										}
									}
								}
							}
						}
					}
				}

				this.checkCooldown--;
				this.findBlocksCooldown--;
			}
		}

		protected boolean canSeeFrom(BlockPos pos, Direction facing, Entity entity) {
			Vec3 ourPos = new Vec3(pos.getX() + this.entity.getBlockWidth() / 2.0D + facing.getStepX() * (this.entity.getBbWidth() / 2 + this.entity.getPeek()), pos.getY() + this.entity.getBlockHeight() / 2.0D + facing.getStepY() * (this.entity.getBbHeight() / 2 + this.entity.getPeek()), pos.getZ() + this.entity.getBlockWidth() / 2.0D + facing.getStepZ() * (this.entity.getBbWidth() / 2 + this.entity.getPeek()));
			Vec3 entityPos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
			return this.entity.level().clip(new ClipContext(ourPos, entityPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.entity)).getType() == HitResult.Type.MISS;
		}

		@Override
		public boolean canContinueToUse() {
			return !this.entity.isMoving() && this.entity.getTarget() != null && this.entity.getTarget().isAlive() && !this.isTargetVisibleAndInRange();
		}
	}

	protected static abstract class WanderGoal<T extends AbstractMovingWallCreature> extends Goal {
		protected final T entity;

		protected int findBlocksCooldown = 0;
		protected List<BlockPos> trackingBlocks;

		protected int checkCooldown = 0;

		protected double speed;
		protected int chance;

		protected double range;
		protected double rangeSq;

		protected boolean wandered = false;

		public WanderGoal(T entity, double range, double speed) {
			this(entity, range, speed, 120);
		}

		public WanderGoal(T entity, double range, double speed, int chance) {
			this.entity = entity;
			this.range = range;
			this.rangeSq = range * range;
			this.speed = speed;
			this.chance = chance;
			this.setFlags(EnumSet.of(Flag.MOVE));
		}

		protected abstract boolean canMove();

		@Override
		public boolean canUse() {
			return this.canMove() && !this.entity.isMoving() && this.entity.getNoActionTime() < 100 && this.entity.getRandom().nextInt(this.chance) == 0;
		}

		@Override
		public void start() {
			this.checkCooldown = 0;
			this.findBlocksCooldown = 20 + this.entity.getRandom().nextInt(30);
			this.trackingBlocks = null;
			this.wandered = false;
		}

		@Override
		public void tick() {
			if (this.findBlocksCooldown <= 0 && (this.trackingBlocks == null || this.trackingBlocks.isEmpty())) {
				this.findBlocksCooldown = 40 + this.entity.getRandom().nextInt(60);
				this.trackingBlocks = this.entity.findNearbyBlocksForMovement();
			}

			if (this.trackingBlocks != null && !this.trackingBlocks.isEmpty() && this.checkCooldown <= 0) {
				this.checkCooldown = 5 + this.entity.getRandom().nextInt(15);

				for (int i = 0; i < 16; i++) {
					if (this.trackingBlocks.isEmpty()) {
						break;
					}

					BlockPos pos = this.trackingBlocks.remove(this.entity.getRandom().nextInt(this.trackingBlocks.size()));

					if (pos.distToCenterSqr(this.entity.getX(), this.entity.getY(), this.entity.getZ()) <= this.rangeSq && this.entity.isWithinRestriction(pos)) {
						Vec3 center = new Vec3(pos.getX() + this.entity.getBlockWidth() / 2.0D, pos.getY() + this.entity.getBlockHeight() / 2.0D, pos.getZ() + this.entity.getBlockWidth() / 2.0D);
						double dx = this.entity.getRandom().nextDouble() - 0.5D;
						double dy = this.entity.getRandom().nextDouble() - 0.5D;
						double dz = this.entity.getRandom().nextDouble() - 0.5D;
						if (this.entity.checkAnchorAt(center, new Vec3(dx, dy, dz), AnchorChecks.ALL) == 0) {
							this.entity.getMoveControl().setWantedPosition(center.x, center.y, center.z, this.speed);
							this.entity.getLookControl().setLookDirection(dx, dy, dz);
							this.wandered = true;
							return;
						}
					}
				}
			}

			this.checkCooldown--;
			this.findBlocksCooldown--;
		}

		@Override
		public boolean canContinueToUse() {
			return !this.wandered && this.canMove() && !this.entity.isMoving();
		}
	}
}
