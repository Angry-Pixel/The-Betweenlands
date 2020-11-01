package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityTarBeast extends EntityMob implements IEntityBL {

	public static final IAttribute SHED_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.shedCooldown", 70.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Shed Cooldown");
	public static final IAttribute SHED_SPEED_ATTRIB = (new RangedAttribute(null, "bl.shedSpeed", 10.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Shedding Speed");

	public static final IAttribute SUCK_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.suckCooldown", 400.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Sucking Cooldown");
	public static final IAttribute SUCK_PREPARATION_SPEED_ATTRIB = (new RangedAttribute(null, "bl.suckPreparationSpeed", 40.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Sucking Preparation Speed");
	public static final IAttribute SUCK_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.suckLength", 130.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Sucking Length");

	private int shedCooldown = (int) SHED_COOLDOWN_ATTRIB.getDefaultValue();
	private int sheddingProgress = 0;

	private int suckingCooldown = (int) SUCK_COOLDOWN_ATTRIB.getDefaultValue();
	private int suckingPreparation = 0;
	private int suckingProgress = 0;

	protected static final DataParameter<Byte> SUCKING_STATE_DW = EntityDataManager.createKey(EntityTarBeast.class, DataSerializers.BYTE);
	protected static final DataParameter<Boolean> SHEDDING_STATE_DW = EntityDataManager.createKey(EntityTarBeast.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> GROW_TIMER = EntityDataManager.createKey(EntityTarBeast.class, DataSerializers.VARINT);
	
	public int growCount, prevGrowCount;

	public EntityTarBeast(World world) {
		super(world);
		this.experienceValue = 20;
		setSize(1.25F, 2F);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(2, new EntityAIMoveToBlock(this, 0.85D, 32) {
			@Override
			protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
				return worldIn.getBlockState(pos).getBlock() == BlockRegistry.TAR;
			}
		});
		this.tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 0.85D));
		this.tasks.addTask(4, new EntityAIWander(this, 0.85D));
		this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(6, new EntityAILookIdle(this));

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(SUCKING_STATE_DW, (byte) 0);
		this.getDataManager().register(SHEDDING_STATE_DW, false);
		dataManager.register(GROW_TIMER, 40);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(25.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

		this.getAttributeMap().registerAttribute(SHED_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(SHED_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(SUCK_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(SUCK_PREPARATION_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(SUCK_LENGTH_ATTRIB);
	}

	@Override
	public boolean getCanSpawnHere() {
		boolean isDifficultyValid = this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
		if(isDifficultyValid) {
			int bx = MathHelper.floor(posX);
			int by = MathHelper.floor(posY);
			int bz = MathHelper.floor(posZ);
			MutableBlockPos pos = new MutableBlockPos();
			boolean isInTar = 
					this.world.getBlockState(pos.setPos(bx, by, bz)).getBlock() == BlockRegistry.TAR &&
					this.world.getBlockState(pos.setPos(bx-1, by, bz)).getBlock() == BlockRegistry.TAR &&
					this.world.getBlockState(pos.setPos(bx+1, by, bz)).getBlock() == BlockRegistry.TAR &&
					this.world.getBlockState(pos.setPos(bx, by, bz-1)).getBlock() == BlockRegistry.TAR &&
					this.world.getBlockState(pos.setPos(bx, by, bz+1)).getBlock() == BlockRegistry.TAR;
			return this.world.checkNoEntityCollision(this.getEntityBoundingBox()) && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && isInTar;
		}
		return false;
	}

	@Override
	public boolean isNotColliding() {
		return this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.TAR_BEAST_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.TAR_BEAST_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.TAR_BEAST_DEATH;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("shedCooldown", this.shedCooldown);
		nbt.setInteger("sheddingProgress", this.sheddingProgress);
		nbt.setBoolean("sheddingState", this.isShedding());

		nbt.setInteger("suckingCooldown", this.suckingCooldown);
		nbt.setInteger("suckingPreparation", this.suckingPreparation);
		nbt.setInteger("suckingProgress", this.suckingProgress);
		nbt.setByte("suckingState", this.getDataManager().get(SUCKING_STATE_DW));
		nbt.setInteger("grow_timer", getGrowTimer());

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("shedCooldown")) {
			this.shedCooldown = nbt.getInteger("shedCooldown");
		}
		if(nbt.hasKey("sheddingProgress")) {
			this.sheddingProgress = nbt.getInteger("sheddingProgress");
		}
		if(nbt.hasKey("sheddingState")) {
			this.getDataManager().set(SHEDDING_STATE_DW, nbt.getBoolean("sheddingState"));
		}
		if(nbt.hasKey("suckingCooldown")) {
			this.suckingCooldown = nbt.getInteger("suckingCooldown");
		}
		if(nbt.hasKey("suckingPreparation")) {
			this.suckingPreparation = nbt.getInteger("suckingPreparation");
		}
		if(nbt.hasKey("suckingProgress")) {
			this.suckingProgress = nbt.getInteger("suckingProgress");
		}
		if(nbt.hasKey("suckingState")) {
			this.getDataManager().set(SUCKING_STATE_DW, nbt.getByte("suckingState"));
		}

		if(nbt.hasKey("grow_timer"))
			setGrowTimer(nbt.getInteger("grow_timer"));

		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundRegistry.TAR_BEAST_STEP, 1, 1);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TAR_BEAST;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (world.isRemote) {
			prevGrowCount = growCount;
			growCount = getGrowTimer();
			if(ticksExisted % 10 == 0) {
				renderParticles(world, posX, posY, posZ, rand);
			}
			if(this.sheddingProgress > this.getSheddingSpeed()) {
				this.sheddingProgress = 0;

				for(int i = 0; i < 200; i++) {
					Random rnd = world.rand;
					float rx = rnd.nextFloat() * 4.0F - 2.0F;
					float ry = rnd.nextFloat() * 4.0F - 2.0F;
					float rz = rnd.nextFloat() * 4.0F - 2.0F;
					Vec3d vec = new Vec3d(rx, ry, rz);
					vec = vec.normalize();
					BLParticles.SPLASH_TAR.spawn(this.world, this.posX + rx + 0.25F, this.posY + ry, this.posZ + rz + 0.25F, ParticleArgs.get().withMotion(vec.x * 0.5F, vec.y * 0.5F, vec.z * 0.5F));
				}
			} else if(this.isShedding() || this.sheddingProgress > 0) {
				this.sheddingProgress++;
			} else {
				this.sheddingProgress = 0;
			}

			if(this.isSucking()) {
				for(int i = 0; i < 5; i++) {
					Random rnd = world.rand;
					float rx = rnd.nextFloat() * 8.0F - 4.0F;
					float ry = rnd.nextFloat() * 8.0F - 4.0F;
					float rz = rnd.nextFloat() * 8.0F - 4.0F;
					Vec3d vec = new Vec3d(rx, ry, rz);
					vec = vec.normalize();
					this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + rx + 0.25F, this.posY + ry, this.posZ + rz + 0.25F, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		}

		if(!world.isRemote) {
			if(this.isInsideOfMaterial(BLMaterialRegistry.TAR)) {
				this.stepHeight = 2.0F;
			} else {
				this.stepHeight = 0.75F;
			}

			if(this.shedCooldown > 0 && this.getAttackTarget() != null) {
				this.shedCooldown--;
			}

			if(!this.isSucking() && !this.isPreparing()) {
				if(this.shedCooldown == 0 && this.getAttackTarget() != null && this.getAttackTarget().getDistance(this) < 6.0D && this.canEntityBeSeen(this.getAttackTarget())) {
					this.setShedding(true);
					this.shedCooldown = this.getSheddingCooldown() + this.world.rand.nextInt(this.getSheddingCooldown() / 2);
				}

				if(this.sheddingProgress > this.getSheddingSpeed()) {
					this.playSound(SoundRegistry.TAR_BEAST_LIVING, 1F, (this.rand.nextFloat() * 0.2F + 1.0F) * 0.6F);
					for(int i = 0; i < 8; i++) {
						this.playSound(SoundRegistry.TAR_BEAST_STEP, 1F, (this.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
					}
					this.sheddingProgress = 0;
					this.setShedding(false);
					if(this.getAttackTarget() != null) {
						List<EntityLivingBase> affectedEntities = (List<EntityLivingBase>)this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(6.0F, 6.0F, 6.0F));
						for(EntityLivingBase e : affectedEntities) {
							if(e == this || e.getDistance(this) > 6.0F || !e.canEntityBeSeen(this) || e instanceof EntityTarBeast) continue;
							if(e instanceof EntityPlayer) {
								if(((EntityPlayer)e).isActiveItemStackBlocking()) continue;
							}
							double dst = e.getDistance(this);
							float dmg = (float) (this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / dst * 7.0F);
							e.attackEntityFrom(DamageSource.causeMobDamage(this), dmg);
							e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int)(20 + (1.0F - dst / 6.0F) * 150), 1));
						}
					}
				}

				if(this.isShedding()) {
					this.sheddingProgress++;
				} else {
					this.sheddingProgress = 0;
				}
			}

			if(this.suckingCooldown > 0 && this.getAttackTarget() != null) {
				this.suckingCooldown--;
			}

			if(!this.isShedding()) {
				if(this.suckingCooldown == 0 && this.getAttackTarget() != null && this.getAttackTarget().getDistance(this) <= 10.0D && this.canEntityBeSeen(this.getAttackTarget())) {
					this.setPreparing();
				}

				if(this.isPreparing()) {
					this.suckingPreparation++;

					if(this.suckingPreparation > this.getEntityAttribute(SUCK_PREPARATION_SPEED_ATTRIB).getAttributeValue()) {
						this.suckingPreparation = 0;

						this.setSucking(true);
						this.suckingCooldown = this.getSuckingCooldown() + this.world.rand.nextInt(this.getSuckingCooldown() / 2);
						this.playSound(SoundRegistry.TAR_BEAST_SUCK, 1F, 1F);
					}
				}

				if(this.suckingProgress > (int)this.getEntityAttribute(SUCK_LENGTH_ATTRIB).getAttributeValue()) {
					this.setSucking(false);
					this.suckingProgress = 0;
				}

				if(this.isSucking()) {
					this.suckingProgress++;

					List<Entity> affectedEntities = (List<Entity>)this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(10.0F, 10.0F, 10.0F));
					for(Entity e : affectedEntities) {
						if(e == this || e.getDistance(this) > 10.0F || !this.canEntityBeSeen(e) || e instanceof EntityTarBeast) continue;
						Vec3d vec = new Vec3d(this.posX - e.posX, this.posY - e.posY, this.posZ - e.posZ);
						vec = vec.normalize();
						float dst = e.getDistance(this);
						float mod = (float) Math.pow(1.0F - dst / 13.0F, 1.2D);
						if(e instanceof EntityPlayer) {
							if(((EntityPlayer)e).isActiveItemStackBlocking()) mod *= 0.18F;
						}
						if(dst < 1.0F && e instanceof EntityLivingBase) {
							((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 20, 3));
							((EntityLivingBase) e).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 3));
							e.motionX *= 0.008F;
							e.motionY *= 0.008F;
							e.motionZ *= 0.008F;
							if(e instanceof EntityPlayer) {
								((EntityPlayer)e).jumpMovementFactor = 0.0F;
							}
							if(this.ticksExisted % 12 == 0) {
								e.attackEntityFrom(DamageSource.DROWN, 1);
							}
						}
						e.motionX += vec.x * 0.18F * mod;
						e.motionY += vec.y * 0.18F * mod;
						e.motionZ += vec.z * 0.18F * mod;
						e.velocityChanged = true;
					}
					getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
				} else {
					this.suckingProgress = 0;
					getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
				}
			}

			if (getGrowTimer() < 40) {
				setGrowTimer(Math.min(40, getGrowTimer() + 1));
			}
		}
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.isSucking() || getGrowTimer() < 40;
	}

	@SideOnly(Side.CLIENT)
	public void renderParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextInt(2) * 2 - 1;
			int motionZ = rand.nextInt(2) * 2 - 1;
			double a = Math.toRadians(renderYawOffset);
			double offSetX = -Math.sin(a) * 0.5D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double offSetZ = Math.cos(a) * 0.5D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			velY = (rand.nextFloat() - 0.5D) * 0.125D;
			velZ = rand.nextFloat() * 0.5F * motionZ;
			velX = rand.nextFloat() * 0.5F * motionX;
			BLParticles.SPLASH_TAR.spawn(world , x, y + rand.nextDouble() * 1.9D, z, ParticleArgs.get().withMotion(velX * 0.15D, velY * 0.1D, velZ * 0.15D));
			BLParticles.TAR_BEAST_DRIP.spawn(world , x + offSetX, y + 1.2D, z + offSetZ);
		}
	}
	
	public float getGrowthFactor(float partialTicks) {
		return prevGrowCount + (growCount - prevGrowCount) * partialTicks;
	}

	@Override
	protected void collideWithEntity(Entity e) {
		if(!this.isSucking()) {
			e.applyEntityCollision(this);
		}
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed() && !this.isSucking();
	}

	public boolean isShedding() {
		return this.getDataManager().get(SHEDDING_STATE_DW);
	}

	public void setShedding(boolean shedding) {
		this.getDataManager().set(SHEDDING_STATE_DW, shedding);
	}

	public int getSheddingProgress() {
		return this.sheddingProgress;
	}

	public int getSheddingCooldown() {
		return (int)this.getEntityAttribute(SHED_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public int getSheddingSpeed() {
		return (int)this.getEntityAttribute(SHED_SPEED_ATTRIB).getAttributeValue();
	}

	public int getSuckingCooldown() {
		return (int)this.getEntityAttribute(SUCK_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public boolean isSucking() {
		return this.getDataManager().get(SUCKING_STATE_DW) == 1;
	}

	public boolean isPreparing() {
		return this.getDataManager().get(SUCKING_STATE_DW) == 2;
	}

	public void setSucking(boolean sucking) {
		this.getDataManager().set(SUCKING_STATE_DW, (byte)(sucking ? 1 : 0));
	}

	public void setPreparing() {
		this.getDataManager().set(SUCKING_STATE_DW, (byte)2);
	}

	public int getGrowTimer() {
		return dataManager.get(GROW_TIMER);
	}

	public void setGrowTimer(int timer) {
		dataManager.set(GROW_TIMER, timer);
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
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        if (!world.isRemote)
            setGrowTimer(0);
		return livingdata;
    }
}
