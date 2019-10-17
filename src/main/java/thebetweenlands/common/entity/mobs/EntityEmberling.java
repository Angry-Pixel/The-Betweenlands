package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO Loot tables
public class EntityEmberling extends EntityMob implements IEntityMultiPart, IEntityBL {

	public MultiPartEntityPart[] tailPart;
	private static final DataParameter<Boolean> IS_CASTING_SPELL = EntityDataManager.<Boolean>createKey(EntityEmberling.class, DataSerializers.BOOLEAN);
	public float animationTicks, prevAnimationTicks;

	public EntityEmberling(World world) {
		super(world);
		setSize(0.9F, 1F);
		stepHeight = 1F;
		isImmuneToFire = true;
		tailPart = new MultiPartEntityPart[] { new MultiPartEntityPart(this, "tail", 0.5F, 0.5F) }; // may use more parts?
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_CASTING_SPELL, false);
	}

	public void setIsCastingSpell(boolean casting) {
		dataManager.set(IS_CASTING_SPELL, casting);
	}

	public boolean getIsCastingSpell() {
		return dataManager.get(IS_CASTING_SPELL);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityEmberling.EntityAIHoverSpinAttack(this, 0.6F));
		tasks.addTask(3, new EntityEmberling.EntityAIFireballColumn(this));
		tasks.addTask(4, new EntityEmberling.AIEmberlingAttack(this));
		tasks.addTask(5, new EntityAIWander(this, 0.4D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 0, false, true, null));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.EMBERLING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.EMBERLING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.EMBERLING_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.EMBERLING;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntityEmberling.class != entity;
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
    public boolean isNotColliding() {
        return !getEntityWorld().containsAnyLiquid(getEntityBoundingBox()) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this);
    }

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}
	
    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevAnimationTicks + (animationTicks - prevAnimationTicks) * partialTicks;
    }

	@Override
    public void onUpdate() {
    	super.onUpdate();
    	renderYawOffset = rotationYaw;
		double a = Math.toRadians(rotationYaw);
		double offSetX = -Math.sin(a) * 1.5D;
		double offSetZ = Math.cos(a) * 1.5D;
		tailPart[0].setLocationAndAngles(posX - offSetX, posY, posZ - offSetZ, 0.0F, 0.0F);

		if (getEntityWorld().getTotalWorldTime()%5 == 0)
			if (getEntityWorld().isRemote)
				flameParticles(getEntityWorld(), tailPart[0].posX, tailPart[0].posY + 0.25, tailPart[0].posZ, rand);

		checkCollision();

		if (getEntityWorld().isRemote) {
			if (getIsCastingSpell()) {
				prevAnimationTicks = animationTicks;

				if (animationTicks <= 1F)
					animationTicks += 0.1F;

			}
			if (!getIsCastingSpell()) {
				prevAnimationTicks = animationTicks;

				if (animationTicks >= 0.1F)
					animationTicks -= 0.1F;

				if (animationTicks < 0F)
					animationTicks = 0F;
			}
		}
		if (!getEntityWorld().isRemote && recentlyHit > 40)
			if(getIsCastingSpell())
				setIsCastingSpell(false);
		
		if (getEntityWorld().isRemote) {
			if(rand.nextInt(4) == 0) {
				ParticleArgs<?> args = ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
					args.withColor(1F, 1F, 1F, 1F);
					args.withScale(0.75F + rand.nextFloat() * 0.75F);
				BLParticles.EMBER_SWIRL.spawn(getEntityWorld(), posX, posY, posZ, args);
			}
		}
    }

	protected Entity checkCollision() {
		List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, tailPart[0].getEntityBoundingBox());
		for (int entityCount = 0; entityCount < list.size(); entityCount++) {
			Entity entity = list.get(entityCount);
			if (entity != null && entity == getAttackTarget() && !(entity instanceof IEntityMultiPart) && !(entity instanceof MultiPartEntityPart))
				if (entity instanceof EntityLivingBase) {
					attackEntityAsMob(entity);
					entity.addVelocity(-MathHelper.sin(tailPart[0].rotationYaw * 3.141593F / 180.0F) * 0.5D, 0.3D, MathHelper.cos(tailPart[0].rotationYaw * 3.141593F / 180.0F) * 0.5D);
					entity.setFire(5); // randomise or something?
				}
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void flameParticles(World world, double x, double y, double z, Random rand) {
		for (int count = 0; count < 3; ++count) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextBoolean() ? 1 : - 1;
			int motionZ = rand.nextBoolean() ? 1 : - 1;
			velY = rand.nextFloat() * 0.05D;
			velZ = rand.nextFloat() * 0.025D * motionZ;
			velX = rand.nextFloat() * 0.025D * motionX;
			world.spawnParticle(EnumParticleTypes.FLAME,  x, y, z, velX, velY, velZ);
		}
	}

	@Override
	public World getWorld() {
		return this.getEntityWorld();
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return false;
	}

	@Override
    public Entity[] getParts(){
        return tailPart;
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	static class AIEmberlingAttack extends EntityAIAttackMelee {

		public AIEmberlingAttack(EntityEmberling emberling) {
			super(emberling, 0.4D, false);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}
	
	static class EntityAIFireballColumn extends EntityAIBase {
		EntityEmberling emberling;
		EntityLivingBase target;
		int missileCount;
		int shootCount;
		
		public EntityAIFireballColumn(EntityEmberling emberling) {
			this.emberling = emberling;
			this.setMutexBits(5);
		}

		@Override
		public boolean shouldExecute() {
			target = emberling.getAttackTarget();

			if (target == null)
				return false;
			else {
				double distance = emberling.getDistanceSq(target);
				if (distance >= 36.0D && distance <= 144.0D) {
					if (!emberling.onGround)
						return false;
					else
						return emberling.getRNG().nextInt(8) == 0;
				} else
					return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return shootCount !=-1 && missileCount !=-1 && emberling.recentlyHit <= 40;
		}

		@Override
		public void startExecuting() {
			missileCount = 0;
			shootCount = 0;
			emberling.getEntityWorld().playSound(null, emberling.getPosition(), SoundRegistry.EMBERLING_FLAMES, SoundCategory.HOSTILE, 1F, 1F);
		}

		@Override
		public void updateTask() {
			if(!emberling.getIsCastingSpell())
				emberling.setIsCastingSpell(true);
			emberling.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			float f = (float) MathHelper.atan2(target.posZ - emberling.posZ, target.posX - emberling.posX);
			int distance = MathHelper.floor(emberling.getDistance(target));
			missileCount++;
			if (missileCount %5 == 0) {
				shootCount++;
				double d2 = 1D * (double) (shootCount);
				EntityFlameJet flame_jet = new EntityFlameJet(emberling.getEntityWorld(), emberling);
				flame_jet.setPosition(emberling.posX + (double) MathHelper.cos(f) * d2, emberling.posY, emberling.posZ + (double) MathHelper.sin(f) * d2);
				emberling.getEntityWorld().spawnEntity(flame_jet);
				emberling.getEntityWorld().playSound(null, emberling.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.HOSTILE, 0.5F, 1F + (emberling.getEntityWorld().rand.nextFloat() - emberling.getEntityWorld().rand.nextFloat()) * 0.8F);
			}
			if (shootCount >= distance || shootCount >= 12) {
				shootCount = -1;
				missileCount = -1;
				if(emberling.getIsCastingSpell())
					emberling.setIsCastingSpell(false);
			}
		}
	}

	static class EntityAIHoverSpinAttack extends EntityAIBase {
		EntityEmberling emberling;
		EntityLivingBase target;
		float motionY;
		float rotation;
		
		public EntityAIHoverSpinAttack(EntityEmberling emberling, float motionYIn) {
			this.emberling = emberling;
			this.motionY = motionYIn;
			this.setMutexBits(7);
		}

		@Override
		public boolean shouldExecute() {
			target = emberling.getAttackTarget();
			rotation = 0F;

			if (target == null)
				return false;
			else {
				double distance = emberling.getDistanceSq(target);
				if (distance >= 4.0D && distance <= 36.0D) {
					if (!emberling.onGround)
						return false;
					else
						return emberling.getRNG().nextInt(5) == 0;
				}
				else
					return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return !emberling.onGround;
		}

		@Override
		public void startExecuting() {
			if(emberling.getIsCastingSpell())
				emberling.setIsCastingSpell(false);
			emberling.getEntityWorld().playSound(null, emberling.getPosition(), SoundRegistry.EMBERLING_JUMP, SoundCategory.HOSTILE, 1F, 1F);
			double d0 = target.posX - emberling.posX;
			double d1 = target.posZ - emberling.posZ;
			float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
			if ((double) f >= 1.0E-4D) {
				emberling.motionX += d0 / (double) f * 0.5D * 0.800000011920929D + emberling.motionX * 0.20000000298023224D;
				emberling.motionZ += d1 / (double) f * 0.5D * 0.800000011920929D + emberling.motionZ * 0.20000000298023224D;
			}
			emberling.motionY = (double) motionY;
		}

		@Override
	    public void updateTask() {
	    		rotation += 30;
	    		emberling.setRotation(rotation, 0F);
	    	if(emberling.motionY < 0)
	    		emberling.motionY *= 0.5F;
	    }
	}
}