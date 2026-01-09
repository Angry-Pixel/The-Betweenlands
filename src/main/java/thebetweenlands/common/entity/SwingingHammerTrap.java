package thebetweenlands.common.entity;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.common.entity.multipart.SwingngTrapMultipart;

public class SwingingHammerTrap extends Entity {

	private static final EntityDataAccessor<Boolean> SWINGING = SynchedEntityData.defineId(SwingingHammerTrap.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SWING_TICKS = SynchedEntityData.defineId(SwingingHammerTrap.class, EntityDataSerializers.INT);
	public final SwingngTrapMultipart[] impactBox;
	public int vel = 0;
	public SwingingHammerTrap(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.impactBox = new SwingngTrapMultipart[] {new SwingngTrapMultipart(this, 0.875F, 0.5F)};
		this.setId(ENTITY_COUNTER.getAndAdd(this.impactBox.length + 1) + 1);
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < this.impactBox.length; i++)
			this.impactBox[i].setId(id + i + 1);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(SWINGING, true);
		builder.define(SWING_TICKS, -70);
	}

	public int getSwingTicks() {
	return this.getEntityData().get(SWING_TICKS);
	}

	public void setSwingTicks(int amount) {
		this.getEntityData().set(SWING_TICKS, amount);
	}

	public boolean isSwinging() {
	return this.getEntityData().get(SWINGING);
	}

	public void setSwinging(boolean state) {
		this.getEntityData().set(SWINGING, state);
	}

	@Override
	public void tick() {
		impactBox[0].setOldPosAndRot();
		super.tick();
		if (!level().isClientSide()) {
			if (isSwinging()) {
				if (getSwingTicks() > 0)
					vel-=2;
				else
					vel+=2;
				if (getSwingTicks() >= 70)
					setSwinging(false);
				else
					setSwingTicks(getSwingTicks() + 1 + vel);
			} else {
				if (getSwingTicks() < 0)
					vel+=2;
				else
					vel-=2;
				if (getSwingTicks() <= -70)
					setSwinging(true);
				else
					setSwingTicks(getSwingTicks() - 1 + vel);
			}
		}
		setImpactBox();
	}
	
	@Override
	public boolean isMultipartEntity() {
		return true;
	}
	
	@Override
	public PartEntity<?>[] getParts() {
		return this.impactBox;
	}

	private void setImpactBox() {
		float swingAngle = convertDegtoRad(getSwingTicks());
		double swingSin = Math.sin(swingAngle) * -1.375D;
		Direction facing = this.getDirection();
		double posX = this.getX() + ((facing == Direction.NORTH || facing == Direction.SOUTH) ?  0 : facing == Direction.WEST ? swingSin : -swingSin);
		double posY = this.getY() + Math.cos(swingAngle) * -1.375D;
		double posZ = this.getZ() + ((facing == Direction.EAST || facing == Direction.WEST) ?  0 : facing == Direction.SOUTH ? swingSin : -swingSin);
		impactBox[0].absMoveTo(posX, posY, posZ);		
	}

	public float convertDegtoRad(float angle) {
		return angle * Mth.DEG_TO_RAD;
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return this.getBoundingBox().inflate(16.0D, 16.0D, 16.0D);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
	}
}
