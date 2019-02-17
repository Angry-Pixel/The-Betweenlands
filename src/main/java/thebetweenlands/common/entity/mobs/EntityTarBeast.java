package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
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

	public EntityTarBeast(World world) {
		super(EntityRegistry.TAR_BEAST, world);
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
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(SUCKING_STATE_DW, (byte) 0);
		this.getDataManager().register(SHEDDING_STATE_DW, false);
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
		getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
		getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
		getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(25.0D);
		getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

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
			return this.world.checkNoEntityCollision(this.getBoundingBox()) && this.world.getCollisionBoxes(this, this.getBoundingBox()).isEmpty() && isInTar;
		}
		return false;
	}

	@Override
	public boolean isNotColliding() {
		return this.world.getCollisionBoxes(this, this.getBoundingBox()).isEmpty() && this.world.checkNoEntityCollision(this.getBoundingBox(), this);
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
	public void writeAdditional(NBTTagCompound nbt) {
		nbt.setInt("shedCooldown", this.shedCooldown);
		nbt.setInt("sheddingProgress", this.sheddingProgress);
		nbt.setBoolean("sheddingState", this.isShedding());

		nbt.setInt("suckingCooldown", this.suckingCooldown);
		nbt.setInt("suckingPreparation", this.suckingPreparation);
		nbt.setInt("suckingProgress", this.suckingProgress);
		nbt.setByte("suckingState", this.getDataManager().get(SUCKING_STATE_DW));

		super.writeAdditional(nbt);
	}

	@Override
	public void readAdditional(NBTTagCompound nbt) {
		if(nbt.contains("shedCooldown")) {
			this.shedCooldown = nbt.getInt("shedCooldown");
		}
		if(nbt.contains("sheddingProgress")) {
			this.sheddingProgress = nbt.getInt("sheddingProgress");
		}
		if(nbt.contains("sheddingState")) {
			this.getDataManager().set(SHEDDING_STATE_DW, nbt.getBoolean("sheddingState"));
		}
		if(nbt.contains("suckingCooldown")) {
			this.suckingCooldown = nbt.getInt("suckingCooldown");
		}
		if(nbt.contains("suckingPreparation")) {
			this.suckingPreparation = nbt.getInt("suckingPreparation");
		}
		if(nbt.contains("suckingProgress")) {
			this.suckingProgress = nbt.getInt("suckingProgress");
		}
		if(nbt.contains("suckingState")) {
			this.getDataManager().set(SUCKING_STATE_DW, nbt.getByte("suckingState"));
		}

		super.readAdditional(nbt);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.play(SoundRegistry.TAR_BEAST_STEP, 1, 1);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TAR_BEAST;
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isRemote()) {
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

		if(!world.isRemote()) {
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
					this.play(SoundRegistry.TAR_BEAST_LIVING, 1F, (this.rand.nextFloat() * 0.2F + 1.0F) * 0.6F);
					for(int i = 0; i < 8; i++) {
						this.play(SoundRegistry.TAR_BEAST_STEP, 1F, (this.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
					}
					this.sheddingProgress = 0;
					this.setShedding(false);
					if(this.getAttackTarget() != null) {
						List<EntityLivingBase> affectedEntities = (List<EntityLivingBase>)this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(6.0F, 6.0F, 6.0F));
						for(EntityLivingBase e : affectedEntities) {
							if(e == this || e.getDistance(this) > 6.0F || !e.canEntityBeSeen(this) || e instanceof EntityTarBeast) continue;
							if(e instanceof EntityPlayer) {
								if(((EntityPlayer)e).isActiveItemStackBlocking()) continue;
							}
							double dst = e.getDistance(this);
							float dmg = (float) (this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue() / dst * 7.0F);
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

					if(this.suckingPreparation > this.getAttribute(SUCK_PREPARATION_SPEED_ATTRIB).getValue()) {
						this.suckingPreparation = 0;

						this.setSucking(true);
						this.suckingCooldown = this.getSuckingCooldown() + this.world.rand.nextInt(this.getSuckingCooldown() / 2);
						this.play(SoundRegistry.TAR_BEAST_SUCK, 1F, 1F);
					}
				}

				if(this.suckingProgress > (int)this.getAttribute(SUCK_LENGTH_ATTRIB).getValue()) {
					this.setSucking(false);
					this.suckingProgress = 0;
				}

				if(this.isSucking()) {
					this.suckingProgress++;

					List<Entity> affectedEntities = (List<Entity>)this.world.getEntitiesWithinAABB(Entity.class, this.getBoundingBox().grow(10.0F, 10.0F, 10.0F));
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
					getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
				} else {
					this.suckingProgress = 0;
					getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
				}
			}
		}
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.isSucking();
	}

	@OnlyIn(Dist.CLIENT)
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
		return (int)this.getAttribute(SHED_COOLDOWN_ATTRIB).getValue();
	}

	public int getSheddingSpeed() {
		return (int)this.getAttribute(SHED_SPEED_ATTRIB).getValue();
	}

	public int getSuckingCooldown() {
		return (int)this.getAttribute(SUCK_COOLDOWN_ATTRIB).getValue();
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

	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
}
