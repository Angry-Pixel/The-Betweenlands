package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
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
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;

public class EntityAnadia extends EntityCreature implements IEntityBL {
	private static final DataParameter<Float> FISH_SIZE = EntityDataManager.<Float>createKey(EntityAnadia.class, DataSerializers.FLOAT);
	private static final DataParameter<Byte> HEAD_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> BODY_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> TAIL_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.BOOLEAN);
	private static float BASE_MULTIPLE = 1F; // just a arbitrary number to increase the size multiplier

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
        tasks.addTask(1, new EntityAIMoveTowardsRestriction(this, 0.4D));
        tasks.addTask(2, new EntityAIWander(this, 0.5D, 20));
        tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(4, new EntityAILookIdle(this));
        // TODO leaving this for future hostile code
        // targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 0, true, true, null));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FISH_SIZE, Math.round(Math.max(0.125F, rand.nextFloat()) * 16F) / 16F);
        dataManager.register(HEAD_TYPE, (byte)rand.nextInt(3));
        dataManager.register(BODY_TYPE, (byte)rand.nextInt(3));
        dataManager.register(TAIL_TYPE, (byte)rand.nextInt(3));
        dataManager.register(IS_LEAPING, false);
    }

    public float getFishSize() {
        return dataManager.get(FISH_SIZE);
    }

    private void setFishSize(float size) {
        dataManager.set(FISH_SIZE, size);
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

    public boolean isLeaping() {
        return dataManager.get(IS_LEAPING);
    }

    private void setIsLeaping(boolean leaping) {
        dataManager.set(IS_LEAPING, leaping);
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("fishSize", getFishSize());
		nbt.setByte("headType", getHeadType());
		nbt.setByte("bodyType", getBodyType());
		nbt.setByte("tailType", getTailType());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setFishSize(nbt.getFloat("fishSize"));
		setHeadType(nbt.getByte("headType"));
		setBodyType(nbt.getByte("bodyType"));
		setTailType(nbt.getByte("tailType"));
	}

	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        setSize(getFishSize(), getFishSize() * 0.75F);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D + getSpeedMods());
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D + getHealthMods());
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D + getStrengthMods());
        if(!getEntityWorld().isRemote) {
        	System.out.println("FISH SIZE:" + getFishSize());
        	System.out.println("HEAD:" + getHeadType() + " BODY: " + getBodyType() + " TAIL: " + getTailType());
        	System.out.println("SPEED:" + getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
        	System.out.println("HEALTH:" + getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue());
        	System.out.println("STRENGTH:" + getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
        	System.out.println("STAMINA:" + getStaminaMods());
        }
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
			if (isInWater()) {
				Vec3d vec3d = getLook(0.0F);
				for (int i = 0; i < 2; ++i)
					getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + (rand.nextDouble() - 0.5D) * (double) width - vec3d.x , posY + rand.nextDouble() * (double) height - vec3d.y , posZ + (rand.nextDouble() - 0.5D) * (double) width - vec3d.z, 0.0D, 0.0D, 0.0D, new int[0]);
			}
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

	// TODO make it work of the speed modifiers properly
	@Override
	public void onUpdate() {
		if (getEntityWorld().isRemote)
			setSize(getFishSize(), getFishSize() * 0.75F);

		if(!getEntityWorld().isRemote) {
			if(getAttackTarget() != null && !getEntityWorld().containsAnyLiquid(getAttackTarget().getEntityBoundingBox())) {
				Double distance = getPosition().getDistance((int) getAttackTarget().posX, (int) getAttackTarget().posY, (int) getAttackTarget().posZ);
				if (distance > 1.0F && distance < 6.0F) // && getAttackTarget().getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && getAttackTarget().getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && rand.nextInt(3) == 0)
					if (isInWater() && getEntityWorld().isAirBlock(new BlockPos((int) posX, (int) posY + 1, (int) posZ))) {
						if(!isLeaping()) {
							setIsLeaping(true);
							getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.HOSTILE, 1F, 2F);
						}
						double distanceX = getAttackTarget().posX - posX;
						double distanceZ = getAttackTarget().posZ - posZ;
						float distanceSqrRoot = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ);
						motionX = distanceX / distanceSqrRoot * 0.5D * 0.900000011920929D + motionX * 0.70000000298023224D;
						motionZ = distanceZ / distanceSqrRoot * 0.5D * 0.900000011920929D + motionZ * 0.70000000298023224D;
						motionY = 0.4D;
					}
			}
		}
		super.onUpdate();
	}

	// TODO make it work of the speed modifiers properly
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
					motionY -= 0.004D;
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
			this.speed = speedModifier;
			this.health = healthModifier;
			this.strength = strengthModifier;
			this.stamina = staminaModifier;
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
			this.speed = speedModifier;
			this.health = healthModifier;
			this.strength = strengthModifier;
			this.stamina = staminaModifier;
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
			this.speed = speedModifier;
			this.health = healthModifier;
			this.strength = strengthModifier;
			this.stamina = staminaModifier;
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

	// TODO make it work of the speed modifiers properly
    //AIs
    static class AnadiaMoveHelper extends EntityMoveHelper {
        private final EntityAnadia anadia;

        public AnadiaMoveHelper(EntityAnadia anadia) {
            super(anadia);
            this.anadia = anadia;
        }

        @Override
		public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO && !anadia.getNavigator().noPath()) {
                double d0 = posX - anadia.posX;
                double d1 = posY - anadia.posY;
                double d2 = posZ - anadia.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double) MathHelper.sqrt(d3);
                d1 = d1 / d3;
                float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                anadia.rotationYaw = limitAngle(anadia.rotationYaw, f, 90.0F);
                anadia.renderYawOffset = anadia.rotationYaw;
                float f1 = (float) (speed * anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                anadia.setAIMoveSpeed(anadia.getAIMoveSpeed() + (f1 - anadia.getAIMoveSpeed()) * 0.125F);
                double d4 = Math.sin((double) (anadia.ticksExisted + anadia.getEntityId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (anadia.rotationYaw * 0.017453292F));
                double d6 = Math.sin((double) (anadia.rotationYaw * 0.017453292F));
                anadia.motionX += d4 * d5;
                anadia.motionZ += d4 * d6;
                d4 = Math.sin((double) (anadia.ticksExisted + anadia.getEntityId()) * 0.75D) * 0.05D;
                anadia.motionY += d4 * (d6 + d5) * 0.25D;
                anadia.motionY += (double) anadia.getAIMoveSpeed() * d1 * 0.1D;
                EntityLookHelper entitylookhelper = anadia.getLookHelper();
                double d7 = anadia.posX + d0 / d3 * 2.0D;
                double d8 = (double) anadia.getEyeHeight() + anadia.posY + d1 / d3;
                double d9 = anadia.posZ + d2 / d3 * 2.0D;
                double d10 = entitylookhelper.getLookPosX();
                double d11 = entitylookhelper.getLookPosY();
                double d12 = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                    d10 = d7;
                    d11 = d8;
                    d12 = d9;
                }

                anadia.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
            } else {
                anadia.setAIMoveSpeed(0.0F);
            }
        }
    }
}
