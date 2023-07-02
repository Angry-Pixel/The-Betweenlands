package thebetweenlands.common.entity.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntityDropHeldCloud;
import thebetweenlands.common.entity.ai.EntityAIFollowTarget;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySporeMinion extends EntityMob implements IEntityBL {
	private float jumpHeightOverride = -1;
	public int animation_1 = 0, prev_animation_1 = 0;
	public int animation_2 = 0, prev_animation_2 = 0;
	protected static final DataParameter<Boolean> IS_FALLING = EntityDataManager.<Boolean>createKey(EntitySporeMinion.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Integer> TYPE = EntityDataManager.<Integer>createKey(EntitySporeMinion.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> INFLATE_SIZE = EntityDataManager.<Integer>createKey(EntitySporeMinion.class, DataSerializers.VARINT);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntitySporeMinion.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<BlockPos> OWNER_POS = EntityDataManager.createKey(EntitySporeMinion.class, DataSerializers.BLOCK_POS);
	private static final byte HEAL_PARTICLES = 110;
	private static final byte HURT_PARTICLES = 111;
	private static final byte POPPING_PARTICLES = 112;
	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;
	
	private EntityAIFollowTarget moveToTarget;
	private boolean canFollow = false;
	private EntityAIAttackMelee meleeAttack;
	private int aggroCooldown = 100;
	private boolean canAttack = false;
	private EntityAIWander wander;
	private EntityAIAvoidEntity<EntityPlayer> runAway;
	private EntityAINearestAttackableTarget<EntityPlayer> target;
	private boolean poppedNaturally = false;

	public static DamageSource sporeminionDamage;

	public EntitySporeMinion(World world) {
		super(world);
		setSize(0.75F, 0.75F);
		stepHeight = 1.0F;
		this.experienceValue = 1;
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		sporeminionDamage = new EntityDamageSource("bl.sporeminion_damage", this);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_FALLING, false);
		dataManager.register(TYPE, rand.nextInt(4));
		dataManager.register(INFLATE_SIZE, 0);
		dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
		dataManager.register(OWNER_POS, new BlockPos(0,0,0));
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		moveToTarget = new EntityAIFollowTarget(this, new EntityAIFollowTarget.FollowClosest(this, EntityPlayer.class, 32D), 1D, 1F, 32.0F, false);
		meleeAttack = new EntityAIAttackMelee(this, 0.6D, false);
		wander = new EntityAIWander(this, 0.5D);
		runAway = new EntityAIAvoidEntity<EntityPlayer>(this, EntityPlayer.class, 5.0F, 0.51D, 0.51D);
		target =  new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true);
		
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, meleeAttack);
		tasks.addTask(2, wander);
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(4, new EntityAILookIdle(this));
		targetTasks.addTask(0, target);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		double attackBuff = 0D;
		double moveSpeedBuff = 0D;
		double healthBuff = 0D;
		switch (getType()) {
		case 0:
			attackBuff = 1.5D;
			moveSpeedBuff = 0.1D;
			break;
		case 1:
			healthBuff = 15D;
			break;
		case 2:
			moveSpeedBuff = -0.1D;
			healthBuff = 25D;
			attackBuff = -0.5D;
			break;
		case 3:
			healthBuff = 15D;
			break;
		}
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D + attackBuff);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D + moveSpeedBuff);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D + healthBuff);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
	}

	@Override
	public void onUpdate() {
		prev_animation_1 = animation_1;
		prev_animation_2 = animation_2;
		if (!getEntityWorld().isRemote) {
			if (!this.isInWater()) {
				boolean canSpin = (this.getRidingEntity() != null ? !this.getRidingEntity().onGround : !onGround) && !this.isInWeb && !this.isInWater() && !this.isInLava() && world.isAirBlock(getPosition().down());
				if (canSpin && (motionY < 0D || (this.getRidingEntity() != null && this.getRidingEntity().motionY < 0))) {
					if (!getIsFalling())
						setIsFalling(true);
					motionY *= 0.7D;
					if(this.getRidingEntity() != null) {
						this.getRidingEntity().motionY *= 0.7D;
						this.getRidingEntity().fallDistance = 0;
					}
				} else {
					if (!canSpin && getIsFalling()) {
						setIsFalling(false);
					}
				}
			}

			if (getType() == 0) {
				if (aggroCooldown == 100 && !canAttack) {
					tasks.removeTask(runAway);
					tasks.addTask(1, meleeAttack);
					targetTasks.addTask(0, target);
					canAttack = true;
				}

				if (aggroCooldown == 0 && canAttack) {
					tasks.removeTask(meleeAttack);
					targetTasks.removeTask(target);
					tasks.addTask(1, runAway);
					canAttack = false;
				}

				if (aggroCooldown <= 100)
					aggroCooldown++;
			}

			if (getType() == 2 && !canFollow) {
				tasks.removeTask(wander);
				tasks.addTask(1, moveToTarget);
				tasks.removeTask(meleeAttack);
				canFollow = true;
			}

			if (getType() == 2 && ticksExisted <= 200) {
				if (getInflateSize() <= 0)
					setInflateSize(0);
				if (getInflateSize() >= 100)
					explode();
				if (getAttackTarget() == null)
					setInflateSize(getInflateSize() - 4);
				if (getAttackTarget() != null) {
					float distance = (float) getDistance(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);
					if (getInflateSize() < 100 && distance <= 2)
						setInflateSize(getInflateSize() + 4);
					if (getInflateSize() < 100 && distance > 2)
						setInflateSize(getInflateSize() - 4);
				}
			}

			if (ticksExisted > 200 && (getType() == 1 ||  getType() == 2 ||  getType() == 3)) {
				if (getInflateSize() < 100)
					setInflateSize(getInflateSize() + 4);
				if (getInflateSize() >= 100) {
					poppedNaturally = true;
					explode();
				}
			}

			if (getType() == 3) {
				if (!canAttack) {
					tasks.removeTask(meleeAttack);
					targetTasks.removeTask(target);
					tasks.addTask(1, runAway);
					canAttack = true; // not really but just need a toggle atm
				}
			}
		}

		this.prevFloatingRotationTicks = this.floatingRotationTicks;
		if(this.getIsFalling()) {
			this.floatingRotationTicks += 30;
			float wrap = MathHelper.wrapDegrees(this.floatingRotationTicks) - this.floatingRotationTicks;
			this.floatingRotationTicks +=wrap;
			this.prevFloatingRotationTicks += wrap;
		} else {
			this.floatingRotationTicks = 0;
		}

		if(ticksExisted >= 20) {
			if (animation_1 <= 10)
				animation_1++;
			if (animation_1 > 10) {
				prev_animation_1 = animation_1 = 10;
				if (animation_2 <= 10)
					animation_2++;
				if (animation_2 > 10)
					prev_animation_2 = animation_2 = 10;
			}
		}
		super.onUpdate();
	}

	@Override
	protected void onDeathUpdate() {
		++this.deathTime;

		if (this.deathTime == 20) {
			if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot()
					&& this.world.getGameRules().getBoolean("doMobLoot"))) {
				int i = this.getExperiencePoints(this.attackingPlayer);
				i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
				while (i > 0) {
					int j = EntityXPOrb.getXPSplit(i);
					i -= j;
					this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
				}
			}

			this.setDead();

			/*
			 * for (int k = 0; k < 20; ++k) { double d2 =
			 * this.rand.nextGaussian() * 0.02D; double d0 =
			 * this.rand.nextGaussian() * 0.02D; double d1 =
			 * this.rand.nextGaussian() * 0.02D;
			 * this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL,
			 * this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) -
			 * (double)this.width, this.posY + (double)(this.rand.nextFloat() *
			 * this.height), this.posZ + (double)(this.rand.nextFloat() *
			 * this.width * 2.0F) - (double)this.width, d2, d0, d1); }
			 */
		}
	}

	private void explode() {
		getEntityWorld().setEntityState(this, POPPING_PARTICLES);
		setDead();
		if (getType() == 2) {
			EntityDropHeldCloud cloud = new EntityDropHeldCloud(world);
			cloud.setPosition(posX, posY, posZ);
			world.spawnEntity(cloud);
		}
		world.playSound(null, getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.HOSTILE, 1F, 0.5F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		if (id == POPPING_PARTICLES) {
			for (int i = 0, amount = 20 + getEntityWorld().rand.nextInt(4); i < amount; i++) {
				double offSetX = getEntityWorld().rand.nextDouble() * 2F - 1F;
				double offSetZ = getEntityWorld().rand.nextDouble() * 2F - 1F;
				BLParticles.ITEM_BREAKING.spawn(world, this.posX + (float) offSetX + (world.rand.nextDouble() * 0.25D - 0.125D) , this.posY + 0.5F, this.posZ + (float) offSetZ + (world.rand.nextDouble() * 0.25D - 0.125D), ParticleArgs.get().withData(new ItemStack(ItemRegistry.PUFFSHROOM_TENDRIL)));
			}
		}
		//TODO - Ideally these should move along a vector from the entity position to the getOwnerPos() 
		if (id == HEAL_PARTICLES) {
			for (int i = 0, amount = 10; i < amount; i++) {
				double offSetX = getEntityWorld().rand.nextDouble() * 0.5F - 0.25F;
				double offSetY = getEntityWorld().rand.nextDouble() * 0.5F - 0.25F;
				double offSetZ = getEntityWorld().rand.nextDouble() * 0.5F - 0.25F;
				BLParticles.SPOREMINION_HEALTH.spawn(world, this.posX +  offSetX + world.rand.nextDouble() * 0.25D - 0.125D, this.posY + offSetY + 0.5F + world.rand.nextDouble() * 0.25D - 0.125D, this.posZ + offSetZ + world.rand.nextDouble() * 0.25D - 0.125D, ParticleArgs.get().withColor(0, 1, 0, 0.5F));
			}
		}
		if (id == HURT_PARTICLES) {
			for (int i = 0, amount = 10; i < amount; i++) {
				double offSetX = getEntityWorld().rand.nextDouble() * 0.5F - 0.25F;
				double offSetY = getEntityWorld().rand.nextDouble() * 0.5F - 0.25F;
				double offSetZ = getEntityWorld().rand.nextDouble() * 0.5F - 0.25F;
				BLParticles.SPOREMINION_HEALTH.spawn(world, this.posX +  offSetX + world.rand.nextDouble() * 0.25D - 0.125D, this.posY + offSetY + 0.5F + world.rand.nextDouble() * 0.25D - 0.125D, this.posZ + offSetZ + world.rand.nextDouble() * 0.25D - 0.125D, ParticleArgs.get().withColor(0, 0, 0, 0.5F));
			}
		}
	}

	@Override
	public void setDead() {
		// parent healing/damage
		if (getOwner() instanceof EntityBigPuffshroom) {
			EntityBigPuffshroom big_puffshroom = (EntityBigPuffshroom) getOwner();
			if (!poppedNaturally) {
				if (getType() == 1) {
					setOwnerPos(big_puffshroom.getPosition());
					getEntityWorld().setEntityState(this, HEAL_PARTICLES);
					big_puffshroom.heal(5F);
				}
				if (getType() == 3) {
					setOwnerPos(big_puffshroom.getPosition());
					getEntityWorld().setEntityState(this, HURT_PARTICLES);
					big_puffshroom.attackEntityFrom(sporeminionDamage, 5F);
				}
			}
		}
		super.setDead();
	}

	@Override
	protected boolean isMovementBlocked() {
		return getInflateSize() > 0;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (canEntityBeSeen(entity)) {
			if (super.attackEntityAsMob(entity)) {
				if (entity instanceof EntityLivingBase) {
					switch (getType()) {
					case 0:
						((EntityLivingBase)entity).knockBack(this, 0.5F, MathHelper.sin(this.rotationYaw * 3.141593F / 180.0F), -MathHelper.cos(this.rotationYaw * 3.141593F / 180.0F));
						if (!getEntityWorld().isRemote)
							aggroCooldown = 0;
						break;
					case 1:
						//this.heal(2F);
						//world.playSound(null, getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.HOSTILE, 0.5F, 2F);
						break;
					case 2:
					/*	if (entity instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) entity;
							if (!isWearingSilkMask(player)) {
								ItemStack stack = player.getHeldItemMainhand();
								if (!stack.isEmpty())
									player.dropItem(true);
							}
						}*/
						break;
					case 3:
						break;
					}
				}
			}
			return true;
		} else
			return false;
	}

    public boolean isWearingSilkMask(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer) {
        	ItemStack helmet = ((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        	if(!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SILK_MASK) {
        		return true;
        	}
        }
    	return false;
    }

    public float smoothedAngle(float partialTicks) {
        return this.prevFloatingRotationTicks + (this.floatingRotationTicks - this.prevFloatingRotationTicks) * partialTicks;
    }

    private float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);

        if (f > maxIncrease)
        {
            f = maxIncrease;
        }

        if (f < -maxIncrease)
        {
            f = -maxIncrease;
        }

        return angle + f;
    }
    
	private void setIsFalling(boolean state) {
		dataManager.set(IS_FALLING, state);
	}

	public boolean getIsFalling() {
		return dataManager.get(IS_FALLING);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPORELING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SPORELING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SPORELING_DEATH;
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return null;//LootTableRegistry.SPORELING;
	}

	public void setJumpHeightOverride(float jumpHeightOverride) {
		this.jumpHeightOverride = jumpHeightOverride;
	}

	@Override
	protected float getJumpUpwardsMotion() {
		if(this.jumpHeightOverride > 0) {
			float height = this.jumpHeightOverride;
			this.jumpHeightOverride = -1;
			return height;
		}
		return super.getJumpUpwardsMotion();
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
		}
		return livingdata;
	}

	public void setType(int skinType) {
		dataManager.set(TYPE, skinType);
	}

	public int getType() {
		return dataManager.get(TYPE);
	}

	public void setInflateSize(int size) {
		dataManager.set(INFLATE_SIZE, size);
	}

	public int getInflateSize() {
		return dataManager.get(INFLATE_SIZE);
	}

    public void setOwnerId(@Nullable UUID uuidIn) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uuidIn));
    }

	public void setOwnerPos(BlockPos pos) {
		dataManager.set(OWNER_POS, pos);
	}

	public BlockPos getOwnerPos() {
		return dataManager.get(OWNER_POS);
	}

    @Nullable
    public UUID getOwnerId() {
        return (UUID)((Optional)this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

	@Nullable
	public Entity getOwner() {
		if (!world.isRemote) {
			WorldServer serverWorld = (WorldServer) world;
			try {
				UUID uuid = getOwnerId();
				return uuid == null ? null : serverWorld.getEntityFromUuid(uuid);
			} catch (IllegalArgumentException nope) {
				return null;
			}
		}
		return null;
    }

    public boolean isOwner(Entity entityIn) {
        return entityIn == getOwner();
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
	       super.writeEntityToNBT(nbt);

	        if (getOwnerId() == null)
	        	nbt.setString("OwnerUUID", "");
	        else
	        	nbt.setString("OwnerUUID", getOwnerId().toString());

		nbt.setInteger("type", getType());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		 String ownerIdString;

	        if (nbt.hasKey("OwnerUUID", 8))
	        	ownerIdString = nbt.getString("OwnerUUID");
	        else
	        {
	            String ownerName = nbt.getString("Owner");
	            ownerIdString = PreYggdrasilConverter.convertMobOwnerIfNeeded(getServer(), ownerName);
	        }

	        if (!ownerIdString.isEmpty()) {
	            try {
	                setOwnerId(UUID.fromString(ownerIdString));
	            }
	            catch (Throwable throwable) {
	               // explode if owner is missing or dead?
	            }
	        }

		setType(nbt.getInteger("type"));
	}
}
