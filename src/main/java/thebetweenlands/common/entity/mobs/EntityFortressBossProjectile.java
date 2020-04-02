package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityFortressBossProjectile extends Entity implements IProjectile {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossProjectile.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Boolean> DEFLECTION_STATE = EntityDataManager.<Boolean>createKey(EntityFortressBossProjectile.class, DataSerializers.BOOLEAN);

	private UUID throwerUUID;
	private int ticksInAir = 0;
	private boolean canDismount = false;

	private Entity cachedOwner;
	private Entity cachedThrower;	

	public EntityFortressBossProjectile(World world) {
		super(world);
		this.setSize(0.65F, 0.65F);
		this.noClip = true;
	}

	public EntityFortressBossProjectile(World world, Entity source) {
		this(world);
		this.setOwner(source);
		this.setThrower(source);
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(OWNER, Optional.absent());
		this.getDataManager().register(DEFLECTION_STATE, false);
	}

	public void setDeflectable(boolean deflectable) {
		this.getDataManager().set(DEFLECTION_STATE, deflectable);
	}

	public boolean isDeflectable() {
		return this.getDataManager().get(DEFLECTION_STATE);
	}

	public void setOwner(@Nullable Entity entity) {
		this.getDataManager().set(OWNER, entity == null ? Optional.absent() : Optional.of(entity.getUniqueID()));
	}

	@Nullable
	public UUID getOwnerUUID() {
		Optional<UUID> uuid = this.getDataManager().get(OWNER);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getOwnerUUID();
		if(uuid == null) {
			this.cachedOwner = null;
		} else if(this.cachedOwner == null || !this.cachedOwner.isEntityAlive() || !this.cachedOwner.getUniqueID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUniqueID().equals(uuid)) {
					this.cachedOwner = entity;
					break;
				}
			}
		}
		return this.cachedOwner;
	}

	public void setThrower(@Nullable Entity entity) {
		this.throwerUUID = entity != null ? entity.getUniqueID() : null;
	}

	@Nullable
	public UUID getThrowerUUID() {
		return this.throwerUUID;
	}

	@Nullable
	public Entity getThrower() {
		UUID uuid = this.getThrowerUUID();
		if(uuid == null) {
			this.cachedThrower = null;
		} else if(this.cachedThrower == null || !this.cachedThrower.isEntityAlive() || !this.cachedThrower.getUniqueID().equals(uuid)) {
			this.cachedThrower = null;
			for(Entity entity : this.getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUniqueID().equals(uuid)) {
					this.cachedThrower = entity;
					break;
				}
			}
		}
		return this.cachedThrower;
	}

	protected void onImpact(RayTraceResult target) {
		if(target.entityHit instanceof EntityFortressBossProjectile == false) {
			if (target.entityHit != null && target.entityHit instanceof EntityLivingBase) {
				if(target.entityHit instanceof EntityFortressBoss) {
					EntityFortressBoss boss = (EntityFortressBoss) target.entityHit;
					Vec3d ray = new Vec3d(this.motionX, this.motionY, this.motionZ);
					ray = ray.normalize().scale(64.0D);
					int shieldHit = EntityFortressBoss.rayTraceShield(boss.shield, new Vec3d(boss.posX + EntityFortressBoss.SHIELD_OFFSET_X, boss.posY + EntityFortressBoss.SHIELD_OFFSET_Y, boss.posZ + EntityFortressBoss.SHIELD_OFFSET_Z), boss.getShieldRotationYaw(1), boss.getShieldRotationPitch(1), boss.getShieldRotationYaw(1), boss.getShieldExplosion(1), new Vec3d(this.posX, this.posY, this.posZ), ray, false);
					if(shieldHit >= 0) {
						if(!this.world.isRemote) {
							boss.shield.setActive(shieldHit, false);

							this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.FORTRESS_BOSS_SHIELD_DOWN, SoundCategory.HOSTILE, 1.0F, 1.0F);

							double angle = Math.PI * 2.0D / 18;
							for(int i = 0; i < 18; i++) {
								Vec3d dir = new Vec3d(Math.sin(angle * i), 0, Math.cos(angle * i));
								dir = dir.normalize();
								float speed = 0.8F;
								EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.world, this.getOwner());
								bullet.setLocationAndAngles(boss.posX, boss.posY, boss.posZ, 0, 0);
								bullet.shoot(dir.x, dir.y, dir.z, speed, 0.0F);
								this.world.spawnEntity(bullet);
							}
						}
					} else {
						boss.attackEntityFrom(DamageSource.GENERIC, 10);
					}

					if(!this.world.isRemote) {
						boss.setFloating(false);
					}
				} else {
					target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getOwner()), 2);
				}

				if(!this.world.isRemote) {
					this.setDead();
				}
			} else if(target.typeOfHit == RayTraceResult.Type.BLOCK) {
				this.setDead();
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else {
			if(this.isDeflectable()) {
				this.markVelocityChanged();
				if (source.getTrueSource() instanceof EntityPlayer) {
					ItemStack heldItem = ((EntityPlayer)source.getTrueSource()).getHeldItem(EnumHand.MAIN_HAND);
					if(heldItem != null && heldItem.getItem() instanceof ItemSword) {
						if(!this.world.isRemote && source.getTrueSource().getPassengers().isEmpty()) {
							this.startRiding(source.getTrueSource(), true);
							this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(source.getTrueSource()));
							return true;
						}
					}
				}
			} else {
				if(!this.world.isRemote) {
					this.setDead();
				}
			}
			return false;
		}
	}

	@Override
	public void onUpdate() {
		if(!this.world.isRemote && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.setDead();
			return;
		}

		if(!this.isDead) {
			if(this.getRidingEntity() == null) {
				this.ticksInAir++;

				if(this.ticksInAir > 200) {
					this.setDead();
				}

				Vec3d currentPos = new Vec3d(this.posX, this.posY, this.posZ);
				Vec3d nextPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
				RayTraceResult hitObject = this.world.rayTraceBlocks(currentPos, nextPos);
				currentPos = new Vec3d(this.posX, this.posY, this.posZ);
				nextPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

				if (hitObject != null) {
					nextPos = new Vec3d(hitObject.hitVec.x, hitObject.hitVec.y, hitObject.hitVec.z);
				}

				Entity hitEntity = null;
				List<Entity> hitEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(this.motionX, this.motionY, this.motionZ).grow(0.1D, 0.1D, 0.1D));
				double minDist = 0.0D;

				for (int i = 0; i < hitEntities.size(); ++i) {
					Entity entity = (Entity)hitEntities.get(i);
					if (entity.canBeCollidedWith() && entity != this.getThrower() && entity != this) {
						float f = 0.1F;
						AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double)f, (double)f, (double)f);
						RayTraceResult movingobjectposition1 = axisalignedbb.calculateIntercept(currentPos, nextPos);
						if (movingobjectposition1 != null) {
							double d1 = currentPos.distanceTo(movingobjectposition1.hitVec);
							if (d1 < minDist || minDist == 0.0D) {
								hitEntity = entity;
								minDist = d1;
							}
						}
					}
				}

				if (hitEntity != null) {
					hitObject = new RayTraceResult(hitEntity);
				}

				if (hitObject != null && hitObject.entityHit != this.getThrower()) {
					this.onImpact(hitObject);
				}
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			} else {
				if(this.getRidingEntity() instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) this.getRidingEntity();
					ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
					if(!this.isDeflectable() || heldItem == null || heldItem.getItem() instanceof ItemSword == false) {
						if(!this.world.isRemote) {
							this.setDead();
						}
					} else {
						player.setInWeb();
						player.motionY -= 1.5D;
						if(player.isSwingInProgress) {
							if(this.canDismount) {
								Vec3d look = this.getRidingEntity().getLookVec();
								look.normalize();
								this.shoot(look.x, look.y, look.z, 0.5F, 0.0F);
								this.dismountRidingEntity();
								this.setThrower(player);
							}
						} else {
							this.canDismount = true;
						}
					}
				}
			}
		}

		super.onUpdate();
	}

	@Override
	public void shoot(double x, double y, double z, float speed, float randMotion) {
		float f2 = MathHelper.sqrt(x * x + y * y + z * z);
		x /= (double)f2;
		y /= (double)f2;
		z /= (double)f2;
		x += this.rand.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		y += this.rand.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		z += this.rand.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		x *= (double)speed;
		y *= (double)speed;
		z *= (double)speed;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f3 = MathHelper.sqrt(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);
		this.velocityChanged = true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		if(nbt.hasUniqueId("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUniqueId("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.absent());
		}
		if(nbt.hasUniqueId("thrower")) {
			this.throwerUUID = nbt.getUniqueId("thrower");
		} else {
			this.throwerUUID = null;
		}
		this.ticksInAir = nbt.getInteger("ticksInAir");
		this.canDismount = nbt.getBoolean("canDismount");
		this.setDeflectable(nbt.getBoolean("deflectable"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.getOwnerUUID() != null) {
			nbt.setUniqueId("owner", this.getOwnerUUID());
		}
		if(this.getThrowerUUID() != null) {
			nbt.setUniqueId("thrower", this.getThrowerUUID());
		}
		nbt.setInteger("ticksInAir", this.ticksInAir);
		nbt.setBoolean("canDismount", this.canDismount);
		nbt.setBoolean("deflectable", this.isDeflectable());
	}
}