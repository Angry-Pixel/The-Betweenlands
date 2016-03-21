package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityChiromaw extends EntityFlying implements IMob {
	public int courseChangeCooldown;
	public double waypointX;
	public double waypointY;
	public double waypointZ;
	private EntityLivingBase targetedEntity;

	public EntityChiromaw(World world) {
		super(world);
	    setSize(0.8F, 0.9F);
	    setIsHanging(false);
	}

    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(16, new Byte((byte)0));
    }

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!worldObj.isRemote) {
			if (motionY < 0 && posX != waypointX && posZ != waypointZ && targetedEntity == null) {
				motionY *= 1D;
				motionX *= 1D;
				motionZ *= 1D;
			}
			if (targetedEntity != null) {
				if (targetedEntity instanceof EntityPlayer)
					if (((EntityPlayer) targetedEntity).capabilities.isCreativeMode) {
						targetedEntity = null;
						return;
					}
				double standOffX = targetedEntity.posX;
				double standOffZ = targetedEntity.posZ;
				waypointX = standOffX;
				waypointY = targetedEntity.posY + 1 - rand.nextFloat() * 0.3F;
				waypointZ = standOffZ;
			}
			if (getIsHanging()) {
				motionX = motionY = motionZ = 0.0D;
				posY = (double) MathHelper.floor_double(posY) + 1.0D - (double) height;
			} else
				motionY *= 0.6000000238418579D;
		}
	}

	@Override
	protected void updateEntityActionState() {
		super.updateEntityActionState();
		if (getIsHanging()) {
			if (!worldObj.getBlock(MathHelper.floor_double(posX), (int) posY + 1, MathHelper.floor_double(posZ)).isNormalCube()) {
				setIsHanging(false);
				worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1015, (int) posX, (int) posY, (int) posZ, 0);
			} else {
				if (worldObj.getClosestPlayerToEntity(this, 4.0D) != null) {
					setIsHanging(false);
					worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1015, (int) posX, (int) posY, (int) posZ, 0);
				}
			}
		} else {
			if (!worldObj.isRemote && worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
				setDead();

			double distanceX = waypointX - posX;
			double distanceY = waypointY - posY;
			double distanceZ = waypointZ - posZ;
			double distanceScaled = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;

			if (distanceScaled < 1.0D || distanceScaled > 3600.0D) {
				waypointX = posX + (double) ((rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
				waypointY = posY + (double) ((rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
				waypointZ = posZ + (double) ((rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
			}

			if (courseChangeCooldown-- <= 0) {
				courseChangeCooldown += rand.nextInt(5) + 2;
				distanceScaled = (double) MathHelper.sqrt_double(distanceScaled);

				if (isCourseTraversable(waypointX, waypointY, waypointZ, distanceScaled)) {
					motionX += distanceX / distanceScaled * 0.2D;
					motionY += distanceY / distanceScaled * 0.2D;
					motionZ += distanceZ / distanceScaled * 0.2D;
				} else if (targetedEntity != null) {
					motionX += distanceX / distanceScaled * 0.2D;
					motionY += distanceY / distanceScaled * 0.2D;
					motionZ += distanceZ / distanceScaled * 0.2D;
				} else {
					waypointX = posX;
					waypointY = posY;
					waypointZ = posZ;
				}
			}
			if (targetedEntity != null && targetedEntity.isDead)
				targetedEntity = null;

			if (targetedEntity == null)
				targetedEntity = worldObj.getClosestVulnerablePlayerToEntity(this, 32D);

			if (targetedEntity != null) {
				distanceX = targetedEntity.posX - posX;
				distanceY = targetedEntity.boundingBox.minY + (double) (targetedEntity.height / 2.0F) - (posY + (double) (height / 2.0F));
				distanceZ = targetedEntity.posZ - posZ;
				renderYawOffset = rotationYaw = -((float) Math.atan2(distanceX, distanceZ)) * 180.0F / (float) Math.PI;
			} else
				renderYawOffset = rotationYaw = -((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI;

			if (targetedEntity == null && rand.nextInt(20) == 0 && worldObj.getBlock(MathHelper.floor_double(posX), (int) posY + 1, MathHelper.floor_double(posZ)).isNormalCube())
				setIsHanging(true);
		}
	}

	private boolean isCourseTraversable(double x, double y, double z, double distance) {
		double boxX = (waypointX - posX) / distance;
		double boxY = (waypointY - posY) / distance;
		double boxZ = (waypointZ - posZ) / distance;
		AxisAlignedBB axisalignedbb = boundingBox.copy();

		for (int i = 1; (double) i < distance; ++i) {
			axisalignedbb.offset(boxX, boxY, boxZ);
			if (!worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		super.onCollideWithPlayer(player);
		if (!player.capabilities.isCreativeMode && !worldObj.isRemote && getEntitySenses().canSee(player))
			if (getDistanceToEntity(player) <= 1.5F)
				if (player.boundingBox.maxY >= boundingBox.minY && player.boundingBox.minY <= boundingBox.maxY)
						player.attackEntityFrom(DamageSource.causeMobDamage(this), 2F);
	}

	@Override
	protected void fall(float damage) {
	}

	@Override
	protected void updateFallState(double distanceFallenThisTick, boolean onGround) {
	}

    public boolean getIsHanging() {
        return (dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setIsHanging(boolean hanging) {
        byte b0 = dataWatcher.getWatchableObjectByte(16);
        if (hanging)
            dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 1)));
        else
            dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 & -2)));
    }

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:flyingFiendLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:flyingFiendHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:flyingFiendDeath";
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
	}

}
