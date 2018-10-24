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
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBloodSnail extends EntityMob implements IEntityBL {
	public static final IAttribute RANGED_ATTACK_MIN_DIST_ATTRIB = (new RangedAttribute(null, "bl.rangedAttackMinDist", 3.0D, 0, Double.MAX_VALUE)).setDescription("Minimum range at which the ranged attack is used");
	public static final IAttribute RANGED_ATTACK_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.rangedAttackCooldown", 50, 0, Integer.MAX_VALUE)).setDescription("Ranged attack cooldown in ticks");

	protected int rangedAttackTimer = 0;

	public EntityBloodSnail(World world) {
		super(world);
		setSize(0.7F, 0.5F);
		stepHeight = 0.0F;
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1D, false));
		tasks.addTask(2, new EntityAIWander(this, 1D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(4, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getAttributeMap().registerAttribute(RANGED_ATTACK_MIN_DIST_ATTRIB);
		getAttributeMap().registerAttribute(RANGED_ATTACK_COOLDOWN_ATTRIB);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.BLOOD_SNAIL;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SNAIL_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
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
				if (world.getDifficulty() == EnumDifficulty.NORMAL)
					duration = 7;
				else if (world.getDifficulty() == EnumDifficulty.HARD)
					duration = 15;

				if (duration > 0) {
					((EntityLiving) entity).addPotionEffect(new PotionEffect(MobEffects.POISON, duration * 20, 0));
					((EntityLiving) entity).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration * 20, 0));
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
			double minDist = this.getEntityAttribute(RANGED_ATTACK_MIN_DIST_ATTRIB).getAttributeValue();

			if(distance > minDist) {
				int cooldown = (int) this.getEntityAttribute(RANGED_ATTACK_COOLDOWN_ATTRIB).getAttributeValue();

				if (getRangeAttackTimer() < cooldown) {
					setRangeAttackTimer(getRangeAttackTimer() + 1);
				} else if (getRangeAttackTimer() >= cooldown) {
					shootMissile(getAttackTarget(), distance);
				}
			}
		}
	}

	public void shootMissile(EntityLivingBase entity, float distance) {
		setRangeAttackTimer(0);
		if (canEntityBeSeen(entity)) {
			EntityThrowable missile = new EntitySnailPoisonJet(world, this);
			missile.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
			missile.rotationPitch -= -20.0F;
			double targetX = entity.posX + entity.motionX - this.posX;
			double targetY = entity.posY + entity.getEyeHeight() / 2.0D - this.posY;
			double targetZ = entity.posZ + entity.motionZ - this.posZ;
			float target = MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
			missile.shoot(targetX, targetY + target * 0.1F, targetZ, 0.75F, 8.0F);
			world.spawnEntity(missile);
		}
	}

	public int getRangeAttackTimer() {
		return rangedAttackTimer;
	}

	public void setRangeAttackTimer(int size) {
		rangedAttackTimer = size;
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
