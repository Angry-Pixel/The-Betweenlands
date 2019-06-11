package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityDecayPitChainOuter extends Entity {
	public int animationTicks = 0;
	public int animationTicksPrev = 0;
	private static final DataParameter<Boolean> IS_RAISING = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_MOVING = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> FACING = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.VARINT);

	public EntityDecayPitChainOuter(World world) {
		super(world);
		setSize(1F, 4F);
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_RAISING, false);
		dataManager.register(IS_MOVING, false);
		dataManager.register(FACING, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;
		if (isMoving())
			animationTicks++;
		if (animationTicksPrev >= 16) {
			animationTicks = animationTicksPrev = 0;
			setMoving(false);
		}
	}

	public void setRaising(boolean raising) {
		dataManager.set(IS_RAISING, raising);
	}

	public boolean isRaising() {
		return dataManager.get(IS_RAISING);
	}

	public void setMoving(boolean moving) {
		dataManager.set(IS_MOVING, moving);
	}

	public boolean isMoving() {
		return dataManager.get(IS_MOVING);
	}

	public void setFacing(int direction) {
		dataManager.set(FACING, direction);
	}

	public int getFacingRender() {
		return dataManager.get(FACING);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
	}

}