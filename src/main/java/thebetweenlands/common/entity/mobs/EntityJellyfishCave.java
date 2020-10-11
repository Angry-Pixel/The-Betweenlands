package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;

public class EntityJellyfishCave extends EntityCreature implements IEntityBL {

	private static final DataParameter<Float> JELLYFISH_SIZE = EntityDataManager.<Float>createKey(EntityJellyfishCave.class, DataSerializers.FLOAT);

	public EntityJellyfishCave(World world) {
		super(world);
        setSize(0.8F, 0.8F);
        moveHelper = new EntityJellyfishCave.JellyfishMoveHelper(this);
		setPathPriority(PathNodeType.WALKABLE, -8.0F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.WATER, 16.0F);
	}

	@Override
    protected void initEntityAI() {
		tasks.addTask(3, new EntityAIWander(this, 0.5D, 20));
        tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 0.4D));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(JELLYFISH_SIZE, 0.75F);
    }

	@Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
    }

    @Nullable
	@Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
    	if(!getEntityWorld().isRemote) {
    		setJellyfishSize(Math.round(Math.max(0.75F, rand.nextFloat()) * 16F) / 16F);
    	}
        return super.onInitialSpawn(difficulty, livingdata);
    }
    
    public float getJellyfishSize() {
        return dataManager.get(JELLYFISH_SIZE);
    }

    private void setJellyfishSize(float size) {
        dataManager.set(JELLYFISH_SIZE, size);
        setSize(getJellyfishSize() * 0.5F, getJellyfishSize());
        setPosition(posX, posY, posZ);
    }


    public void notifyDataManagerChange(DataParameter<?> key) {
    	 if (JELLYFISH_SIZE.equals(key)) {
             setSize(getJellyfishSize(), getJellyfishSize());
             rotationYaw = rotationYawHead;
             renderYawOffset = rotationYawHead;
         }
        super.notifyDataManagerChange(key);
    }
 
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("jellyfishSize", getJellyfishSize());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setJellyfishSize(nbt.getFloat("jellyfishSize"));
	}

	@Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return super.getHurtSound(source);
    }
/*
    @Override
    protected SoundEvent getDeathSound() {
       return SoundRegistry.JELLYFISH_DEATH;
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
        return null;//LootTableRegistry.JELLYFISH;
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
			if(getEntityWorld().getTotalWorldTime()%20==0)
				damageEntity(DamageSource.DROWN, 0.5F);
		}

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		 if (getMoveHelper().isUpdating() && motionY < 0)  //only way I can figure to stop the pathfinding messing up rendering rotations when looking for a new path atm
			 rotationYaw = 0F;
	}

    @Override
    public void travel(float strafe, float up, float forward) {
        if (isServerWorld()) {
            if (isInWater()) {
                moveRelative(strafe, up, forward, 0.075F);
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

    static class JellyfishMoveHelper extends EntityMoveHelper {
        private final EntityJellyfishCave jellyfish;

        public JellyfishMoveHelper(EntityJellyfishCave jellyfishIn) {
            super(jellyfishIn);
            this.jellyfish= jellyfishIn;
        }

        @Override
		public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO && !jellyfish.getNavigator().noPath()) {
                double targetX = posX - jellyfish.posX;
                double targetY = posY - jellyfish.posY;
                double targetZ = posZ - jellyfish.posZ;
                double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
                targetDistance = (double) MathHelper.sqrt(targetDistance);
                targetY = targetY / targetDistance;
                float targetAngle = (float) (MathHelper.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
                jellyfish.rotationYaw = limitAngle(jellyfish.rotationYaw, targetAngle, 90.0F);
                jellyfish.renderYawOffset = jellyfish.rotationYaw;
                float travelSpeed = (float) (speed * jellyfish.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                jellyfish.setAIMoveSpeed(jellyfish.getAIMoveSpeed() + (travelSpeed - jellyfish.getAIMoveSpeed()) * 0.125F);
                jellyfish.motionY += (double) jellyfish.getAIMoveSpeed() * targetY * 0.2D;
                EntityLookHelper entitylookhelper = jellyfish.getLookHelper();
                double targetDirectionX = jellyfish.posX + targetX / targetDistance * 2.0D;
                double targetDirectionY = (double) jellyfish.getEyeHeight() + jellyfish.posY + targetY / targetDistance;
                double targetDirectionZ = jellyfish.posZ + targetZ / targetDistance * 2.0D;
                double lookX = entitylookhelper.getLookPosX();
                double lookY = entitylookhelper.getLookPosY();
                double lookZ = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                	lookX = targetDirectionX;
                	lookY = targetDirectionY;
                	lookZ = targetDirectionZ;
                }

                jellyfish.getLookHelper().setLookPosition(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);
            } else {
                jellyfish.setAIMoveSpeed(0.0F);
            }
        }
    }
}
