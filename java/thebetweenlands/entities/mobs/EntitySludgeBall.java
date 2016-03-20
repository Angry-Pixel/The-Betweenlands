package thebetweenlands.entities.mobs;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by jnad325 on 2/23/16.
 */

public class EntitySludgeBall extends EntityThrowable {
	private int bounces = 0;
	private String ownerUUID;

	public EntitySludgeBall(World world) {
		super(world);
		this.setSize(0.75F, 0.75F);
		this.ownerUUID = "";
	}

	public EntitySludgeBall(World world, EntityLivingBase owner) {
		this(world);
		this.ownerUUID = owner.getUniqueID().toString();
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(this.ownerUUID);
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID uuid) {
		for (int i = 0; i < this.worldObj.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.worldObj.loadedEntityList.get(i);
			if (uuid.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.posX = this.lastTickPosX;
		this.posY = this.lastTickPosY;
		this.posZ = this.lastTickPosZ;
		this.setPosition(this.posX, this.posY, this.posZ);
		double prevMotionX = this.motionX;
		double prevMotionY = this.motionY;
		double prevMotionZ = this.motionZ;
		double prevX = this.posX;
		double prevY = this.posY;
		double prevZ = this.posZ;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		if(!this.worldObj.isRemote && prevX == this.posX && prevY == this.posY && prevZ == this.posZ) {
			this.explode();
		}
		this.motionX = prevMotionX;
		this.motionY = prevMotionY;
		this.motionZ = prevMotionZ;
	}

	@Override
	protected void onImpact(MovingObjectPosition collision) {
		if(collision.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			Block block = this.worldObj.getBlock(collision.blockX, collision.blockY, collision.blockZ);
			if(block.canCollideCheck(this.worldObj.getBlockMetadata(collision.blockX, collision.blockY, collision.blockZ), false)) {
				AxisAlignedBB collisionBox = block.getCollisionBoundingBoxFromPool(this.worldObj, collision.blockX, collision.blockY, collision.blockZ);
				if(collisionBox != null) {
					MovingObjectPosition intercept = collisionBox.calculateIntercept(Vec3.createVectorHelper(this.posX, this.posY, this.posZ), Vec3.createVectorHelper(this.posX+this.motionX, this.posY+this.motionY, this.posZ+this.motionZ));
					if(intercept != null) {
						if (collision.sideHit == 1 || collision.sideHit == 0) {
							this.motionY *= -0.9D;
							this.velocityChanged = true;
							this.bounces++;
							if (this.bounces >= 3) {
								if(this.worldObj.isRemote)
									this.motionX = this.motionY = this.motionZ = 0.0D;
								explode();
							} else {
								playSound("mob.slime.big", 1, 0.9f);
								spawnBounceParticles(8);
							}
						} else if (collision.sideHit == 2 || collision.sideHit == 3) {
							this.motionZ *= -0.9D;
							this.velocityChanged = true;
							this.bounces++;
							if(this.bounces >= 3) {
								if(this.worldObj.isRemote)
									this.motionX = this.motionY = this.motionZ = 0.0D;
								explode();
							}
						} else if (collision.sideHit == 4 || collision.sideHit == 5) {
							this.motionX *= -0.9D;
							this.velocityChanged = true;
							this.bounces++;
							if(this.bounces >= 3) {
								if(this.worldObj.isRemote)
									this.motionX = this.motionY = this.motionZ = 0.0D;
								explode();
							}
						}
					}
				}
			}
		}
		if (collision.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			if(!(collision.entityHit instanceof EntityPeatMummy) && !(collision.entityHit instanceof EntityDreadfulMummy))
				explode();
		}
	}

	private void explode() {
		if(!this.worldObj.isRemote) {
			float radius = 3;
			AxisAlignedBB region = AxisAlignedBB.getBoundingBox(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + radius, this.posZ + radius);
			List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, region);
			double radiusSq = radius * radius;
			for (Entity entity : entities) {
				if (entity instanceof EntityLivingBase && !(entity instanceof EntityPeatMummy) && !(entity instanceof EntityDreadfulMummy) && getDistanceSqToEntity(entity) < radiusSq) {
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 80, 3, false));
					Entity owner = this.getOwner();
					if (owner != null) {
						entity.attackEntityFrom(new EntityDamageSourceIndirect("mob", this, owner), 8);
					} else {
						entity.attackEntityFrom(new EntityDamageSource("entity", this), 8);
					}
				}
			}
			playSound("mob.slime.big", 1, 0.5f);
			playSound("mob.slime.small", 1, 0.5f);
			playSound("mob.slime.small", 1, 1f);
			playSound("mob.slime.big", 1, 1f);
			setDead();
		} else {
			//TODO Better explosion particle effects
			spawnBounceParticles(20);
		}
	}

	@Override
	protected float getGravityVelocity() {
		return 0.08F;
	}

	private void spawnBounceParticles(int amount) {
		for (int i = 0; i <= amount; i++) {
			this.worldObj.spawnParticle("slime", this.posX + (amount/8) * (this.rand.nextFloat() - 0.5), this.posY + 0.3, this.posZ + (amount/8) * (this.rand.nextFloat() - 0.5), 0, 0, 0);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("bounces", this.bounces);
		nbt.setString("ownerUUID", this.ownerUUID);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		this.bounces = nbt.getInteger("bounces");
		this.ownerUUID = nbt.getString("ownerUUID");
	}
}
