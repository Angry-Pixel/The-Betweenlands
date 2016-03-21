package thebetweenlands.entities.mobs;

import java.util.List;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBoss;
import thebetweenlands.entities.projectiles.EntityVolatileSoul;
import thebetweenlands.event.player.PlayerLocationHandler;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage.EnumLocationType;

public class EntityWight extends EntityMob implements IEntityBL {
	public static final IAttribute VOLATILE_HEALTH_START_ATTRIB = (new RangedAttribute("bl.volatileHealthStart", 1.0D, 0.0D, 1.0D)).setDescription("Volatile Health Percentage Start");
	public static final IAttribute VOLATILE_COOLDOWN_ATTRIB = (new RangedAttribute("bl.volatileCooldown", 400.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Volatile Cooldown");
	public static final IAttribute VOLATILE_FLIGHT_SPEED_ATTRIB = (new RangedAttribute("bl.volatileFlightSpeed", 0.25D, 0.0D, 5.0D)).setDescription("Volatile Flight Speed");
	public static final IAttribute VOLATILE_LENGTH_ATTRIB = (new RangedAttribute("bl.volatileLength", 600.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Volatile Length");
	public static final IAttribute VOLATILE_MAX_DAMAGE_ATTRIB = (new RangedAttribute("bl.volatileMaxDamage", 30.0D, 0.0D, Double.MAX_VALUE)).setDescription("Volatile Max Damage");

	public static final int ATTACK_STATE_DW = 20;
	public static final int ANIMATION_STATE_DW = 21;
	public static final int VOLATILE_STATE_DW = 22;
	public static final int GUARD_STATE_DW = 23;
	public static final int REPAIR_X_DW = 24;
	public static final int REPAIR_Y_DW = 25;
	public static final int REPAIR_Z_DW = 26;

	private EntityAIAttackOnCollide meleeAttack = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.5D, false);
	private EntityPlayer previousTarget;
	private boolean updateHasBeenSeen = false;

	//Volatile stuff
	private int volatileCooldown = (int) VOLATILE_COOLDOWN_ATTRIB.getDefaultValue() / 2 + 20;
	private int volatileProgress = 0;
	private float volatileReceivedDamage = 0.0F;
	private double waypointX;
	private double waypointY;
	private double waypointZ;
	private boolean prevVolatile = false;

	//Wight temple stuff
	private String locationViolatorUUID = null;
	private boolean breakBlock = false;
	private Block repairBlock = null;
	private int repairMeta = 0;
	private int repairX = 0;
	private int repairY = 0;
	private int repairZ = 0;
	private boolean canTurnVolatile = true;

	public EntityWight(World world) {
		super(world);
		setSize(0.7F, 2.2F);
		this.getNavigator().setCanSwim(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, this.meleeAttack);
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWander(this, 0.3D));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(ATTACK_STATE_DW, (byte) 0);
		this.dataWatcher.addObject(ANIMATION_STATE_DW, (float) 1);
		this.dataWatcher.addObject(VOLATILE_STATE_DW, (byte) 0);
		this.dataWatcher.addObject(GUARD_STATE_DW, (byte) 0);
		this.dataWatcher.addObject(REPAIR_X_DW, 0);
		this.dataWatcher.addObject(REPAIR_Y_DW, 0);
		this.dataWatcher.addObject(REPAIR_Z_DW, 0);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(76.0D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);

		this.getAttributeMap().registerAttribute(VOLATILE_HEALTH_START_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_LENGTH_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_MAX_DAMAGE_ATTRIB);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("volatileState", this.getDataWatcher().getWatchableObjectByte(VOLATILE_STATE_DW));
		nbt.setInteger("volatileCooldown", this.volatileCooldown);
		nbt.setInteger("volatileProgress", this.volatileProgress);
		nbt.setFloat("volatileReceivedDamage", this.volatileReceivedDamage);
		nbt.setBoolean("isLocationGuard", this.isLocationGuard());
		if(this.locationViolatorUUID != null)
			nbt.setString("locationViolatorUUID", this.locationViolatorUUID);

		nbt.setBoolean("isRepairGuard", this.isRepairGuard());
		nbt.setInteger("repairMeta", this.repairMeta);
		nbt.setInteger("repairX", this.repairX);
		nbt.setInteger("repairY", this.repairY);
		nbt.setInteger("repairZ", this.repairZ);
		nbt.setBoolean("breakBlock", this.breakBlock);
		if(this.repairBlock != null)
			nbt.setString("repairBlock", Block.blockRegistry.getNameForObject(this.repairBlock));
		nbt.setBoolean("canTurnVolatile", this.canTurnVolatile);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.getDataWatcher().updateObject(VOLATILE_STATE_DW, nbt.getByte("volatileState"));
		this.volatileCooldown = nbt.getInteger("volatileCooldown");
		this.volatileProgress = nbt.getInteger("volatileProgress");
		this.volatileReceivedDamage = nbt.getFloat("volatileReceivedDamage");
		if(nbt.getBoolean("isLocationGuard"))
			this.dataWatcher.updateObject(GUARD_STATE_DW, (byte) 1);
		if(this.isLocationGuard() && nbt.hasKey("locationViolatorUUID"))
			this.locationViolatorUUID = nbt.getString("locationViolatorUUID");

		if(nbt.getBoolean("isRepairGuard"))
			this.dataWatcher.updateObject(GUARD_STATE_DW, (byte) 2);
		if(this.isRepairGuard()) {
			this.repairMeta = nbt.getInteger("repairMeta");
			this.repairX = nbt.getInteger("repairX");
			this.repairY = nbt.getInteger("repairY");
			this.repairZ = nbt.getInteger("repairZ");
			this.breakBlock = nbt.getBoolean("breakBlock");
			this.repairBlock = (Block) Block.blockRegistry.getObject(nbt.getString("repairBlock"));
		}
		this.canTurnVolatile = nbt.getBoolean("canTurnVolatile");
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:wightMoan";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:wightHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:wightDeath";
	}

	public boolean canPossess(EntityLivingBase entity) {
		return !this.isRepairGuard() && (this.isLocationGuard() || !(entity instanceof EntityPlayer && ((EntityPlayer)entity).getCurrentArmor(3) != null && ((EntityPlayer)entity).getCurrentArmor(3).getItem() == BLItemRegistry.skullMask));
	}

	@Override
	public void onUpdate() {
		if(!this.worldObj.isRemote) {
			if(this.isLocationGuard() && (this.getViolator() == null || !this.getViolator().isEntityAlive())) {
				this.setDead();
			} else if(this.isLocationGuard() && this.getViolator() != null) {
				if(!LocationStorage.isInLocationType(this.getViolator(), EnumLocationType.WIGHT_TOWER)) {
					this.setDead();
				} else {
					this.setAttackTarget(this.getViolator());
				}
			}
		}

		if(!this.isRepairGuard()) {
			EntityPlayer target = this.getAttackTarget() instanceof EntityPlayer ? (EntityPlayer)this.getAttackTarget() : null;
			if(target == null || target.isDead || target.getDistanceToEntity(this) > this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue()) {
				target = this.worldObj.getClosestVulnerablePlayerToEntity(this, 25.0D);
			}

			if(target != null && !target.isSneaking()) {
				this.setTargetSpotted(target, true);
			}

			if(target != null && target != this.previousTarget && target.isSneaking()) {
				this.setTargetSpotted(target, false);
			}

			if(target == null && this.previousTarget != null) {
				this.setTargetSpotted(target, false);
			}

			if(this.getAttackTarget() instanceof EntityFortressBoss)
				this.setTargetSpotted(null, false);
			if(this.ridingEntity instanceof EntityFortressBoss)
				this.dismountEntity(this.ridingEntity);

			if(this.getAttackTarget() != null) {
				if (getAnimation() > 0)
					setAnimation(getAnimation() - 0.1F);
			} else {
				if (getAnimation() < 1)
					setAnimation(getAnimation() + 0.1F);
				if (getAnimation() == 0 && previousTarget != null) {
					previousTarget = null;
				}
			}

			if (!this.worldObj.isRemote && getAttackTarget() != null) {
				this.dataWatcher.updateObject(ATTACK_STATE_DW, Byte.valueOf((byte) 1));

				if(!this.isVolatile() && this.canPossess(this.getAttackTarget()) && this.canTurnVolatile) {
					if(this.volatileCooldown > 0)
						this.volatileCooldown--;
					if(this.getHealth() <= this.getMaxHealth() * this.getEntityAttribute(VOLATILE_HEALTH_START_ATTRIB).getAttributeValue() && this.volatileCooldown <= 0) {
						this.setVolatile(true);
						this.volatileCooldown = this.getVolatileCooldown() + this.worldObj.rand.nextInt(this.getVolatileCooldown()) + 20;
						this.volatileProgress = 0;
					}
				} else if(this.isVolatile() && !this.canPossess(this.getAttackTarget())) {
					this.setVolatile(false);
				}
			}

			if(this.isVolatile()) {
				if(this.getAttackTarget() != null) {
					if(this.ridingEntity == null) {
						double dx = this.getAttackTarget().posX - this.posX;
						double dz = this.getAttackTarget().posZ - this.posZ;
						double dy;
						if (this.getAttackTarget() instanceof EntityLivingBase) {
							EntityLivingBase entitylivingbase = (EntityLivingBase)this.getAttackTarget();
							dy = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
						} else {
							dy = (this.getAttackTarget().boundingBox.minY + this.getAttackTarget().boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
						}
						double dist = (double)MathHelper.sqrt_double(dx * dx + dz * dz);
						float yaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
						float pitch = (float)(-(Math.atan2(dy, dist) * 180.0D / Math.PI));
						this.setRotation(yaw, pitch);
						if(this.worldObj.isRemote) {
							this.setRotationYawHead(yaw);
						}
					} else {
						this.setRotation(this.ridingEntity.rotationYaw, 0);
						if(this.worldObj.isRemote) {
							this.setRotationYawHead(this.ridingEntity.rotationYaw);
						}
					}
				}
				if(!this.worldObj.isRemote) {
					if(this.ridingEntity != null && this.ridingEntity.isDead) {
						this.dismountEntity(this.ridingEntity);
					}
					if(this.volatileProgress < this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
						this.volatileProgress++;
					}
					if(this.volatileProgress >= 20) {
						if(this.getAttackTarget() != null) {
							this.waypointX = this.getAttackTarget().posX;
							this.waypointY = this.getAttackTarget().boundingBox.minY + (this.getAttackTarget().boundingBox.maxY - this.getAttackTarget().boundingBox.minY) / 2.0D;
							this.waypointZ = this.getAttackTarget().posZ;
						} else {
							this.waypointX = this.posX;
							this.waypointY = this.posY;
							this.waypointZ = this.posZ;
						}
					} else {
						this.waypointX = this.posX;
						this.waypointY = this.posY + 3.0D;
						this.waypointZ = this.posZ;
					}
					if (getAttackTarget() != null && getDistanceToEntity(getAttackTarget()) < 1) {
						onCollideWithEntity(getAttackTarget());
					}
					if(this.ridingEntity != null) {
						if(this.ticksExisted % 30 == 0) {
							List<EntityVolatileSoul> existingSouls = this.worldObj.getEntitiesWithinAABB(EntityVolatileSoul.class, this.boundingBox.expand(16.0D, 16.0D, 16.0D));
							if(existingSouls.size() < 16) {
								EntityVolatileSoul soul = new EntityVolatileSoul(this.worldObj);
								float mx = this.worldObj.rand.nextFloat() - 0.5F;
								float my = this.worldObj.rand.nextFloat() / 2.0F;
								float mz = this.worldObj.rand.nextFloat() - 0.5F;
								Vec3 dir = Vec3.createVectorHelper(mx, my, mz).normalize();
								soul.setOwner(this.getUniqueID().toString());
								soul.setLocationAndAngles(this.posX + dir.xCoord * 0.5D, this.posY + dir.yCoord * 1.5D, this.posZ + dir.zCoord * 0.5D, 0, 0);
								soul.setThrowableHeading(mx * 2.0D, my * 2.0D, mz * 2.0D, 1.0F, 1.0F);
								this.worldObj.spawnEntityInWorld(soul);
							}
						}
					}
				} else {
					if(this.ridingEntity == null || this.ticksExisted % 4 == 0) {
						this.spawnVolatileParticles();
					}
				}
				this.setSize(0.7F, 0.7F);
			} else {
				this.setSize(0.7F, 2.2F);
			}

			if (!this.worldObj.isRemote && getAttackTarget() == null) {
				this.dataWatcher.updateObject(ATTACK_STATE_DW, Byte.valueOf((byte) 0));
			}

			if(this.prevVolatile != this.isVolatile()) {
				if(this.worldObj.isRemote) {
					for(int i = 0; i < 80; i++) {
						double px = this.posX + this.worldObj.rand.nextFloat() * 0.7F;
						double py = this.posY + this.worldObj.rand.nextFloat() * 2.2F;
						double pz = this.posZ + this.worldObj.rand.nextFloat() * 0.7F;
						Vec3 vec = Vec3.createVectorHelper(px, py, pz).subtract(Vec3.createVectorHelper(this.posX + 0.35F, this.posY + 1.1F, this.posZ + 0.35F)).normalize();
						BLParticle.SWAMP_SMOKE.spawn(this.worldObj, px, py, pz, vec.xCoord * 0.25F, vec.yCoord * 0.25F, vec.zCoord * 0.25F, 1.0F);
					}
				}
				this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "thebetweenlands:wightAttack", 1.6F, 1.0F);
			}
			this.prevVolatile = this.isVolatile();
		} else {
			this.setAttackTarget(null);
			this.setVolatile(true);
			this.waypointX = this.repairX + 0.5D;
			this.waypointY = this.repairY + 0.5D;
			this.waypointZ = this.repairZ + 0.5D;

			if(this.getDistance(this.repairX + 0.5D, this.repairY + 0.5D, this.repairZ + 0.5D) <= this.getEntityAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue() + 0.1D) {
				if(this.breakBlock) {
					this.worldObj.setBlock(this.repairX, this.repairY, this.repairZ, Blocks.air);
				} else {
					if(this.repairBlock != null)
						this.worldObj.setBlock(this.repairX, this.repairY, this.repairZ, this.repairBlock, this.repairMeta, 2);
				}
				this.setDead();
			}

			if(!this.worldObj.isRemote) {
				this.dataWatcher.updateObject(REPAIR_X_DW, this.repairX);
				this.dataWatcher.updateObject(REPAIR_Y_DW, this.repairY);
				this.dataWatcher.updateObject(REPAIR_Z_DW, this.repairZ);
			} else {
				this.spawnVolatileParticles();
				this.repairX = this.dataWatcher.getWatchableObjectInt(REPAIR_X_DW);
				this.repairY = this.dataWatcher.getWatchableObjectInt(REPAIR_Y_DW);
				this.repairZ = this.dataWatcher.getWatchableObjectInt(REPAIR_Z_DW);
				for(int i = 0; i <= 2; i++) {
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY, this.repairZ, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY, this.repairZ + 1, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY + 1, this.repairZ, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY + 1, this.repairZ + 1, 0.0D, 0.0D, 0.0D, 0);
				}
				for(int i = 0; i <= 2; i++) {
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY + 0.5D * i, this.repairZ, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY + 0.5D * i, this.repairZ + 1, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY + 0.5D * i, this.repairZ, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY + 0.5D * i, this.repairZ + 1, 0.0D, 0.0D, 0.0D, 0);
				}
				for(int i = 0; i <= 2; i++) {
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY, this.repairZ + 0.5D * i, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY +1, this.repairZ + 0.5D * i, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY, this.repairZ + 0.5D * i, 0.0D, 0.0D, 0.0D, 0);
					BLParticle.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY + 1, this.repairZ + 0.5D * i, 0.0D, 0.0D, 0.0D, 0);
				}
			}
			AxisAlignedBB repairBB = AxisAlignedBB.getBoundingBox(this.repairX, this.repairY, this.repairZ, this.repairX + 1, this.repairY + 1, this.repairZ + 1);
			List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, repairBB);
			for(EntityLivingBase entity : entities) {
				if(entity != this && entity instanceof EntityWight == false) {
					if(entity.posX < this.repairX + 0.5D) {
						entity.motionX = -0.5D;
					} else if(entity.posX > this.repairX + 0.5D) {
						entity.motionX = 0.5D;
					}
					if(entity.posY < this.repairY + 0.5D) {
						entity.motionY = -0.5D;
					} else if(entity.posY > this.repairY + 0.5D) {
						entity.motionY = 0.5D;
					}
					if(entity.posZ < this.repairZ + 0.5D) {
						entity.motionZ = -0.5D;
					} else if(entity.posZ > this.repairZ + 0.5D) {
						entity.motionZ = 0.5D;
					}
					entity.attackEntityFrom(DamageSource.magic, 4.0F);
					entity.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 160, 2));
					entity.addPotionEffect(new PotionEffect(Potion.blindness.getId(), 160, 2));
				}
			}
		}

		super.onUpdate();
	}

	public void onCollideWithEntity(EntityLivingBase entity) {
		if (!this.worldObj.isRemote && this.isVolatile() && entity == this.getAttackTarget()) {
			if (entity.riddenByEntity == null) {
				mountEntity(entity);
				this.volatileProgress = 20;
			}
		}
	}

	public void setLocationGuard(EntityPlayer violator) {
		this.dataWatcher.updateObject(GUARD_STATE_DW, (byte) 1);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(999);
		this.setHealth(999);
		this.locationViolatorUUID = violator.getUniqueID().toString();
	}

	public EntityPlayer getViolator() {
		if(this.locationViolatorUUID != null) {
			try {
				return this.worldObj.func_152378_a(UUID.fromString(this.locationViolatorUUID));
			} catch(Exception ex) {
				this.locationViolatorUUID = null;
			}
		}
		return null;
	}

	public boolean isLocationGuard() {
		return this.dataWatcher.getWatchableObjectByte(GUARD_STATE_DW) == 1;
	}

	public void setRepairGuard(Block block, int x, int y, int z, int meta) {
		this.dataWatcher.updateObject(GUARD_STATE_DW, (byte) 2);
		this.breakBlock = false;
		this.repairBlock = block;
		this.repairX = x;
		this.repairY = y;
		this.repairZ = z;
		this.repairMeta = meta;
	}

	public void setRepairGuard(int x, int y, int z) {
		this.dataWatcher.updateObject(GUARD_STATE_DW, (byte) 2);
		this.breakBlock = true;
		this.repairX = x;
		this.repairY = y;
		this.repairZ = z;
	}

	public boolean isRepairGuard() {
		return this.dataWatcher.getWatchableObjectByte(GUARD_STATE_DW) == 2;
	}

	@SideOnly(Side.CLIENT)
	private void spawnVolatileParticles() {
		final double radius = 0.3F;
		final double cx = this.posX;
		final double cy = this.posY + 0.35D;
		final double cz = this.posZ;
		for(int i = 0; i < 8; i++) {
			double px = this.worldObj.rand.nextFloat() * 0.7F;
			double py = this.worldObj.rand.nextFloat() * 0.7F;
			double pz = this.worldObj.rand.nextFloat() * 0.7F;
			Vec3 vec = Vec3.createVectorHelper(px, py, pz).subtract(Vec3.createVectorHelper(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.xCoord * radius;
			py = cy + vec.yCoord * radius;
			pz = cz + vec.zCoord * radius;
			BLParticle.STEAM_PURIFIER.spawn(this.worldObj, px, py, pz, 0.0D, 0.0D, 0.0D, 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return !this.isVolatile() ? super.getShadowSize() : 0.0F;
	}

	@Override
	protected void fall(float dst) {
		if(!this.isVolatile()) {
			super.fall(dst);
		}
	}

	@Override
	protected void updateFallState(double dst, boolean onGround) {
		if(!this.isVolatile()) {
			super.updateFallState(dst, onGround);
		}
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if(this.isVolatile()) {
			if (this.isInWater()) {
				this.moveFlying(strafe, forward, 0.02F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.800000011920929D;
				this.motionY *= 0.800000011920929D;
				this.motionZ *= 0.800000011920929D;
			} else if (this.handleLavaMovement()) {
				this.moveFlying(strafe, forward, 0.02F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			} else {
				float friction = 0.91F;

				if (this.onGround) {
					friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
				}

				float groundFriction = 0.16277136F / (friction * friction * friction);
				this.moveFlying(strafe, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
				friction = 0.91F;

				if (this.onGround) {
					friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= (double)friction;
				this.motionY *= (double)friction;
				this.motionZ *= (double)friction;
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double dx = this.posX - this.prevPosX;
			double dz = this.posZ - this.prevPosZ;
			float distanceMoved = MathHelper.sqrt_double(dx * dx + dz * dz) * 4.0F;

			if (distanceMoved > 1.0F) {
				distanceMoved = 1.0F;
			}

			this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			super.moveEntityWithHeading(strafe, forward);
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if(this.isVolatile()) {
			this.noClip = true;

			if (this.worldObj.isRemote) {
				return;
			}

			if(this.volatileProgress >= this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
				this.motionY -= 0.075D;
				if(this.onGround) {
					this.setVolatile(false);
				}
			} else {
				double dx = this.waypointX - this.posX;
				double dy = this.waypointY - this.posY;
				double dz = this.waypointZ - this.posZ;
				double dist = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
				double speed = this.getEntityAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue();
				if(dist <= speed) {
					this.waypointX = this.posX;
					this.waypointY = this.posY;
					this.waypointZ = this.posZ;
				} else {
					this.motionX = dx / dist * speed;
					this.motionY = dy / dist * speed;
					this.motionZ = dz / dist * speed;
				}
			}
		} else {
			this.noClip = false;
		}
	}

	private void setTargetSpotted(EntityPlayer target, boolean hasBeenSeen) {
		if (hasBeenSeen) {
			if (!updateHasBeenSeen) {
				updateHasBeenSeen = true;
				tasks.addTask(1, meleeAttack);
				setAttackTarget(target);
				previousTarget = target;
			}
		} else {
			if (updateHasBeenSeen) {
				updateHasBeenSeen = false;
				setAttackTarget(null);
				tasks.removeTask(meleeAttack);
			}
		}
	}

	private void setAnimation(float progress) {
		dataWatcher.updateObject(ANIMATION_STATE_DW, progress);
	}

	public float getAnimation() {
		return dataWatcher.getWatchableObjectFloat(ANIMATION_STATE_DW);
	}

	@Override
	public boolean isEntityInvulnerable() {
		return dataWatcher.getWatchableObjectByte(ATTACK_STATE_DW) == 0;
	}

	@Override
	public boolean canBePushed() {
		return dataWatcher.getWatchableObjectByte(ATTACK_STATE_DW) == 1;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if(!this.isLocationGuard() && !this.isRepairGuard())
			entityDropItem(new ItemStack(BLItemRegistry.wightsHeart), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(this.isVolatile() && source == DamageSource.inWall) {
			return false;
		}
		float prevHealth = this.getHealth();
		boolean ret = super.attackEntityFrom(source, damage);
		float dealtDamage = prevHealth - this.getHealth();
		if(this.isVolatile() && this.ridingEntity != null) {
			this.volatileReceivedDamage += dealtDamage;
			if(this.volatileReceivedDamage >= this.getEntityAttribute(VOLATILE_MAX_DAMAGE_ATTRIB).getAttributeValue()) {
				this.setVolatile(false);
			}
		}
		return ret;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if(this.isVolatile()) {
			return false;
		}
		return super.attackEntityAsMob(entity);
	}

	@Override
	public String pageName() {
		return "wight";
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public double getYOffset() {
		if(this.ridingEntity != null && this.ridingEntity instanceof EntityPlayer && this.worldObj.isRemote) {
			return -1.7D;
		}
		return this.yOffset;
	}

	public int getVolatileCooldown() {
		return (int) this.getEntityAttribute(VOLATILE_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public void setVolatile(boolean v) {
		if(!this.canTurnVolatile)
			v = false;
		this.dataWatcher.updateObject(VOLATILE_STATE_DW, (byte)(v ? 1 : 0));
		this.volatileProgress = 0;
		this.volatileReceivedDamage = 0.0F;
		if(!v && this.ridingEntity != null) {
			this.dismountEntity(this.ridingEntity);
			this.ridingEntity = null;
		}
	}

	public boolean isVolatile() {
		return this.dataWatcher.getWatchableObjectByte(VOLATILE_STATE_DW) == 1;
	}

	public void setCanTurnVolatile(boolean canTurnVolatile) {
		this.canTurnVolatile = canTurnVolatile;
	}
}
