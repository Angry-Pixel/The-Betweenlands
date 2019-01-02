package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;

public class EntityShambler extends EntityMob implements IEntityMultiPart, IEntityBL {

	private static final DataParameter<Boolean> JAWS_OPEN = EntityDataManager.createKey(EntityShambler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TONGUE_EXTEND = EntityDataManager.createKey(EntityShambler.class, DataSerializers.BOOLEAN);
	public int jawAngle, prevJawAngle;
	public int tongueLength, prevTongueLength;

	public MultiPartEntityPart[] tongue_array; // we may want to make more tongue parts
	public MultiPartEntityPart tongue_end = new MultiPartEntityPart(this, "tongue_end", 0.5F, 0.5F);

	public EntityShambler(World world) {
		super(world);
		this.setSize(1.0F, 1.25F);
		tongue_array = new MultiPartEntityPart[] {tongue_end};
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 0.8D, true));
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWander(this, 0.75D));
		//this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(JAWS_OPEN, false);
		dataManager.register(TONGUE_EXTEND, false);
	}

	public boolean jawsAreOpen() {
		return dataManager.get(JAWS_OPEN);
	}

	private void setOpenJaws(boolean jawState) {
		dataManager.set(JAWS_OPEN, jawState);
	}
	
	public boolean isExtendingTongue() {
		return dataManager.get(TONGUE_EXTEND);
	}

	private void setExtendingTongue(boolean tongueState) {
		dataManager.set(TONGUE_EXTEND, tongueState);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(28.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;

	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	public void onLivingUpdate() {
		if (!getEntityWorld().isRemote) {

			if (getAttackTarget() != null) {
				faceEntity(getAttackTarget(), 10.0F, 20.0F);
				double distance = getDistance(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);

				if (distance > 5.0D) {
					if(jawsAreOpen()) {
						setOpenJaws(false);
						if (isExtendingTongue())
							setExtendingTongue(false);
					}
				}

				if (distance <= 5.0D) {
					if (!jawsAreOpen()) {
						setOpenJaws(true);
						if (!isExtendingTongue())
							setExtendingTongue(true);
					}
				}
			}

			if (getAttackTarget() == null) {
				if(jawsAreOpen())
					setOpenJaws(false);
				if (isExtendingTongue())
					setExtendingTongue(false);
			}
		}

			prevJawAngle = jawAngle;
			prevTongueLength = tongueLength;

			if (jawAngle > 0 && !jawsAreOpen())
				jawAngle -= 1;

			if (jawsAreOpen() && jawAngle <= 10)
				jawAngle += 1;
			
			if (jawAngle < 0 && !jawsAreOpen())
				jawAngle = 0;

			if (jawsAreOpen() && jawAngle > 10)
				jawAngle = 10;

			if (tongueLength > 1 && !isExtendingTongue())
				tongueLength -= 1;

			if (isExtendingTongue() && tongueLength <= 10)
				tongueLength += 1;
			
			if (tongueLength < 1 && !isExtendingTongue()) {
				tongueLength = 1;
			}

			if (isExtendingTongue() && tongueLength > 10) {
				tongueLength = 10;
				setExtendingTongue(false);
			}
		super.onLivingUpdate();
	}

	@Override
    public void onUpdate() {
		super.onUpdate();
		renderYawOffset = rotationYaw;
		Vec3d vector = getLookVec();
		tongue_end.setLocationAndAngles(posX + ((double) vector.x * tongueLength * 0.5D), (posY + getEyeHeight() - 0.3D) + ((double) vector.y * tongueLength * 0.5D), posZ + ((double) vector.z * tongueLength * 0.5D), 0.0F, 0.0F);
    	checkCollision();
    }

	@Override
	public void updatePassenger(Entity entity) {
		if (entity instanceof EntityLivingBase) {
			double a = Math.toRadians(rotationYaw);
			double offSetX = Math.sin(a) * -0.25D;
			double offSetZ = -Math.cos(a) * -0.25D;
			entity.setPosition(tongue_end.posX + offSetX, tongue_end.posY - entity.height * 0.3D, tongue_end.posZ + offSetZ);
		}
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@SuppressWarnings("unchecked")
	protected Entity checkCollision() {
		List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, tongue_end.getEntityBoundingBox());
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null && !(entity instanceof EntityShambler)) {
				if (entity instanceof EntityLivingBase)
					if (!isBeingRidden()) {
						entity.startRiding(this, true);
						if (isExtendingTongue())
							setExtendingTongue(false);
					}
			}
		}
		return null;
	}

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return prevJawAngle + (jawAngle - prevJawAngle) * partialTicks;
    }

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float dmg) {
		damageShambler(source, dmg); // we may want seperate tongue damage - dunno
		return true;
	}

	protected boolean damageShambler(DamageSource source, float ammount) {
		return super.attackEntityFrom(source, ammount);
	}
}
