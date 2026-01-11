package thebetweenlands.common.entity;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MovingShrinkform extends Entity {

	private static final EntityDataAccessor<Boolean> HOLD_STILL = SynchedEntityData.defineId(MovingShrinkform.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<BlockPos> ORIGIN_POSITION = SynchedEntityData.defineId(MovingShrinkform.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Integer> MOVE_TICKS = SynchedEntityData.defineId(MovingShrinkform.class, EntityDataSerializers.INT);

	private int holdCount;
	protected float speed = 0.05F;

	public MovingShrinkform(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(HOLD_STILL, false);
		builder.define(ORIGIN_POSITION, blockPosition());
		builder.define(MOVE_TICKS, 0);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide()) {
			setOldPosAndRot();
			if (getMoveTicks() >= 360)
				setMoveTicks(0);
			else
				setMoveTicks(getMoveTicks() + 2);

			moveAsVerticalCircle(); // first test
			
			// TODO may need to stop it at some point
			//if (isHoldingStill()) {
			//	if (holdCount-- <= 0) {
			//		setHoldStill(false);
			//		holdCount = 20;
			//	}
			//}
		}
		
		checkSurfaceCollisions();
	}

	private void moveAsVerticalCircle() {
		float angle = convertDegtoRad(getMoveTicks());
		double sin = Math.sin(angle) * -3D;
		Direction facing = getDirection();
		double posX = getOriginBlockPos().getX() + 0.5D + ((facing == Direction.NORTH || facing == Direction.SOUTH) ?  0 : facing == Direction.WEST ? sin : -sin);
		double posY = getOriginBlockPos().getY() + 0.5D + Math.cos(angle) * -3D;
		double posZ = getOriginBlockPos().getZ() + 0.5D + ((facing == Direction.EAST || facing == Direction.WEST) ?  0 : facing == Direction.SOUTH ? sin : -sin);

		double deltaX = posX - this.getX();
		double deltaY = posY - this.getY();
        double deltaZ = posZ - this.getZ();

        this.setDeltaMovement(deltaX, deltaY, deltaZ); 
        this.move(MoverType.SELF, this.getDeltaMovement());
        hurtMarked = true;
	}

	public float convertDegtoRad(float angle) {
		return angle * Mth.DEG_TO_RAD;
	}

	private void checkSurfaceCollisions() {
		for (Entity entity : this.getEntityAbove()) {
			if (entity != null && !(entity instanceof MovingShrinkform)) {
				entity.setOnGround(true);
				if (entity instanceof Player player && player.getAbilities().flying)
					return;
				if (entity.getBoundingBox().minY < getBoundingBox().maxY)
					entity.setDeltaMovement(0.0D, 0.1D, 0.0D);
				entity.setDeltaMovement(getDeltaMovement());
				this.checkJumpOnTopOfAABB(entity);
			}
		}
	}

	public void checkJumpOnTopOfAABB(Entity entity) {
		if (entity.level().isClientSide() && entity instanceof Player player) {
			boolean jump = Minecraft.getInstance().options.keyJump.isDown();
			if (jump)
				player.jumpFromGround();
		}
	}

	public List<Entity> getEntityAbove() {
		return level().getEntitiesOfClass(Entity.class, this.getBoundingBox().expandTowards(0D, 0.0625D, 0D), EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.NO_SPECTATORS));
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void push(double x, double y, double z) {
		if (isHoldingStill()) {
			setDeltaMovement(Vec3.ZERO);
		}
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return getBoundingBox().inflate(16.0D, 16.0D, 16.0D);
	}

	public void setHoldStill(boolean hold_still) {
		getEntityData().set(HOLD_STILL, hold_still);
	}

	public boolean isHoldingStill() {
		return getEntityData().get(HOLD_STILL);
	}

	public void setOriginBlockPos(BlockPos pos) {
		getEntityData().set(ORIGIN_POSITION, pos);
		
	}

	public BlockPos getOriginBlockPos() {
		return getEntityData().get(ORIGIN_POSITION);
	}

	public void setMoveTicks(int amount) {
		getEntityData().set(MOVE_TICKS, amount);
	}

	public int getMoveTicks() {
		return getEntityData().get(MOVE_TICKS);
	}

	public boolean isMoving() {
		return !isHoldingStill();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		setOriginBlockPos(NbtUtils.readBlockPos(tag, "originPos").orElse(blockPosition()));
		setMoveTicks(tag.getInt("move_ticks"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.put("originPos", NbtUtils.writeBlockPos(getOriginBlockPos()));
		tag.putInt("move_ticks", getMoveTicks());
	}

	@Override
	public boolean ignoreExplosion(Explosion explosion) {
		return true;
	}

}
