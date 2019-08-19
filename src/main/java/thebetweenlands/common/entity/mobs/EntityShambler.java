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
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO Loot tables
public class EntityShambler extends EntityMob implements IEntityMultiPart, IEntityBL {

	private static final DataParameter<Boolean> JAWS_OPEN = EntityDataManager.createKey(EntityShambler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TONGUE_EXTEND = EntityDataManager.createKey(EntityShambler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> JAW_ANGLE = EntityDataManager.createKey(EntityShambler.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TONGUE_LENGTH = EntityDataManager.createKey(EntityShambler.class, DataSerializers.VARINT);

	private int prevJawAngle;
	private int prevTongueLength;
	
	public MultiPartEntityPart[] tongue_array; // we may want to make more tongue parts
	public MultiPartEntityPart tongue_end = new MultiPartEntityPart(this, "tongue_end", 0.5F, 0.5F);

	public EntityShambler(World world) {
		super(world);
		this.setSize(0.95F, 1.25F);
		tongue_array = new MultiPartEntityPart[16];
		for(int i = 0; i < tongue_array.length - 1; i++) {
			tongue_array[i] = new MultiPartEntityPart(this, "tongue_" + i, 0.125F, 0.125F);
		}
		tongue_array[tongue_array.length - 1] = tongue_end;
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
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
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
		dataManager.register(JAW_ANGLE, 0);
		dataManager.register(TONGUE_LENGTH, 0);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SHAMBLER;
	}

	public boolean jawsAreOpen() {
		return dataManager.get(JAWS_OPEN);
	}

	public void setOpenJaws(boolean jawState) {
		dataManager.set(JAWS_OPEN, jawState);
	}

	public boolean isExtendingTongue() {
		return dataManager.get(TONGUE_EXTEND);
	}

	public void setExtendingTongue(boolean tongueState) {
		dataManager.set(TONGUE_EXTEND, tongueState);
	}

	public void setJawAngle(int count) {
		dataManager.set(JAW_ANGLE, count);
	}

	public void setJawAnglePrev(int count) {
		prevJawAngle = count;
	}

	public void setTongueLength(int count) {
		dataManager.set(TONGUE_LENGTH, count);
	}

	public void setTongueLengthPrev(int count) {
		prevTongueLength = count;
	}

	public int getJawAngle() {
		return dataManager.get(JAW_ANGLE);
	}

	public int getJawAnglePrev() {
		return prevJawAngle;
	}

	public int getTongueLength() {
		return dataManager.get(TONGUE_LENGTH);
	}

	public int getTongueLengthPrev() {
		return prevTongueLength;
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
	protected float getSoundPitch() {
		return super.getSoundPitch() * 1.5F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SHAMBLER_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SHAMBLER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SHAMBLER_DEATH;
	}

	@Override
	public void onLivingUpdate() {
		setJawAnglePrev(getJawAngle());
		setTongueLengthPrev(getTongueLength());
		
		if (!getEntityWorld().isRemote) {
			if (getAttackTarget() != null && canEntityBeSeen(getAttackTarget())) {
				faceEntity(getAttackTarget(), 10.0F, 20.0F);
				double distance = getDistance(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);

				if (distance > 5.0D) {
					if(jawsAreOpen()) {
						setOpenJaws(false);
						if (isExtendingTongue())
							setExtendingTongue(false);
					}
				}

				if (distance <= 5.0D && distance >= 1) {
					if (!jawsAreOpen()) {
						setOpenJaws(true);
						if (!isExtendingTongue()) {
							setExtendingTongue(true);
							playSound(SoundRegistry.SHAMBLER_LICK, 1F, 1F + this.rand.nextFloat() * 0.3F);
						}
					}
				}
			}

			if (getAttackTarget() == null) {
				if(jawsAreOpen())
					setOpenJaws(false);
				if (isExtendingTongue())
					setExtendingTongue(false);
			}

			if (getJawAngle() > 0 && !jawsAreOpen())
				setJawAngle(getJawAngle() - 1);

			if (jawsAreOpen() && getJawAngle() <= 10)
				setJawAngle(getJawAngle() + 1);

			if (getJawAngle() < 0 && !jawsAreOpen())
				setJawAngle(0);

			if (jawsAreOpen() && getJawAngle() > 10)
				setJawAngle(10);

			if (getTongueLength() > 0 && !isExtendingTongue())
				setTongueLength(getTongueLength() - 1);

			if (isExtendingTongue() && getTongueLength() <= 9)
				setTongueLength(getTongueLength() + 1);

			if (getTongueLength() < 0 && !isExtendingTongue())
				setTongueLength(0);

			if (isExtendingTongue() && getTongueLength() > 9) {
				setTongueLength(9);
				setExtendingTongue(false);
			}
		}
		super.onLivingUpdate();
	}

	@Override
    public void onUpdate() {
		super.onUpdate();
		
		Vec3d vector = getLook(1);
		
		double offSetX = vector.x * -0.25D;
		double offsetY = vector.y * -0.25D;
		double offSetZ = vector.z * -0.25D;
		
		double lengthIncrement = 0.5D / tongue_array.length;
		
		double tongueLength = lengthIncrement;
		
		for(MultiPartEntityPart part : tongue_array) {
			part.prevRotationYaw = part.rotationYaw;
			part.prevRotationPitch = part.rotationPitch;
			
			part.lastTickPosX = part.prevPosX = part.posX;
			part.lastTickPosY = part.prevPosY = part.posY;
			part.lastTickPosZ = part.prevPosZ = part.posZ;
			
			part.setPosition(posX + offSetX + ((double) vector.x * getTongueLength() * tongueLength), posY + this.getEyeHeight() - 0.32 + offsetY + ((double) vector.y * getTongueLength() * tongueLength), posZ + offSetZ + ((double) vector.z * getTongueLength() * tongueLength));
			part.rotationYaw = this.rotationYaw;
			part.rotationPitch = this.rotationPitch;
		
			tongueLength += lengthIncrement;
		}
		
		checkCollision();
    }
	
	@Override
	public void updatePassenger(Entity entity) {
		if (entity instanceof EntityLivingBase) {
			double a = Math.toRadians(rotationYaw);
			double offSetX = Math.sin(a) * getTongueLength() > 0 ? -0.125D : -0.35D;
			double offSetZ = -Math.cos(a) * getTongueLength() > 0 ? -0.125D : -0.35D;
			entity.setPosition(tongue_end.posX + offSetX, tongue_end.posY - entity.height * 0.3D, tongue_end.posZ + offSetZ);
			if (entity.isSneaking())
				entity.setSneaking(false);
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

	protected Entity checkCollision() {
		List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, tongue_end.getEntityBoundingBox());
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null && entity == getAttackTarget() && !(entity instanceof IEntityMultiPart) && !(entity instanceof MultiPartEntityPart)) {
				if (entity instanceof EntityLivingBase)
					if (!isBeingRidden()) {
						entity.startRiding(this, true);
						if (!getEntityWorld().isRemote)
							if (isExtendingTongue())
								setExtendingTongue(false); //eeeeeh!
					}
			}
		}
		return null;
	}

    @SideOnly(Side.CLIENT)
    public float smoothedAngle(float partialTicks) {
        return getJawAnglePrev() + (getJawAngle() - getJawAnglePrev()) * partialTicks;
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
