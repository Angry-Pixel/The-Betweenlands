package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityCameraOffset;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.common.entity.projectiles.EntitySludgeBall;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityDreadfulMummy extends EntityMob implements IEntityBL, IBLBoss, IEntityScreenShake, IEntityCameraOffset, IEntityMusic {
	
	private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);
	public static final IAttribute SPAWN_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.spawnLength", 180.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Spawning Length");
	public static final IAttribute SPAWN_OFFSET_ATTRIB = (new RangedAttribute(null, "bl.spawnOffset", 3.0D, -Integer.MAX_VALUE, Integer.MAX_VALUE)).setDescription("Spawning Y Offset");

	private static final DataParameter<Integer> SPAWNING_STATE_DW = EntityDataManager.<Integer>createKey(EntityDreadfulMummy.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PREY = EntityDataManager.<Integer>createKey(EntityDreadfulMummy.class, DataSerializers.VARINT);
	private static final DataParameter<Float> Y_OFFSET = EntityDataManager.<Float>createKey(EntityDreadfulMummy.class, DataSerializers.FLOAT);

	private static final int BREAK_COUNT = 20;

	public EntityDreadfulMummy(World world) {
		super(world);
		setSize(1.1F, 2.0F);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		tasks.addTask(4, new EntityAIWander(this, 0.25D));
		tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
	}

	private static final int SPAWN_MUMMY_COOLDOWN = 350;
	private int untilSpawnMummy = 0;
	private static final int SPAWN_SLUDGE_COOLDOWN = 150;
	private int untilSpawnSludge = 0;
	private float prevYOffset;

	private int eatPreyTimer = 60;
	public EntityLivingBase currentEatPrey;

	public int deathTicks = 0;

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SPAWNING_STATE_DW, 0);
		dataManager.register(PREY, 0);
		dataManager.register(Y_OFFSET, 0F);
	}
/*
	@Override
	public String pageName() {
		return null;
	}
*/
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(550.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

		getAttributeMap().registerAttribute(SPAWN_LENGTH_ATTRIB);
		getAttributeMap().registerAttribute(SPAWN_OFFSET_ATTRIB);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.DREADFUL_PEAT_MUMMY_DEATH;
	}

	@Override
	public double getYOffset() {
		return dataManager.get(Y_OFFSET);
	}

	public void setYOffset(float yOffset) {
		dataManager.set(Y_OFFSET, yOffset);
	}

	public float getCurrentOffset() {
		return (float) ((-getSpawnOffset() + getSpawningProgress() * getSpawnOffset()));
	}

	public double getSpawnOffset() {
		return getEntityAttribute(SPAWN_OFFSET_ATTRIB).getAttributeValue();
	}

	public int getSpawningState() {
		return dataManager.get(SPAWNING_STATE_DW);
	}

	public int getSpawningLength() {
		return (int) getEntityAttribute(SPAWN_LENGTH_ATTRIB).getAttributeValue();
	}

	public float getSpawningProgress() {
		if(getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / getSpawningLength() * getSpawningState();
	}

	public float getSpawningProgress(float delta) {
		if(getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / getSpawningLength() * (getSpawningState() + (getSpawningState() == getSpawningLength() ? 0 : delta));
	}

	/**
	 * Returns the interpolated relative spawning progress
	 * @param partialTicks
	 * @return
	 */
	public float getInterpolatedYOffsetProgress(float partialTicks) {
		return this.prevYOffset + (((float)this.getYOffset()) - this.prevYOffset) * partialTicks;
	}

	public void updateSpawningState() {
		int spawningState = getSpawningState();
		if(spawningState < getSpawningLength()) {
			dataManager.set(SPAWNING_STATE_DW, spawningState + 1);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevYOffset = (float) getYOffset();

		Entity prey = getPrey();
		if(prey instanceof EntityLivingBase) {
			currentEatPrey = (EntityLivingBase)prey;
			if(currentEatPrey != null) {
				updateEatPrey();
			}
		} else {
			currentEatPrey = null;
		}

		if(deathTicks > 80) {
			setYOffset(-(deathTicks - 80) * 0.08F);
		}

		if(getEntityWorld().isRemote) {
			if(getSpawningProgress() < 1.0F) {
				setYOffset(getCurrentOffset());
				motionX = 0;
				motionY = 0;
				motionZ = 0;
				if(getSpawningState() == getSpawningLength() - 1) {
					setPosition(posX, posY, posZ);
				}
				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningState() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.posX, this.posY - 1, this.posZ);
					IBlockState state = getEntityWorld().getBlockState(pos);
					double px = posX + rand.nextDouble() - 0.5F;
					double py = posY + rand.nextDouble() * 0.2 + 0.075;
					double pz = posZ + rand.nextDouble() - 0.5F;
					for (int i = 0, amount = rand.nextInt(20) + 15; i < amount; i++) {
						double ox = rand.nextDouble() * 0.1F - 0.05F;
						double oz = rand.nextDouble() * 0.1F - 0.05F;
						double motionX = rand.nextDouble() * 0.2 - 0.1;
						double motionY = rand.nextDouble() * 0.25 + 0.1;
						double motionZ = rand.nextDouble() * 0.2 - 0.1;
						getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(state));
					}
				}
			} else {
				setYOffset(0F);
			}
		} else {
			if(getSpawningProgress() < 1.0F) {

				if(getSpawningState() == 0) {
					getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_EMERGE, SoundCategory.HOSTILE, 1.2F, 1.0F);
				}
				updateSpawningState();

				setYOffset(getCurrentOffset());
				motionY = 0;
				motionX = 0;
				motionZ = 0;
				velocityChanged = true;

				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningState() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.posX, this.posY - 1, this.posZ);
					IBlockState blockState = this.world.getBlockState(pos);
					playSound(blockState.getBlock().getSoundType().getBreakSound(), this.rand.nextFloat() * 0.3F + 0.3F, this.rand.nextFloat() * 0.15F + 0.7F);
				}

				if(getAttackTarget() != null) faceEntity(getAttackTarget(), 360, 360);
				if(getSpawningState() == getSpawningLength() - 1) {
					setPosition(posX, posY, posZ);
				}
			} else if(deathTicks < 80) {
				setYOffset(0);
				prevYOffset = 0;
			}
		}

		if(!getEntityWorld().isRemote && isEntityAlive()) {
			if (getAttackTarget() != null) {
				AxisAlignedBB checkAABB = getEntityBoundingBox().expand(32, 32, 32);
				List<EntityPeatMummy> peatMummies = getEntityWorld().getEntitiesWithinAABB(EntityPeatMummy.class, checkAABB);
				int mummies = 0;
				for(EntityPeatMummy mummy : peatMummies) {
					if(mummy.getDistance(this) <= 32.0D)
						mummies++;
				}
				//Max. 4 peat mummies
				if(mummies < 4 && untilSpawnMummy <= 0)
					spawnMummy();

				if(untilSpawnSludge <= 0)
					spawnSludge();
			}

			if(untilSpawnMummy > 0)
				untilSpawnMummy--;

			if(untilSpawnSludge > 0)
				untilSpawnSludge--;

			if(eatPreyTimer > 0 && currentEatPrey != null)
				eatPreyTimer--;

			if(eatPreyTimer <= 0) {
				setPrey(null);
				eatPreyTimer = 60;
			}
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	private void spawnMummy() {
		EntityPeatMummy mummy = new EntityPeatMummy(getEntityWorld());
		mummy.setPosition(posX + (rand.nextInt(6) - 3), posY, posZ + (rand.nextInt(6) - 3));
		if(mummy.getEntityWorld().checkNoEntityCollision(mummy.getEntityBoundingBox()) && mummy.getEntityWorld().getCollisionBoxes(mummy, mummy.getEntityBoundingBox()).isEmpty()) {
			untilSpawnMummy = SPAWN_MUMMY_COOLDOWN;
			mummy.setAttackTarget((EntityLivingBase) getAttackTarget());
			mummy.setHealth(30);
			getEntityWorld().spawnEntity(mummy);
			mummy.setCarryShimmerstone(false);
			getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_SCREAM, SoundCategory.HOSTILE, 1F, 1.0F);
		} else {
			//Try again the next tick
			untilSpawnMummy = 1;
		}
	}

	private void spawnSludge() {
		untilSpawnSludge = SPAWN_SLUDGE_COOLDOWN;
		if(getAttackTarget() != null)
			faceEntity(getAttackTarget(), 360.0F, 360.0F);
		Vec3d look = getLookVec();
		double direction = Math.toRadians(renderYawOffset);
		EntitySludgeBall sludge = new EntitySludgeBall(getEntityWorld(), this);
		sludge.setPosition(posX - Math.sin(direction) * 3.5, posY + height, posZ + Math.cos(direction) * 3.5);
		sludge.motionX = look.x * 0.5D;
		sludge.motionY = look.y;
		sludge.motionZ = look.z * 0.5D;
		getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_RETCH, SoundCategory.HOSTILE, 1F, 0.7F + rand.nextFloat() * 0.6F);
		getEntityWorld().spawnEntity(sludge);
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		if(getSpawningProgress() < 1.0F)
			return false;
		boolean attacked = super.attackEntityAsMob(target);
		if (attacked && rand.nextInt(6) == 0 && target != currentEatPrey && target instanceof EntityLivingBase && !(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode) && !getEntityWorld().isRemote) {
			setPrey((EntityLivingBase)target);
		}
		if(attacked)
			getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_SWIPE, SoundCategory.HOSTILE, 1F, 0.7F + rand.nextFloat() * 0.6F);
		return attacked;
	}

	private void updateEatPrey() {
		double direction = Math.toRadians(renderYawOffset);
		currentEatPrey.setPositionAndRotation(posX - Math.sin(direction) * 1.7, posY + 1.7, posZ + Math.cos(direction) * 1.7, (float) (Math.toDegrees(direction) + 180), 0);
		currentEatPrey.rotationYawHead = currentEatPrey.prevRotationYawHead = currentEatPrey.rotationYaw = currentEatPrey.prevRotationYaw = ((float) (Math.toDegrees(direction) + 180));
		currentEatPrey.fallDistance = 0;
		if (ticksExisted % 10 == 0) {
			if(!getEntityWorld().isRemote) {
				currentEatPrey.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
				getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_BITE, SoundCategory.HOSTILE, 1F, 0.7F + rand.nextFloat() * 0.6F);
			}
		}
		if (!currentEatPrey.isEntityAlive() && !getEntityWorld().isRemote) 
			setPrey(null);
	}

	private void setPrey(EntityLivingBase prey) {
		if (prey == null) {
			dataManager.set(PREY, -1);
		} else {
			dataManager.set(PREY, prey.getEntityId());
		}
	}

	private EntityLivingBase getPrey() {
		int id = dataManager.get(PREY);
		Entity prey = id != -1 ? getEntityWorld().getEntityByID(id) : null;
		if(prey instanceof EntityLivingBase)
			return (EntityLivingBase) prey;
		return null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getTrueSource() == currentEatPrey) 
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Override
    public boolean isNonBoss() {
        return false;
    }

	@Override
	protected void onDeathUpdate() {
		if(deathTicks == 0) {
			if(!getEntityWorld().isRemote) {
				getEntityWorld().playSound(null, getPosition(), SoundRegistry.DREADFUL_PEAT_MUMMY_DEATH, SoundCategory.HOSTILE, 1F, 1F);
			}
		}

		++deathTicks;

		if(!getEntityWorld().isRemote) {
			posX = lastTickPosX;
			posY = lastTickPosY;
			posZ = lastTickPosZ;
			motionX = 0;
			motionY = 0;
			motionZ = 0;

			if (deathTicks > 40 && deathTicks % 5 == 0) {
				int xp = 100;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					getEntityWorld().spawnEntity(new EntityXPOrb(getEntityWorld(), posX, posY + height / 2.0D, posZ, dropXP));
				}
			}

			if(deathTicks == 80) {
				int xp = 1200;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					getEntityWorld().spawnEntity(new EntityXPOrb(getEntityWorld(), posX, posY + height / 2.0D, posZ, dropXP));
				}
			}

			if(deathTicks > 120) {
				setDead();
			}
		}

		if(deathTicks > 80) {
			if(getEntityWorld().isRemote && deathTicks % 5 == 0) {
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						int x = MathHelper.floor(posX) + xo, y = MathHelper.floor(posY - getYOffset() - 0.1D), z = MathHelper.floor(posZ) + zo;
						IBlockState state = getEntityWorld().getBlockState(new BlockPos(x, y, z));
						Block block = state.getBlock();
						if(block != Blocks.AIR) {
							double px = posX + rand.nextDouble() - 0.5F;
							double py = posY - getYOffset() + rand.nextDouble() * 0.2 + 0.075;
							double pz = posZ + rand.nextDouble() - 0.5F;
							getEntityWorld().playSound(null, getPosition(), block.getSoundType(state, getEntityWorld(), getPosition().down(), null).getBreakSound(), SoundCategory.BLOCKS, rand.nextFloat() * 0.3F + 0.3F, rand.nextFloat() * 0.15F + 0.7F);
							for (int i = 0, amount = rand.nextInt(20) + 10; i < amount; i++) {
								double ox = rand.nextDouble() * 0.1F - 0.05F;
								double oz = rand.nextDouble() * 0.1F - 0.05F;
								double motionX = rand.nextDouble() * 0.2 - 0.1;
								double motionY = rand.nextDouble() * 0.25 + 0.1;
								double motionZ = rand.nextDouble() * 0.2 - 0.1;
								getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox + xo, py, pz + oz + zo, motionX, motionY, motionZ, Block.getStateId(state));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if(isEntityAlive()) {
			super.onCollideWithPlayer(player);
		}
	}

	@Override
	public boolean canBePushed() {
		return isEntityAlive() && super.canBePushed();
	}

	@Override
	public boolean canBeCollidedWith() {
		return isEntityAlive();
	}

	@Override
	protected boolean isMovementBlocked() {
		return isEntityAlive() && super.isMovementBlocked();
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(deathTicks > 0) {
			double dist = getDistance(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(deathTicks / 120.0D * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}

	@Override
	public boolean applyOffset(Entity view, float partialTicks) {
		if(currentEatPrey == view) {
			double direction = Math.toRadians(prevRenderYawOffset + (renderYawOffset - prevRenderYawOffset) * partialTicks);
			view.prevRotationYaw = view.rotationYaw = (float) (Math.toDegrees(direction) + 180);
			view.prevRotationPitch = view.rotationPitch = 0;
			view.setRotationYawHead((float) (Math.toDegrees(direction) + 180));
			return true;
		}
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("spawningState", getSpawningState());
		nbt.setInteger("deathTicks", deathTicks);
		nbt.setDouble("initialPosY", posY);
		nbt.setFloat("previousYOffset", prevYOffset);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		deathTicks = nbt.getInteger("deathTicks");
		posY = nbt.getDouble("initialPosY");
		setYOffset(nbt.getFloat("previousYOffset"));
		dataManager.set(SPAWNING_STATE_DW, nbt.getInteger("spawningState"));
        if(hasCustomName())
        	bossInfo.setName(this.getDisplayName());
	}

	@Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        bossInfo.setName(this.getDisplayName());
    }

	@Override
    public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
        bossInfo.addPlayer(player);
    }

	@Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo.removePlayer(player);
    }

	@Override
	protected void dropFewItems(boolean killedByPlayer, int looting) {
		//TODO loot table
		dropItem(ItemRegistry.RING_OF_SUMMONING, 1);
		for(int i = 0; i < getEntityWorld().rand.nextInt(3) + 1 + getEntityWorld().rand.nextInt(looting + 1) * 2; i++) {
			dropItem(ItemRegistry.SHIMMER_STONE, 1);
		}
		dropItem(ItemRegistry.AMULET_SLOT, 1);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return 0.5F;
	}

	@Override
	protected boolean isValidLightLevel() {
		return true;
	}

	@Override
	public String getMusicFile(EntityPlayer listener) {
		return "thebetweenlands:dreadful_peat_mummy_loop";
	}

	@Override
	public double getMusicRange(EntityPlayer listener) {
		return 32.0D;
	}

	@Override
	public boolean isMusicActive(EntityPlayer listener) {
		return isEntityAlive();
	}

}