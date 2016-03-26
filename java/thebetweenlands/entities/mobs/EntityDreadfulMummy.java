package thebetweenlands.entities.mobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.entities.ICameraOffset;
import thebetweenlands.entities.IEntityMusic;
import thebetweenlands.entities.IScreenShake;
import thebetweenlands.entities.mobs.boss.IBossBL;
import thebetweenlands.items.BLItemRegistry;

public class EntityDreadfulMummy extends EntityMob implements IEntityBL, IBossBL, IScreenShake, ICameraOffset, IEntityMusic {
	public EntityDreadfulMummy(World world) {
		super(world);
		this.getNavigator().setCanSwim(true);
		this.setSize(1.1F, 2.0F);

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		this.tasks.addTask(4, new EntityAIWander(this, 0.25D));
		this.tasks.addTask(5, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
	}

	private static final int SPAWN_MUMMY_COOLDOWN = 350;
	private int untilSpawnMummy = 0;
	private static final int SPAWN_SLUDGE_COOLDOWN = 150;
	private int untilSpawnSludge = 0;

	private int eatPreyTimer = 60;
	public EntityLivingBase currentEatPrey;

	public int deathTicks = 0;

	@Override
	public String pageName() {
		return null;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(550.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(8);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:dreadfulPeatMummyLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:dreadfulPeatMummyHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:dreadfulPeatMummyDeath";
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public double getYOffset() {
		if(this.deathTicks > 80) {
			return -(this.deathTicks - 80) * 0.08F;
		} else {
			return super.getYOffset();
		}
	}

	@Override
	public void onUpdate() {
		if(this.deathTicks > 80) {
			this.yOffset = -(this.deathTicks - 80) * 0.08F;
		}
		super.onUpdate();
		if(this.deathTicks > 80) {
			this.yOffset = -(this.deathTicks - 80) * 0.08F;
		}
		Entity prey = this.getPrey();
		if(prey instanceof EntityLivingBase) {
			this.currentEatPrey = (EntityLivingBase)prey;
			if(this.currentEatPrey != null) {
				updateEatPrey();
			}
		} else {
			this.currentEatPrey = null;
		}
		if(!this.worldObj.isRemote && this.isEntityAlive()) {
			if (this.getEntityToAttack() != null) {
				AxisAlignedBB checkAABB = this.boundingBox.expand(32, 32, 32);
				List<EntityPeatMummy> peatMummies = this.worldObj.getEntitiesWithinAABB(EntityPeatMummy.class, checkAABB);
				int mummies = 0;
				for(EntityPeatMummy mummy : peatMummies) {
					if(mummy.getDistanceToEntity(this) <= 32.0D)
						mummies++;
				}
				//Max. 4 peat mummies
				if(mummies < 4 && this.untilSpawnMummy <= 0)
					spawnMummy();

				if(this.untilSpawnSludge <= 0)
					spawnSludge();
			}

			if(this.untilSpawnMummy > 0)
				this.untilSpawnMummy--;

			if(this.untilSpawnSludge > 0)
				this.untilSpawnSludge--;

			if(this.eatPreyTimer > 0 && this.currentEatPrey != null)
				this.eatPreyTimer--;

			if(this.eatPreyTimer <= 0) {
				setPrey(null);
				this.eatPreyTimer = 60;
			}
		}
	}

	private void spawnMummy() {
		EntityPeatMummy mummy = new EntityPeatMummy(this.worldObj);
		mummy.setPosition(this.posX + (this.rand.nextInt(6) - 3), this.posY, this.posZ + (this.rand.nextInt(6) - 3));
		if(mummy.worldObj.checkNoEntityCollision(mummy.boundingBox) && mummy.worldObj.getCollidingBoundingBoxes(mummy, mummy.boundingBox).isEmpty()) {
			this.untilSpawnMummy = SPAWN_MUMMY_COOLDOWN;
			mummy.setAttackTarget((EntityLivingBase) getEntityToAttack());
			mummy.setHealth(30);
			this.worldObj.spawnEntityInWorld(mummy);
			mummy.setCarryShimmerStone(false);
		} else {
			//Try again the next tick
			this.untilSpawnMummy = 1;
		}
	}

	private void spawnSludge() {
		this.untilSpawnSludge = SPAWN_SLUDGE_COOLDOWN;
		if(this.getEntityToAttack() != null)
			this.faceEntity(this.getEntityToAttack(), 360.0F, 360.0F);
		Vec3 look = this.getLookVec();
		double direction = Math.toRadians(this.renderYawOffset);
		EntitySludgeBall sludge = new EntitySludgeBall(this.worldObj, this);
		sludge.setPosition(this.posX - Math.sin(direction) * 3.5, this.posY + this.height, this.posZ + Math.cos(direction) * 3.5);
		sludge.motionX = look.xCoord * 0.5D;
		sludge.motionY = look.yCoord;
		sludge.motionZ = look.zCoord * 0.5D;
		playSound("thebetweenlands:dreadfulPeatMummyRetch", 1, 0.7f + this.rand.nextFloat() * 0.6f);
		this.worldObj.spawnEntityInWorld(sludge);
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		boolean attacked = super.attackEntityAsMob(target);
		if (attacked && this.rand.nextInt(6) == 0 && target != this.currentEatPrey && target instanceof EntityLivingBase && !(target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isCreativeMode) && !this.worldObj.isRemote) {
			setPrey((EntityLivingBase)target);
		}
		if(attacked)
			playSound("thebetweenlands:dreadfulPeatMummySwipe", 1, 0.7f + this.rand.nextFloat() * 0.6f);
		return attacked;
	}

	private void updateEatPrey() {
		double direction = Math.toRadians(this.renderYawOffset);
		this.currentEatPrey.setPositionAndRotation(this.posX - Math.sin(direction) * 1.7, this.posY + 1.7, this.posZ + Math.cos(direction) * 1.7, (float) (Math.toDegrees(direction) + 180), 0);
		this.currentEatPrey.rotationYawHead = this.currentEatPrey.prevRotationYawHead = this.currentEatPrey.rotationYaw = this.currentEatPrey.prevRotationYaw = ((float) (Math.toDegrees(direction) + 180));
		this.currentEatPrey.fallDistance = 0;
		if (this.ticksExisted % 10 == 0) {
			if(!this.worldObj.isRemote) {
				this.currentEatPrey.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
				playSound("thebetweenlands:dreadfulPeatMummyBite", 1, 0.7f + this.rand.nextFloat() * 0.6f);
			}
		}
		if (!this.currentEatPrey.isEntityAlive() && !this.worldObj.isRemote) 
			setPrey(null);
	}

	private void setPrey(EntityLivingBase prey) {
		if (prey == null) {
			this.dataWatcher.updateObject(24, -1);
		} else {
			this.dataWatcher.updateObject(24, prey.getEntityId());
		}
	}

	private EntityLivingBase getPrey() {
		int id = this.dataWatcher.getWatchableObjectInt(24);
		Entity prey = id != -1 ? this.worldObj.getEntityByID(id) : null;
		if(prey instanceof EntityLivingBase)
			return (EntityLivingBase) prey;
		return null;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(24, 0);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getEntity() == this.currentEatPrey) 
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public float getMaxBossHealth() {
		return this.getMaxHealth();
	}

	@Override
	public float getBossHealth() {
		return this.getHealth();
	}

	@Override
	public IChatComponent getBossName() {
		return this.func_145748_c_();
	}

	@Override
	protected void onDeathUpdate() {
		if(this.deathTicks == 0) {
			if(!this.worldObj.isRemote) {
				this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "thebetweenlands:dreadfulPeatMummyDeath", 1.0F, 1.0F);
			}
		}

		++this.deathTicks;

		if(!this.worldObj.isRemote) {
			this.posX = this.lastTickPosX;
			this.posY = this.lastTickPosY;
			this.posZ = this.lastTickPosZ;
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;

			if (this.deathTicks > 40 && this.deathTicks % 5 == 0) {
				int xp = 100;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY + this.height / 2.0D, this.posZ, dropXP));
				}
			}

			if(this.deathTicks == 80) {
				int xp = 1200;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY + this.height / 2.0D, this.posZ, dropXP));
				}
			}

			if(this.deathTicks > 120) {
				this.setDead();
			}
		}

		if(this.deathTicks > 80) {
			if(this.worldObj.isRemote && this.deathTicks % 5 == 0) {
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						int x = MathHelper.floor_double(this.posX) + xo, y = MathHelper.floor_double(this.posY - this.yOffset - 0.1D), z = MathHelper.floor_double(this.posZ) + zo;
						Block block = this.worldObj.getBlock(x, y, z);
						if(block != Blocks.air) {
							int metadata = this.worldObj.getBlockMetadata(x, y, z);
							String particle = "blockdust_" + Block.getIdFromBlock(block) + "_" + metadata;
							double px = this.posX + this.rand.nextDouble() - 0.5F;
							double py = this.posY - this.yOffset + this.rand.nextDouble() * 0.2 + 0.075;
							double pz = this.posZ + this.rand.nextDouble() - 0.5F;
							this.worldObj.playSound(this.posX, this.posY, this.posZ, block.stepSound.getBreakSound(), this.rand.nextFloat() * 0.3F + 0.3F, this.rand.nextFloat() * 0.15F + 0.7F, false);
							for (int i = 0, amount = this.rand.nextInt(20) + 10; i < amount; i++) {
								double ox = this.rand.nextDouble() * 0.1F - 0.05F;
								double oz = this.rand.nextDouble() * 0.1F - 0.05F;
								double motionX = this.rand.nextDouble() * 0.2 - 0.1;
								double motionY = this.rand.nextDouble() * 0.25 + 0.1;
								double motionZ = this.rand.nextDouble() * 0.2 - 0.1;
								this.worldObj.spawnParticle(particle, px + ox + xo, py, pz + oz + zo, motionX, motionY, motionZ);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if(this.isEntityAlive()) {
			super.onCollideWithPlayer(player);
		}
	}

	@Override
	public boolean canBePushed() {
		return this.isEntityAlive() && super.canBePushed();
	}

	@Override
	public boolean canBeCollidedWith() {
		return this.isEntityAlive();
	}

	@Override
	protected boolean isMovementBlocked() {
		return this.isEntityAlive() && super.isMovementBlocked();
	}

	@Override
	public float getShakeIntensity(EntityLivingBase viewer, float partialTicks) {
		if(this.deathTicks > 0) {
			double dist = this.getDistanceToEntity(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(this.deathTicks / 120.0D * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}

	@Override
	public boolean applyOffset(EntityLivingBase view, float partialTicks) {
		if(this.currentEatPrey == view) {
			double direction = Math.toRadians(this.prevRenderYawOffset + (this.renderYawOffset - this.prevRenderYawOffset) * partialTicks);
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
		nbt.setInteger("deathTicks", this.deathTicks);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.deathTicks = nbt.getInteger("deathTicks");
	}

	@Override
	protected void dropFewItems(boolean killedByPlayer, int looting) {
		this.dropItem(BLItemRegistry.ringOfSummoning, 1);
		for(int i = 0; i < this.worldObj.rand.nextInt(3) + 1 + this.worldObj.rand.nextInt(looting + 1) * 2; i++) {
			this.dropItem(BLItemRegistry.shimmerStone, 1);
		}
		this.dropItem(BLItemRegistry.amuletSlot, 1);
	}

	@Override
	public String getMusicFile(EntityPlayer listener) {
		return "thebetweenlands:dreadfulPeatMummyLoop";
	}

	@Override
	public double getMusicRange(EntityPlayer listener) {
		return 32.0D;
	}

	@Override
	public boolean isMusicActive(EntityPlayer listener) {
		return this.isEntityAlive();
	}
}