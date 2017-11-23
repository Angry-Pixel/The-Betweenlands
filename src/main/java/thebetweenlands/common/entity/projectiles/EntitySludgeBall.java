package thebetweenlands.common.entity.projectiles;

import java.util.List;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;

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
		for (int i = 0; i < this.getEntityWorld().loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.getEntityWorld().loadedEntityList.get(i);
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
		move(MoverType.SELF,this.motionX, this.motionY, this.motionZ);
		if(!this.getEntityWorld().isRemote && prevX == this.posX && prevY == this.posY && prevZ == this.posZ) {
			this.explode();
		}
		this.motionX = prevMotionX;
		this.motionY = prevMotionY;
		this.motionZ = prevMotionZ;
	}

	@Override
	protected void onImpact(RayTraceResult collision) {
		if(collision.typeOfHit == RayTraceResult.Type.BLOCK) {
			IBlockState state = getEntityWorld().getBlockState(collision.getBlockPos());
			if (state.getBlock().canCollideCheck(state, false)) {
				AxisAlignedBB collisionBox = state.getCollisionBoundingBox(this.getEntityWorld(), collision.getBlockPos());
				if(collisionBox != null) {
					RayTraceResult intercept = collisionBox.calculateIntercept(new Vec3d(this.posX, this.posY, this.posZ), new Vec3d(this.posX+this.motionX, this.posY+this.motionY, this.posZ+this.motionZ));
					if(intercept != null) {
						if (collision.sideHit.getIndex() == 1 || collision.sideHit.getIndex() == 0) {
							this.motionY *= -0.9D;
							this.velocityChanged = true;
							this.bounces++;
							if (this.bounces >= 3) {
								if(this.getEntityWorld().isRemote)
									this.motionX = this.motionY = this.motionZ = 0.0D;
								explode();
							} else {
								getEntityWorld().playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.9f);
								spawnBounceParticles(8);
							}
						} else if (collision.sideHit.getIndex() == 2 || collision.sideHit.getIndex() == 3) {
							this.motionZ *= -0.9D;
							this.velocityChanged = true;
							this.bounces++;
							if(this.bounces >= 3) {
								if(this.getEntityWorld().isRemote)
									this.motionX = this.motionY = this.motionZ = 0.0D;
								explode();
							}
						} else if (collision.sideHit.getIndex() == 4 || collision.sideHit.getIndex() == 5) {
							this.motionX *= -0.9D;
							this.velocityChanged = true;
							this.bounces++;
							if(this.bounces >= 3) {
								if(this.getEntityWorld().isRemote)
									this.motionX = this.motionY = this.motionZ = 0.0D;
								explode();
							}
						}
					}
				}
			}
		}
		if (collision.typeOfHit == RayTraceResult.Type.ENTITY) {
			if(!(collision.entityHit instanceof EntityPeatMummy) && !(collision.entityHit instanceof EntityDreadfulMummy))
				explode();
		}
	}

	private void explode() {
		if(!this.getEntityWorld().isRemote) {
			float radius = 3;
			AxisAlignedBB region = new AxisAlignedBB(this.posX - radius, this.posY - radius, this.posZ - radius, this.posX + radius, this.posY + radius, this.posZ + radius);
			List<Entity> entities = this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this, region);
			double radiusSq = radius * radius;
			for (Entity entity : entities) {
				if (entity instanceof EntityLivingBase && !(entity instanceof EntityPeatMummy) && !(entity instanceof EntityDreadfulMummy) && getDistanceSq(entity) < radiusSq) {
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 3));
					Entity owner = this.getOwner();
					if (owner != null) {
						entity.attackEntityFrom(new EntityDamageSourceIndirect("mob", this, owner), 8);
					} else {
						entity.attackEntityFrom(new EntityDamageSource("entity", this), 8);
					}
				}
			}
			getEntityWorld().playSound(null, getPosition(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.5f);
			getEntityWorld().playSound(null, getPosition(), SoundEvents.ENTITY_SMALL_SLIME_SQUISH, SoundCategory.HOSTILE, 1, 0.5f);
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
			this.getEntityWorld().spawnParticle(EnumParticleTypes.SLIME, this.posX + (amount/8) * (this.rand.nextFloat() - 0.5), this.posY + 0.3, this.posZ + (amount/8) * (this.rand.nextFloat() - 0.5), 0, 0, 0);
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
