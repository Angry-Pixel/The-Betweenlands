package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBarrishee extends EntityMob {

	private static final DataParameter<Boolean> AMBUSH_SPAWNED = EntityDataManager.createKey(EntityBarrishee.class, DataSerializers.BOOLEAN);
	public float standingAngle, prevStandingAngle;

	public EntityBarrishee(World world) {
		super(world);
		setSize(2.25F, 1.8F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(AMBUSH_SPAWNED, true);
	}

	public boolean isAmbushSpawn() {
		return dataManager.get(AMBUSH_SPAWNED);
	}

	private void setIsAmbushSpawn(boolean is_ambush) {
		dataManager.set(AMBUSH_SPAWNED, is_ambush);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityBarrishee.AIBarrisheeAttack(this));
		tasks.addTask(3, new EntityAIWander(this, 0.4D, 20));
		//tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		//tasks.addTask(5, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityZombie.class, 0, true, true, null));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
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
		return 1;
	}

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevStandingAngle + (standingAngle - prevStandingAngle) * partialTicks;
    }

	@Override
	public void onLivingUpdate() {

		if (getEntityWorld().isRemote) {
			prevStandingAngle = standingAngle;

			if (isAmbushSpawn() && standingAngle <= 0.1F)
				standingAngle += 0.01F;
			if (isAmbushSpawn() && standingAngle > 0.1F && standingAngle <= 1F)
				standingAngle += 0.1F;

			if (isAmbushSpawn() && standingAngle > 1F)
				standingAngle = 1F;
		}

		super.onLivingUpdate();
	}

	static class AIBarrisheeAttack extends EntityAIAttackMelee {

		public AIBarrisheeAttack(EntityBarrishee barrishee) {
			super(barrishee, 0.4D, false);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (4.0F + attackTarget.width);
		}
	}

}