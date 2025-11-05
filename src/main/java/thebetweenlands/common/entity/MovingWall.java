package thebetweenlands.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import thebetweenlands.api.entity.ScreenShaker;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.audio.MovingWallSoundInstance;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;

import java.util.List;

public class MovingWall extends Entity implements ScreenShaker, IEntityWithComplexSpawn {

	private static final EntityDataAccessor<Boolean> IS_NEW_SPAWN = SynchedEntityData.defineId(MovingWall.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> HOLD_STILL = SynchedEntityData.defineId(MovingWall.class, EntityDataSerializers.BOOLEAN);

	private int holdCount;
	public boolean playSlideSound = true;
	private int shake_timer;
	private boolean shaking = false;
	private int shakingTimerMax = 20;
	private int impacts;

	protected float speed = 0.05F;
	protected boolean isBlockAligned = true;
	protected boolean isDungeonWall = false;

	public MovingWall(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	public MovingWall(Level level, boolean isDungeonWall) {
		this(EntityRegistry.MOVING_WALL.get(), level);
		this.isDungeonWall = isDungeonWall;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(IS_NEW_SPAWN, true);
		builder.define(HOLD_STILL, false);
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide()) {
			if (this.tickCount == 1 && this.isNewSpawn())
				this.checkSpawnArea();

			if (this.tickCount == 2) //needs to have moved 1 tick for direction to work
				this.doJankSafetyCheck();

			if (this.isHoldingStill()) {
				if (this.holdCount-- <= 0) {
					this.setHoldStill(false);
					this.holdCount = 20;
				}
			}
		}

		this.calculateAllCollisions(this.getX(), this.getY() + 0.5D, this.getZ());
		this.calculateAllCollisions(this.getX(), this.getY() + 1.5D, this.getZ());
		this.calculateAllCollisions(this.getX(), this.getY() + 2.5D, this.getZ());

		if (this.getDirection().getAxis() == Direction.Axis.Z) {
			this.calculateAllCollisions(this.getX() - 1D, this.getY() + 0.5D, this.getZ());
			this.calculateAllCollisions(this.getX() - 1D, this.getY() + 1.5D, this.getZ());
			this.calculateAllCollisions(this.getX() - 1D, this.getY() + 2.5D, this.getZ());
			this.calculateAllCollisions(this.getX() + 1D, this.getY() + 0.5D, this.getZ());
			this.calculateAllCollisions(this.getX() + 1D, this.getY() + 1.5D, this.getZ());
			this.calculateAllCollisions(this.getX() + 1D, this.getY() + 2.5D, this.getZ());
		} else {
			this.calculateAllCollisions(this.getX(), this.getY() + 0.5D, this.getZ() - 1D);
			this.calculateAllCollisions(this.getX(), this.getY() + 1.5D, this.getZ() - 1D);
			this.calculateAllCollisions(this.getX(), this.getY() + 2.5D, this.getZ() - 1D);
			this.calculateAllCollisions(this.getX(), this.getY() + 0.5D, this.getZ() + 1D);
			this.calculateAllCollisions(this.getX(), this.getY() + 1.5D, this.getZ() + 1D);
			this.calculateAllCollisions(this.getX(), this.getY() + 2.5D, this.getZ() + 1D);
		}
		if (!this.level().isClientSide() && this.impacts > 0) {
			if (this.impacts-- > 2) {
				this.discard();
				return;
			}
		}

		Direction heading = Direction.getNearest(this.getDeltaMovement());

		if (this.isBlockAligned) {
			this.setPos(
				heading.getAxis() != Direction.Axis.X ? Mth.floor(this.getX()) + 0.5D : this.getX(),
				this.getY(),
				heading.getAxis() != Direction.Axis.Z ? Mth.floor(this.getZ()) + 0.5D : this.getZ());
		}

		if (!this.isHoldingStill()) {
			this.setDeltaMovement(heading.getStepX() * this.speed, 0.0D, heading.getStepZ() * this.speed);
			this.setPos(this.position().add(this.getDeltaMovement()));
			this.pushEntitiesAway();
		}

		this.setXRot(0.0F);
		this.setYRot((float) (Mth.atan2(-this.getDeltaMovement().x(), this.getDeltaMovement().z()) * Mth.RAD_TO_DEG));

		if (this.isShaking())
			this.shake(10);

		if (this.level().isClientSide()) {
			if (this.isHoldingStill())
				if (!this.playSlideSound)
					this.playSlideSound = true;

			if (!this.isHoldingStill())
				if (this.playSlideSound) {
					BetweenlandsClient.playLocalSound(new MovingWallSoundInstance(this));
					this.playSlideSound = false;
				}
		}

		//Remove wall if it is a dungeon wall and the dungeon is defeated
		if (!this.level().isClientSide() && this.isDungeonWall) {
			var storage = BetweenlandsWorldStorage.getForLevel(this.level());
			if (storage.isPresent()) {
				List<LocationSludgeWormDungeon> dungeons = storage.get().getLocalStorageHandler().getLocalStorages(LocationSludgeWormDungeon.class, this.getBoundingBox(), l -> true);

				if (dungeons.isEmpty()) {
					this.discard();
				} else {
					for (LocationSludgeWormDungeon dungeon : dungeons) {
						if (dungeon.isDefeated()) {
							this.discard();
							break;
						}
					}
				}
			}
		}
	}

	protected void pushEntitiesAway() {
		boolean collision = false;

		double maxReverseX = -1;
		double maxReverseZ = -1;

		AABB collisionAABB = this.getBoundingBox();
		if (collisionAABB != null) {
			List<Entity> entities = this.level().getEntities(this, collisionAABB);

			for (Entity entity : entities) {
				if (entity.isPickable()) {
					if (!entity.isPushable() && !(entity instanceof MovingWall)) {
						collision = true;
					} else {
						AABB entityAABB = entity.getBoundingBox();
						boolean squished = false;
						double dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);
						double dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);

						if (Math.abs(dz) < Math.abs(dx)) {
							entity.move(MoverType.PISTON, new Vec3(0, 0, (dz - 0.005D) * Math.signum(this.getZ() - entity.getZ())));
							entityAABB = entity.getBoundingBox();
							dz = Math.max(collisionAABB.minZ - entityAABB.maxZ, entityAABB.minZ - collisionAABB.maxZ);

							if (-dz > 0.025D) {
								squished = true;
								maxReverseZ = Math.max(-dz, maxReverseZ);
							}
						} else {
							entity.move(MoverType.SHULKER, new Vec3((dx - 0.005D) * Math.signum(this.getX() - entity.getX()), 0, 0));
							entityAABB = entity.getBoundingBox();
							dx = Math.max(collisionAABB.minX - entityAABB.maxX, entityAABB.minX - collisionAABB.maxX);

							if (-dx > 0.025D) {
								squished = true;
								maxReverseX = Math.max(-dx, maxReverseX);
							}
						}

						//Move slightly towards ground to update onGround state etc.
						entity.move(MoverType.PISTON, new Vec3(0, -0.01D, 0));

						if (squished) {
							collision = true;

							if (!this.level().isClientSide()) {
								entity.hurt(this.damageSources().inWall(), 10F);
								this.setHoldStill(true);
								this.holdCount = 20;
								this.playSound(SoundRegistry.WALL_SLAM.get(), 0.5F, 0.75F);
							}
						}
					}
				}
			}
		}

		if (collision) {
			this.setPos(
				maxReverseX > 0 ? this.getX() - (maxReverseX + 0.05D) * Math.signum(this.getDeltaMovement().x()) : this.getX(),
				this.getY(),
				maxReverseZ > 0 ? this.getZ() - (maxReverseZ + 0.05D) * Math.signum(this.getDeltaMovement().z()) : this.getZ());

			this.shaking = true;
			this.shake_timer = 0;

			if (!this.level().isClientSide()) {
				this.setDeltaMovement(this.getDeltaMovement().multiply(-1.0D, 0.0D, -1.0D));
				this.hurtMarked = true;
			}
		}
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	private void checkSpawnArea() {
		BlockPos posEntity = this.blockPosition();
		Iterable<BlockPos> blocks = BlockPos.betweenClosed(posEntity.offset(-1, 0, -1), posEntity.offset(1, 2, 1));
		for (BlockPos pos : blocks) {
			if (this.isUnbreakableBlock(pos)) {
				this.discard();
			}
		}
		if (this.isUnbreakableBlock(posEntity.offset(2, 0, 0)) && this.isUnbreakableBlock(posEntity.offset(-2, 0, 0))) {
			this.setDeltaMovement(0.0D, 0.0D, this.speed);
			this.setIsNewSpawn(false);
		} else if (this.isUnbreakableBlock(posEntity.offset(0, 0, 2)) && this.isUnbreakableBlock(posEntity.offset(0, 0, -2))) {
			this.setDeltaMovement(this.speed, 0.0D, 0.0D);
			this.setIsNewSpawn(false);
		} else {
			this.discard();
		}
	}

	public boolean isUnbreakableBlock(BlockPos pos) {
		BlockState state = this.level().getBlockState(pos);
		return state.is(BLBlockTagProvider.MOVING_WALL_UNBREAKABLE) || state.getDestroySpeed(this.level(), pos) < 0.0F || !state.canEntityDestroy(this.level(), pos, this);
	}

	private void doJankSafetyCheck() {
		Direction facing = this.getDirection();
		Vec3 posVec = this.position();
		Vec3 moveVec = this.position().relative(facing, 28); //should be long enough
		HitResult raytraceresult = this.level().clip(new ClipContext(posVec, moveVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (raytraceresult.getType() != HitResult.Type.MISS) {
			moveVec = raytraceresult.getLocation();
			AABB rayBox = new AABB(this.position(), moveVec);
			List<Entity> list = this.level().getEntities(this, rayBox);
			for (Entity entity : list) {
				if (entity instanceof MovingWall)
					entity.discard();
			}
		}
	}

	public void calculateAllCollisions(double posX, double posY, double posZ) {
		Vec3 posVec = new Vec3(posX, posY, posZ);
		Vec3 moveVec = posVec.add(this.getDeltaMovement().scale(12.0D)); //adjust multiplier higher for slower speeds

		HitResult hitresult = this.level().clip(new ClipContext(posVec, moveVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		if (hitresult.getType() != HitResult.Type.MISS) {
			moveVec = hitresult.getLocation();
		}

		HitResult hitresult1 = ProjectileUtil.getEntityHitResult(this.level(), this, posVec, moveVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), Entity::isAttackable, 0.0F);
		if (hitresult1 != null) {
			hitresult = hitresult1;
		}

		if (hitresult.getType() != HitResult.Type.MISS) {
			this.onImpact(hitresult);
		}
	}

	protected void onImpact(HitResult result) {
		if (result.getType() == HitResult.Type.BLOCK) {
			BlockHitResult hitResult = (BlockHitResult) result;
			if (this.isUnbreakableBlock(hitResult.getBlockPos())) {
				if (hitResult.getDirection().getAxis() != Direction.Axis.Y) {
					this.shaking = true;
					this.shake_timer = 0;
					this.setDeltaMovement(this.getDeltaMovement().multiply(-Mth.abs(hitResult.getDirection().getStepX()), 0.0D, -Mth.abs(hitResult.getDirection().getStepZ())));
					this.hurtMarked = true;
					if (!this.level().isClientSide()) {
						this.setHoldStill(true);
						this.holdCount = 20;
						this.playSound(SoundRegistry.WALL_SLAM.get(), 0.5F, 0.75F);
					}
				}
				this.impacts += 2;
			} else {
				this.level().destroyBlock(hitResult.getBlockPos(), false);
			}
		}
	}

	@Override
	public void move(MoverType type, Vec3 movement) {
		//No regular moving
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void push(double x, double y, double z) {
		if (this.isHoldingStill()) {
			this.setDeltaMovement(Vec3.ZERO);
		}
	}

	@Override
	protected AABB makeBoundingBox() {
		return new AABB(this.position().subtract(0.5D, 0.0D, 0.5D), this.position().add(0.5D, 3.0D, 0.5D)).inflate(Mth.abs(this.getDirection().getStepZ()), 0.0D, Mth.abs(this.getDirection().getStepX()));
	}

	public void setIsNewSpawn(boolean new_spawn) {
		this.getEntityData().set(IS_NEW_SPAWN, new_spawn);
	}

	public boolean isNewSpawn() {
		return this.getEntityData().get(IS_NEW_SPAWN);
	}

	private void setHoldStill(boolean hold_still) {
		this.getEntityData().set(HOLD_STILL, hold_still);
	}

	public boolean isHoldingStill() {
		return this.getEntityData().get(HOLD_STILL);
	}

	public boolean isMoving() {
		return !isHoldingStill();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.setIsNewSpawn(tag.getBoolean("new_spawn"));
		this.isBlockAligned = tag.getBoolean("block_aligned");
		this.isDungeonWall = tag.getBoolean("dungeon_wall");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putBoolean("new_spawn", isNewSpawn());
		tag.putBoolean("block_aligned", this.isBlockAligned);
		tag.putBoolean("dungeon_wall", this.isDungeonWall);
	}

	public void shake(int shakeTimerMax) {
		this.shakingTimerMax = shakeTimerMax;
		if (this.shake_timer == 0) {
			this.shaking = true;
			this.shake_timer = 1;
		}
		if (this.shake_timer > 0)
			this.shake_timer++;

		this.shaking = this.shake_timer < this.shakingTimerMax;
	}

	@Override
	public float getShakeIntensity(Entity viewer) {
		if (isShaking()) {
			double dist = this.distanceTo(viewer);
			float shakeMult = (float) (1.0F - dist / 16.0F);
			if (dist >= 16.0F) {
				return 0.0F;
			}
			return (Mth.sin((1.0F / this.shakingTimerMax * this.shake_timer) * Mth.PI) + 0.1F) * 0.075F * shakeMult;
		} else {
			return 0.0F;
		}
	}

	public boolean isShaking() {
		return this.shaking;
	}

	@Override
	public boolean ignoreExplosion(Explosion explosion) {
		return true;
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		buffer.writeBoolean(this.isBlockAligned);
		buffer.writeBoolean(this.isDungeonWall);
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buffer) {
		this.isBlockAligned = buffer.readBoolean();
		this.isDungeonWall = buffer.readBoolean();
	}
}
