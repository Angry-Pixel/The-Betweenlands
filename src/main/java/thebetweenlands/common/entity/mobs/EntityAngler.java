package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
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
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityAngler extends EntityMob implements IEntityBL {
	private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityAngler.class, DataSerializers.BOOLEAN);

	public EntityAngler(World world) {
		super(world);
		setSize(0.8F, 0.7F);
		this.moveHelper = new EntityAngler.AnglerMoveHelper(this);
		tasks.addTask(0, new EntityAIAttackMelee(this, 0.75D, true));
		tasks.addTask(1, new EntityAIMoveTowardsRestriction(this, 0.75D));
		tasks.addTask(2, new EntityAIWander(this, 0.75D, 80));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(4, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true, true, null));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_LEAPING, false);
	}

	public boolean isLeaping() {
		return dataManager.get(IS_LEAPING);
	}

	private void setIsLeaping(boolean leaping) {
		dataManager.set(IS_LEAPING, leaping);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(34.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	@Override
	protected SoundEvent getHurtSound() {
		return super.getHurtSound();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.ANGLER_DEATH;
	}

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
		return LootTableRegistry.ANGLER;
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
	}

	public boolean isGrounded() {
		return !isInWater() && worldObj.isAirBlock(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY + 1), MathHelper.floor_double(posZ))) && worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 1), MathHelper.floor_double(posZ))).getBlock().isCollidable();
	}

	@Override
    protected PathNavigate getNewNavigator(World world){
        return new PathNavigateSwimmer(this, world);
    }

	@Override
    protected boolean isValidLightLevel() {
        return true;
    }

	@Override
    public boolean isNotColliding() {
		 return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.worldObj.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty();
    }

	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return worldObj.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + worldObj.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
    }

	@Override
	public void onLivingUpdate() {
		if (worldObj.isRemote) {
			if (isInWater()) {
				Vec3d vec3d = getLook(0.0F);
				for (int i = 0; i < 2; ++i)
					worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + (rand.nextDouble() - 0.5D) * (double) width - vec3d.xCoord * 1.5D, posY + rand.nextDouble() * (double) height - vec3d.yCoord * 1.5D, posZ + (rand.nextDouble() - 0.5D) * (double) width - vec3d.zCoord * 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
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
			if(worldObj.getWorldTime()%5==0)
				worldObj.playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_GUARDIAN_FLOP, SoundCategory.HOSTILE, 1F, 1F);
				this.damageEntity(DamageSource.drown, 0.5F);
		}
		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		if(!worldObj.isRemote) {
		if(getAttackTarget() != null && !worldObj.containsAnyLiquid(getAttackTarget().getEntityBoundingBox())) {
			Double distance = this.getPosition().getDistance((int) getAttackTarget().posX, (int) getAttackTarget().posY, (int) getAttackTarget().posZ);
			if (distance > 1.0F && distance < 6.0F) // && getAttackTarget().getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && getAttackTarget().getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && rand.nextInt(3) == 0)
				if (isInWater() && worldObj.isAirBlock(new BlockPos((int) posX, (int) posY + 1, (int) posZ))) {
					if(!isLeaping()) {
						setIsLeaping(true);
						worldObj.playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.HOSTILE, 1F, 2F);
					}
					double distanceX = getAttackTarget().posX - posX;
					double distanceZ = getAttackTarget().posZ - posZ;
					float distanceSqrRoot = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
					motionX = distanceX / distanceSqrRoot * 0.5D * 0.900000011920929D + motionX * 0.70000000298023224D;
					motionZ = distanceZ / distanceSqrRoot * 0.5D * 0.900000011920929D + motionZ * 0.70000000298023224D;
					motionY = 0.4D;
					}
			}
		}
		super.onUpdate();
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if (isServerWorld()) {
			if (isInWater()) {
				moveRelative(strafe, forward, 0.1F);
				moveEntity(motionX, motionY, motionZ);
				motionX *= 0.8999999761581421D;
				motionY *= 0.8999999761581421D;
				motionZ *= 0.8999999761581421D;

				if (getAttackTarget() == null) {
					motionY -= 0.005D;
				}
			} else {
				super.moveEntityWithHeading(strafe, forward);
			}
		} else {
			super.moveEntityWithHeading(strafe, forward);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
			if (super.attackEntityAsMob(entity)) {
				this.playSound(SoundRegistry.ANGLER_ATTACK, 1, 1);
				return true;
			}
		return false;
	}
	
	//AIs
	
	static class AnglerMoveHelper extends EntityMoveHelper {
		private final EntityAngler angler;

		public AnglerMoveHelper(EntityAngler angler) {
			super(angler);
			this.angler = angler;
		}

		public void onUpdateMoveHelper() {
			if (action == EntityMoveHelper.Action.MOVE_TO && !angler.getNavigator().noPath()) {
				double d0 = posX - angler.posX;
				double d1 = posY - angler.posY;
				double d2 = posZ - angler.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;
				d3 = (double) MathHelper.sqrt_double(d3);
				d1 = d1 / d3;
				float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
				angler.rotationYaw = limitAngle(angler.rotationYaw, f, 90.0F);
				angler.renderYawOffset = angler.rotationYaw;
				float f1 = (float) (speed * angler.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				angler.setAIMoveSpeed(angler.getAIMoveSpeed() + (f1 - angler.getAIMoveSpeed()) * 0.125F);
				double d4 = Math.sin((double) (angler.ticksExisted + angler.getEntityId()) * 0.5D) * 0.05D;
				double d5 = Math.cos((double) (angler.rotationYaw * 0.017453292F));
				double d6 = Math.sin((double) (angler.rotationYaw * 0.017453292F));
				angler.motionX += d4 * d5;
				angler.motionZ += d4 * d6;
				d4 = Math.sin((double) (angler.ticksExisted + angler.getEntityId()) * 0.75D) * 0.05D;
				angler.motionY += d4 * (d6 + d5) * 0.25D;
				angler.motionY += (double) angler.getAIMoveSpeed() * d1 * 0.1D;
				EntityLookHelper entitylookhelper = angler.getLookHelper();
				double d7 = angler.posX + d0 / d3 * 2.0D;
				double d8 = (double) angler.getEyeHeight() + angler.posY + d1 / d3;
				double d9 = angler.posZ + d2 / d3 * 2.0D;
				double d10 = entitylookhelper.getLookPosX();
				double d11 = entitylookhelper.getLookPosY();
				double d12 = entitylookhelper.getLookPosZ();

				if (!entitylookhelper.getIsLooking()) {
					d10 = d7;
					d11 = d8;
					d12 = d9;
				}

				angler.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
			} else {
				angler.setAIMoveSpeed(0.0F);
			}
		}
	}

}
