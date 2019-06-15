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

public class EntityDecayPitChain extends Entity {
	public int animationTicks = 0;
	public int animationTicksPrev = 0;
	private static final DataParameter<Boolean> IS_RAISING = EntityDataManager.createKey(EntityDecayPitChain.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_MOVING = EntityDataManager.createKey(EntityDecayPitChain.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_SLOW = EntityDataManager.createKey(EntityDecayPitChain.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> FACING = EntityDataManager.createKey(EntityDecayPitChain.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LENGTH = EntityDataManager.createKey(EntityDecayPitChain.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_HANGING = EntityDataManager.createKey(EntityDecayPitChain.class, DataSerializers.BOOLEAN);

	public EntityDecayPitChain(World world) {
		super(world);
		setSize(0.625F, 1F);
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_RAISING, false);
		dataManager.register(IS_MOVING, false);
		dataManager.register(IS_HANGING, false);
		dataManager.register(IS_SLOW, true);
		dataManager.register(FACING, 0);
		dataManager.register(LENGTH, 1);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;
		if (isMoving()) {
			if(isSlow())
				animationTicks++;
			else
				animationTicks += 8;
		// meh bit janky this here is the cuase of all the desync
			if (animationTicksPrev < (isSlow() ? 127 : 120)) {
				if (isHanging()) {
					if (!isRaising())
						if (getLength() < 7)
							setHangingLength(getLength(), (float) animationTicks * 0.0078125F);

					if (isRaising())
						if (getLength() > 2)
							setHangingLength(getLength(), -(float) animationTicks * 0.0078125F);

				}
			}
		
		if (animationTicksPrev >= 128) {
			animationTicks = animationTicksPrev = 0;
			setMoving(false);
			if (isHanging()) {
				if (!isRaising()) {
					if (getLength() < 7) {
						setPositionAndUpdate(posX, posY - 1D, posZ);
						setLength(getLength() + 1);
					}
				}
				if (isRaising()) {
					if (getLength() > 2) {
						setPositionAndUpdate(posX, posY + 1D, posZ);
						setLength(getLength() - 1);
					}
				}
			}
		}
		}
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
	/*	if (!player.isSneaking() && !isMoving() && getLength() > 1) {
			setRaising(true);
			setMoving(true);
			return true;
		}
		if (player.isSneaking() && !isMoving()) {
			setRaising(false);
			setMoving(true);
			return true;
		}
		*/
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
			AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.posX - this.width * 0.5D, this.posY, this.posZ - this.width * 0.5D, this.posX + this.width * 0.5D, this.posY + this.height, this.posZ + this.width * 0.5D);
			setEntityBoundingBox(axisalignedbb);
		}
	}

	protected void setHangingLength(float height, float extended) {
		if (this.height != height + extended) {
			this.height = height;
			AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.posX - this.width * 0.5D, this.posY, this.posZ - this.width * 0.5D, this.posX + this.width * 0.5D, this.posY + this.height, this.posZ + this.width * 0.5D);
			setEntityBoundingBox(axisalignedbb.grow(0, extended * 0.5D, 0).offset(0, -extended * 0.5D, 0));
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

	public void setSlow(boolean slow) {
		dataManager.set(IS_SLOW, slow);
	}

	public boolean isSlow() {
		return dataManager.get(IS_SLOW);
	}

	public boolean isMoving() {
		return dataManager.get(IS_MOVING);
	}

	public void setFacing(int direction) {
		// S = 0, W = 1, N = 2, E = 3
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
		if (source instanceof EntityDamageSourceIndirect) // may want to remove this line so it 'dinks' on all damage attempts
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