package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EntityCaveFish extends EntityCreature implements IEntityBL {
	
    public EntityCaveFish(World world) {
        super(world);
        setSize(0.6F, 0.4F);
        setAir(80);
        this.moveHelper = new EntityCaveFish.CaveFishMoveHelper(this);
        setPathPriority(PathNodeType.WALKABLE, -8.0F);
        setPathPriority(PathNodeType.BLOCKED, -8.0F);
        setPathPriority(PathNodeType.WATER, 16.0F);
    }

    @Override
    protected void initEntityAI() {
    	tasks.addTask(0, new EntityAIWander(this, 0.5D, 10));
        tasks.addTask(1, new EntityAIMoveTowardsRestriction(this, 0.4D));
        tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(3, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
    }

    @Override
    public PathNavigate createNavigator(World world) {
        return new PathNavigateSwimmer(this, world);
    }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
			for (int x = 0; x < 3 + world.rand.nextInt(4); x++) {
				EntityCaveFishSmall fish = new EntityCaveFishSmall(world);
				fish.setLocationAndAngles(posX, posY, posZ, world.rand.nextFloat() * 360, 0);
				world.spawnEntity(fish);
			}
		}
		return livingdata;
	}

	@Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundRegistry.FISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.FISH_DEATH;
    }

    @Override
    @MethodsReturnNonnullByDefault
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.posY <= WorldProviderBetweenlands.CAVE_WATER_HEIGHT && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
    }

    @Override
    public boolean isInWater() {
        return world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getMaterial() == Material.WATER;
    }

    @Override
    public void travel(float strafe, float up, float forward) {
        if (isServerWorld()) {
            if (isInWater()) {
                moveRelative(strafe, up, forward, 0.1F);
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
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		if (world.isRemote) {
			// maybe 
		}

		if (inWater) {
			setAir(300);
		} else if (onGround) {
			motionY += 0.25D;
			motionX += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.075F);
			motionZ += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.075F);
			rotationYaw = rand.nextFloat() * 360.0F;
			onGround = false;
			isAirBorne = true;
			if (world.getTotalWorldTime() % 5 == 0)
				world.playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_GUARDIAN_FLOP,SoundCategory.HOSTILE, 1F, 1F);
			this.damageEntity(DamageSource.DROWN, 0.5F);
		}
    }

	@Override
    public boolean isNotColliding() {
        return this.getEntityWorld().getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.getEntityWorld().checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return null;//LootTableRegistry.BLIND_CAVE_FISH;
    }

    static class CaveFishMoveHelper extends EntityMoveHelper {
        private final EntityCaveFish fish;

        public CaveFishMoveHelper(EntityCaveFish fish) {
            super(fish);
            this.fish = fish;
        }

        @Override
		public void onUpdateMoveHelper() {
        	  if (action == EntityMoveHelper.Action.MOVE_TO && !fish.getNavigator().noPath()) {
                  double targetX = posX - fish.posX;
                  double targetY = posY - fish.posY;
                  double targetZ = posZ - fish.posZ;
                  double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
                  targetDistance = (double) MathHelper.sqrt(targetDistance);
                  targetY = targetY / targetDistance;
                  float targetAngle = (float) (MathHelper.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
                  fish.rotationYaw = limitAngle(fish.rotationYaw, targetAngle, 90.0F);
                  fish.renderYawOffset = fish.rotationYaw;
                  float travelSpeed = (float) (speed * fish.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                  fish.setAIMoveSpeed(fish.getAIMoveSpeed() + (travelSpeed - fish.getAIMoveSpeed()) * 0.125F);
                  double wiggleSpeed = Math.sin((double) (fish.ticksExisted + fish.getEntityId()) * 0.5D) * fish.height * 0.05D;
                  double wiggleOffsetX = Math.cos((double) (fish.rotationYaw * 0.017453292F));
                  double wiggleOffsetZ = Math.sin((double) (fish.rotationYaw * 0.017453292F));
                  fish.motionX += wiggleSpeed * wiggleOffsetX;
                  fish.motionZ += wiggleSpeed * wiggleOffsetZ;
                  wiggleSpeed = Math.sin((double) (fish.ticksExisted + fish.getEntityId()) * 0.75D) * 0.05D;
                  fish.motionY += wiggleSpeed * (wiggleOffsetZ + wiggleOffsetX) * 0.25D;
                  fish.motionY += (double) fish.getAIMoveSpeed() * targetY * 0.1D;
                  EntityLookHelper entitylookhelper = fish.getLookHelper();
                  double targetDirectionX = fish.posX + targetX / targetDistance * 2.0D;
                  double targetDirectionY = (double) fish.getEyeHeight() + fish.posY + targetY / targetDistance;
                  double targetDirectionZ = fish.posZ + targetZ / targetDistance * 2.0D;
                  double lookX = entitylookhelper.getLookPosX();
                  double lookY = entitylookhelper.getLookPosY();
                  double lookZ = entitylookhelper.getLookPosZ();

                  if (!entitylookhelper.getIsLooking()) {
                  	lookX = targetDirectionX;
                  	lookY = targetDirectionY;
                  	lookZ = targetDirectionZ;
                  }

                  fish.getLookHelper().setLookPosition(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);
              } else {
                  fish.setAIMoveSpeed(0.0F);
              }
          }
    }
}
