package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityDecayPitChainOuter extends Entity {
	public int animationTicks = 0;
	public int animationTicksPrev = 0;
	private static final DataParameter<Boolean> IS_RAISING = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_MOVING = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> FACING = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LENGTH = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_HANGING = EntityDataManager.createKey(EntityDecayPitChainOuter.class, DataSerializers.BOOLEAN);

	public EntityDecayPitChainOuter(World world) {
		super(world);
		setSize(0.625F, 1F);
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_RAISING, false);
		dataManager.register(IS_MOVING, false);
		dataManager.register(IS_HANGING, false);
		dataManager.register(FACING, 0);
		dataManager.register(LENGTH, 1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;
		if (isMoving())
			animationTicks++;
		/*  meh bit janky 
			if (animationTicksPrev < 16) {
				if (isHanging()) {
					if (!isRaising())
						if (getLength() < 7)
							setHangingLength(getLength(), (float) animationTicks * 0.003125F);

					if (isRaising())
						if (getLength() > 1)
							setHangingLength(getLength(), -(float) animationTicks * 0.003125F);

				}
			}
		*/
		if (animationTicksPrev >= 16) {
			animationTicks = animationTicksPrev = 0;
			setMoving(false);
			if (isHanging()) {
				if (!isRaising()) {
					if (getLength() < 7) {
						setLength(getLength() + 1);
						setPositionAndUpdate(posX, posY - 1D, posZ);
					}
				}
				if (isRaising()) {
					if (getLength() > 1) {
						setLength(getLength() - 1);
						setPositionAndUpdate(posX, posY + 1D, posZ);
					}
				}
			}

		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (!player.isSneaking() && !isMoving() && getLength() > 1) {
			setRaising(true);
			setMoving(true);
			return true;
		}
		if (player.isSneaking() && !isMoving()) {
			setRaising(false);
			setMoving(true);
			return true;
		}
		return false;
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (LENGTH.equals(key))
			setNewLength((float)getLength());
		super.notifyDataManagerChange(key);
	}

	protected void setNewLength(float height) {
		if (this.height != height) {
			this.height = height;
			AxisAlignedBB axisalignedbb = getEntityBoundingBox();
			setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + 0.625D, axisalignedbb.minY + (double) height, axisalignedbb.minZ + 0.625D));
		}
	}
	
	/* meh bit janky 
	protected void setHangingLength(float height, float extended) {
		if (this.height != height + extended) {
			this.height = height;
			AxisAlignedBB axisalignedbb = getEntityBoundingBox();
			setEntityBoundingBox(axisalignedbb.grow(0, extended, 0).offset(0, -extended, 0));
		}
	}
	*/
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

	public void setLength(int length) {
		dataManager.set(LENGTH, length);
	}

	public int getLength() {
		return dataManager.get(LENGTH);
	}

	public void setHanging(boolean hanging) {
		dataManager.set(IS_HANGING, hanging);
	}

	public boolean isHanging() {
		return dataManager.get(IS_HANGING);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
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
		if (source instanceof EntityDamageSourceIndirect)
			getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, 3F);
		return false;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setLength(nbt.getInteger("length"));
		setHanging(nbt.getBoolean("hanging"));
		setFacing(nbt.getInteger("facing"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("length", getLength());
		nbt.setBoolean("hanging", isHanging());
		nbt.setInteger("facing", getFacingRender());
	}

}