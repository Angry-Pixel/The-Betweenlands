package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
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
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.LootTableRegistry;

//TODO Loot tables
public class EntityEmberling extends EntityMob implements IEntityMultiPart, IEntityBL {

	public MultiPartEntityPart[] tailPart;

	public EntityEmberling(World world) {
		super(world);
		setSize(1F, 1F);
		stepHeight = 1F;
		isImmuneToFire = true;
		tailPart = new MultiPartEntityPart[] { new MultiPartEntityPart(this, "tail", 0.5F, 0.5F) }; // may use more parts?
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityEmberling.EntityAIHoverSpinAttack(this, 0.6F));
		tasks.addTask(3, new EntityEmberling.AIEmberlingAttack(this));
		tasks.addTask(4, new EntityAIWander(this, 0.4D));
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(6, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, 0, false, true, null));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
		//return SoundRegistry.EMBERLING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return null;
		//return SoundRegistry.EMBERLING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
		//return SoundRegistry.EMBERLING_DEATH;
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
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		//TODO add some spawn stuffs
		return livingdata;
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

		public boolean shouldExecute() {
			target = emberling.getAttackTarget();
			rotation = 0F;

			if (target == null)
				return false;
			else {
				double distance = emberling.getDistanceSq(target);
				if (distance >= 4.0D && distance <= 64.0D) {
					if (!emberling.onGround)
						return false;
					else
						return emberling.getRNG().nextInt(5) == 0;
				}
				else
					return false;
			}
		}

		public boolean shouldContinueExecuting() {
			return !emberling.onGround;
		}

		public void startExecuting() {
			double d0 = target.posX - emberling.posX;
			double d1 = target.posZ - emberling.posZ;
			float f = MathHelper.sqrt(d0 * d0 + d1 * d1);

			if ((double) f >= 1.0E-4D) {
				emberling.motionX += d0 / (double) f * 0.5D * 0.800000011920929D + emberling.motionX * 0.20000000298023224D;
				emberling.motionZ += d1 / (double) f * 0.5D * 0.800000011920929D + emberling.motionZ * 0.20000000298023224D;
			}

			emberling.motionY = (double) motionY;
		}

	    public void updateTask() {
	    		rotation += 30;
	    		emberling.setRotation(rotation, 0F);
	    	if(emberling.motionY < 0)
	    		emberling.motionY *= 0.5F;
	    }
	}
}