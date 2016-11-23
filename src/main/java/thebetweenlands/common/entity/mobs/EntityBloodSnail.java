package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBloodSnail extends EntityMob implements IEntityBL {
	private static final DataParameter<Integer> RANGE_ATTACK_TIMER = EntityDataManager.createKey(EntityBloodSnail.class, DataSerializers.VARINT);

	public EntityBloodSnail(World world) {
		super(world);
		setSize(0.7F, 0.5F);
		stepHeight = 0.0F;
		
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));
		tasks.addTask(2, new EntityAIWander(this, 0.5D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(4, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(RANGE_ATTACK_TIMER, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if (isBurning())
			entityDropItem(new ItemStack(ItemRegistry.SNAIL_FLESH_COOKED, 1, 0), 0.0F);
		else
			entityDropItem(new ItemStack(ItemRegistry.SNAIL_FLESH_RAW, 1, 0), 0.0F);

		if (rand.nextBoolean())
			entityDropItem(EnumItemMisc.BLOOD_SNAIL_SHELL.create(1), 0.0F);
		else
			entityDropItem(EnumItemMisc.POISON_GLAND.create(1), 0.0F);
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SNAIL_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundRegistry.SNAIL_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SNAIL_DEATH;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			if (entity instanceof EntityLiving) {
				byte duration = 0;
				if (worldObj.getDifficulty() == EnumDifficulty.NORMAL)
					duration = 7;
				else if (worldObj.getDifficulty() == EnumDifficulty.HARD)
					duration = 15;

				if (duration > 0) {
					((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("poison"), duration * 20, 0));
					((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("nausea"), duration * 20, 0));
				}
			}
			return true;
		} else
			return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getAttackTarget() != null && this.isEntityAlive()) {
			float distance = (float) getDistance(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);
			if (getRangeAttackTimer() < 100 && distance > 3)
				setRangeAttackTimer(getRangeAttackTimer() + 2);
			if (getRangeAttackTimer() == 100 && distance > 3)
				shootMissile(getAttackTarget(), distance);
		}
	}

	public void shootMissile(EntityLivingBase entity, float distance) {
		setRangeAttackTimer(0);
		if (canEntityBeSeen(entity)) {
			EntityThrowable missile = new EntitySnailPoisonJet(worldObj, this);
			missile.rotationPitch -= -20.0F;
			double targetX = entity.posX + entity.motionX - posX;
			double targetY = entity.posY + entity.getEyeHeight() - 1.100000023841858D - posY;
			double targetZ = entity.posZ + entity.motionZ - posZ;
			float target = MathHelper.sqrt_double(targetX * targetX + targetZ * targetZ);
			missile.setThrowableHeading(targetX, targetY + target * 0.1F, targetZ, 0.75F, 8.0F);
			worldObj.spawnEntityInWorld(missile);
		}
	}

	public int getRangeAttackTimer() {
		return dataManager.get(RANGE_ATTACK_TIMER);
	}

	public void setRangeAttackTimer(int size) {
		dataManager.set(RANGE_ATTACK_TIMER, size);
	}
}
