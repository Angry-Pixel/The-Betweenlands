package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import org.joml.Vector3f;
import thebetweenlands.common.entity.movement.climb.*;
import thebetweenlands.common.registries.EntityDataSerializerRegistry;
import thebetweenlands.util.AABBUtil;
import thebetweenlands.util.BoxSmoothingUtil;
import thebetweenlands.util.Matrix4f;

public abstract class ClimbingMob extends PathfinderMob implements BLEntity, PathObstructionAwareEntity {

	public static final EntityDataAccessor<Vector3f> HEAD_ROTATION = SynchedEntityData.defineId(ClimbingMob.class, EntityDataSerializers.VECTOR3);
	public static final EntityDataAccessor<Vector3f> BODY_ROTATION = SynchedEntityData.defineId(ClimbingMob.class, EntityDataSerializers.VECTOR3);
	public static final EntityDataAccessor<Optional<BlockPos>> PATHING_TARGET = SynchedEntityData.defineId(ClimbingMob.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
	public static final EntityDataAccessor<List<Optional<BlockPos>>> PATHING_TARGETS = SynchedEntityData.defineId(ClimbingMob.class, EntityDataSerializerRegistry.OPTIONAL_POS_LIST.get());
	public static final EntityDataAccessor<List<Direction>> PATHING_SIDES = SynchedEntityData.defineId(ClimbingMob.class, EntityDataSerializerRegistry.DIRECTION_LIST.get());

	private double prevAttachmentOffsetX, prevAttachmentOffsetY, prevAttachmentOffsetZ;
	private double attachmentOffsetX, attachmentOffsetY, attachmentOffsetZ;

	public Vec3 attachmentNormal = new Vec3(0, 1, 0);
	private Vec3 prevAttachmentNormal = new Vec3(0, 1, 0);

	private float prevOrientationYawDelta;
	private float orientationYawDelta;

	private double lastAttachmentOffsetX, lastAttachmentOffsetY, lastAttachmentOffsetZ;

	private Vec3 lastAttachmentOrientationNormal = new Vec3(0, 1, 0);

	private int attachedTicks = 5;

	private Vec3 attachedSides = new Vec3(0, 0, 0);
	private Vec3 prevAttachedSides = new Vec3(0, 0, 0);

	private boolean isTravelingInFluid = false;

	private Orientation orientation;
	private Pair<Direction, Vec3> groundDirection = Pair.of(Direction.DOWN, new Vec3(0, -1, 0));

	public ClimbingMob(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.moveControl = new ClimberMoveControl(this);
		this.orientation = this.calculateOrientation(1);
		this.groundDirection = this.getGroundDirection();
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(HEAD_ROTATION, new Vector3f());
		builder.define(BODY_ROTATION, new Vector3f());
		builder.define(PATHING_TARGET, Optional.empty());
		builder.define(PATHING_TARGETS, List.of(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()));
		builder.define(PATHING_SIDES, List.of(
			Direction.DOWN,
			Direction.DOWN,
			Direction.DOWN,
			Direction.DOWN,
			Direction.DOWN,
			Direction.DOWN,
			Direction.DOWN,
			Direction.DOWN));
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		ObstructionAwareGroundNavigation<ClimbingMob> navigate = new ObstructionAwareClimberNavigation<>(this, level, false, true, true);
		navigate.setCanFloat(true);
		return navigate;
	}

	@Override
	public void onPathingObstructed(Direction facing) {

	}

	@Override
	public int getMaxFallDistance() {
		return 0;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.orientation = this.calculateOrientation(1.0F);
	}

	private void updateWalkingSide() {
		Direction avoidPathingFacing = null;

		AABB entityBox = this.getBoundingBox();

		double closestFacingDst = Double.MAX_VALUE;
		Direction closestFacing = null;

		Vec3 weighting = new Vec3(0, 0, 0);

		float stickingDistance = this.zza != 0 ? 1.5f : 0.1f;

		for (Direction facing : Direction.values()) {
			if (avoidPathingFacing == facing) {
				continue;
			}

			List<AABB> collisionBoxes = this.getCollisionBoxes(entityBox.inflate(0.2f).expandTowards(facing.getStepX() * stickingDistance, facing.getStepY() * stickingDistance, facing.getStepZ() * stickingDistance));

			double closestDst = Double.MAX_VALUE;

			for (AABB collisionBox : collisionBoxes) {
				switch (facing) {
					case EAST:
					case WEST:
						closestDst = Math.min(closestDst, Math.abs(AABBUtil.calculateXOffset(entityBox, collisionBox, -facing.getStepX() * stickingDistance)));
						break;
					case UP:
					case DOWN:
						closestDst = Math.min(closestDst, Math.abs(AABBUtil.calculateYOffset(entityBox, collisionBox, -facing.getStepY() * stickingDistance)));
						break;
					case NORTH:
					case SOUTH:
						closestDst = Math.min(closestDst, Math.abs(AABBUtil.calculateZOffset(entityBox, collisionBox, -facing.getStepZ() * stickingDistance)));
						break;
				}
			}

			if (closestDst < closestFacingDst) {
				closestFacingDst = closestDst;
				closestFacing = facing;
			}

			if (closestDst < Double.MAX_VALUE) {
				weighting = weighting.add(new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ()).scale(1 - Math.min(closestDst, stickingDistance) / stickingDistance));
			}
		}

		if (closestFacing == null) {
			this.groundDirection = Pair.of(Direction.DOWN, new Vec3(0, -1, 0));
		} else {
			this.groundDirection = Pair.of(closestFacing, weighting.normalize().add(0, -0.001f, 0).normalize());
		}
	}

	public Pair<Direction, Vec3> getGroundDirection() {
		return this.groundDirection;
	}

	public Direction getGroundSide() {
		return this.groundDirection.getKey();
	}

	public Orientation getOrientation() {
		return this.orientation;
	}

	public double getAttachmentOffset(Direction.Axis axis, float partialTicks) {
		return switch (axis) {
			case Y -> Mth.lerp(partialTicks, this.prevAttachmentOffsetY, this.attachmentOffsetY);
			case Z -> Mth.lerp(partialTicks, this.prevAttachmentOffsetZ, this.attachmentOffsetZ);
			default -> Mth.lerp(partialTicks, this.prevAttachmentOffsetX, this.attachmentOffsetX);
		};
	}

	@Override
	public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 target) {
		Vec3 dir = target.subtract(this.position());
		dir = this.getOrientation().getLocal(dir);
		super.lookAt(anchor, dir);
	}

	@Override
	public boolean onClimbable() {
		return false;
	}

	public Orientation calculateOrientation(float partialTicks) {
		Vec3 orientationNormal = this.prevAttachmentNormal.add(this.attachmentNormal.subtract(this.prevAttachmentNormal).scale(partialTicks));

		Vec3 localZ = new Vec3(0, 0, 1);
		Vec3 localY = new Vec3(0, 1, 0);
		Vec3 localX = new Vec3(1, 0, 0);

		float componentZ = (float) localZ.dot(orientationNormal);
		float componentY;
		float componentX = (float) localX.dot(orientationNormal);

		float yaw = (float) Math.toDegrees(Mth.atan2(componentX, componentZ));

		localZ = new Vec3(Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
		localY = new Vec3(0, 1, 0);
		localX = new Vec3(Math.sin(Math.toRadians(yaw - 90)), 0, Math.cos(Math.toRadians(yaw - 90)));

		componentZ = (float) localZ.dot(orientationNormal);
		componentY = (float) localY.dot(orientationNormal);
		componentX = (float) localX.dot(orientationNormal);

		float pitch = (float) Math.toDegrees(Mth.atan2(Mth.sqrt(componentX * componentX + componentZ * componentZ), componentY));

		Matrix4f m = new Matrix4f();
		m.multiply(new Matrix4f((float) Math.toRadians(yaw), 0, 1, 0));
		m.multiply(new Matrix4f((float) Math.toRadians(pitch), 1, 0, 0));
		m.multiply(new Matrix4f((float) Math.toRadians(Math.signum(0.5f - componentY - componentZ - componentX) * yaw), 0, 1, 0));

		localZ = m.multiply(new Vec3(0, 0, -1));
		localY = m.multiply(new Vec3(0, 1, 0));
		localX = m.multiply(new Vec3(1, 0, 0));

		return new Orientation(orientationNormal, localZ, localY, localX, componentZ, componentY, componentX, yaw, pitch);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (BODY_ROTATION.equals(key)) {
			Vector3f rotation = this.getEntityData().get(BODY_ROTATION);
			Vec3 look = new Vec3(rotation.x(), rotation.y(), rotation.z());

			Pair<Float, Float> rotations = this.getOrientation().getLocalRotation(look);

			this.lerpYRot = rotations.getLeft();
			this.lerpXRot = rotations.getRight();
		} else if (HEAD_ROTATION.equals(key)) {
			Vector3f rotation = this.getEntityData().get(HEAD_ROTATION);
			Vec3 look = new Vec3(rotation.x(), rotation.y(), rotation.z());

			Pair<Float, Float> rotations = this.getOrientation().getLocalRotation(look);

			this.lerpYHeadRot = rotations.getLeft();
			this.lerpHeadSteps = 3;
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide() && this.level() instanceof ServerLevel server) {
			ChunkMap.TrackedEntity entityTracker = server.getChunkSource().chunkMap.entityMap.get(this.getId());

			//Prevent premature syncing of position causing overly smoothed movement
			if (entityTracker != null && entityTracker.serverEntity.tickCount % entityTracker.serverEntity.updateInterval == 0) {
				Orientation orientation = this.getOrientation();

				Vec3 look = orientation.getGlobal(this.getYRot(), this.getXRot());
				this.getEntityData().set(BODY_ROTATION, new Vector3f((float) look.x, (float) look.y, (float) look.z));

				look = orientation.getGlobal(this.yHeadRot, 0.0f);
				this.getEntityData().set(HEAD_ROTATION, new Vector3f((float) look.x, (float) look.y, (float) look.z));

				if (this.shouldTrackPathingTargets()) {
					if (this.getNavigation().isInProgress()) {
						if (this.xxa != 0) {
							Vec3 forwardVector = orientation.getGlobal(this.getYRot(), 0);
							Vec3 strafeVector = orientation.getGlobal(this.getYRot() + 90.0f, 0);

							Vec3 offset = forwardVector.scale(this.zza).add(strafeVector.scale(this.xxa)).normalize();

							this.getEntityData().set(PATHING_TARGET, Optional.of(BlockPos.containing(this.getX() + offset.x, this.getY() + this.getBbHeight() * 0.5f + offset.y, this.getZ() + offset.z)));
						} else {
							this.getEntityData().set(PATHING_TARGET, Optional.of(BlockPos.containing(this.getMoveControl().getWantedX(), this.getMoveControl().getWantedY(), this.getMoveControl().getWantedZ())));
						}
					} else {
						this.getEntityData().set(PATHING_TARGET, Optional.empty());
					}

					Path path = this.getNavigation().getPath();
					if (path != null) {
						for (int i = 0; i < this.getPathingTargets().size(); i++) {
							if (path.getNextNodeIndex() + i < path.getNodeCount()) {
								Node point = path.getNode(path.getNextNodeIndex() + i);

								this.setPathingTarget(i, new BlockPos(point.x, point.y, point.z));

								if (point instanceof DirectionalNode node) {
									Direction dir = node.getPathSide();

									this.setPathingSide(i, Objects.requireNonNullElse(dir, Direction.DOWN));
								}
							} else {
								this.setPathingTarget(i, null);
								this.setPathingSide(i, Direction.DOWN);
							}
						}
					} else {
						for (int i = 0; i < this.getPathingTargets().size(); i++) {
							this.setPathingTarget(i, null);
						}

						for (int i = 0; i < this.getPathingSides().size(); i++) {
							this.setPathingSide(i, Direction.DOWN);
						}
					}
				}
			}
		}

		this.updateWalkingSide();
	}

	@Nullable
	public List<PathingTarget> getTrackedPathingTargets() {
		if (this.shouldTrackPathingTargets()) {
			List<PathingTarget> pathingTargets = new ArrayList<>(this.getPathingTargets().size());

			int i = 0;
			for (Optional<BlockPos> key : this.getPathingTargets()) {
				BlockPos pos = key.orElse(null);

				if (pos != null) {
					pathingTargets.add(new PathingTarget(pos, this.getPathingSides().get(i)));
				}

				i++;
			}

			return pathingTargets;
		}

		return null;
	}

	protected boolean canAttachToWalls() {
		return !this.isInWater();
	}

	protected void updateOffsetsAndOrientation() {
		Vec3 direction = this.getOrientation().getGlobal(this.getYRot(), this.getXRot());

		boolean isAttached = false;

		double baseStickingOffsetX = 0.0F;
		double baseStickingOffsetY = 0.075F;
		double baseStickingOffsetZ = 0.0F;
		Vec3 baseOrientationNormal = new Vec3(0, 1, 0);

		if (!this.isTravelingInFluid && this.onGround() && this.getVehicle() == null) {
			Vec3 p = this.position();

			Vec3 s = p.add(0, this.getBbHeight() * 0.5F, 0);
			AABB inclusionBox = new AABB(s.x, s.y, s.z, s.x, s.y, s.z).inflate(2.0F);

			Pair<Vec3, Vec3> attachmentPoint = null;
			if (this.canAttachToWalls()) attachmentPoint = BoxSmoothingUtil.findClosestPoint(consumer -> this.forEachCollisionBox(inclusionBox, consumer), s, this.attachmentNormal.scale(-1), 1.25f, 1.0f, 0.001f, 20, 0.05f, s);

			AABB entityBox = this.getBoundingBox();

			if (attachmentPoint != null) {
				Vec3 attachmentPos = attachmentPoint.getLeft();

				double dx = Math.max(entityBox.minX - attachmentPos.x, attachmentPos.x - entityBox.maxX);
				double dy = Math.max(entityBox.minY - attachmentPos.y, attachmentPos.y - entityBox.maxY);
				double dz = Math.max(entityBox.minZ - attachmentPos.z, attachmentPos.z - entityBox.maxZ);

				if (Math.max(dx, Math.max(dy, dz)) < 0.5f) {
					isAttached = true;

					this.lastAttachmentOffsetX = Mth.clamp(attachmentPos.x - p.x, -this.getBbWidth() / 2, this.getBbWidth() / 2);
					this.lastAttachmentOffsetY = Mth.clamp(attachmentPos.y - p.y, 0, this.getBbHeight());
					this.lastAttachmentOffsetZ = Mth.clamp(attachmentPos.z - p.z, -this.getBbWidth() / 2, this.getBbWidth() / 2);
					this.lastAttachmentOrientationNormal = attachmentPoint.getRight();
				}
			}
		}

		this.prevAttachmentOffsetX = this.attachmentOffsetX;
		this.prevAttachmentOffsetY = this.attachmentOffsetY;
		this.prevAttachmentOffsetZ = this.attachmentOffsetZ;
		this.prevAttachmentNormal = this.attachmentNormal;

		float attachmentBlend = this.attachedTicks * 0.2f;

		this.attachmentOffsetX = baseStickingOffsetX + (this.lastAttachmentOffsetX - baseStickingOffsetX) * attachmentBlend;
		this.attachmentOffsetY = baseStickingOffsetY + (this.lastAttachmentOffsetY - baseStickingOffsetY) * attachmentBlend;
		this.attachmentOffsetZ = baseStickingOffsetZ + (this.lastAttachmentOffsetZ - baseStickingOffsetZ) * attachmentBlend;
		this.attachmentNormal = baseOrientationNormal.add(this.lastAttachmentOrientationNormal.subtract(baseOrientationNormal).scale(attachmentBlend)).normalize();

		if (!isAttached) {
			this.attachedTicks = Math.max(0, this.attachedTicks - 1);
		} else {
			this.attachedTicks = Math.min(5, this.attachedTicks + 1);
		}

		this.orientation = this.calculateOrientation(1.0F);

		Pair<Float, Float> newRotations = this.getOrientation().getLocalRotation(direction);

		float yawDelta = newRotations.getLeft() - this.getYRot();
		float pitchDelta = newRotations.getRight() - this.getXRot();

		this.prevOrientationYawDelta = this.orientationYawDelta;
		this.orientationYawDelta = yawDelta;

		this.setYRot(Mth.wrapDegrees(this.getYRot() + yawDelta));
		this.yRotO = this.wrapAngleInRange(this.yRotO, this.getYRot());
		this.lerpYRot = Mth.wrapDegrees(this.lerpYRot + yawDelta);

		this.yBodyRot = Mth.wrapDegrees(this.yBodyRot + yawDelta);
		this.yBodyRotO = this.wrapAngleInRange(this.yBodyRotO, this.yBodyRot);

		this.yHeadRot = Mth.wrapDegrees(this.yHeadRot + yawDelta);
		this.yHeadRotO = this.wrapAngleInRange(this.yHeadRotO, this.yHeadRot);
		this.lerpYHeadRot = Mth.wrapDegrees(this.lerpYHeadRot + yawDelta);

		this.setXRot(Mth.wrapDegrees(this.getXRot() + pitchDelta));
		this.xRotO = this.wrapAngleInRange(this.xRotO, this.getXRot());
		this.lerpXRot = Mth.wrapDegrees(this.lerpXRot + pitchDelta);
	}

	private float wrapAngleInRange(float angle, float target) {
		while (target - angle < -180.0F) {
			angle -= 360.0F;
		}

		while (target - angle >= 180.0F) {
			angle += 360.0F;
		}

		return angle;
	}

	private void forEachCollisionBox(AABB aabb, Shapes.DoubleLineConsumer action) {
		int minChunkX = ((Mth.floor(aabb.minX - 1.0E-7D) - 1) >> 4);
		int maxChunkX = ((Mth.floor(aabb.maxX + 1.0E-7D) + 1) >> 4);
		int minChunkZ = ((Mth.floor(aabb.minZ - 1.0E-7D) - 1) >> 4);
		int maxChunkZ = ((Mth.floor(aabb.maxZ + 1.0E-7D) + 1) >> 4);

		int width = maxChunkX - minChunkX + 1;
		int depth = maxChunkZ - minChunkZ + 1;

		BlockGetter[] blockReaderCache = new BlockGetter[width * depth];

		CollisionGetter collisionReader = this.level();

		for (int cx = minChunkX; cx <= maxChunkX; cx++) {
			for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
				blockReaderCache[(cx - minChunkX) + (cz - minChunkZ) * width] = collisionReader.getChunkForCollisions(cx, cz);
			}
		}

		CollisionGetter cachedCollisionReader = new CollisionGetter() {
			@Override
			public int getHeight() {
				return ClimbingMob.this.level().getHeight();
			}

			@Override
			public int getMinBuildHeight() {
				return ClimbingMob.this.level().getMinBuildHeight();
			}

			@Override
			public BlockEntity getBlockEntity(BlockPos pos) {
				return collisionReader.getBlockEntity(pos);
			}

			@Override
			public BlockState getBlockState(BlockPos pos) {
				return collisionReader.getBlockState(pos);
			}

			@Override
			public FluidState getFluidState(BlockPos pos) {
				return collisionReader.getFluidState(pos);
			}

			@Override
			public WorldBorder getWorldBorder() {
				return collisionReader.getWorldBorder();
			}

			@Override
			public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB aabb) {
				return collisionReader.getEntityCollisions(entity, aabb);
			}

			@Override
			public BlockGetter getChunkForCollisions(int chunkX, int chunkZ) {
				return blockReaderCache[(chunkX - minChunkX) + (chunkZ - minChunkZ) * width];
			}
		};

		Iterable<VoxelShape> shapes = cachedCollisionReader.getBlockCollisions(this, aabb);
		shapes.forEach(shape -> shape.forAllBoxes(action));
	}

	private List<AABB> getCollisionBoxes(AABB aabb) {
		List<AABB> boxes = new ArrayList<>();
		this.forEachCollisionBox(aabb, (minX, minY, minZ, maxX, maxY, maxZ) -> boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ)));
		return boxes;
	}

	public float getMovementSpeed() {
		AttributeInstance attribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
		return attribute != null ? (float) attribute.getValue() : 1.0f;
	}

	public Vec3 getStickingForce(Pair<Direction, Vec3> walkingSide) {
		double uprightness = Math.max(this.attachmentNormal.y(), 0);
		double gravity = this.getGravity();
		double stickingForce = gravity * uprightness + 0.08D * (1 - uprightness);
		return walkingSide.getRight().scale(stickingForce);
	}

	@Override
	public void travel(Vec3 travelVector) {
		boolean canTravel = this.isEffectiveAi() || this.isControlledByLocalInstance();

		this.isTravelingInFluid = false;

		FluidState fluidState = this.level().getFluidState(this.blockPosition());

		if (this.isInFluidType(fluidState) && this.isAffectedByFluids() && !this.canStandOnFluid(fluidState)) {
			this.isTravelingInFluid = true;

			if (canTravel) {
				super.travel(travelVector);
				return;
			}
		} else if (canTravel) {
			this.travelOnGround(travelVector);
		}

		if (!canTravel) {
			this.calculateEntityAnimation(true);
		}

		this.updateOffsetsAndOrientation();
	}

	private void travelOnGround(Vec3 relative) {
		Orientation orientation = this.getOrientation();

		Vec3 forwardVector = orientation.getGlobal(this.getYRot(), 0);
		Vec3 strafeVector = orientation.getGlobal(this.getYRot() + 90.0f, 0);
		Vec3 upVector = orientation.getGlobal(this.getYRot(), -90.0f);

		Pair<Direction, Vec3> groundDirection = this.groundDirection;

		Vec3 stickingForce = this.getStickingForce(groundDirection);

		boolean isFalling = this.getDeltaMovement().y <= 0.0D;

		if (isFalling && this.hasEffect(MobEffects.SLOW_FALLING)) {
			this.fallDistance = 0;
		}

		float forward = (float) relative.z;
		float strafe = (float) relative.x;

		if (forward != 0 || strafe != 0) {
			float slipperiness = 0.91f;

			if (this.onGround()) {
				BlockPos offsetPos = new BlockPos(this.blockPosition()).relative(groundDirection.getLeft());
				slipperiness = this.getBlockSlipperiness(offsetPos);
			}

			float f = forward * forward + strafe * strafe;
			if (f >= 1.0E-4F) {
				f = Math.max(Mth.sqrt(f), 1.0f);
				f = this.getRelevantMoveFactor(slipperiness) / f;
				forward *= f;
				strafe *= f;

				Vec3 movementOffset = new Vec3(forwardVector.x * forward + strafeVector.x * strafe, forwardVector.y * forward + strafeVector.y * strafe, forwardVector.z * forward + strafeVector.z * strafe);

				double px = this.getX();
				double py = this.getY();
				double pz = this.getZ();
				Vec3 motion = this.getDeltaMovement();
				AABB aabb = this.getBoundingBox();

				//Probe actual movement vector
				this.move(MoverType.SELF, movementOffset);

				Vec3 movementDir = new Vec3(this.getX() - px, this.getY() - py, this.getZ() - pz).normalize();

				this.setBoundingBox(aabb);
				this.setLocationFromBoundingbox();
				this.setDeltaMovement(motion);

				//Probe collision normal
				Vec3 probeVector = new Vec3(Math.abs(movementDir.x) < 0.001D ? -Math.signum(upVector.x) : 0, Math.abs(movementDir.y) < 0.001D ? -Math.signum(upVector.y) : 0, Math.abs(movementDir.z) < 0.001D ? -Math.signum(upVector.z) : 0).normalize().scale(0.0001D);
				this.move(MoverType.SELF, probeVector);

				Vec3 collisionNormal = new Vec3(Math.abs(this.getX() - px - probeVector.x) > 0.000001D ? Math.signum(-probeVector.x) : 0, Math.abs(this.getY() - py - probeVector.y) > 0.000001D ? Math.signum(-probeVector.y) : 0, Math.abs(this.getZ() - pz - probeVector.z) > 0.000001D ? Math.signum(-probeVector.z) : 0).normalize();

				this.setBoundingBox(aabb);
				this.setLocationFromBoundingbox();
				this.setDeltaMovement(motion);

				//Movement vector projected to surface
				Vec3 surfaceMovementDir = movementDir.subtract(collisionNormal.scale(collisionNormal.dot(movementDir))).normalize();

				boolean isInnerCorner = Math.abs(collisionNormal.x) + Math.abs(collisionNormal.y) + Math.abs(collisionNormal.z) > 1.0001f;

				//Only project movement vector to surface if not moving across inner corner, otherwise it'd get stuck in the corner
				if (!isInnerCorner) {
					movementDir = surfaceMovementDir;
				}

				//Nullify sticking force along movement vector projected to surface
				stickingForce = stickingForce.subtract(surfaceMovementDir.scale(surfaceMovementDir.normalize().dot(stickingForce)));

				float moveSpeed = Mth.sqrt(forward * forward + strafe * strafe);
				this.setDeltaMovement(this.getDeltaMovement().add(movementDir.scale(moveSpeed)));
			}
		}

		this.setDeltaMovement(this.getDeltaMovement().add(stickingForce));

		double px = this.getX();
		double py = this.getY();
		double pz = this.getZ();
		Vec3 motion = this.getDeltaMovement();

		this.move(MoverType.SELF, motion);

		this.prevAttachedSides = this.attachedSides;
		this.attachedSides = new Vec3(Math.abs(this.getX() - px - motion.x) > 0.001D ? -Math.signum(motion.x) : 0, Math.abs(this.getY() - py - motion.y) > 0.001D ? -Math.signum(motion.y) : 0, Math.abs(this.getZ() - pz - motion.z) > 0.001D ? -Math.signum(motion.z) : 0);

		float slipperiness = 0.91f;

		if (this.onGround()) {
			this.fallDistance = 0;

			BlockPos offsetPos = new BlockPos(blockPosition()).relative(groundDirection.getLeft());
			slipperiness = this.getBlockSlipperiness(offsetPos);
		}

		motion = this.getDeltaMovement();
		Vec3 orthogonalMotion = upVector.scale(upVector.dot(motion));
		Vec3 tangentialMotion = motion.subtract(orthogonalMotion);

		this.setDeltaMovement(tangentialMotion.x * slipperiness + orthogonalMotion.x * 0.98f, tangentialMotion.y * slipperiness + orthogonalMotion.y * 0.98f, tangentialMotion.z * slipperiness + orthogonalMotion.z * 0.98f);

		boolean detachedX = this.attachedSides.x != this.prevAttachedSides.x && Math.abs(this.attachedSides.x) < 0.001D;
		boolean detachedY = this.attachedSides.y != this.prevAttachedSides.y && Math.abs(this.attachedSides.y) < 0.001D;
		boolean detachedZ = this.attachedSides.z != this.prevAttachedSides.z && Math.abs(this.attachedSides.z) < 0.001D;

		if (detachedX || detachedY || detachedZ) {
			float stepHeight = this.maxUpStep();
			this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0);
			boolean prevOnGround = this.onGround();
			boolean prevCollidedHorizontally = this.horizontalCollision;
			boolean prevCollidedVertically = this.verticalCollision;

			//Offset so that AABB is moved above the new surface
			this.move(MoverType.SELF, new Vec3(detachedX ? -this.prevAttachedSides.x * 0.25f : 0, detachedY ? -this.prevAttachedSides.y * 0.25f : 0, detachedZ ? -this.prevAttachedSides.z * 0.25f : 0));

			Vec3 axis = this.prevAttachedSides.normalize();
			Vec3 attachVector = upVector.scale(-1);
			attachVector = attachVector.subtract(axis.scale(axis.dot(attachVector)));

			if (Math.abs(attachVector.x) > Math.abs(attachVector.y) && Math.abs(attachVector.x) > Math.abs(attachVector.z)) {
				attachVector = new Vec3(Math.signum(attachVector.x), 0, 0);
			} else if (Math.abs(attachVector.y) > Math.abs(attachVector.z)) {
				attachVector = new Vec3(0, Math.signum(attachVector.y), 0);
			} else {
				attachVector = new Vec3(0, 0, Math.signum(attachVector.z));
			}

			double attachDst = motion.length() + 0.1f;

			AABB aabb = this.getBoundingBox();
			motion = this.getDeltaMovement();

			//Offset AABB towards new surface until it touches
			for (int i = 0; i < 2 && !this.onGround(); i++) {
				this.move(MoverType.SELF, attachVector.scale(attachDst));
			}

			this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(stepHeight);

			//Attaching failed, fall back to previous position
			if (!this.onGround()) {
				this.setBoundingBox(aabb);
				this.setLocationFromBoundingbox();
				this.setDeltaMovement(motion);
				this.setOnGround(prevOnGround);
				this.horizontalCollision = prevCollidedHorizontally;
				this.verticalCollision = prevCollidedVertically;
			} else {
				this.setDeltaMovement(Vec3.ZERO);
			}
		}

		this.calculateEntityAnimation(true);
	}

	public float getBlockSlipperiness(BlockPos pos) {
		BlockState offsetState = this.level().getBlockState(pos);
		return offsetState.getBlock().getFriction() * 0.91f;
	}

	private float getRelevantMoveFactor(float slipperiness) {
		return this.onGround() ? this.getSpeed() * (0.16277136F / (slipperiness * slipperiness * slipperiness)) : this.getFlyingSpeed();
	}

	@Override
	public void move(MoverType type, Vec3 movement) {
		double py = this.getY();

		super.move(type, movement);

		if (Math.abs(this.getY() - py - movement.y()) > 0.000001D) {
			this.setDeltaMovement(this.getDeltaMovement().x(), 0.0D, this.getDeltaMovement().z());
		}

		this.setOnGround(this.horizontalCollision || this.verticalCollision);
	}

	@Override
	protected BlockPos getOnPos(float yOffset) {
		float verticalOffset = 0.075F;

		int x = Mth.floor(this.getX() + this.attachmentOffsetX - (float) this.attachmentNormal.x * (verticalOffset + 0.2f));
		int y = Mth.floor(this.getY() + this.attachmentOffsetY - (float) this.attachmentNormal.y * (verticalOffset + 0.2f));
		int z = Mth.floor(this.getZ() + this.attachmentOffsetZ - (float) this.attachmentNormal.z * (verticalOffset + 0.2f));
		BlockPos pos = new BlockPos(x, y, z);

		if(this.level().isEmptyBlock(pos) && this.attachmentNormal.y < 0.0f) {
			BlockPos posDown = pos.below();
			BlockState stateDown = this.level().getBlockState(posDown);

			if (stateDown.is(BlockTags.FENCES) || stateDown.is(BlockTags.WALLS) || stateDown.getBlock() instanceof FenceGateBlock) {
				return posDown;
			}
		}

		return pos;
	}

	public void setLocationFromBoundingbox() {
		AABB axisalignedbb = this.getBoundingBox();
		this.setPosRaw((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
	}

	public boolean shouldTrackPathingTargets() {
		return true;
	}

	@Nullable
	public BlockPos getPathingTarget() {
		return this.getEntityData().get(PATHING_TARGET).orElse(null);
	}

	public List<Optional<BlockPos>> getPathingTargets() {
		return this.getEntityData().get(PATHING_TARGETS);
	}

	public void setPathingTarget(int i, @Nullable BlockPos pos) {
		ArrayList<Optional<BlockPos>> copy = new ArrayList<>(this.getEntityData().get(PATHING_TARGETS));
		copy.set(i, Optional.ofNullable(pos));
		this.getEntityData().set(PATHING_TARGETS, copy);
	}

	public List<Direction> getPathingSides() {
		return this.getEntityData().get(PATHING_SIDES);
	}

	public void setPathingSide(int i, Direction side) {
		ArrayList<Direction> copy = new ArrayList<>(this.getEntityData().get(PATHING_SIDES));
		copy.set(i, side);
		this.getEntityData().set(PATHING_SIDES, copy);
	}

	public record PathingTarget(BlockPos pos, Direction side) {
	}
}