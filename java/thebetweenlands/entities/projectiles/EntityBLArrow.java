package thebetweenlands.entities.projectiles;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.bow.EnumArrowType;
import thebetweenlands.utils.PotionHelper;

public class EntityBLArrow extends EntityArrow implements IProjectile {
	private static final int DW_SHOOTER = 9;
	private static final int DW_TYPE = 10;
	private boolean checkedShooter = false;
	private boolean inGround = false;

	public EntityBLArrow(World world) {
		super(world);
	}

	public EntityBLArrow(World world, EntityLivingBase entity, float strength) {
		super(world, entity, strength);
		this.shootingEntity = entity;
		this.getDataWatcher().updateObject(DW_SHOOTER, entity.getUniqueID().toString());
	}

	@Override
	public void entityInit() {
		super.entityInit();
		this.getDataWatcher().addObject(DW_SHOOTER, "");
		this.getDataWatcher().addObject(DW_TYPE, (int) 0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("arrowType", this.getArrowType().ordinal());
		nbt.setString("shooter", this.getDataWatcher().getWatchableObjectString(DW_SHOOTER));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setArrowType(nbt.getInteger("arrowType"));
		this.getDataWatcher().updateObject(DW_SHOOTER, "shooter");
	}

	@Override
	public void onUpdate() {
		if(this.shootingEntity == null && !this.checkedShooter) {
			try {
				UUID uuid = UUID.fromString(this.getDataWatcher().getWatchableObjectString(DW_SHOOTER));
				if(uuid != null) {
					this.shootingEntity = this.worldObj.func_152378_a(uuid);
				}
			} catch(Exception ex) { } finally {
				this.checkedShooter = true;
			}
		}
		if(!this.worldObj.isRemote && !this.inGround) {
			MovingObjectPosition collision = getCollision(this);
			if(collision != null && collision.typeOfHit == MovingObjectType.ENTITY && collision.entityHit instanceof EntityLivingBase) {
				EntityLivingBase hitEntity = (EntityLivingBase) collision.entityHit;
				switch(this.getArrowType()) {
				case ANGLER_POISON:
					hitEntity.addPotionEffect(new PotionEffect(Potion.poison.getId(), 200, 2));
					break;
				case OCTINE:
					if(hitEntity.isBurning()) {
						hitEntity.setFire(9);
					} else {
						hitEntity.setFire(5);
					}
					break;
				case BASILISK:
					hitEntity.addPotionEffect(new PotionEffect(PotionHelper.petrify.getId(), 100));
					break;
				default:
				}
			} else if(collision != null && collision.typeOfHit == MovingObjectType.BLOCK) {
				this.inGround = true;
			}
		}
		super.onUpdate();
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if (!this.worldObj.isRemote && this.inGround && this.ticksExisted > 20 && this.arrowShake <= 0) {
			boolean canPickUp = this.canBePickedUp == 1 || (this.canBePickedUp == 2 && player.capabilities.isCreativeMode);
			if (canPickUp && !this.pickUp(player)) {
				canPickUp = false;
			}
			if (canPickUp) {
				playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.onItemPickup(this, 1);
				setDead();
			}
		}
	}

	public boolean pickUp(EntityPlayer player) {
		switch(this.getArrowType()) {
		case ANGLER_POISON:
			return player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.poisonedAnglerToothArrow, 1));
		case OCTINE:
			return player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.octineArrow, 1));
		case BASILISK:
			return player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.basiliskArrow, 1));
		case DEFAULT:
		default:
			return player.inventory.addItemStackToInventory(new ItemStack(BLItemRegistry.anglerToothArrow, 1));
		}
	}

	private static MovingObjectPosition getCollision(EntityArrow ea) {
		Vec3 start = Vec3.createVectorHelper(ea.posX, ea.posY, ea.posZ);
		Vec3 dest = Vec3.createVectorHelper(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
		MovingObjectPosition hit = ea.worldObj.func_147447_a(start, dest, false, true, false);
		start = Vec3.createVectorHelper(ea.posX, ea.posY, ea.posZ);
		dest = Vec3.createVectorHelper(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
		if (hit != null) {
			dest = Vec3.createVectorHelper(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
		}
		Entity collidedEntity = null;
		List entityList = ea.worldObj.getEntitiesWithinAABBExcludingEntity(ea, ea.boundingBox.addCoord(ea.motionX, ea.motionY, ea.motionZ).expand(1.05D, 1.05D, 1.05D));
		double lastDistance = 0.0D;
		for (int c = 0; c < entityList.size(); ++c) {
			Entity currentEntity = (Entity)entityList.get(c);
			if (currentEntity.canBeCollidedWith() && (currentEntity != ea.shootingEntity)) {
				AxisAlignedBB entityBoundingBox = currentEntity.boundingBox.expand((double)0.35F, (double)0.35F, (double)0.35F);
				MovingObjectPosition collision = entityBoundingBox.calculateIntercept(start, dest);
				if (collision != null) {
					double currentDistance = start.distanceTo(collision.hitVec);

					if (currentDistance < lastDistance || lastDistance == 0.0D) {
						collidedEntity = currentEntity;
						lastDistance = currentDistance;
					}
				}
			}
		}
		if (collidedEntity != null) {
			hit = new MovingObjectPosition(collidedEntity);
		}
		return hit;
	}

	private void setArrowType(int type) {
		this.getDataWatcher().updateObject(DW_TYPE, type);
	}

	public void setArrowType(EnumArrowType type) {
		this.getDataWatcher().updateObject(DW_TYPE, type.ordinal());
	}

	public EnumArrowType getArrowType() {
		int arrowType = this.getDataWatcher().getWatchableObjectInt(DW_TYPE);
		return arrowType < EnumArrowType.values().length ? EnumArrowType.values()[arrowType] : EnumArrowType.DEFAULT;
	}
}
