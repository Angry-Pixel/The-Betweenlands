//package thebetweenlands.common.entity.mobs;
//
//import java.util.List;
//import java.util.UUID;
//
//import javax.annotation.Nullable;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.SharedMonsterAttributes;
//import net.minecraft.entity.ai.EntityAIAttackMelee;
//import net.minecraft.entity.ai.EntityAIHurtByTarget;
//import net.minecraft.entity.ai.EntityAILookIdle;
//import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
//import net.minecraft.entity.ai.EntityAISwimming;
//import net.minecraft.entity.ai.EntityAIWander;
//import net.minecraft.entity.ai.EntityAIWatchClosest;
//import net.minecraft.entity.ai.attributes.IAttribute;
//import net.minecraft.entity.ai.attributes.RangedAttribute;
//import net.minecraft.entity.monster.EntityMob;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.init.MobEffects;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.network.datasync.DataParameter;
//import net.minecraft.network.datasync.DataSerializers;
//import net.minecraft.network.datasync.EntityDataManager;
//import net.minecraft.pathfinding.PathNodeType;
//import net.minecraft.potion.PotionEffect;
//import net.minecraft.util.DamageSource;
//import net.minecraft.util.EntityDamageSource;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.SoundCategory;
//import net.minecraft.util.SoundEvent;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import thebetweenlands.api.entity.IEntityBL;
//import thebetweenlands.client.render.particle.BLParticles;
//import thebetweenlands.client.render.particle.ParticleFactory;
//import thebetweenlands.common.TheBetweenlands;
//import thebetweenlands.common.registries.ItemRegistry;
//import thebetweenlands.common.registries.SoundRegistry;
//import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;
//
////Only leaving this here in case we still need some of the not yet updated code
//@Deprecated
//public class EntityWightOld extends EntityMob implements IEntityBL {
//	public static final IAttribute VOLATILE_HEALTH_START_ATTRIB = (new RangedAttribute(null, "bl.volatileHealthStart", 1.0D, 0.0D, 1.0D)).setDescription("Volatile Health Percentage Start");
//	public static final IAttribute VOLATILE_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.volatileCooldown", 400.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Volatile Cooldown");
//	public static final IAttribute VOLATILE_FLIGHT_SPEED_ATTRIB = (new RangedAttribute(null, "bl.volatileFlightSpeed", 0.25D, 0.0D, 5.0D)).setDescription("Volatile Flight Speed");
//	public static final IAttribute VOLATILE_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.volatileLength", 600.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Volatile Length");
//	public static final IAttribute VOLATILE_MAX_DAMAGE_ATTRIB = (new RangedAttribute(null, "bl.volatileMaxDamage", 20.0D, 0.0D, Double.MAX_VALUE)).setDescription("Volatile Max Damage");
//
//	private static final DataParameter<Byte> ATTACK_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.BYTE);
//	private static final DataParameter<Float> ANIMATION_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.FLOAT);
//	private static final DataParameter<Byte> VOLATILE_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.BYTE);
//	private static final DataParameter<Byte> GUARD_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.BYTE);
//	private static final DataParameter<Integer> REPAIR_X_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.VARINT);
//	private static final DataParameter<Integer> REPAIR_Y_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.VARINT);
//	private static final DataParameter<Integer> REPAIR_Z_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.VARINT);
//
//	private EntityAIAttackMelee meleeAttack = new EntityAIAttackMelee(this, 0.5D, false);
//	private EntityPlayer previousTarget;
//	private boolean updateHasBeenSeen = false;
//
//	//Volatile stuff
//	private int volatileCooldown = (int) VOLATILE_COOLDOWN_ATTRIB.getDefaultValue() / 2 + 20;
//	private int volatileProgress = 0;
//	private float volatileReceivedDamage = 0.0F;
//	private double waypointX;
//	private double waypointY;
//	private double waypointZ;
//	private boolean prevVolatile = false;
//
//	//Wight temple stuff
//	private String locationViolatorUUID = null;
//	private boolean breakBlock = false;
//	private Block repairBlock = null;
//	private int repairMeta = 0;
//	private int repairX = 0;
//	private int repairY = 0;
//	private int repairZ = 0;
//	private boolean canTurnVolatile = true;
//	private boolean canTurnVolatileOnTarget = false;
//
//	public EntityWightOld(World world) {
//		super(world);
//		setSize(0.7F, 2.2F);
//		this.setPathPriority(PathNodeType.WATER, 0.0f);
//		this.tasks.addTask(0, new EntityAISwimming(this));
//		this.tasks.addTask(1, this.meleeAttack);
//		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
//		this.tasks.addTask(3, new EntityAIWander(this, 0.3D));
//		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
//		this.tasks.addTask(5, new EntityAILookIdle(this));
//		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
//	}
//
//	@Override
//	protected void entityInit() {
//		super.entityInit();
//		this.dataManager.register(ATTACK_STATE_DW, (byte) 0);
//		this.dataManager.register(ANIMATION_STATE_DW, (float) 1);
//		this.dataManager.register(VOLATILE_STATE_DW, (byte) 0);
//		this.dataManager.register(GUARD_STATE_DW, (byte) 0);
//		this.dataManager.register(REPAIR_X_DW, 0);
//		this.dataManager.register(REPAIR_Y_DW, 0);
//		this.dataManager.register(REPAIR_Z_DW, 0);
//	}
//
//	@Override
//	protected void applyEntityAttributes() {
//		super.applyEntityAttributes();
//		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
//		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(76.0D);
//		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
//		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D);
//
//		this.getAttributeMap().registerAttribute(VOLATILE_HEALTH_START_ATTRIB);
//		this.getAttributeMap().registerAttribute(VOLATILE_COOLDOWN_ATTRIB);
//		this.getAttributeMap().registerAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB);
//		this.getAttributeMap().registerAttribute(VOLATILE_LENGTH_ATTRIB);
//		this.getAttributeMap().registerAttribute(VOLATILE_MAX_DAMAGE_ATTRIB);
//	}
//
//	@Override
//	public void writeEntityToNBT(NBTTagCompound nbt) {
//		super.writeEntityToNBT(nbt);
//		nbt.setByte("volatileState", this.getDataManager().get(VOLATILE_STATE_DW));
//		nbt.setInteger("volatileCooldown", this.volatileCooldown);
//		nbt.setInteger("volatileProgress", this.volatileProgress);
//		nbt.setFloat("volatileReceivedDamage", this.volatileReceivedDamage);
//		nbt.setBoolean("isLocationGuard", this.isLocationGuard());
//		if (this.locationViolatorUUID != null)
//			nbt.setString("locationViolatorUUID", this.locationViolatorUUID);
//
//		nbt.setBoolean("isRepairGuard", this.isRepairGuard());
//		nbt.setInteger("repairMeta", this.repairMeta);
//		nbt.setInteger("repairX", this.repairX);
//		nbt.setInteger("repairY", this.repairY);
//		nbt.setInteger("repairZ", this.repairZ);
//		nbt.setBoolean("breakBlock", this.breakBlock);
//		if (this.repairBlock != null)
//			nbt.setString("repairBlock", Block.REGISTRY.getNameForObject(this.repairBlock).toString());
//		nbt.setBoolean("canTurnVolatile", this.canTurnVolatile);
//		nbt.setBoolean("canTurnVolatileOnTarget", this.canTurnVolatileOnTarget);
//	}
//
//	@Override
//	public void readEntityFromNBT(NBTTagCompound nbt) {
//		super.readEntityFromNBT(nbt);
//		this.dataManager.set(VOLATILE_STATE_DW, nbt.getByte("volatileState"));
//		this.volatileCooldown = nbt.getInteger("volatileCooldown");
//		this.volatileProgress = nbt.getInteger("volatileProgress");
//		this.volatileReceivedDamage = nbt.getFloat("volatileReceivedDamage");
//		if (nbt.getBoolean("isLocationGuard"))
//			this.dataManager.set(GUARD_STATE_DW, (byte) 1);
//		if (this.isLocationGuard() && nbt.hasKey("locationViolatorUUID"))
//			this.locationViolatorUUID = nbt.getString("locationViolatorUUID");
//
//		if (nbt.getBoolean("isRepairGuard"))
//			this.dataManager.set(GUARD_STATE_DW, (byte) 2);
//		if (this.isRepairGuard()) {
//			this.repairMeta = nbt.getInteger("repairMeta");
//			this.repairX = nbt.getInteger("repairX");
//			this.repairY = nbt.getInteger("repairY");
//			this.repairZ = nbt.getInteger("repairZ");
//			this.breakBlock = nbt.getBoolean("breakBlock");
//			this.repairBlock = Block.REGISTRY.getObject(new ResourceLocation(nbt.getString("repairBlock")));
//		}
//		this.canTurnVolatile = nbt.getBoolean("canTurnVolatile");
//		this.canTurnVolatileOnTarget = nbt.getBoolean("canTurnVolatileOnTarget");
//	}
//
//	@Override
//	public boolean getCanSpawnHere() {
//		return worldObj.checkNoEntityCollision(getEntityBoundingBox()) && worldObj.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !worldObj.containsAnyLiquid(getEntityBoundingBox());
//	}
//
//	@Override
//	public int getMaxSpawnedInChunk() {
//		return 3;
//	}
//
//	@Nullable
//	@Override
//	protected SoundEvent getAmbientSound() {
//		return SoundRegistry.WIGHT_MOAN;
//	}
//
//	@Override
//	protected SoundEvent getHurtSound() {
//		return SoundRegistry.WIGHT_HURT;
//	}
//
//	@Override
//	protected SoundEvent getDeathSound() {
//		return SoundRegistry.WIGHT_DEATH;
//	}
//
//	public boolean canPossess(EntityLivingBase entity) {
//		return !this.isRepairGuard() && (this.isLocationGuard() || !(entity instanceof EntityPlayer && ((EntityPlayer) entity).inventory.getStackInSlot(103) != null && (((EntityPlayer) entity).inventory.getStackInSlot(103).getItem() == ItemRegistry.SKULL_MASK)));
//	}
//
//	@Override
//	public void onUpdate() {
//		if (!this.worldObj.isRemote) {
//			if (this.isLocationGuard() && (this.getViolator() == null || !this.getViolator().isEntityAlive())) {
//				this.setDead();
//			} else if (this.isLocationGuard() && this.getViolator() != null) {
//				/*if (!LocationStorage.isLocationGuarded(this.getViolator())) {
//					this.setDead();
//				} else {
//					this.setAttackTarget(this.getViolator());
//				}*/
//			}
//		}
//
//		if (!this.isRepairGuard()) {
//			EntityPlayer target = this.getAttackTarget() instanceof EntityPlayer ? (EntityPlayer) this.getAttackTarget() : null;
//			if (target == null || target.isDead || target.getDistanceToEntity(this) > this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue()) {
//				target = this.worldObj.getClosestPlayer(posX, posY, posZ, 16.0D, false);
//			}
//
//			if (target != null && !target.isSneaking()) {
//				this.setTargetSpotted(target, true);
//			}
//
//			if (target != null && target != this.previousTarget && target.isSneaking()) {
//				this.setTargetSpotted(target, false);
//			}
//
//			if (target == null && this.previousTarget != null) {
//				this.setTargetSpotted(target, false);
//			}
//
//			/*TODO add boss
//            if(this.getAttackTarget() instanceof EntityFortressBoss)
//                this.setTargetSpotted(null, false);
//            if(this.getRidingEntity() instanceof EntityFortressBoss)
//                this.dismountEntity(this.getRidingEntity());*/
//
//			if(!this.worldObj.isRemote) {
//			if (this.getAttackTarget() != null) {
//				if (getAnimation() > 0)
//					setAnimation(getAnimation() - 0.1F);
//			} else {
//				if (getAnimation() < 1)
//					setAnimation(getAnimation() + 0.1F);
//
//				if (getAnimation() == 0 && previousTarget != null) {
//					previousTarget = null;
//				}
//			}
//
//			}
//			if (this.getAttackTarget() == null) {
//				this.canTurnVolatileOnTarget = false;
//			}
//
//			if (!this.worldObj.isRemote && getAttackTarget() != null) {
//				this.dataManager.set(ATTACK_STATE_DW, (byte) 1);
//
//				if (!this.isVolatile() && this.canPossess(this.getAttackTarget()) && this.canTurnVolatile && this.canTurnVolatileOnTarget) {
//					if (this.volatileCooldown > 0)
//						this.volatileCooldown--;
//					if (this.getHealth() <= this.getMaxHealth() * this.getEntityAttribute(VOLATILE_HEALTH_START_ATTRIB).getAttributeValue() && this.volatileCooldown <= 0) {
//						this.setVolatile(true);
//						this.volatileCooldown = this.getVolatileCooldown() + this.worldObj.rand.nextInt(this.getVolatileCooldown()) + 20;
//						this.volatileProgress = 0;
//					}
//				} else if (this.isVolatile() && !this.canPossess(this.getAttackTarget())) {
//					this.setVolatile(false);
//				}
//			}
//
//			if (this.isVolatile()) {
//				if (this.getAttackTarget() != null) {
//					if (this.getRidingEntity() == null) {
//						double dx = this.getAttackTarget().posX - this.posX;
//						double dz = this.getAttackTarget().posZ - this.posZ;
//						double dy;
//						if (this.getAttackTarget() instanceof EntityLivingBase) {
//							EntityLivingBase entitylivingbase = this.getAttackTarget();
//							dy = entitylivingbase.posY + (double) entitylivingbase.getEyeHeight() - (this.posY + (double) this.getEyeHeight());
//						} else {
//							dy = (this.getAttackTarget().getEntityBoundingBox().minY + this.getAttackTarget().getEntityBoundingBox().maxY) / 2.0D - (this.posY + (double) this.getEyeHeight());
//						}
//						double dist = (double) MathHelper.sqrt_double(dx * dx + dz * dz);
//						float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
//						float pitch = (float) (-(Math.atan2(dy, dist) * 180.0D / Math.PI));
//						this.setRotation(yaw, pitch);
//						if (this.worldObj.isRemote) {
//							this.setRotationYawHead(yaw);
//						}
//					} else {
//						this.setRotation(this.getRidingEntity().rotationYaw, 0);
//						if (this.worldObj.isRemote) {
//							this.setRotationYawHead(this.getRidingEntity().rotationYaw);
//						}
//					}
//				}
//				if (!this.worldObj.isRemote) {
//					if (this.getRidingEntity() != null && this.getRidingEntity().isDead) {
//						this.dismountEntity(this.getRidingEntity());
//					}
//					if (this.volatileProgress < this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
//						this.volatileProgress++;
//					}
//					if (this.volatileProgress >= 20) {
//						if (this.getAttackTarget() != null) {
//							this.waypointX = this.getAttackTarget().posX;
//							this.waypointY = this.getAttackTarget().getEntityBoundingBox().minY + (this.getAttackTarget().getEntityBoundingBox().maxY - this.getAttackTarget().getEntityBoundingBox().minY) / 2.0D;
//							this.waypointZ = this.getAttackTarget().posZ;
//						} else {
//							this.waypointX = this.posX;
//							this.waypointY = this.posY;
//							this.waypointZ = this.posZ;
//						}
//					} else {
//						this.waypointX = this.posX;
//						this.waypointY = this.posY + 3.0D;
//						this.waypointZ = this.posZ;
//					}
//					if (getAttackTarget() != null && getDistanceToEntity(getAttackTarget()) < 1) {
//						onCollideWithEntity(getAttackTarget());
//					}
//					if (this.getRidingEntity() != null) {
//						if (this.ticksExisted % 30 == 0) {
//							/* TODO add EntityVolatileSoul
//                            List<EntityVolatileSoul> existingSouls = this.worldObj.getEntitiesWithinAABB(EntityVolatileSoul.class, this.getEntityBoundingBox().expand(16.0D, 16.0D, 16.0D));
//                            if (existingSouls.size() < 16) {
//                                EntityVolatileSoul soul = new EntityVolatileSoul(this.worldObj);
//                                float mx = this.worldObj.rand.nextFloat() - 0.5F;
//                                float my = this.worldObj.rand.nextFloat() / 2.0F;
//                                float mz = this.worldObj.rand.nextFloat() - 0.5F;
//                                Vec3d dir = new Vec3d(mx, my, mz).normalize();
//                                soul.setOwner(this.getUniqueID().toString());
//                                soul.setLocationAndAngles(this.posX + dir.xCoord * 0.5D, this.posY + dir.yCoord * 1.5D, this.posZ + dir.zCoord * 0.5D, 0, 0);
//                                soul.setThrowableHeading(mx * 2.0D, my * 2.0D, mz * 2.0D, 1.0F, 1.0F);
//                                this.worldObj.spawnEntityInWorld(soul);
//                            }*/
//						}
//					}
//				} else {
//					if (this.getRidingEntity() == null || this.ticksExisted % 4 == 0) {
//						this.spawnVolatileParticles();
//					}
//				}
//				this.setSize(0.7F, 0.7F);
//			} else {
//				this.setSize(0.7F, 2.2F);
//			}
//
//			if (!this.worldObj.isRemote && getAttackTarget() == null) {
//				this.dataManager.set(ATTACK_STATE_DW, (byte) 0);
//			}
//
//			if (this.prevVolatile != this.isVolatile()) {
//				if (this.worldObj.isRemote) {
//					for (int i = 0; i < 80; i++) {
//						double px = this.posX + this.worldObj.rand.nextFloat() * 0.7F;
//						double py = this.posY + this.worldObj.rand.nextFloat() * 2.2F;
//						double pz = this.posZ + this.worldObj.rand.nextFloat() * 0.7F;
//						Vec3d vec = new Vec3d(px, py, pz).subtract(new Vec3d(this.posX + 0.35F, this.posY + 1.1F, this.posZ + 0.35F)).normalize();
//						BLParticles.SWAMP_SMOKE.spawn(this.worldObj, px, py, pz, ParticleFactory.ParticleArgs.get().withMotion(vec.xCoord * 0.25F, vec.yCoord * 0.25F, vec.zCoord * 0.25F));
//					}
//				}
//				this.worldObj.playSound(this.posX, this.posY, this.posZ, SoundRegistry.WIGHT_ATTACK, SoundCategory.HOSTILE, 1.6F, 1.0F, false);
//			}
//			this.prevVolatile = this.isVolatile();
//		} else {
//			this.setAttackTarget(null);
//			this.setVolatile(true);
//			this.waypointX = this.repairX + 0.5D;
//			this.waypointY = this.repairY + 0.5D;
//			this.waypointZ = this.repairZ + 0.5D;
//
//			if (this.getDistance(this.repairX + 0.5D, this.repairY + 0.5D, this.repairZ + 0.5D) <= this.getEntityAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue() + 0.1D) {
//				if (this.breakBlock) {
//					this.worldObj.setBlockToAir(new BlockPos(this.repairX, this.repairY, this.repairZ));
//				} else {
//					if (this.repairBlock != null)
//						this.worldObj.setBlockState(new BlockPos(this.repairX, this.repairY, this.repairZ), repairBlock.getStateFromMeta(this.repairMeta), 2);
//				}
//				this.setDead();
//			}
//
//			if (!this.worldObj.isRemote) {
//				this.dataManager.set(REPAIR_X_DW, this.repairX);
//				this.dataManager.set(REPAIR_Y_DW, this.repairY);
//				this.dataManager.set(REPAIR_Z_DW, this.repairZ);
//			} else {
//				this.spawnVolatileParticles();
//				this.repairX = this.getDataManager().get(REPAIR_X_DW);
//				this.repairY = this.getDataManager().get(REPAIR_Y_DW);
//				this.repairZ = this.getDataManager().get(REPAIR_Z_DW);
//				for (int i = 0; i <= 2; i++) {
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY, this.repairZ);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY, this.repairZ + 1);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY + 1, this.repairZ);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 0.5D * i, this.repairY + 1, this.repairZ + 1);
//				}
//				for (int i = 0; i <= 2; i++) {
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY + 0.5D * i, this.repairZ);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY + 0.5D * i, this.repairZ + 1);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY + 0.5D * i, this.repairZ);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY + 0.5D * i, this.repairZ + 1);
//				}
//				for (int i = 0; i <= 2; i++) {
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY, this.repairZ + 0.5D * i);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX, this.repairY + 1, this.repairZ + 0.5D * i);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY, this.repairZ + 0.5D * i);
//					BLParticles.STEAM_PURIFIER.spawn(this.worldObj, this.repairX + 1, this.repairY + 1, this.repairZ + 0.5D * i);
//				}
//			}
//			AxisAlignedBB repairBB = new AxisAlignedBB(this.repairX, this.repairY, this.repairZ, this.repairX + 1, this.repairY + 1, this.repairZ + 1);
//			List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, repairBB);
//			for (EntityLivingBase entity : entities) {
//				if (entity != this && entity instanceof EntityWight == false) {
//					if (entity.posX < this.repairX + 0.5D) {
//						entity.motionX = -0.5D;
//					} else if (entity.posX > this.repairX + 0.5D) {
//						entity.motionX = 0.5D;
//					}
//					if (entity.posY < this.repairY + 0.5D) {
//						entity.motionY = -0.5D;
//					} else if (entity.posY > this.repairY + 0.5D) {
//						entity.motionY = 0.5D;
//					}
//					if (entity.posZ < this.repairZ + 0.5D) {
//						entity.motionZ = -0.5D;
//					} else if (entity.posZ > this.repairZ + 0.5D) {
//						entity.motionZ = 0.5D;
//					}
//					entity.attackEntityFrom(DamageSource.magic, 4.0F);
//					entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 160, 2));
//					entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 160, 2));
//				}
//			}
//		}
//
//		super.onUpdate();
//	}
//
//	public void onCollideWithEntity(EntityLivingBase entity) {
//		if (!this.worldObj.isRemote && this.isVolatile() && entity == this.getAttackTarget()) {
//			startRiding(entity, true);
//			this.volatileProgress = 20;
//		}
//	}
//
//	public void setLocationGuard(EntityPlayer violator) {
//		this.dataManager.set(GUARD_STATE_DW, (byte) 1);
//		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(999);
//		this.setHealth(999);
//		this.locationViolatorUUID = violator.getUniqueID().toString();
//	}
//
//	public EntityPlayer getViolator() {
//		if (this.locationViolatorUUID != null) {
//			try {
//				return this.worldObj.getPlayerEntityByUUID(UUID.fromString(this.locationViolatorUUID));
//			} catch (Exception ex) {
//				this.locationViolatorUUID = null;
//			}
//		}
//		return null;
//	}
//
//	public boolean isLocationGuard() {
//		return this.dataManager.get(GUARD_STATE_DW) == 1;
//	}
//
//	public void setRepairGuard(Block block, int x, int y, int z, int meta) {
//		this.dataManager.set(GUARD_STATE_DW, (byte) 2);
//		this.breakBlock = false;
//		this.repairBlock = block;
//		this.repairX = x;
//		this.repairY = y;
//		this.repairZ = z;
//		this.repairMeta = meta;
//	}
//
//	public void setRepairGuard(int x, int y, int z) {
//		this.dataManager.set(GUARD_STATE_DW, (byte) 2);
//		this.breakBlock = true;
//		this.repairX = x;
//		this.repairY = y;
//		this.repairZ = z;
//	}
//
//	public boolean isRepairGuard() {
//		return this.dataManager.get(GUARD_STATE_DW) == 2;
//	}
//
//	@SideOnly(Side.CLIENT)
//	private void spawnVolatileParticles() {
//		final double radius = 0.3F;
//		final double cx = this.posX;
//		final double cy = this.posY + 0.35D;
//		final double cz = this.posZ;
//		for (int i = 0; i < 8; i++) {
//			double px = this.worldObj.rand.nextFloat() * 0.7F;
//			double py = this.worldObj.rand.nextFloat() * 0.7F;
//			double pz = this.worldObj.rand.nextFloat() * 0.7F;
//			Vec3d vec = new Vec3d(px, py, pz).subtract( new Vec3d(0.35F, 0.35F, 0.35F)).normalize();
//			px = cx + vec.xCoord * radius;
//			py = cy + vec.yCoord * radius;
//			pz = cz + vec.zCoord * radius;
//			BLParticles.STEAM_PURIFIER.spawn(this.worldObj, px, py, pz);
//		}
//	}
//
//
//	@Override
//	public void fall(float distance, float damageMultiplier) {
//		if (!this.isVolatile()) {
//			super.fall(distance, damageMultiplier);
//		}
//	}
//
//	@Override
//	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
//		if (!this.isVolatile()) {
//			super.updateFallState(y, onGroundIn, state, pos);
//		}
//	}
//
//	@Override
//	public void moveEntityWithHeading(float strafe, float forward) {
//		if (this.isVolatile()) {
//			if (this.isInWater()) {
//				this.moveRelative(strafe, forward, 0.02F);
//				this.moveEntity(this.motionX, this.motionY, this.motionZ);
//				this.motionX *= 0.800000011920929D;
//				this.motionY *= 0.800000011920929D;
//				this.motionZ *= 0.800000011920929D;
//			} else if (this.handleWaterMovement()) {
//				this.moveRelative(strafe, forward, 0.02F);
//				this.moveEntity(this.motionX, this.motionY, this.motionZ);
//				this.motionX *= 0.5D;
//				this.motionY *= 0.5D;
//				this.motionZ *= 0.5D;
//			} else {
//				float friction = 0.91F;
//
//				if (this.onGround) {
//					friction = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
//				}
//
//				float groundFriction = 0.16277136F / (friction * friction * friction);
//				this.moveRelative(strafe, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
//				friction = 0.91F;
//
//				if (this.onGround) {
//					friction = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
//				}
//
//				this.moveEntity(this.motionX, this.motionY, this.motionZ);
//				this.motionX *= (double) friction;
//				this.motionY *= (double) friction;
//				this.motionZ *= (double) friction;
//			}
//
//			this.prevLimbSwingAmount = this.limbSwingAmount;
//			double dx = this.posX - this.prevPosX;
//			double dz = this.posZ - this.prevPosZ;
//			float distanceMoved = MathHelper.sqrt_double(dx * dx + dz * dz) * 4.0F;
//
//			if (distanceMoved > 1.0F) {
//				distanceMoved = 1.0F;
//			}
//
//			this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
//			this.limbSwing += this.limbSwingAmount;
//		} else {
//			super.moveEntityWithHeading(strafe, forward);
//		}
//	}
//
//	@Override
//	protected void updateAITasks() {
//		super.updateAITasks();
//
//		if (this.isVolatile()) {
//			this.noClip = true;
//
//			if (this.worldObj.isRemote) {
//				return;
//			}
//
//			if (this.volatileProgress >= this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
//				this.motionY -= 0.075D;
//				if (this.onGround) {
//					this.setVolatile(false);
//				}
//			} else {
//				double dx = this.waypointX - this.posX;
//				double dy = this.waypointY - this.posY;
//				double dz = this.waypointZ - this.posZ;
//				double dist = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
//				double speed = this.getEntityAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue();
//				if (dist <= speed) {
//					this.waypointX = this.posX;
//					this.waypointY = this.posY;
//					this.waypointZ = this.posZ;
//				} else {
//					this.motionX = dx / dist * speed;
//					this.motionY = dy / dist * speed;
//					this.motionZ = dz / dist * speed;
//				}
//			}
//		} else {
//			this.noClip = false;
//		}
//	}
//
//	private void setTargetSpotted(EntityPlayer target, boolean hasBeenSeen) {
//		if (hasBeenSeen) {
//			if (!updateHasBeenSeen) {
//				updateHasBeenSeen = true;
//				tasks.addTask(1, meleeAttack);
//				setAttackTarget(target);
//				previousTarget = target;
//			}
//		} else {
//			if (updateHasBeenSeen) {
//				updateHasBeenSeen = false;
//				setAttackTarget(null);
//				tasks.removeTask(meleeAttack);
//			}
//		}
//	}
//
//	private void setAnimation(float progress) {
//		dataManager.set(ANIMATION_STATE_DW, progress);
//	}
//
//	public float getAnimation() {
//		return dataManager.get(ANIMATION_STATE_DW);
//	}
//
//	@Override
//	public boolean isEntityInvulnerable(DamageSource source) {
//		return dataManager.get(ATTACK_STATE_DW) == 0;
//	}
//
//	@Override
//	public boolean canBePushed() {
//		return dataManager.get(ATTACK_STATE_DW) == 1;
//	}
//
//	@Override
//	protected void dropFewItems(boolean recentlyHit, int looting) {
//		if (!this.isLocationGuard() && !this.isRepairGuard())
//			entityDropItem(new ItemStack(ItemRegistry.WIGHT_HEART), 0F);
//	}
//
//	@Override
//	public boolean attackEntityFrom(DamageSource source, float damage) {
//		if (this.isVolatile() && source == DamageSource.inWall) {
//			return false;
//		}
//		float prevHealth = this.getHealth();
//		boolean ret = super.attackEntityFrom(source, damage);
//		float dealtDamage = prevHealth - this.getHealth();
//		if (this.isVolatile() && this.getRidingEntity() != null) {
//			this.volatileReceivedDamage += dealtDamage;
//			if (this.volatileReceivedDamage >= this.getEntityAttribute(VOLATILE_MAX_DAMAGE_ATTRIB).getAttributeValue()) {
//				this.setVolatile(false);
//			}
//		}
//		if (this.getAttackTarget() != null && source instanceof EntityDamageSource && source.getEntity() == this.getAttackTarget())
//			this.canTurnVolatileOnTarget = true;
//		return ret;
//	}
//
//	@Override
//	public boolean attackEntityAsMob(Entity entity) {
//		if (this.isVolatile()) {
//			return false;
//		}
//		if (super.attackEntityAsMob(entity)) {
//			if (entity == this.getAttackTarget())
//				this.canTurnVolatileOnTarget = true;
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	protected boolean canDespawn() {
//		return !this.isLocationGuard() && !this.isRepairGuard();
//	}
//
//	@Override
//	public double getYOffset() {
//		if (this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityPlayer && this.worldObj.isRemote) {
//			if (this.getRidingEntity() == TheBetweenlands.proxy.getClientPlayer()) {
//				return -1.6D;
//			} else {
//				return 0.05D;
//			}
//		}
//		return 0.0f;
//	}
//
//	public int getVolatileCooldown() {
//		return (int) this.getEntityAttribute(VOLATILE_COOLDOWN_ATTRIB).getAttributeValue();
//	}
//
//	public void setVolatile(boolean v) {
//		if (!this.canTurnVolatile)
//			v = false;
//		this.dataManager.set(VOLATILE_STATE_DW, (byte) (v ? 1 : 0));
//		this.volatileProgress = 0;
//		this.volatileReceivedDamage = 0.0F;
//		if (!v && this.getRidingEntity() != null) {
//			this.dismountEntity(this.getRidingEntity());
//			this.dismountRidingEntity();
//		}
//	}
//
//	public boolean isVolatile() {
//		return this.dataManager.get(VOLATILE_STATE_DW) == 1;
//	}
//
//	public void setCanTurnVolatile(boolean canTurnVolatile) {
//		this.canTurnVolatile = canTurnVolatile;
//	}
//}
