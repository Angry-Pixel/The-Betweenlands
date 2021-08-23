package thebetweenlands.common.entity.mobs;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.PlayerUtil;

public class EntityRockSnotTendril extends Entity implements IEntityAdditionalSpawnData {

    public EntityRockSnot parent;
	private static final DataParameter<Boolean> IS_EXTENDING = EntityDataManager.createKey(EntityRockSnotTendril.class, DataSerializers.BOOLEAN);

	public EntityRockSnotTendril(World world) {
		super(world);
		setSize(0.25F, 0.25F);
		this.parent = null;
	}

	public EntityRockSnotTendril(EntityRockSnot parent) {
        super(parent.getWorld());
        setSize(0.25F, 0.25F);
        this.parent = parent;
        ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_EXTENDING, false);
	}

	public boolean getExtending() {
		return dataManager.get(IS_EXTENDING);
	}

	public void setExtending(boolean extending) {
		dataManager.set(IS_EXTENDING, extending);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (IS_EXTENDING.equals(key))
			setExtending(getExtending());
		super.notifyDataManagerChange(key);
	}

	@Override
	public void onUpdate() {
		if (!this.getEntityWorld().isRemote)
			if (getParentEntity() == null || getParentEntity().isDead)
				setDead();

		checkCollision();

		if (parent != null && !getExtending()) {
				returnToParent();
			if (getEntityBoundingBox().intersects(parent.getEntityBoundingBox())) {
				if (posX != parent.posX || posZ != parent.posZ)
					setPosition(parent.posX, parent.posY + parent.height * 0.5D, parent.posZ);
				motionX = 0D;
				motionY = 0D;
				motionZ = 0D;

				if (!getEntityWorld().isRemote) {
					if (isBeingRidden() && !parent.isBeingRidden()) {
						Entity entity = getPassengers().get(0);
						entity.startRiding(parent, true);
					}
					setDead();
					parent.setTendrilCount(parent.getTendrilCount() - 1);
				}
			}
		}

		if (parent != null && (!isBeingRidden() && ticksExisted > 20) || parent != null && collidedVertically) {
			if (!getEntityWorld().isRemote) {
				if (getExtending())
					setExtending(false);
			}
		}

		if(getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime()%5 == 0)
			spawnDrips();

		move(MoverType.SELF, motionX, motionY, motionZ);
		motionX *= 1D;
		motionY *= 1D;
		motionZ *= 1D;
		super.onUpdate();
	}

	public void moveToTarget(double targetX, double targetY, double targetZ, float velocity) {
		float distSq = MathHelper.sqrt(targetX * targetX + targetY * targetY + targetZ * targetZ);
		targetX = targetX / (double) distSq;
		targetY = targetY / (double) distSq;
		targetZ = targetZ / (double) distSq;
		targetX = targetX * (double) velocity;
		targetY = targetY * (double) velocity;
		targetZ = targetZ * (double) velocity;
		motionX = targetX;
		motionY = targetY;
		motionZ = targetZ;
		float angle = MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
		rotationYaw = (float) (MathHelper.atan2(targetX, targetZ) * (180D / Math.PI));
		rotationPitch = (float) (MathHelper.atan2(targetY, (double) angle) * (180D / Math.PI));
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
	}

	protected Entity checkCollision() {
		if (parent != null) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox());
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null) {
					if (entity instanceof EntityLivingBase && !(entity instanceof EntityRockSnot) && !(entity instanceof EntityRockSnotTendril) && !(entity instanceof EntityLurker) && entity.isNonBoss()) {
						if (entity instanceof EntityPlayer && parent.getPlacedByPlayer() || parent.isBeingRidden())
							return null;
						if (!isBeingRidden()) {
							if (!getEntityWorld().isRemote) {
								entity.startRiding(this, true);
								getEntityWorld().playSound(null, getPosition(), SoundRegistry.ROCK_SNOT_ATTACH, SoundCategory.HOSTILE, 1F, 1F);
								if (getExtending())
									setExtending(false);
							}
							returnToParent();
						}
					}
				}
			}
		}
		return null;
	}

	public void returnToParent() {
		double targetX = parent.posX - posX;
		double targetY = parent.posY + parent.height * 0.5D - posY + height * 0.5D;
		double targetZ = parent.posZ - posZ;
		moveToTarget(targetX, targetY, targetZ, 0.25F);
	}

	@Override
	public void updatePassenger(Entity entity) {
		PlayerUtil.resetFloating(entity);
		if (entity instanceof EntityLivingBase)
			entity.setPosition(posX, posY + height, posZ);
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
	}

	public EntityRockSnot getParentEntity() {
		return parent;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		if (getParentEntity() != null)
			buffer.writeInt(getParentEntity().getEntityId());
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		if (buffer.isReadable()) {
			int parentEntityID = buffer.readInt();
			EntityRockSnot parentEntityIn = (EntityRockSnot) world.getEntityByID(parentEntityID);
			this.parent = parentEntityIn;
		}
	}

	@Override	
    public boolean canBeCollidedWith() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().grow(10D);
    }

	@SideOnly(Side.CLIENT)
	public void spawnDrips() {
		double x = posX+ (double) rand.nextFloat() * 0.25F;
		double y = posY;
		double z = posZ+ (double) rand.nextFloat() * 0.25F;
		BLParticles.RAIN.spawn(world, x, y,z).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
	}

}
