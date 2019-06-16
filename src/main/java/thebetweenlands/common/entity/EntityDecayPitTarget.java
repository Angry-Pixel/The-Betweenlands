package thebetweenlands.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityDecayPitTarget extends Entity implements IEntityMultiPartPitTarget {
	public int animationTicks = 0;
	public int animationTicksPrev = 0;
	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public EntityDecayPitTargetPart[] shield_array;
	public EntityDecayPitTargetPart shield_1;
	public EntityDecayPitTargetPart shield_2;
	public EntityDecayPitTargetPart shield_3;
	public EntityDecayPitTargetPart shield_4;
	public EntityDecayPitTargetPart shield_5;
	public EntityDecayPitTargetPart shield_6;
	public EntityDecayPitTargetPart shield_7;
	public EntityDecayPitTargetPart shield_8;
	public EntityDecayPitTargetPart target;
	public EntityDecayPitTargetPart bottom;
	public EntityDecayPitTargetPart chain_1;
	public EntityDecayPitTargetPart chain_2;
	public EntityDecayPitTargetPart chain_3;
	public EntityDecayPitTargetPart chain_4;
	
	private static final DataParameter<Boolean> IS_RAISING = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_MOVING = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_SLOW = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> LENGTH = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.VARINT);
	private static final DataParameter<Float> PROGRESS = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.FLOAT);

	public EntityDecayPitTarget(World world) {
		super(world);
		setSize(5F, 13F);
		shield_array = new EntityDecayPitTargetPart[] {
				shield_1 = new EntityDecayPitTargetPart(this, "part1", 0.75F, 1F),
				shield_2 = new EntityDecayPitTargetPart(this, "part2", 0.75F, 1F),
				shield_3 = new EntityDecayPitTargetPart(this, "part3", 0.75F, 1F),
				shield_4 = new EntityDecayPitTargetPart(this, "part4", 0.75F, 1F),
				shield_5 = new EntityDecayPitTargetPart(this, "part5", 0.75F, 1F),
				shield_6 = new EntityDecayPitTargetPart(this, "part6", 0.75F, 1F),
				shield_7 = new EntityDecayPitTargetPart(this, "part7", 0.75F, 1F),
				shield_8 = new EntityDecayPitTargetPart(this, "part8", 0.75F, 1F),
				target = new EntityDecayPitTargetPart(this, "target", 2F, 1.75F),
				bottom = new EntityDecayPitTargetPart(this, "bottom", 3F, 1F),
				chain_1 = new EntityDecayPitTargetPart(this, "chain_1", 0.625F, 2F),
				chain_2 = new EntityDecayPitTargetPart(this, "chain_2", 0.625F, 2F),
				chain_3 = new EntityDecayPitTargetPart(this, "chain_3", 0.625F, 2F),
				chain_4 = new EntityDecayPitTargetPart(this, "chain_4", 0.625F, 2F)
				};
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_RAISING, false);
		dataManager.register(IS_MOVING, false);
		dataManager.register(IS_SLOW, true);
		dataManager.register(PROGRESS, 2F);
		dataManager.register(LENGTH, 2);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;
		animationTicksChainPrev = animationTicksChain;
		animationTicks += 5;
		if (animationTicks >= 355)
			animationTicks = animationTicksPrev = 0;
		
		for(EntityDecayPitTargetPart part :shield_array) {
			part.prevPosX = part.lastTickPosX = part.posX;
			part.prevPosY = part.lastTickPosY = part.posY;
			part.prevPosZ = part.lastTickPosZ = part.posZ;
		}
		
		setNewShieldHitboxPos(animationTicks, shield_1);
		setNewShieldHitboxPos(animationTicks + 45, shield_2);
		setNewShieldHitboxPos(animationTicks + 90, shield_3);
		setNewShieldHitboxPos(animationTicks + 135, shield_4);
		setNewShieldHitboxPos(animationTicks + 180, shield_5);
		setNewShieldHitboxPos(animationTicks + 225, shield_6);
		setNewShieldHitboxPos(animationTicks + 270, shield_7);
		setNewShieldHitboxPos(animationTicks + 315, shield_8);
		target.setPosition(posX, getEntityBoundingBox().maxY - getProgress() + 1.25, posZ);
		bottom.setPosition(posX, getEntityBoundingBox().maxY - getProgress(), posZ);
		chain_1.setPosition(posX, getEntityBoundingBox().maxY - getProgress(), posZ - 1D);
		chain_2.setPosition(posX, getEntityBoundingBox().maxY - getProgress(), posZ + 1D);
		chain_3.setPosition(posX + 1D, getEntityBoundingBox().maxY - getProgress(), posZ);
		chain_4.setPosition(posX - 1D, getEntityBoundingBox().maxY - getProgress(), posZ);
		target.setPosition(posX, chain_1.getEntityBoundingBox().minY - 1.75D, posZ);
		bottom.setPosition(posX, chain_1.getEntityBoundingBox().minY - 3D, posZ);

		if (isMoving()) {
			if (isSlow())
				animationTicksChain++;
			else
				animationTicksChain += 8;

			if (!isRaising())
				setProgress(getProgress() + 0.0078125F * 8F);

			if (isRaising())
				setProgress(getProgress() - 0.0078125F);

			setHangingLength(chain_1, getProgress());
			setHangingLength(chain_2, getProgress());
			setHangingLength(chain_3, getProgress());
			setHangingLength(chain_4, getProgress());
		}

		if (animationTicksChainPrev >= 128) {
			animationTicksChain = animationTicksChainPrev = 0;
			setMoving(false);
		}

		if (!getEntityWorld().isRemote && getProgress() > 2F && getEntityWorld().getTotalWorldTime() % 60 == 0) // upsy-daisy
			moveUp();
	}

	protected void setHangingLength(EntityDecayPitTargetPart chain, float extended) {
		chain.height = extended;
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(chain.posX - chain.width * 0.5D, getEntityBoundingBox().maxY - getProgress(), chain.posZ - chain.width * 0.5D, chain.posX + chain.width * 0.5D, getEntityBoundingBox().maxY, chain.posZ + chain.width * 0.5D);
		chain.setEntityBoundingBox(axisalignedbb);
		chain.onUpdate();
	}

	protected void setNewShieldHitboxPos(int animationTicks, EntityDecayPitTargetPart shield) {
		double a = Math.toRadians(animationTicks);
		double offSetX = -Math.sin(a) * 2.825D;
		double offSetZ = Math.cos(a) * 2.825D;
		float wobble = 0F;
		if (shield == shield_1 || shield == shield_3 || shield == shield_5 || shield == shield_7)
			wobble = MathHelper.sin((float) ((animationTicks) * 0.07F)) * 0.45F;
		else
			wobble = MathHelper.cos((float) ((animationTicks) * 0.07F)) * 0.7F;
		shield.setPosition(posX + offSetX, target.posY + target.height / 2.0D - shield.height / 2.0D + wobble, posZ + offSetZ);
		shield.onUpdate();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Nullable
	public Entity[] getParts() {
		return shield_array;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	public boolean attackEntityFromPart(EntityDecayPitTargetPart part, DamageSource source, float damage) {
		if (!getEntityWorld().isRemote) {
			//if (part != target && part != bottom && part != chain_1 && part != chain_2 && part != chain_3 && part != chain_4)
			//	moveUp();

			if (part == target)
				moveDown();

			if (part == chain_1 || part == chain_2 || part == chain_3 || part == chain_4)
				if (source instanceof EntityDamageSourceIndirect) // may want to remove this line so it 'dinks' on all damage attempts
					getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, 3F);
		}
		return true;
	}

	private void moveUp() {
		if (getProgress() > 2) {
			for (EntityDecayPitChain chain : getChains()) {
				chain.setRaising(false);
				chain.setMoving(true);
				chain.setSlow(true);

			}
			setRaising(true);
			setMoving(true);
			setSlow(true);
		}
	}

	private void moveDown() {
		if (getProgress() < 9) {
			for (EntityDecayPitChain chain : getChains()) {
				chain.setRaising(true);
				chain.setMoving(true);
				chain.setSlow(false);
			}
			setRaising(false);
			setMoving(true);
			setSlow(false);
		}
	}

	public List<EntityDecayPitChain> getChains() {
		return getWorld().<EntityDecayPitChain>getEntitiesWithinAABB(EntityDecayPitChain.class, getEntityBoundingBox().grow(10D, 0D, 10D));
    }
	
	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (PROGRESS.equals(key))
			setLength(MathHelper.floor(getProgress()));
		super.notifyDataManagerChange(key);
	}

	public void setLength(int length) {
		dataManager.set(LENGTH, length);
	}

	public int getLength() {
		return dataManager.get(LENGTH);
	}
	
	public void setProgress(float progress) {
		dataManager.set(PROGRESS, progress);
	}

	public float getProgress() {
		return dataManager.get(PROGRESS);
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

	public void setSlow(boolean slow) {
		dataManager.set(IS_SLOW, slow);
	}

	public boolean isSlow() {
		return dataManager.get(IS_SLOW);
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setLength(nbt.getInteger("length"));
		setProgress(nbt.getFloat("progress"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("length", getLength());
		nbt.setFloat("progress", getProgress());
	}
}