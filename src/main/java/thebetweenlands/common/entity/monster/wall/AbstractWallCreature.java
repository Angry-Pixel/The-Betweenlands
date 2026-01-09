package thebetweenlands.common.entity.monster.wall;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.BLEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractWallCreature extends PathfinderMob implements BLEntity {

	private static final EntityDataAccessor<Direction> FACING = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.DIRECTION);
	private static final EntityDataAccessor<Direction> FACING_UP = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.DIRECTION);
	private static final EntityDataAccessor<BlockPos> ANCHOR = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.BLOCK_POS);

	private int targetFacingTimeout = 40;
	private Direction targetFacing;
	private Direction targetFacingUp;

	private int targetAnchorTimeout = 40;
	private BlockPos targetAnchor;

	private static final EntityDataAccessor<Boolean> MOVING = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> MOVE_SPEED = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Direction> MOVE_FACING = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.DIRECTION);
	private static final EntityDataAccessor<Direction> MOVE_FACING_UP = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.DIRECTION);
	private static final EntityDataAccessor<BlockPos> MOVE_ANCHOR = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Byte> MOVE_REASON = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> ANCHORED = SynchedEntityData.defineId(AbstractWallCreature.class, EntityDataSerializers.BOOLEAN);

	protected float lookMoveSpeedMultiplier = 1.0F;

	private float lastMoveProgress = 0;
	private float moveProgress = 0;

	protected float peek = 0.25F;

	public enum MoveReason {
		POSITION, LOOK, POSITION_AND_LOOK
	}

	public AbstractWallCreature(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.lookControl = new LookHelper(this);
		this.moveControl = new MoveHelper(this);
		this.noCulling = true;
	}

	@Override
	public LookHelper getLookControl() {
		return (LookHelper) super.getLookControl();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(FACING, Direction.NORTH);
		builder.define(FACING_UP, Direction.UP);
		builder.define(ANCHOR, BlockPos.ZERO);
		builder.define(MOVING, false);
		builder.define(MOVE_SPEED, 1.0F);
		builder.define(MOVE_FACING, Direction.NORTH);
		builder.define(MOVE_FACING_UP, Direction.UP);
		builder.define(MOVE_ANCHOR, BlockPos.ZERO);
		builder.define(MOVE_REASON, (byte) 0);
		builder.define(ANCHORED, true);
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData spawnGroupData) {
		Direction[] randomFacing = this.findRandomValidFacingAt(this.blockPosition());
		if (randomFacing == null) {
			randomFacing = new Direction[]{Direction.NORTH, Direction.UP};
		}
		this.setPositionToAnchor(this.blockPosition(), randomFacing[0], randomFacing[1]);
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	public boolean checkSpawnRules(LevelAccessor level, MobSpawnType reason) {
		BlockState surfaceState = this.level().getBlockState(this.blockPosition().below());
		return surfaceState.isValidSpawn(this.level(), this.blockPosition().below(), this.getType()) && this.findRandomValidFacingAt(this.blockPosition()) != null;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
	}

	@Override
	protected boolean shouldDropLoot() {
		return true;
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (this.isEffectiveAi() && !this.isImmobile()) {
			this.lookControl.tick();
		}
	}

	@Override
	public void knockback(double strength, double x, double z) {
		//No knockback
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected boolean isImmobile() {
		return this.isMoving();
	}

	@Override
	public void setSpeed(float speedIn) {
		this.getEntityData().set(MOVE_SPEED, speedIn);
	}

	@Override
	public float getSpeed() {
		if (this.isMoving() && this.getMoveReason() == MoveReason.LOOK) {
			return this.getEntityData().get(MOVE_SPEED) * this.lookMoveSpeedMultiplier;
		}
		return this.getEntityData().get(MOVE_SPEED);
	}

//	@Override
//	public int getBrightnessForRender() {
//		if (!this.isAnchored()) {
//			return super.getBrightnessForRender();
//		}
//
//		Direction facing = this.getFacing();
//		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(this.getX()) + facing.getStepX(), 0, Mth.floor(this.getZ()) + facing.getStepZ());
//
//		if (this.level().isLoaded(pos)) {
//			pos.setY(Mth.floor(this.getY() + (double) this.getEyeHeight()) + facing.getStepY());
//			return this.level().getLightEngine().getRawBrightness(pos, 0);
//		} else {
//			return 0;
//		}
//	}

	@Override
	public @Nullable ItemEntity spawnAtLocation(ItemStack stack, float offsetY) {
		if (stack.isEmpty()) {
			return null;
		} else if (this.level().isClientSide()) {
			return null;
		} else {
			ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY() + (double) offsetY, this.getZ(), stack);

			Direction facing = this.getFacing();
			Vec3 dropPos = this.getFrontCenter().add(facing.getStepX() * item.getBbWidth(), facing.getStepY() * item.getBbHeight(), facing.getStepZ() * item.getBbWidth());

			item.moveTo(dropPos.x, dropPos.y, dropPos.z);

			item.setDefaultPickUpDelay();
			if (this.captureDrops() != null) {
				this.captureDrops().add(item);
			} else {
				this.level().addFreshEntity(item);
			}
			return item;
		}
	}

	@Override
	public boolean hasLineOfSight(Entity entity) {
		if (entity.level() != this.level()) {
			return false;
		} else {
			Vec3 vec3 = this.getFrontCenter();
			Vec3 vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
			return !(vec31.distanceTo(vec3) > 128.0) && this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		tag.putInt("facing", this.getFacing().ordinal());
		tag.putInt("facingUp", this.getFacingUp().ordinal());
		tag.putLong("anchor", this.getAnchor().asLong());
		tag.putBoolean("anchored", this.isAnchored());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		this.getEntityData().set(FACING, Direction.from3DDataValue(tag.getInt("facing")));
		this.getEntityData().set(FACING_UP, Direction.from3DDataValue(tag.getInt("facingUp")));
		this.getEntityData().set(ANCHOR, BlockPos.of(tag.getLong("anchor")));
		this.setAnchored(tag.getBoolean("anchored"));
	}

	@Override
	public void tick() {
		boolean isAnchored = this.getEntityData().get(ANCHORED);

		if (isAnchored) {
			this.resetFallDistance();
			this.setOnGround(true);
			this.setNoGravity(true);

			double px = this.getX(), py = this.getY(), pz = this.getZ();

			super.tick();

			this.setPosRaw(
				this.xo = this.xOld = px,
				this.yo = this.yOld = py,
				this.zo = this.zOld = pz
			);

			this.setDeltaMovement(Vec3.ZERO);
		} else {
			this.setNoGravity(false);

			super.tick();
		}

		this.updatePositioning(isAnchored);

		this.updateMovement();
	}

	@Override
	public void move(MoverType type, Vec3 pos) {
		if (this.isAnchored()) {
			this.horizontalCollision = this.verticalCollision = true;
		} else {
			super.move(type, pos);
		}
	}

	@Override
	public void moveTo(BlockPos pos, float yRot, float xRot) {
		if (!this.isAnchored()) {
			super.moveTo(pos, yRot, xRot);
		}
	}

	@Override
	public void moveRelative(float amount, Vec3 relative) {
		if (!this.isAnchored()) {
			super.moveRelative(amount, relative);
		}
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {

	}

	@Override
	public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
		return false;
	}

	protected void updatePositioning(boolean isAnchored) {
		Direction facing = this.getFacing();
		Direction facingUp = this.getFacingUp();

		if (isAnchored) {
			if (facing == Direction.UP || facing == Direction.DOWN) {
				this.setXRot(this.xRotO = facing == Direction.UP ? -90.0F : 90.0F);
				this.yBodyRotO = this.yBodyRot = facingUp.toYRot() + (facing == Direction.DOWN ? 0.0F : 180.0F);
			} else {
				this.setXRot(this.xRotO = 0);
				this.yBodyRotO = this.yBodyRot = facing.toYRot();
			}
		} else {
			if (this.onGround() && Math.abs(this.getXRot() + 90.0f) > 1) {
				if (this.getXRot() > -90.0f) {
					this.setXRot(this.getXRot() - 25.0F);
					if (this.getXRot() < -90.0f) {
						this.setXRot(-90.0F);
					}
				} else {
					this.setXRot(this.getXRot() + 25.0F);
					if (this.getXRot() > -90.0f) {
						this.setXRot(-90.0F);
					}
				}
			}
		}

		if (!this.level().isClientSide()) {
			if (!this.isMoving()) {
				if (this.targetFacingTimeout > 0) {
					this.targetFacingTimeout--;
				} else {
					this.targetFacingUp = null;
					this.targetFacing = null;
				}

				if (this.targetAnchorTimeout > 0) {
					this.targetAnchorTimeout--;
				} else {
					this.targetAnchor = null;
				}

				if (!this.isTravelBlocked() && !this.isMoving() && (this.targetFacing != null || this.targetAnchor != null)) {
					Direction targetFacing = this.targetFacing != null ? this.targetFacing : facing;
					Direction targetFacingUp = this.targetFacingUp != null ? this.targetFacingUp : facingUp;
					BlockPos targetAnchor = this.targetAnchor != null ? this.targetAnchor : this.getAnchor();

					boolean isLookDifferent = facing != targetFacing || facingUp != targetFacingUp;
					boolean isPositionDifferent = !this.getAnchor().equals(targetAnchor);

					if (isLookDifferent || isPositionDifferent) {
						if (this.checkAnchorAt(targetAnchor, targetFacing, targetFacingUp, AnchorChecks.ALL) == 0) {
							this.getEntityData().set(MOVING, true);
							this.getEntityData().set(MOVE_FACING, targetFacing);
							this.getEntityData().set(MOVE_FACING_UP, targetFacingUp);
							this.getEntityData().set(MOVE_ANCHOR, targetAnchor);

							if (isPositionDifferent && isLookDifferent) {
								this.setMoveReason(MoveReason.POSITION_AND_LOOK);
							} else if (isPositionDifferent) {
								this.setMoveReason(MoveReason.POSITION);
							} else if (isLookDifferent) {
								this.setMoveReason(MoveReason.LOOK);
							}

							this.targetFacing = null;
							this.targetFacingUp = null;
							this.targetAnchor = null;
						}
					}
				}
			}

			int violatedChecks = this.checkAnchorHere(AnchorChecks.ALL);
			if (violatedChecks != 0) {
				this.fixUnsuitablePosition(violatedChecks);
			}
		}

		if (!this.isMoving() && isAnchored) {
			Vec3 offset = this.getOffset(1);
			Vec3 position = this.getCenter().add(offset);
			this.setPos(position.x, position.y - this.getBbHeight() / 2.0D, position.z);
		}
	}

	protected boolean isTravelBlocked() {
		return this.isImmobile();
	}

	protected void updateMovement() {
		this.lastMoveProgress = this.moveProgress;

		if (this.isMoving()) {
			float movementProgress = this.getMovementProgress(1);
			if (movementProgress < 0.5F) {
				Vec3 offset = this.getOffset(movementProgress);
				Vec3 position = this.getCenter().add(offset);
				this.setPosRaw(position.x, position.y - this.getBbHeight() / 2.0D, position.z);
			} else {
				this.getEntityData().set(ANCHOR, this.getEntityData().get(MOVE_ANCHOR));
				this.getEntityData().set(FACING, this.getEntityData().get(MOVE_FACING));
				this.getEntityData().set(FACING_UP, this.getEntityData().get(MOVE_FACING_UP));
				this.setAnchored(true);
				Vec3 offset = this.getOffset(movementProgress);
				Vec3 position = this.getCenter().add(offset);
				double px = this.getX();
				double py = this.getY();
				double pz = this.getZ();
				this.setPosRaw(position.x, position.y - this.getBbHeight() / 2.0D, position.z);
				if ((this.getX() - px) * (this.getX() - px) + (this.getY() - py) * (this.getY() - py) + (this.getZ() - pz) * (this.getZ() - pz) >= 1.0D) {
					this.setPos(this.getX(), this.getY(), this.getZ());
				}
			}
			if (this.moveProgress >= 1.0F) {
				this.getEntityData().set(MOVING, false);
				this.lastMoveProgress = this.moveProgress = 0;
			} else {
				this.moveProgress += 0.05F * (this.getSpeed() + 0.05F);
			}
		} else {
			this.moveProgress = this.lastMoveProgress = 0;
		}
	}

	public float getPeek() {
		return this.peek;
	}

	private float getHalfMovementProgressFromRegular(float movementProgress) {
		float halfProgress;
		if (movementProgress < 0.5F) {
			halfProgress = (0.5F - movementProgress) / 0.5F;
		} else {
			halfProgress = (movementProgress - 0.5F) / 0.5F;
		}
		return halfProgress;
	}

	public float getHalfMovementProgress(float partialTicks) {
		return this.getHalfMovementProgressFromRegular(this.getMovementProgress(partialTicks));
	}

	public float getMovementProgress(float partialTicks) {
		return Mth.clamp(this.lastMoveProgress + (this.moveProgress - this.lastMoveProgress) * partialTicks, 0, 1);
	}

	public Vec3 getOffset(float movementProgress) {
		float offsetLength = this.getHalfMovementProgressFromRegular(movementProgress);
		Vec3i normal = this.getFacing().getNormal();
		return new Vec3(normal.getX() - 1.65D, normal.getY() - 1.65D, normal.getZ() - 1.65D).scale(this.getPeek() + (this.getFacing().getAxis().isHorizontal() ? (this.getBlockWidth() - this.getBbWidth()) : (this.getBlockHeight() - this.getBbHeight())) / 2.0D).scale(offsetLength);
	}

	public int getBlockWidth() {
		return Mth.ceil(this.getBbWidth());
	}

	public int getBlockHeight() {
		return Mth.ceil(this.getBbHeight());
	}

	public Direction getFacing() {
		return this.getEntityData().get(FACING);
	}

	public Direction getFacingUp() {
		return this.getEntityData().get(FACING_UP);
	}

	public BlockPos getAnchor() {
		return this.getEntityData().get(ANCHOR);
	}

	public boolean isMoving() {
		return this.getEntityData().get(MOVING);
	}

	public Vec3 getCenter() {
		return Vec3.atCenterOf(this.getAnchor()).add(this.getBlockWidth() / 2.0D, this.getBlockHeight() / 2.0D, this.getBlockWidth() / 2.0D);
	}

	public Vec3 getFrontCenter() {
		Direction facing = this.getFacing();
		Vec3 center = this.getCenter();
		return center.add(this.getOffset(this.getMovementProgress(1))).add(facing.getStepX() * this.getBbWidth() / 2.0F, facing.getStepY() * this.getBbHeight() / 2.0F, facing.getStepY() * this.getBbWidth() / 2.0F);
	}

	public boolean isAnchored() {
		return this.getEntityData().get(ANCHORED);
	}

	public void setAnchored(boolean anchored) {
		this.getEntityData().set(ANCHORED, anchored);
	}

	public void stopMovement() {
		this.getEntityData().set(MOVING, false);
		this.lastMoveProgress = this.moveProgress = 0;
	}

	public Direction[] getFacingForLookDir(Vec3 lookDir) {
		Direction[] facing = new Direction[2];
		Direction dir = Direction.getNearest((float) lookDir.x, (float) lookDir.y, (float) lookDir.z);
		facing[0] = dir;
		if (dir == Direction.DOWN || dir == Direction.UP) {
			facing[1] = Direction.getNearest((float) lookDir.x, 0, (float) lookDir.z);
			if (dir == Direction.UP) {
				facing[1] = facing[1].getOpposite();
			}
		} else {
			facing[1] = Direction.UP;
		}
		return facing;
	}

	public void setPositionToAnchor(BlockPos anchor, Direction facing, Direction facingUp) {
		this.getEntityData().set(ANCHOR, anchor);
		this.getEntityData().set(FACING, facing);
		this.getEntityData().set(FACING_UP, facingUp);

		this.getEntityData().set(MOVING, false);
		this.lastMoveProgress = this.moveProgress = 0;

		this.setAnchored(true);
		this.stopMovement();

		this.updatePositioning(true);
	}

	public MoveReason getMoveReason() {
		return switch (this.getEntityData().get(MOVE_REASON)) {
			case 1 -> MoveReason.LOOK;
			case 2 -> MoveReason.POSITION_AND_LOOK;
			default -> MoveReason.POSITION;
		};
	}

	private void setMoveReason(MoveReason type) {
		switch (type) {
			case POSITION -> this.getEntityData().set(MOVE_REASON, (byte) 0);
			case LOOK -> this.getEntityData().set(MOVE_REASON, (byte) 1);
			case POSITION_AND_LOOK -> this.getEntityData().set(MOVE_REASON, (byte) 2);

		}
	}

	public static class AnchorChecks {
		/**
		 * Checks whether the blocks around the anchor are valid
		 */
		public static final int ANCHOR_BLOCKS = 0b001;

		/**
		 * Checks whether the blocks at the entity's face at the anchor are valid
		 */
		public static final int FACE_BLOCKS = 0b010;

		/**
		 * Checks whether the entities around the anchor and the entity's face at the anchor are valid
		 */
		public static final int ENTITIES = 0b100;

		public static final int BLOCKS = ANCHOR_BLOCKS | FACE_BLOCKS;
		public static final int ALL = BLOCKS | ENTITIES;
	}

	@Nullable
	protected Direction[] findRandomValidFacingAt(BlockPos anchor) {
		List<Direction> forwardFacings = new ArrayList<>(Arrays.asList(Direction.values()));
		Collections.shuffle(forwardFacings);

		List<Direction> horizontalFacings = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
		Collections.shuffle(horizontalFacings);

		for (Direction forwardFacing : forwardFacings) {
			if (forwardFacing.getAxis() == Direction.Axis.Y) {
				for (Direction horizontalFacing : horizontalFacings) {
					if (this.checkAnchorAt(anchor, forwardFacing, horizontalFacing, AnchorChecks.ALL) == 0) {
						return new Direction[]{forwardFacing, horizontalFacing};
					}
				}
			} else {
				if (this.checkAnchorAt(anchor, forwardFacing, Direction.UP, AnchorChecks.ALL) == 0) {
					return new Direction[]{forwardFacing, Direction.UP};
				}
			}
		}

		return null;
	}

	public int checkAnchorAt(Vec3 pos, Vec3 lookDir, int checks) {
		Direction[] facing = this.getFacingForLookDir(lookDir);
		BlockPos anchor = BlockPos.containing(pos.x - (this.getBlockWidth() / 2), pos.y - (this.getBlockHeight() / 2), pos.z - (this.getBlockWidth() / 2));
		return this.checkAnchorAt(anchor, facing[0], facing[1], checks);
	}

	public int checkAnchorAt(BlockPos anchor, Direction facing, Direction facingUp, int checks) {
		int violations = 0;

		if ((checks & AnchorChecks.ENTITIES) != 0) {
			if (!this.level().getEntitiesOfClass(AbstractWallCreature.class, this.getBoundingBox().move(anchor.subtract(this.getAnchor())).inflate(facing.getStepX() * this.getPeek(), facing.getStepY() * this.getPeek(), facing.getStepZ() * this.getPeek()), e -> e != this).isEmpty()) {
				violations |= AnchorChecks.ENTITIES;
			}
		}

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		if ((checks & AnchorChecks.ANCHOR_BLOCKS) != 0) {
			outer:
			for (int xo = 0; xo < this.getBlockWidth(); xo++) {
				for (int yo = 0; yo < this.getBlockHeight(); yo++) {
					for (int zo = 0; zo < this.getBlockWidth(); zo++) {
						pos.set(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + zo);
						if (!this.canResideInBlock(pos, facing, facingUp)) {
							violations |= AnchorChecks.ANCHOR_BLOCKS;
							break outer;
						}
					}
				}
			}
		}

		if ((checks & AnchorChecks.FACE_BLOCKS) != 0) {
			if (facing == Direction.UP || facing == Direction.DOWN) {
				int y = facing == Direction.UP ? this.getBlockHeight() : -1;
				outer:
				for (int xo = 0; xo < this.getBlockWidth(); xo++) {
					for (int zo = 0; zo < this.getBlockWidth(); zo++) {
						for (int yo = 0; yo < Mth.ceil(this.getPeek()); yo++) {
							pos.set(anchor.getX() + xo, anchor.getY() + y + facing.getStepY() * yo, anchor.getZ() + zo);
							if (!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			} else if (facing == Direction.NORTH || facing == Direction.SOUTH) {
				int z = facing == Direction.NORTH ? -1 : this.getBlockWidth();
				outer:
				for (int xo = 0; xo < this.getBlockWidth(); xo++) {
					for (int yo = 0; yo < this.getBlockHeight(); yo++) {
						for (int zo = 0; zo < Mth.ceil(this.getPeek()); zo++) {
							pos.set(anchor.getX() + xo, anchor.getY() + yo, anchor.getZ() + z + facing.getStepZ() * zo);
							if (!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			} else if (facing == Direction.WEST || facing == Direction.EAST) {
				int x = facing == Direction.WEST ? -1 : this.getBlockWidth();
				outer:
				for (int zo = 0; zo < this.getBlockWidth(); zo++) {
					for (int yo = 0; yo < this.getBlockHeight(); yo++) {
						for (int xo = 0; xo < Mth.ceil(this.getPeek()); xo++) {
							pos.set(anchor.getX() + x + facing.getStepX() * xo, anchor.getY() + yo, anchor.getZ() + zo);
							if (!this.canMoveFaceInto(pos, facing, facingUp)) {
								violations |= AnchorChecks.FACE_BLOCKS;
								break outer;
							}
						}
					}
				}
			}
		}

		return violations;
	}

	protected int checkAnchorHere(int checks) {
		return this.checkAnchorAt(this.getAnchor(), this.getFacing(), this.getFacingUp(), checks);
	}

	public abstract boolean canResideInBlock(BlockPos pos, Direction facing, Direction facingUp);

	public abstract boolean canMoveFaceInto(BlockPos pos, Direction facing, Direction facingUp);

	protected void fixUnsuitablePosition(int violatedChecks) {

	}

	public static final class LookHelper extends LookControl {
		private final AbstractWallCreature face;

		private int lookingMode = 0;

		private double x, y, z;

		private LookHelper(AbstractWallCreature entity) {
			super(entity);
			this.face = entity;
		}

		@Override
		public void setLookAt(Entity entity, float deltaYaw, float deltaPitch) {
			this.x = entity.getX();

			if (entity instanceof LivingEntity) {
				this.y = entity.getY() + (double) entity.getEyeHeight();
			} else {
				this.y = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D;
			}

			this.z = entity.getZ();
			this.lookingMode = 1;
		}

		@Override
		public void setLookAt(double x, double y, double z, float deltaYaw, float deltaPitch) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.lookingMode = 1;
		}

		public void setLookDirection(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.lookingMode = 2;
		}

		@Override
		public boolean isLookingAtTarget() {
			return this.lookingMode != 0;
		}

		@Override
		public double getWantedX() {
			return this.x;
		}

		@Override
		public double getWantedY() {
			return this.y;
		}

		@Override
		public double getWantedZ() {
			return this.z;
		}

		@Override
		public void tick() {
			if (this.lookingMode == 1) {
				Vec3 center = this.face.getCenter();
				Direction[] facing = this.face.getFacingForLookDir(new Vec3(this.x - center.x, this.y - center.y, this.z - center.z));
				this.face.targetFacingTimeout = 30 + this.face.getRandom().nextInt(30);
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
				this.setSpeed(1);
			} else if (this.lookingMode == 2) {
				Direction[] facing = this.face.getFacingForLookDir(new Vec3(this.x, this.y, this.z));
				this.face.targetFacingTimeout = 30 + this.face.getRandom().nextInt(30);
				this.face.targetFacing = facing[0];
				this.face.targetFacingUp = facing[1];
				this.setSpeed(1);
			}
			this.lookingMode = 0;
		}

		public void setSpeed(double speed) {
			if (!this.face.isMoving() && this.face.targetAnchor == null) {
				this.face.setSpeed((float) (speed * this.face.getAttributeValue(Attributes.MOVEMENT_SPEED)));
			}
		}
	}

	public static class MoveHelper extends MoveControl {
		private final AbstractWallCreature face;

		private MoveHelper(AbstractWallCreature entity) {
			super(entity);
			this.face = entity;
		}

		@Override
		public void tick() {
			if (this.operation == Operation.STRAFE && this.strafeRight != 0) {
				Vec3i horDir = this.face.getFacing().getNormal().cross(this.face.getFacingUp().getNormal());
				int strafeDir = -(int) Math.signum(this.strafeRight);
				this.face.targetAnchorTimeout = 30 + this.face.getRandom().nextInt(30);
				this.face.targetAnchor = this.face.getAnchor().offset(horDir.getX() * strafeDir, horDir.getY() * strafeDir, horDir.getZ() * strafeDir);
				this.setSpeed(this.speedModifier);
			} else if (this.operation == Operation.MOVE_TO) {
				this.face.targetAnchorTimeout = 30 + this.face.getRandom().nextInt(30);
				this.face.targetAnchor = BlockPos.containing(this.getWantedX() - this.face.getBlockWidth() / 2.0D, this.getWantedY() - this.face.getBlockHeight() / 2.0D, this.getWantedZ() - this.face.getBlockWidth() / 2.0D);
				this.setSpeed(this.speedModifier);
			}
			this.operation = Operation.WAIT;
		}

		public void setSpeed(double speed) {
			this.face.setSpeed((float) (speed * this.face.getAttributeValue(Attributes.MOVEMENT_SPEED)));
		}
	}
}
