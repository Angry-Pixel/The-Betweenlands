package thebetweenlands.common.entity.mobs;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.EntityFishBait;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.TranslationHelper;

public class EntityAnadia extends EntityCreature implements IEntityBL {

	private static final DataParameter<Float> FISH_SIZE = EntityDataManager.<Float>createKey(EntityAnadia.class, DataSerializers.FLOAT);
	private static final DataParameter<Byte> HEAD_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> BODY_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> TAIL_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.BOOLEAN);
	//private static final DataParameter<Integer> HUNGER_COOLDOWN = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> STAMINA_TICKS = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> FISH_COLOUR = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);

	private static float BASE_MULTIPLE = 1F; // just a arbitrary number to increase the size multiplier
	public EntityAnadia.AIFindBait aiFindBait;
	public EntityAnadia.AIFindHook aiFindHook;
	public byte BASE = 0;
	public byte SILVER = 1;
	public byte SMOKED = 2;
	public byte ROTTEN = 3;
	
	public EntityAnadia(World world) {
		super(world);
        setSize(0.8F, 0.8F);
        moveHelper = new EntityAnadia.AnadiaMoveHelper(this);
		setPathPriority(PathNodeType.WALKABLE, -8.0F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.WATER, 16.0F);
	}

	@Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAIAttackMelee(this, 0.7D, true) {
            @Override
            protected double getAttackReachSqr(EntityLivingBase attackTarget) {
                return 0.75D + attackTarget.width;
            }
        });
        tasks.addTask(1, new EntityAIAvoidEntity(this, EntityLurker.class, 8F, 4D, 8D));
        tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 0.4D));
        tasks.addTask(3, new EntityAnadia.EntityAIAFishCalledWander(this, 0.5D, 20));
        tasks.addTask(4, new EntityAIPanicWhenHooked(this));
        tasks.addTask(5, new EntityAIAvoidEntity(this, EntityLurker.class, 8F, 4D, 8D));
       // tasks.addTask(3, new EntityAnadia.EntityAIAvoidWhenHooked(this, EntityPlayer.class, 16));
        aiFindBait = new EntityAnadia.AIFindBait(this, 2D);
        aiFindHook = new EntityAnadia.AIFindHook(this, 2D);
        tasks.addTask(6, aiFindBait);
        tasks.addTask(7, aiFindHook);
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        // TODO leaving this for future hostile code
        // targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 0, true, true, null));
       // targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FISH_SIZE, 0.5F);
        dataManager.register(HEAD_TYPE, (byte) 0);
        dataManager.register(BODY_TYPE, (byte) 0);
        dataManager.register(TAIL_TYPE, (byte) 0);
        dataManager.register(IS_LEAPING, false);
      //  dataManager.register(HUNGER_COOLDOWN, 0);
        dataManager.register(STAMINA_TICKS, 40);
        dataManager.register(FISH_COLOUR, (byte) 0);
    }

	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
    }

    @Nullable
	@Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        setHeadType((byte)rand.nextInt(3));
        setBodyType((byte)rand.nextInt(3));
        setTailType((byte)rand.nextInt(3));
        setFishSize(Math.round(Math.max(0.125F, rand.nextFloat()) * 16F) / 16F);
        setTailType((byte)rand.nextInt(3));
        setFishColour((byte)rand.nextInt(2)); // testing colours - TODO set this based on biome spawned in /other possible things
        return super.onInitialSpawn(difficulty, livingdata);
    }

    public float getFishSize() {
        return dataManager.get(FISH_SIZE);
    }

    private void setFishSize(float size) {
        dataManager.set(FISH_SIZE, size);
        setSize(getFishSize(), getFishSize() * 0.75F);
        setPosition(posX, posY, posZ);

        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D + getSpeedMods());
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D + getHealthMods());

        if(getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE) == null)
        	getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D + getStrengthMods());
       // else
        	//getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D + getStrengthMods());

        setHealth(getMaxHealth());
/*
        if(!getEntityWorld().isRemote) {
        	System.out.println("NAME: " + getName());
        	System.out.println("HEALTH: " + getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
        	System.out.println("FISH SIZE:" + getFishSize());
        	System.out.println("HEAD: " + getHeadType() + " BODY: " + getBodyType() + " TAIL: " + getTailType());
        	System.out.println("SPEED: " + getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
        	System.out.println("STRENGTH: " + getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        	System.out.println("STAMINA: " + getStaminaMods());
        }
 */
    }

    public byte getHeadType() {
        return dataManager.get(HEAD_TYPE);
    }

    private void setHeadType(byte type) {
        dataManager.set(HEAD_TYPE, type);
    }

    public byte getBodyType() {
        return dataManager.get(BODY_TYPE);
    }

    private void setBodyType(byte type) {
        dataManager.set(BODY_TYPE, type);
    }

    public byte getTailType() {
        return dataManager.get(TAIL_TYPE);
    }

    private void setTailType(byte type) {
        dataManager.set(TAIL_TYPE, type);
    }

    public byte getFishColour() {
        return dataManager.get(FISH_COLOUR);
    }

    public void setFishColour(byte colour) {
        dataManager.set(FISH_COLOUR, colour);
    }

    public boolean isLeaping() {
        return dataManager.get(IS_LEAPING);
    }

    private void setIsLeaping(boolean leaping) {
        dataManager.set(IS_LEAPING, leaping);
    }
/*    
    public int getHungerCooldown() {
        return dataManager.get(HUNGER_COOLDOWN);
    }

    private void setHungerCooldown(int count) {
        dataManager.set(HUNGER_COOLDOWN, count);
    }
*/
    public int getStaminaTicks() {
        return dataManager.get(STAMINA_TICKS);
    }

    public void setStaminaTicks(int count) {
        dataManager.set(STAMINA_TICKS, count);
    }

	@Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (FISH_SIZE.equals(key)) {
            setSize(getFishSize(), getFishSize());
            rotationYaw = rotationYawHead;
            renderYawOffset = rotationYawHead;
        }
        if (FISH_COLOUR.equals(key)) {
            setFishColour(getFishColour());
        }
        super.notifyDataManagerChange(key);
    }
 
	@Override
    public String getName() {
		String body = TranslationHelper.translateToLocal("entity.thebetweenlands.anadia_body" + "_" + EnumAnadiaBodyParts.values()[getBodyType()].ordinal());
		String tail = TranslationHelper.translateToLocal("entity.thebetweenlands.anadia_tail" + "_" + EnumAnadiaTailParts.values()[getTailType()].ordinal());
		String head = TranslationHelper.translateToLocal("entity.thebetweenlands.anadia_head" + "_" + EnumAnadiaHeadParts.values()[getHeadType()].ordinal());
		return body + " " + tail + " " + head;
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("headType", getHeadType());
		nbt.setByte("bodyType", getBodyType());
		nbt.setByte("tailType", getTailType());
		nbt.setFloat("fishSize", getFishSize());
//		nbt.setInteger("hunger", getHungerCooldown());
		nbt.setByte("fishColour", getFishColour());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setHeadType(nbt.getByte("headType"));
		setBodyType(nbt.getByte("bodyType"));
		setTailType(nbt.getByte("tailType"));
		setFishSize(nbt.getFloat("fishSize"));
//		setHungerCooldown(nbt.getInteger("hunger"));
		setFishColour(nbt.getByte("fishColour"));
	}

	//cumulative speed, health, strength, & stamina modifiers
	public float getSpeedMods() {
		float head = EnumAnadiaHeadParts.values()[getHeadType()].getSpeedModifier();
		float body = EnumAnadiaHeadParts.values()[getBodyType()].getSpeedModifier();
		float tail = EnumAnadiaHeadParts.values()[getTailType()].getSpeedModifier();
		return Math.round((getFishSize() * 0.5F) * head + body + tail * 16F) / 16F;
	}

	public float getHealthMods() {
		float head = EnumAnadiaHeadParts.values()[getHeadType()].getHealthModifier();
		float body = EnumAnadiaHeadParts.values()[getBodyType()].getHealthModifier();
		float tail = EnumAnadiaHeadParts.values()[getTailType()].getHealthModifier();
		return Math.round(getFishSize() * head + body + tail * 2F) / 2F;
	}

	public float getStrengthMods() {
		float head = EnumAnadiaHeadParts.values()[getHeadType()].getStrengthModifier();
		float body = EnumAnadiaHeadParts.values()[getBodyType()].getStrengthModifier();
		float tail = EnumAnadiaHeadParts.values()[getTailType()].getStrengthModifier();
		return Math.round((getFishSize() * 0.5F) * head + body + tail * 2F) / 2F;
	}

	public float getStaminaMods() {
		float head = EnumAnadiaHeadParts.values()[getHeadType()].getStaminaModifier();
		float body = EnumAnadiaHeadParts.values()[getBodyType()].getStaminaModifier();
		float tail = EnumAnadiaHeadParts.values()[getTailType()].getStaminaModifier();
		return Math.round(getFishSize() * BASE_MULTIPLE * head + body + tail * 2F) / 2F;
	}

	@Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return super.getHurtSound(source);
    }
/*
    @Override
    protected SoundEvent getDeathSound() {
       return SoundRegistry.ANADIA_DEATH;
    }
*/
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return null;//LootTableRegistry.ANADIA;
    }

    @Override
    public boolean getCanSpawnHere() {
        return world.getDifficulty() != EnumDifficulty.PEACEFUL && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    public boolean isGrounded() {
        return !isInWater() && world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY + 1), MathHelper.floor(posZ))) && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 1), MathHelper.floor(posZ))).getBlock().isCollidable();
    }

	@Override
    protected PathNavigate createNavigator(World world){
        return new PathNavigateSwimmer(this, world);
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

	@Override
	public void onLivingUpdate() {
		if (getEntityWorld().isRemote) {
		/*	if (isInWater()) {
				Vec3d vec3d = getLook(0.0F);
				for (int i = 0; i < 2; ++i)
					getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + (rand.nextDouble() - 0.5D) * (double) width - vec3d.x , posY + rand.nextDouble() * (double) height - vec3d.y , posZ + (rand.nextDouble() - 0.5D) * (double) width - vec3d.z, 0.0D, 0.0D, 0.0D, new int[0]);
			}*/
		}

		if (inWater) {
			setAir(300);
		} else if (onGround) {
			motionY += 0.5D;
			motionX += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			motionZ += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			rotationYaw = rand.nextFloat() * 360.0F;
			if(isLeaping())
				setIsLeaping(false);
			onGround = false;
			isAirBorne = true;
			if(getEntityWorld().getTotalWorldTime()%5==0)
				getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_GUARDIAN_FLOP, SoundCategory.HOSTILE, 1F, 1F);
				damageEntity(DamageSource.DROWN, 0.5F);
		}

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		if (getEntityWorld().isRemote)
			setSize(getFishSize(), getFishSize() * 0.75F);

		if(!getEntityWorld().isRemote) {
			if(getAttackTarget() != null && !getEntityWorld().containsAnyLiquid(getAttackTarget().getEntityBoundingBox())) {
				double distance = getPosition().getDistance((int) getAttackTarget().posX, (int) getAttackTarget().posY, (int) getAttackTarget().posZ);
				if (distance > 1.0F && distance < 6.0F)
					if (isInWater() && getEntityWorld().isAirBlock(new BlockPos((int) posX, (int) posY + 1, (int) posZ))) 
						leapAtTarget(getAttackTarget().posX, getAttackTarget().posY, getAttackTarget().posZ);
			}

//			if(getHungerCooldown() >= 0)
//				setHungerCooldown(getHungerCooldown() - 1);

			//regains stamina over time whilst not hooked
	        if(!isBeingRidden() && getStaminaTicks() < (int) (getStaminaMods() * 20))
	        	setStaminaTicks(getStaminaTicks() + 1);

		}
		super.onUpdate();
	}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntityBLFishHook) {
			double a = Math.toRadians(rotationYaw);
			double offSetX = -Math.sin(a) * width * 0.5D + entity.width * 0.5D;
			double offSetZ = Math.cos(a) * width * 0.5D + entity.width * 0.5D;
			entity.setPosition(posX + offSetX, posY + height* 0.5D, posZ + offSetZ);
		}
	}

	public void leapAtTarget(double targetX, double targetY, double targetZ) {
		if(!isLeaping()) {
			setIsLeaping(true);
			getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_HOSTILE_SWIM, SoundCategory.NEUTRAL, 1F, 2F);
		}
		double distanceX = targetX - posX;
		double distanceZ = targetZ - posZ;
		float distanceSqrRoot = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ);
		motionX = distanceX / distanceSqrRoot * 0.1D + motionX * getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
		motionZ = distanceZ / distanceSqrRoot * 0.1D + motionZ * getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
		motionY = 0.3F;
		
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (isServerWorld()) {
			if (isInWater()) {
				moveRelative(strafe, up,  forward, 0.1F);
				move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.75D;
				motionY *= 0.75D;
				motionZ *= 0.75D;

				if (getAttackTarget() == null) {
					motionY -= 0.003D;
				}
			} else {
				super.travel(strafe, up, forward);
			}
		} else {
			super.travel(strafe, up, forward);
		}
	}

	@Override
    public boolean isNotColliding() {
		 return getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

	//TODO Made separate methods so we can maintain ordering if new parts are added rather than ordinal juggling
	public enum EnumAnadiaHeadParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		HEAD_1(0.125F, 1F, 1F, 1F),
		HEAD_2(0.25F, 2F, 2F, 2F),
		HEAD_3(0.5F, 3F, 3F, 3F);

		float speed; // added to movement speed
		float health; // added to health
		float strength; // added to attack if aggressive, and/or rod damage per catch
		float stamina; // possible use for how much it pulls or time taken to catch once hooked

		EnumAnadiaHeadParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			speed = speedModifier;
			health = healthModifier;
			strength = strengthModifier;
			stamina = staminaModifier;
		}

		EnumAnadiaHeadParts() {
			this(0.0F, 0.0F, 0.0F, 0.0F); // just in case no modifiers need to be added
		}

		public float getSpeedModifier() {
			return speed;
		}

		public float getHealthModifier() {
			return health;
		}

		public float getStrengthModifier() {
			return strength;
		}

		public float getStaminaModifier() {
			return stamina;
		}
	}

	public enum EnumAnadiaBodyParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		BODY_1(0.125F, 1F, 1F, 1F),
		BODY_2(0.25F, 2F, 2F, 2F),
		BODY_3(0.5F, 3F, 3F, 3F);

		float speed; // added to movement speed
		float health; // added to health
		float strength; // added to attack if aggressive, and/or rod damage per catch
		float stamina; // possible use for how much it pulls or time taken to catch once hooked

		EnumAnadiaBodyParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			speed = speedModifier;
			health = healthModifier;
			strength = strengthModifier;
			stamina = staminaModifier;
		}

		EnumAnadiaBodyParts() {
			this(0.0F, 0.0F, 0.0F, 0.0F); // just in case no modifiers need to be added
		}

		public float getSpeedModifier() {
			return speed;
		}

		public float getHealthModifier() {
			return health;
		}

		public float getStrengthModifier() {
			return strength;
		}

		public float getStaminaModifier() {
			return stamina;
		}
	}

	public enum EnumAnadiaTailParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		TAIL_1(0.125F, 1F, 1F, 1F),
		TAIL_2(0.25F, 2F, 2F, 2F),
		TAIL_3(0.5F, 3F, 3F, 3F);

		float speed; // added to movement speed
		float health; // added to health
		float strength; // added to attack if aggressive, and/or rod damage per catch
		float stamina; // possible use for how much it pulls or time taken to catch once hooked

		EnumAnadiaTailParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			speed = speedModifier;
			health = healthModifier;
			strength = strengthModifier;
			stamina = staminaModifier;
		}

		EnumAnadiaTailParts() {
			this(0.0F, 0.0F, 0.0F, 0.0F); // just in case no modifiers need to be added
		}

		public float getSpeedModifier() {
			return speed;
		}

		public float getHealthModifier() {
			return health;
		}

		public float getStrengthModifier() {
			return strength;
		}

		public float getStaminaModifier() {
			return stamina;
		}
	}

    static class AnadiaMoveHelper extends EntityMoveHelper {
        private final EntityAnadia anadia;

        public AnadiaMoveHelper(EntityAnadia anadia) {
            super(anadia);
            this.anadia = anadia;
        }

        @Override
		public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO && !anadia.getNavigator().noPath()) {
                double targetX = posX - anadia.posX;
                double targetY = posY - anadia.posY;
                double targetZ = posZ - anadia.posZ;
                double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
                targetDistance = (double) MathHelper.sqrt(targetDistance);
                targetY = targetY / targetDistance;
                float targetAngle = (float) (MathHelper.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
                anadia.rotationYaw = limitAngle(anadia.rotationYaw, targetAngle, 90.0F);
                anadia.renderYawOffset = anadia.rotationYaw;
                float travelSpeed = (float) (speed * anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                anadia.setAIMoveSpeed(anadia.getAIMoveSpeed() + (travelSpeed - anadia.getAIMoveSpeed()) * 0.125F);
                double wiggleSpeed = Math.sin((double) (anadia.ticksExisted + anadia.getEntityId()) * 0.5D) * anadia.getFishSize()* 0.05D;
                double wiggleOffsetX = Math.cos((double) (anadia.rotationYaw * anadia.getFishSize() * 0.01F));
                double wiggleOffsetZ = Math.sin((double) (anadia.rotationYaw * anadia.getFishSize() * 0.01F));
                anadia.motionX += wiggleSpeed * wiggleOffsetX;
                anadia.motionZ += wiggleSpeed * wiggleOffsetZ;
                wiggleSpeed = Math.sin((double) (anadia.ticksExisted + anadia.getEntityId()) * 0.75D) * 0.05D;
                anadia.motionY += wiggleSpeed * (wiggleOffsetZ + wiggleOffsetX) * 0.25D;
                anadia.motionY += (double) anadia.getAIMoveSpeed() * targetY * 0.1D;
                EntityLookHelper entitylookhelper = anadia.getLookHelper();
                double targetDirectionX = anadia.posX + targetX / targetDistance * 2.0D;
                double targetDirectionY = (double) anadia.getEyeHeight() + anadia.posY + targetY / targetDistance;
                double targetDirectionZ = anadia.posZ + targetZ / targetDistance * 2.0D;
                double lookX = entitylookhelper.getLookPosX();
                double lookY = entitylookhelper.getLookPosY();
                double lookZ = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                	lookX = targetDirectionX;
                	lookY = targetDirectionY;
                	lookZ = targetDirectionZ;
                }

                anadia.getLookHelper().setLookPosition(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);
            } else {
                anadia.setAIMoveSpeed(0.0F);
            }
        }
    }

    public class AIFindBait extends EntityAIBase {

    	private final EntityAnadia anadia;
    	private double searchRange;
    	public EntityFishBait bait = null;

    	public AIFindBait(EntityAnadia anadiaIn, double searchRangeIn) {
    		anadia = anadiaIn;
    		searchRange = searchRangeIn;
    	}

    	@Override
    	public boolean shouldExecute() {
    		return bait == null;
    		//return anadia.getHungerCooldown() <= 0 && bait == null;
    	}

    	@Override
    	public void startExecuting() {
    		if(bait == null)
    			bait = getClosestBait(searchRange);
    	}

    	@Override
    	public boolean shouldContinueExecuting() {
    		return bait != null && !bait.isDead;
    	//	return anadia.getHungerCooldown() <= 0 && bait != null && !bait.isDead;
        }

		@Override
		public void updateTask() {
			if (!anadia.world.isRemote && shouldContinueExecuting()) {

				if (bait != null) {
					float distance = bait.getDistance(anadia);
					double x = bait.posX;
					double y = bait.posY;
					double z = bait.posZ;
					if (bait.cannotPickup()) {
						if (distance >= 1F) {
							anadia.getLookHelper().setLookPosition(x, y, z, 20.0F, 8.0F);
							moveToItem(bait);
						}

						if (distance <= 2F)
							if (anadia.isInWater() && anadia.getEntityWorld().isAirBlock(new BlockPos(x, y + 1D, z)) && anadia.canEntityBeSeen(bait))
								anadia.leapAtTarget(x, y, z);

						if (distance <= 1F) {
							anadia.getMoveHelper().setMoveTo(x, y, z, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
							//anadia.setHungerCooldown(bait.getBaitSaturation());
							bait.getItem().shrink(1);
							if (bait.getItem().getCount() <= 0)
								bait.setDead();
							anadia.setIsLeaping(false);
							resetTask();
						}
					}
				}
			}
		}
 
		@Override
		public void resetTask() {
			bait = null;
		}

    	@SuppressWarnings("unchecked")
    	public EntityFishBait getClosestBait(double distance) {
    		List<EntityFishBait> list = anadia.getEntityWorld().getEntitiesWithinAABB(EntityFishBait.class, anadia.getEntityBoundingBox().grow(distance, distance, distance));
    		for (Iterator<EntityFishBait> iterator = list.iterator(); iterator.hasNext();) {
    			EntityFishBait bait = iterator.next();
    			if (bait.getAge() >= bait.lifespan || !bait.isInWater())
    				iterator.remove();
    		}
    		if (list.isEmpty())
    			return null;
    		if (!list.isEmpty())
				Collections.shuffle(list);
    		return list.get(0);
    	}

    	public void moveToItem(EntityFishBait bait) {
    		Path pathentity = anadia.getNavigator().getPath();
    		if (pathentity != null) {
    			//entity.getNavigator().setPath(pathentity, 0.5D);
    			anadia.getNavigator().tryMoveToXYZ(bait.posX, bait.posY, bait.posZ, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
    		}
    	}
    }
    
    
    public class AIFindHook extends EntityAIBase {

    	private final EntityAnadia anadia;
    	private double searchRange;
    	public EntityBLFishHook hook = null;

    	public AIFindHook(EntityAnadia anadiaIn, double searchRangeIn) {
    		anadia = anadiaIn;
    		searchRange = searchRangeIn;
    	}

    	@Override
    	public boolean shouldExecute() {
    		return hook == null;
 //   		return anadia.getHungerCooldown() <= 0 && hook == null;
    	}

    	@Override
    	public void startExecuting() {
    		if(hook == null)
    			hook = getClosestHook(searchRange);
    	}

    	@Override
    	public boolean shouldContinueExecuting() {
    		return   hook != null && !hook.isDead &&  hook.getBaited() ? true : hook != null && !hook.isDead && (anadia.getEntityWorld().rand.nextInt(50) == 0 && !hook.getBaited());
//    		return  anadia.getHungerCooldown() <= 0 && hook != null && !hook.isDead &&  hook.getBaited() ? true : anadia.getHungerCooldown() <= 0 && hook != null && !hook.isDead && (anadia.getEntityWorld().rand.nextInt(50) == 0 && !hook.getBaited());
        }

		@Override
		public void updateTask() {
			if (!anadia.world.isRemote && shouldContinueExecuting()) {

				if (hook != null && hook.caughtEntity == null) {
					float distance = hook.getDistance(anadia);
					double x = hook.posX;
					double y = hook.posY;
					double z = hook.posZ;

						if (distance >= 1F) {
							anadia.getLookHelper().setLookPosition(x, y, z, 20.0F, 8.0F);
							moveToEntity(hook);
						}

						if (distance <= 2F)
							if (anadia.isInWater() && anadia.getEntityWorld().isAirBlock(new BlockPos(x, y + 1D, z)) && anadia.canEntityBeSeen(hook))
								anadia.leapAtTarget(x, y, z);

						if (distance <= 1F) {
							anadia.getMoveHelper().setMoveTo(x, y, z, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
						//	anadia.setHungerCooldown(600);
							anadia.setIsLeaping(false);
							hook.caughtEntity = anadia;
							hook.startRiding(anadia, true);
							hook.setBaited(false);
							//resetTask();
						}
					}
			}
		}
 
		@Override
		public void resetTask() {
			hook = null;
		}

    	@SuppressWarnings("unchecked")
    	public EntityBLFishHook getClosestHook(double distance) {
    		List<EntityBLFishHook> list = anadia.getEntityWorld().getEntitiesWithinAABB(EntityBLFishHook.class, anadia.getEntityBoundingBox().grow(distance, distance, distance));
    		for (Iterator<EntityBLFishHook> iterator = list.iterator(); iterator.hasNext();) {
    			EntityBLFishHook hook = iterator.next();
    			if (!hook.isInWater())
    				iterator.remove();
    		}
    		if (list.isEmpty())
    			return null;
    		if (!list.isEmpty())
				Collections.shuffle(list);
    		return list.get(0);
    	}

    	public void moveToEntity(EntityBLFishHook hook) {
    		Path pathentity = anadia.getNavigator().getPath();
    		if (pathentity != null) {
    			//entity.getNavigator().setPath(pathentity, 0.5D);
    			anadia.getNavigator().tryMoveToXYZ(hook.posX, hook.posY, hook.posZ, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
    		}
    	}
    }
    
	public class EntityAIAFishCalledWander extends EntityAIWander {

		public EntityAIAFishCalledWander(EntityCreature creatureIn, double speedIn, int chance) {
			super(creatureIn, speedIn, chance);
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute() && !isBeingRidden();
		}

		@Override
	    public boolean shouldContinueExecuting(){
	        return !entity.getNavigator().noPath() && !isBeingRidden();
	    }
	}
	
	public class EntityAIAvoidWhenHooked extends EntityAIAvoidEntity {
		private final EntityAnadia anadia;
		
		public EntityAIAvoidWhenHooked(EntityAnadia entity, Class classToAvoidIn, float avoidDistanceIn) {
			super(entity, classToAvoidIn, avoidDistanceIn, entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue(), entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			anadia = entity;
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute() && anadia.isBeingRidden() && anadia.getStaminaTicks() >= 1;
		}

		@Override
	    public boolean shouldContinueExecuting(){
	        return !entity.getNavigator().noPath() && anadia.isBeingRidden() && anadia.getStaminaTicks() >= 1;
	    }
	}

	public class EntityAIPanicWhenHooked extends EntityAIPanic {
		private final EntityAnadia anadia;
		
		public EntityAIPanicWhenHooked(EntityAnadia entity) {
			super(entity, entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 2D);
			anadia = entity;
		}

		@Override
		public boolean shouldExecute() {
			return findRandomPosition() && anadia.isBeingRidden() && anadia.getStaminaTicks() >= 1;
		}

		@Override
	    public boolean shouldContinueExecuting(){
	        return !anadia.getNavigator().noPath() && anadia.isBeingRidden() && anadia.getStaminaTicks() >= 1;
	    }
	    
	    public void startExecuting() {
	        anadia.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
	    }
	}
}
