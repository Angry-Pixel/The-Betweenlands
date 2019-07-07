package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO Loot tables
public class EntityCryptCrawler extends EntityMob implements IEntityBL {
	private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.createKey(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	public float standingAngle, prevStandingAngle;

	public EntityCryptCrawler(World world) {
		super(world);
		setSize(1F, 1.25F);
		stepHeight = 1F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_STANDING, false);
	}

	public boolean isStanding() {
		return dataManager.get(IS_STANDING);
	}

	private void setIsStanding(boolean standing) {
		dataManager.set(IS_STANDING, standing);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityCryptCrawler.AICryptCrawlerAttack(this));
		tasks.addTask(3, new EntityAIWander(this, 0.6D));
		tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityZombie.class, 0, false, true, null));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CRYPT_CRAWLER;
	}
	
	@Override
	public void onLivingUpdate() {

		if (!getEntityWorld().isRemote) {

			if (getAttackTarget() != null) {
				faceEntity(getAttackTarget(), 10.0F, 20.0F);
				double distance = getDistance(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);

				if (distance > 5.0D)
					setIsStanding(false);

				if (distance <= 5.0D)
					setIsStanding(true);
			}
	
			if (getAttackTarget() == null)
				setIsStanding(false);
		}

		if (getEntityWorld().isRemote) {
			prevStandingAngle = standingAngle;

			if (standingAngle > 0 && !isStanding())
				standingAngle -= 0.1F;

			if (isStanding() && standingAngle <= 1F)
				standingAngle += 0.1F;
			
			if (standingAngle < 0 && !isStanding())
				standingAngle = 0F;

			if (isStanding() && standingAngle > 1F)
				standingAngle = 1F;
		}

		super.onLivingUpdate();
	}

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevStandingAngle + (standingAngle - prevStandingAngle) * partialTicks;
    }

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntityCryptCrawler.class != entity;
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
	public boolean attackEntityAsMob(Entity entity) {
		if (canEntityBeSeen(entity)) {
			boolean hasHitTarget = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

			if (hasHitTarget) {
				entity.addVelocity(-MathHelper.sin(rotationYaw * 3.141593F / 180.0F) * 0.5F, 0.2D, MathHelper.cos(rotationYaw * 3.141593F / 180.0F) * 0.5F);
				if (!getEntityWorld().isRemote)
					getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundRegistry.CRYPT_CRAWLER_LIVING, SoundCategory.HOSTILE, 1F, 0.5F);
			}
			return hasHitTarget;
		}
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CRYPT_CRAWLER_LIVING;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.CRYPT_CRAWLER_HURT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CRYPT_CRAWLER_DEATH;
	}

	static class AICryptCrawlerAttack extends EntityAIAttackMelee {

		public AICryptCrawlerAttack(EntityCryptCrawler crypt_crawler) {
			super(crypt_crawler, 0.6D, false);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}

}